# Javali - Datos ampliados para Postman

Esta carpeta contiene una carga independiente y reproducible para el perfil `demo` de Javali.

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
5. Ejecutar primero `00 - Verificar API`.
6. Ejecutar `Run folder` sobre `Carga ampliada completa`.
7. Abrir `http://localhost:8080/web/cursos`.

## Importante

- Ejecutar la carga una sola vez por inicio de H2.
- Esta colección es independiente de `Javali Demo - Carga inicial`.
- Para evitar duplicados, después de reiniciar Javali ejecutar una colección u otra, no ambas.
- Al detener Javali se eliminan los datos porque H2 funciona en memoria.
