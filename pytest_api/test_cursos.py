from conftest import ID_INEXISTENTE


def curso_valido(titulo="[pytest] Curso temporal"):
    return {"titulo": titulo, "descripcion": "Curso temporal", "activo": False}


def test_listar_cursos_es_publico(http):
    r = http.get("/api/cursos")
    assert r.status_code == 200
    assert isinstance(r.json(), list)


def test_crear_curso_sin_token_devuelve_401(http):
    r = http.post("/api/cursos", json=curso_valido())
    assert r.status_code == 401


def test_estudiante_no_puede_crear_curso(http, estudiante):
    r = http.post("/api/cursos", json=curso_valido(), headers=estudiante["headers"])
    assert r.status_code == 403


def test_admin_crea_consulta_modifica_y_elimina_curso(http, admin):
    creado = http.post("/api/cursos", json=curso_valido(), headers=admin["headers"])
    assert creado.status_code == 201
    curso = creado.json()
    assert creado.headers["Location"].endswith(f"/api/cursos/{curso['id']}")
    assert curso["titulo"] == "[pytest] Curso temporal"
    assert curso["activo"] is False

    try:
        consulta = http.get(f"/api/cursos/{curso['id']}")
        assert consulta.status_code == 200
        assert consulta.json()["id"] == curso["id"]

        cambios = curso_valido("[pytest] Curso modificado")
        modificado = http.put(
            f"/api/cursos/{curso['id']}", json=cambios, headers=admin["headers"]
        )
        assert modificado.status_code == 200
        assert modificado.json()["titulo"] == "[pytest] Curso modificado"
    finally:
        eliminado = http.delete(
            f"/api/cursos/{curso['id']}", headers=admin["headers"]
        )

    assert eliminado.status_code == 204
    assert http.get(f"/api/cursos/{curso['id']}").status_code == 404


def test_crear_curso_con_datos_invalidos_devuelve_400(http, admin):
    r = http.post(
        "/api/cursos",
        json={"titulo": "", "descripcion": "x", "activo": True},
        headers=admin["headers"],
    )
    assert r.status_code == 400


def test_obtener_curso_inexistente_devuelve_404(http):
    assert http.get(f"/api/cursos/{ID_INEXISTENTE}").status_code == 404


def test_estudiante_no_puede_modificar_ni_eliminar_cursos(http, estudiante, contenido):
    ruta = f"/api/cursos/{contenido['curso_id']}"
    assert http.put(
        ruta, json=curso_valido(), headers=estudiante["headers"]
    ).status_code == 403
    assert http.delete(ruta, headers=estudiante["headers"]).status_code == 403


def test_no_se_elimina_un_curso_con_unidades(http, admin):
    curso = http.post(
        "/api/cursos", json=curso_valido("[pytest] Curso con unidad"),
        headers=admin["headers"],
    ).json()
    unidad = http.post(
        f"/api/cursos/{curso['id']}/unidades",
        json={"titulo": "[pytest] Unidad", "description": "x", "orden": 1},
        headers=admin["headers"],
    ).json()
    try:
        r = http.delete(f"/api/cursos/{curso['id']}", headers=admin["headers"])
        assert r.status_code == 409
    finally:
        http.delete(f"/api/unidades/{unidad['id']}", headers=admin["headers"])
        http.delete(f"/api/cursos/{curso['id']}", headers=admin["headers"])
