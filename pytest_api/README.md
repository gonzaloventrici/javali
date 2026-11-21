# Pruebas de la API con pytest

Suite que prueba la API REST de Javali **por HTTP**, contra la app corriendo
(login con JWT, cursos, contenido, respuestas y progreso). Convive con las
pruebas JUnit de `src/test`, que siguen siendo las pruebas unitarias.

## Preparacion (una sola vez)

```powershell
cd pytest_api
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

## Ejecucion

1. Iniciar Javali con SQL Server (en otra terminal, con `DB_PASSWORD` definida):

```powershell
.\mvnw.cmd spring-boot:run
```

2. Correr las pruebas desde `pytest_api`:

```powershell
pytest
```

Para correr un solo archivo: `pytest test_auth.py`.

## Configuracion opcional

Por defecto usa `http://localhost:8080` y el administrador del demo.
Se puede cambiar con variables de entorno:

| Variable | Valor por defecto |
|---|---|
| `JAVALI_BASE_URL` | `http://localhost:8080` |
| `JAVALI_ADMIN_EMAIL` | `vebes@javali.com` |
| `JAVALI_ADMIN_PASSWORD` | `vebes123` |
| `JAVALI_ESTUDIANTE_EMAIL` | `pytest.estudiante@javali.com` |
| `JAVALI_ESTUDIANTE_PASSWORD` | `Pytest12345` |

## Datos que dejan las pruebas

Con SQL Server los datos persisten, asi que las pruebas se cuidan de no
acumular basura:

- El estudiante `pytest.estudiante@javali.com` se registra la primera vez y
  se reutiliza despues.
- El curso `[pytest] Curso de pruebas` (con su unidad, leccion y ejercicio)
  se crea una sola vez y queda **inactivo**, por lo que no aparece en las
  pantallas web. No se puede borrar porque tiene intentos del estudiante.
- Todo lo demas que crean los tests se elimina al terminar.
