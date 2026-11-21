import pytest

from conftest import ID_INEXISTENTE


def test_el_admin_crea_unidad_leccion_y_ejercicio(http, admin, contenido):
    unidad = http.post(
        f"/api/cursos/{contenido['curso_id']}/unidades",
        json={"titulo": "[pytest] Unidad extra", "description": "x", "orden": 2},
        headers=admin["headers"],
    )
    assert unidad.status_code == 201
    unidad_id = unidad.json()["id"]
    assert unidad.json()["cursoId"] == contenido["curso_id"]

    leccion = http.post(
        f"/api/unidades/{unidad_id}/lecciones",
        json={"titulo": "[pytest] Leccion extra", "explicacion": "x", "orden": 1},
        headers=admin["headers"],
    )
    assert leccion.status_code == 201
    leccion_id = leccion.json()["id"]

    ejercicio = http.post(
        f"/api/lecciones/{leccion_id}/ejercicios",
        json={
            "tipo": "OPCIONES", "enunciado": "Pregunta temporal", "codigo": None,
            "explicacion": "x", "pista": "x", "orden": 1,
            "opciones": ["a", "b"], "indiceCorrecto": 0,
        },
        headers=admin["headers"],
    )
    assert ejercicio.status_code == 201
    ejercicio_id = ejercicio.json()["id"]

    try:
        listado = http.get(f"/api/lecciones/{leccion_id}/ejercicios")
        assert listado.status_code == 200
        assert [e["id"] for e in listado.json()] == [ejercicio_id]
    finally:
        # Se borra de adentro hacia afuera: un padre con hijos devuelve 409.
        assert http.delete(
            f"/api/ejercicios/{ejercicio_id}", headers=admin["headers"]
        ).status_code == 204
        assert http.delete(
            f"/api/lecciones/{leccion_id}", headers=admin["headers"]
        ).status_code == 204
        assert http.delete(
            f"/api/unidades/{unidad_id}", headers=admin["headers"]
        ).status_code == 204


def test_estudiante_no_puede_crear_contenido(http, estudiante, contenido):
    cabeceras = estudiante["headers"]
    assert http.post(
        f"/api/cursos/{contenido['curso_id']}/unidades",
        json={"titulo": "x", "description": "x", "orden": 1}, headers=cabeceras,
    ).status_code == 403
    assert http.post(
        f"/api/unidades/{contenido['unidad_id']}/lecciones",
        json={"titulo": "x", "explicacion": "x", "orden": 1}, headers=cabeceras,
    ).status_code == 403
    assert http.post(
        f"/api/lecciones/{contenido['leccion_id']}/ejercicios",
        json={
            "tipo": "OPCIONES", "enunciado": "x", "orden": 1,
            "opciones": ["a", "b"], "indiceCorrecto": 0,
        },
        headers=cabeceras,
    ).status_code == 403


def test_consultar_contenido_es_publico(http, contenido):
    assert http.get(f"/api/cursos/{contenido['curso_id']}/unidades").status_code == 200
    assert http.get(f"/api/unidades/{contenido['unidad_id']}/lecciones").status_code == 200
    assert http.get(f"/api/lecciones/{contenido['leccion_id']}").status_code == 200


def test_el_ejercicio_no_revela_la_respuesta_correcta(http, contenido):
    r = http.get(f"/api/ejercicios/{contenido['ejercicio_id']}")
    assert r.status_code == 200
    cuerpo = r.json()
    assert cuerpo["tipo"] == "OPCIONES"
    assert len(cuerpo["opciones"]) == 3
    assert "indiceCorrecto" not in cuerpo


@pytest.mark.parametrize("cuerpo", [
    # menos de dos opciones
    {"tipo": "OPCIONES", "enunciado": "x", "orden": 1,
     "opciones": ["solo una"], "indiceCorrecto": 0},
    # indice correcto fuera de rango
    {"tipo": "OPCIONES", "enunciado": "x", "orden": 1,
     "opciones": ["a", "b"], "indiceCorrecto": 5},
    # ORDENAR sin permutacion valida
    {"tipo": "ORDENAR", "enunciado": "x", "orden": 1,
     "opciones": ["a", "b", "c"], "ordenCorrecto": [0, 0, 1]},
])
def test_ejercicio_invalido_devuelve_400(http, admin, contenido, cuerpo):
    r = http.post(
        f"/api/lecciones/{contenido['leccion_id']}/ejercicios",
        json=cuerpo, headers=admin["headers"],
    )
    assert r.status_code == 400


def test_recursos_inexistentes_devuelven_404(http):
    assert http.get(f"/api/unidades/{ID_INEXISTENTE}").status_code == 404
    assert http.get(f"/api/lecciones/{ID_INEXISTENTE}").status_code == 404
    assert http.get(f"/api/ejercicios/{ID_INEXISTENTE}").status_code == 404
