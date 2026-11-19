"""Fixtures compartidas para probar la API REST de Javali por HTTP.

La app tiene que estar corriendo (por defecto en http://localhost:8080).
Configuracion opcional por variables de entorno:
  JAVALI_BASE_URL, JAVALI_ADMIN_EMAIL, JAVALI_ADMIN_PASSWORD,
  JAVALI_ESTUDIANTE_EMAIL, JAVALI_ESTUDIANTE_PASSWORD
"""
import os

import pytest
import requests

BASE_URL = os.getenv("JAVALI_BASE_URL", "http://localhost:8080").rstrip("/")
ADMIN_EMAIL = os.getenv("JAVALI_ADMIN_EMAIL", "vebes@javali.com")
ADMIN_PASSWORD = os.getenv("JAVALI_ADMIN_PASSWORD", "vebes123")
ESTUDIANTE_EMAIL = os.getenv(
    "JAVALI_ESTUDIANTE_EMAIL", "pytest.estudiante@javali.com"
)
ESTUDIANTE_PASSWORD = os.getenv("JAVALI_ESTUDIANTE_PASSWORD", "Pytest12345")
TIMEOUT = 15
TITULO_CURSO_PRUEBAS = "[pytest] Curso de pruebas"
ID_INEXISTENTE = 999999999


class ClienteApi(requests.Session):
    """Sesion HTTP que antepone la URL base a cada ruta."""

    def __init__(self, base_url):
        super().__init__()
        self.base_url = base_url

    def request(self, method, url, **kwargs):
        kwargs.setdefault("timeout", TIMEOUT)
        return super().request(method, self.base_url + url, **kwargs)


def bearer(token):
    return {"Authorization": f"Bearer {token}"}


@pytest.fixture(scope="session")
def http():
    cliente = ClienteApi(BASE_URL)
    try:
        cliente.get("/api/cursos")
    except requests.ConnectionError:
        pytest.exit(
            f"Javali no responde en {BASE_URL}. "
            "Iniciala con: .\\mvnw.cmd spring-boot:run",
            returncode=2,
        )
    return cliente


@pytest.fixture(scope="session")
def admin(http):
    """Administrador del demo: id, email y token."""
    r = http.post(
        "/api/auth/login",
        json={"email": ADMIN_EMAIL, "password": ADMIN_PASSWORD},
    )
    assert r.status_code == 200, (
        f"No se pudo iniciar sesion como administrador ({ADMIN_EMAIL}): "
        f"{r.status_code} {r.text}"
    )
    datos = r.json()
    return {
        "id": datos["usuarioId"],
        "email": datos["email"],
        "headers": bearer(datos["token"]),
    }


@pytest.fixture(scope="session")
def estudiante(http):
    """Estudiante de pruebas: se registra la primera vez y luego se reutiliza."""
    registro = http.post(
        "/api/usuarios",
        json={
            "nombre": "Estudiante Pytest",
            "email": ESTUDIANTE_EMAIL,
            "password": ESTUDIANTE_PASSWORD,
        },
    )
    assert registro.status_code in (201, 409), (
        f"No se pudo registrar al estudiante de pruebas: "
        f"{registro.status_code} {registro.text}"
    )
    r = http.post(
        "/api/auth/login",
        json={"email": ESTUDIANTE_EMAIL, "password": ESTUDIANTE_PASSWORD},
    )
    assert r.status_code == 200, (
        f"No se pudo iniciar sesion como estudiante: {r.status_code} {r.text}"
    )
    datos = r.json()
    return {
        "id": datos["usuarioId"],
        "email": datos["email"],
        "headers": bearer(datos["token"]),
    }


def _crear(http, admin, ruta, cuerpo):
    r = http.post(ruta, json=cuerpo, headers=admin["headers"])
    assert r.status_code == 201, f"POST {ruta}: {r.status_code} {r.text}"
    return r.json()


@pytest.fixture(scope="session")
def contenido(http, admin):
    """Curso > unidad > leccion > ejercicio que se reutilizan entre corridas.

    El curso queda inactivo para no aparecer en las pantallas web. Se busca
    por titulo, asi no se duplica en cada ejecucion (los intentos del
    estudiante impiden borrarlo).
    """
    cursos = http.get("/api/cursos").json()
    curso = next(
        (c for c in cursos if c["titulo"] == TITULO_CURSO_PRUEBAS), None
    )
    if curso is None:
        curso = _crear(http, admin, "/api/cursos", {
            "titulo": TITULO_CURSO_PRUEBAS,
            "descripcion": "Datos creados por la suite pytest.",
            "activo": False,
        })
        unidad = _crear(
            http, admin, f"/api/cursos/{curso['id']}/unidades",
            {"titulo": "[pytest] Unidad", "description": "Unidad de prueba", "orden": 1},
        )
        leccion = _crear(
            http, admin, f"/api/unidades/{unidad['id']}/lecciones",
            {"titulo": "[pytest] Leccion", "explicacion": "Leccion de prueba", "orden": 1},
        )
        ejercicio = _crear(
            http, admin, f"/api/lecciones/{leccion['id']}/ejercicios",
            {
                "tipo": "OPCIONES",
                "enunciado": "¿Cual declaracion crea una variable entera?",
                "codigo": None,
                "explicacion": "Primero va el tipo y luego el nombre.",
                "pista": "Busca la opcion que empieza con int.",
                "orden": 1,
                "opciones": ["edad int = 20;", "int edad = 20;", "variable edad = 20;"],
                "indiceCorrecto": 1,
            },
        )
        _crear(
            http, admin, f"/api/ejercicios/{ejercicio['id']}/retroalimentaciones",
            {
                "indiceOpcion": 0,
                "categoriaError": "SINTAXIS",
                "explicacion": "En Java el tipo va antes del nombre.",
                "pista": "Mira el orden de tipo y nombre.",
            },
        )
        return {
            "curso_id": curso["id"],
            "unidad_id": unidad["id"],
            "leccion_id": leccion["id"],
            "ejercicio_id": ejercicio["id"],
        }

    unidad = http.get(f"/api/cursos/{curso['id']}/unidades").json()[0]
    leccion = http.get(f"/api/unidades/{unidad['id']}/lecciones").json()[0]
    ejercicio = http.get(f"/api/lecciones/{leccion['id']}/ejercicios").json()[0]
    return {
        "curso_id": curso["id"],
        "unidad_id": unidad["id"],
        "leccion_id": leccion["id"],
        "ejercicio_id": ejercicio["id"],
    }
