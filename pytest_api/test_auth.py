import pytest

from conftest import (
    ADMIN_EMAIL, ADMIN_PASSWORD, ESTUDIANTE_PASSWORD, bearer,
)


def login(http, email, password):
    return http.post("/api/auth/login", json={"email": email, "password": password})


def test_login_admin_devuelve_token(http):
    r = login(http, ADMIN_EMAIL, ADMIN_PASSWORD)
    assert r.status_code == 200
    cuerpo = r.json()
    assert cuerpo["token"].count(".") == 2  # header.payload.firma
    assert cuerpo["tipo"] == "Bearer"
    assert cuerpo["expiraEnSegundos"] > 0
    assert cuerpo["email"] == ADMIN_EMAIL
    assert cuerpo["rol"] == "ADMINISTRADOR"
    assert cuerpo["usuarioId"] > 0


def test_login_ignora_mayusculas_en_el_email(http):
    r = login(http, ADMIN_EMAIL.upper(), ADMIN_PASSWORD)
    assert r.status_code == 200
    assert r.json()["email"] == ADMIN_EMAIL


def test_login_con_espacios_en_el_email_devuelve_400(http):
    r = login(http, f" {ADMIN_EMAIL} ", ADMIN_PASSWORD)
    assert r.status_code == 400


def test_login_estudiante_tiene_rol_estudiante(http, estudiante):
    r = login(http, estudiante["email"], ESTUDIANTE_PASSWORD)
    assert r.status_code == 200
    assert r.json()["rol"] == "ESTUDIANTE"


def test_login_con_password_incorrecta_devuelve_401(http):
    r = login(http, ADMIN_EMAIL, "incorrecta123")
    assert r.status_code == 401
    assert "token" not in r.json()


def test_login_con_email_inexistente_devuelve_401(http):
    r = login(http, "nadie@javali.com", "cualquiera123")
    assert r.status_code == 401


def test_login_responde_igual_si_falla_email_o_password(http):
    sin_usuario = login(http, "nadie@javali.com", "cualquiera123")
    password_mala = login(http, ADMIN_EMAIL, "incorrecta123")
    assert sin_usuario.json()["detail"] == password_mala.json()["detail"]


@pytest.mark.parametrize("cuerpo", [
    {},
    {"email": "", "password": ""},
    {"email": "no-es-un-email", "password": "abc12345"},
    {"email": ADMIN_EMAIL},
])
def test_login_con_datos_invalidos_devuelve_400(http, cuerpo):
    r = http.post("/api/auth/login", json=cuerpo)
    assert r.status_code == 400


def test_endpoint_protegido_sin_token_devuelve_401(http, estudiante):
    r = http.get(f"/api/usuarios/{estudiante['id']}")
    assert r.status_code == 401


def test_token_invalido_devuelve_401(http, estudiante):
    r = http.get(
        f"/api/usuarios/{estudiante['id']}",
        headers=bearer("esto.no.es-un-token"),
    )
    assert r.status_code == 401


def test_token_adulterado_devuelve_401(http, estudiante):
    token = estudiante["headers"]["Authorization"].split(" ", 1)[1]
    adulterado = token[:-3] + ("AAA" if not token.endswith("AAA") else "BBB")
    r = http.get(
        f"/api/usuarios/{estudiante['id']}", headers=bearer(adulterado)
    )
    assert r.status_code == 401


def test_token_valido_permite_acceder(http, estudiante):
    r = http.get(f"/api/usuarios/{estudiante['id']}", headers=estudiante["headers"])
    assert r.status_code == 200
    assert r.json()["email"] == estudiante["email"]
    assert "passwordHash" not in r.json()


def test_basic_auth_ya_no_es_valido_en_la_api(http, estudiante):
    r = http.get(
        f"/api/usuarios/{estudiante['id']}",
        auth=(ADMIN_EMAIL, ADMIN_PASSWORD),
    )
    assert r.status_code == 401


def test_registro_con_email_repetido_devuelve_409(http, estudiante):
    r = http.post("/api/usuarios", json={
        "nombre": "Repetido",
        "email": estudiante["email"],
        "password": ESTUDIANTE_PASSWORD,
    })
    assert r.status_code == 409


def test_registro_con_password_debil_devuelve_400(http):
    r = http.post("/api/usuarios", json={
        "nombre": "Debil",
        "email": "debil.pytest@javali.com",
        "password": "soloLetras",
    })
    assert r.status_code == 400
