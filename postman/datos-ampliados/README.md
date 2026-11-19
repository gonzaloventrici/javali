# Javali - Datos ampliados para Postman

Esta carpeta contiene una carga independiente para Javali (SQL Server o perfil `demo`).

## Contenido creado

- 3 cursos
- 6 unidades
- 12 lecciones
- 24 ejercicios de opción múltiple
- Retroalimentación para cada opción incorrecta

## Credenciales administrativas

```text
Email: vebes@javali.com
Contraseña: vebes123
```

## Ejecución desde VS Code

1. Iniciar Javali:

```powershell
.\mvnw.cmd spring-boot:run
```

2. Abrir la extensión Postman de VS Code.
3. Importar esta carpeta con **Import → Folder**.
4. Seleccionar el environment `Javali Datos Ampliados`.
5. Ejecutar `Run folder` sobre `Carga ampliada completa`. El paso `01 - Login (JWT)` guarda el token y los demas pedidos lo usan.
6. Abrir `http://localhost:8080/web/cursos`.

## Importante

- Ejecutar la carga una sola vez por base de datos: con SQL Server los datos persisten y una segunda ejecucion duplica cursos, unidades y ejercicios.
- Para empezar de cero, recrear la base `javali` y volver a iniciar la app.
- Con el perfil `demo` (H2 en memoria) los datos se pierden al detener Javali.
- La API usa JWT: el token vence (60 minutos por defecto). Si vence, volver a ejecutar `01 - Login (JWT)`.
