from conftest import ID_INEXISTENTE


def responder(http, estudiante, contenido, indice, usuario_id=None):
    return http.post(
        f"/api/ejercicios/{contenido['ejercicio_id']}/respuestas",
        json={
            "usuarioId": usuario_id or estudiante["id"],
            "indiceSeleccionado": indice,
        },
        headers=estudiante["headers"],
    )


def test_responder_sin_token_devuelve_401(http, estudiante, contenido):
    r = http.post(
        f"/api/ejercicios/{contenido['ejercicio_id']}/respuestas",
        json={"usuarioId": estudiante["id"], "indiceSeleccionado": 1},
    )
    assert r.status_code == 401


def test_respuesta_correcta(http, estudiante, contenido):
    r = responder(http, estudiante, contenido, 1)
    assert r.status_code == 200
    resultado = r.json()
    assert resultado["correcta"] is True
    assert resultado["categoriaError"] is None


def test_respuesta_incorrecta_con_retroalimentacion(http, estudiante, contenido):
    r = responder(http, estudiante, contenido, 0)
    assert r.status_code == 200
    resultado = r.json()
    assert resultado["correcta"] is False
    assert resultado["categoriaError"] == "SINTAXIS"
    assert resultado["pista"]


def test_respuesta_incorrecta_sin_retroalimentacion_usa_la_pista_general(
        http, estudiante, contenido):
    r = responder(http, estudiante, contenido, 2)
    assert r.status_code == 200
    resultado = r.json()
    assert resultado["correcta"] is False
    assert resultado["pista"]


def test_indice_fuera_de_rango_devuelve_400(http, estudiante, contenido):
    assert responder(http, estudiante, contenido, 99).status_code == 400


def test_ejercicio_inexistente_devuelve_404(http, estudiante):
    r = http.post(
        f"/api/ejercicios/{ID_INEXISTENTE}/respuestas",
        json={"usuarioId": estudiante["id"], "indiceSeleccionado": 0},
        headers=estudiante["headers"],
    )
    assert r.status_code == 404


def test_estudiante_no_puede_responder_por_otro_usuario(
        http, estudiante, admin, contenido):
    r = responder(http, estudiante, contenido, 1, usuario_id=admin["id"])
    assert r.status_code == 403


def test_las_respuestas_quedan_en_el_progreso(http, estudiante, contenido):
    responder(http, estudiante, contenido, 1)
    base = f"/api/usuarios/{estudiante['id']}"
    cabeceras = estudiante["headers"]

    progresos = http.get(f"{base}/progresos", headers=cabeceras)
    assert progresos.status_code == 200
    de_la_leccion = [
        p for p in progresos.json() if p["leccionId"] == contenido["leccion_id"]
    ]
    assert len(de_la_leccion) == 1
    assert de_la_leccion[0]["cantidadIntentos"] >= 1
    assert de_la_leccion[0]["respuestasCorrectas"] >= 1

    resumen = http.get(f"{base}/progreso/resumen", headers=cabeceras)
    assert resumen.status_code == 200
    assert resumen.json()["usuarioId"] == estudiante["id"]
    assert resumen.json()["totalIntentos"] >= 1

    intentos = http.get(f"{base}/intentos", headers=cabeceras)
    assert intentos.status_code == 200
    assert any(i["ejercicioId"] == contenido["ejercicio_id"] for i in intentos.json())

    estado = http.get(
        f"{base}/lecciones/{contenido['leccion_id']}/estado", headers=cabeceras
    )
    assert estado.status_code == 200
    assert estado.json()["leccionId"] == contenido["leccion_id"]


def test_progreso_sin_token_devuelve_401(http, estudiante):
    r = http.get(f"/api/usuarios/{estudiante['id']}/progresos")
    assert r.status_code == 401


def test_estudiante_no_ve_el_progreso_de_otro(http, estudiante, admin):
    base = f"/api/usuarios/{admin['id']}"
    for ruta in ("/progresos", "/progreso/resumen", "/intentos"):
        r = http.get(base + ruta, headers=estudiante["headers"])
        assert r.status_code == 403, ruta


def test_el_admin_puede_ver_el_progreso_de_un_estudiante(http, estudiante, admin):
    r = http.get(
        f"/api/usuarios/{estudiante['id']}/progresos", headers=admin["headers"]
    )
    assert r.status_code == 200
