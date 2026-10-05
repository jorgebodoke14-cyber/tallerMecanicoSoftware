# Catálogo postal SEPOMEX

## Fuente

La fuente de datos es el Catálogo Nacional de Códigos Postales del Servicio Postal Mexicano (SEPOMEX):

<https://www.correosdemexico.gob.mx/sslservicios/consultacp/CodigoPostal_Exportar.aspx>

Durante la operación normal el portal consulta exclusivamente MySQL. No consulta la fuente remota ni envía direcciones a terceros.

## Actualización del catálogo

1. Descarga el TXT oficial desde la URL anterior y guárdalo fuera de Git, por ejemplo en `backend/data/sepomex/CPdescarga.txt`.
2. Ejecuta el importador explícitamente desde `backend/`:

```bash
./gradlew bootRun --args='--app.postal.import.enabled=true --app.postal.import-file=./data/sepomex/CPdescarga.txt'
```

3. Revisa el resumen final: estados, municipios, asentamientos, descartados, duplicados, errores y tiempo.
4. Inicia la aplicación normalmente y verifica `GET /api/postal/states` con una sesión JWT; debe incluir Hidalgo (`13`).
5. En Registro de clientes selecciona Estado, Municipio, Colonia y confirma que el CP se complete automáticamente.

El importador valida los encabezados SEPOMEX y procesa por lotes JDBC. Primero analiza completamente el archivo y sólo después reemplaza el catálogo dentro de una transacción; ante un error el catálogo anterior se conserva por rollback.

## Uso en registro de clientes

1. Selecciona **Estado**.
2. Selecciona el **Municipio** cargado para ese estado.
3. Busca y selecciona la **Colonia o asentamiento**.
4. El **Código Postal** queda en modo sólo lectura y se obtiene del asentamiento.

El backend vuelve a verificar estado, municipio, asentamiento y CP. El cliente conserva los textos oficiales de la dirección como histórico, aun cuando el catálogo se actualice más adelante.

## Recuperación ante fallo

No reinicies el portal con `app.postal.import.enabled=true` como configuración normal. Si una importación falla, corrige o vuelve a descargar el TXT y repite el comando. La transacción evita dejar el catálogo parcialmente reemplazado. Si el catálogo está vacío, el registro de clientes se bloquea con un mensaje que indica importar SEPOMEX.
