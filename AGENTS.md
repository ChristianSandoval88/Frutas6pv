# AGENTS.md — Cremería El 13 (Openbravo POS 2.30.2 personalizado)

Guía para agentes de código (Claude Code, Codex, etc.) que trabajen en este repositorio.

## Qué es este proyecto

Punto de venta de escritorio (Java Swing, pantallas táctiles) basado en **Openbravo POS 2.30.2**
(GPL v3), adaptado para la **Cremería El 13**. Soporta impresoras de tickets ESC/POS, visores
de cliente, básculas y lectores de código de barras. La base de datos en producción es **MySQL**
(el driver `mysql.jar` se empaqueta con el build).

El código original es de 2007‑2009 (escrito para Java 1.6, hoy se compila como **Java 8**), sin genéricos en muchas partes, sin Maven/Gradle
y sin pruebas automatizadas. El idioma de trabajo del equipo es **español** (commits, comentarios
nuevos y mensajes al usuario).

## Estructura

| Carpeta | Contenido |
|---|---|
| `src-beans/` | Utilidades base: `com.openbravo.basic`, `beans`, `editor` (teclados/editores táctiles), `format`, `images`. Sin dependencias externas. |
| `src-data/` | Capa de acceso a datos propia de Openbravo: `com.openbravo.data.loader` (`Session`, `PreparedSentence`, `StaticSentence`, serializadores `SerializerRead*`/`SerializerWrite*`, `Transaction`), `data.gui`, `data.model`, `data.user`. |
| `src-pos/` | La aplicación POS (`com.openbravo.pos.*`). Aquí ocurre casi todo el trabajo. |
| `src-erp/` | Sincronización con Openbravo ERP vía web services (Axis). Rara vez se toca. |
| `locales/` | Textos i18n (`*_messages.properties` y `*_es.properties`). |
| `reports/` | Reportes JasperReports (`.jrxml`/`.bs`). |
| `build.xml` | Build con Apache Ant. |
| `start.bat` / `start.sh` | Arranque de la app (`com.openbravo.pos.forms.StartPOS`). |
| `configure.bat` / `configure.sh` | Abre la ventana de configuración (`com.openbravo.pos.config.JFrmConfig`). |
| `build/` | Salida del build (no versionar). |

Paquetes clave en `src-pos/com/openbravo/pos/`:

- `forms/` — arranque y núcleo: `StartPOS`, `JRootApp` (abre/gestiona la caja activa), `AppConfig`,
  `AppLocal`, `DataLogicSales` (tickets, inventario, impuestos, pagos), `DataLogicSystem`
  (usuarios, roles, recursos, cortes).
- `sales/` — pantalla de venta: `JPanelTicket` (cobro y guardado del ticket), `JTicketsBag*`
  (consulta/edición/reimpresión de tickets), `JPanelButtons`.
- `panels/` — `JPanelCloseMoney` (corte/cierre de caja), pagos, movimientos de efectivo.
- `payment/`, `printer/`, `scale/`, `inventory/`, `catalog/`, `customers/`, `reports/`, `admin/`.
- `scripts/` — SQL de creación y actualización por motor (`MySQL-create.sql`, `MySQL-upgrade-*.sql`, etc.).
- `templates/` — recursos por defecto que se cargan en la tabla `RESOURCES`: menú (`Menu.Root.txt`),
  roles (`Role.*.xml`), plantillas de impresión (`Printer.*.xml`), botones (`Ticket.Buttons.xml`).

Los archivos `.form` son diseños de **NetBeans GUI Builder**; el bloque
`// <editor-fold defaultstate="collapsed" desc="Generated Code">` dentro de `initComponents()` está
generado a partir de ellos. Si modificas la UI a mano, mantén coherente el `.form` o evita tocar
el código generado.

## Personalizaciones de la Cremería (no son de Openbravo original)

Antes de "corregir" algo que parezca raro, revisa si es una personalización intencional:

- **Clases duplicadas "2"**: `JPanelTicket2`, `JPanelButtons2`, `JTicketsBag2`,
  `shared/JTicketsBagShared2`, `JPanelCloseMoney2`, plantillas `Printer.Ticket2.xml` y
  `Printer.CustomerPaid2.xml`. Son variantes paralelas de las originales; al cambiar una, verifica
  si la otra necesita el mismo cambio.
- **Cortes de caja**: `forms/Cortes.java` y `DataLogicSystem.findCortes()` / `findCortes2()`.
- **Caja activa al cobrar**: `DataLogicSales.getActiveCashIndex()` lee el corte abierto más reciente
  de `CLOSEDCASH` (`DATEEND IS NULL`), y `JPanelTicket.getCurrentActiveCashIndex()` lo usa en lugar
  del valor en memoria de la aplicación.
- **Edición de tickets de otros cortes**: en `JTicketsBagTicket.printTicket()` el botón Editar
  está siempre habilitado (se comentó la validación `isCashActive`).
- **Inventario/impuestos al guardar o borrar tickets**: en `DataLogicSales` está comentada la
  inserción en `TAXLINES` y la reversión de inventario/`TAXLINES` al borrar un ticket.
- **Envío del corte por correo** (Gmail SMTP, puerto 587) en `JPanelCloseMoney`/`JPanelCloseMoney2`,
  usando las propiedades `correoEmisor`, `claveCorreoEmisor` y `correoReceptor1` del archivo de
  configuración. Usa `javax.mail` (`mailapi.jar`, `smtp.jar`, etc.).
- **Ubicación de la configuración**: `AppConfig.getDefaultConfig()` busca `openbravopos.properties`
  en el **directorio de trabajo** (no en `user.home` como el original). Ojo: el envío de correo en
  `JPanelCloseMoney` sí lo lee de `user.home`.
- **SSL de PayPoint**: se quitó el proveedor `com.sun.net.ssl.internal.ssl.Provider` de
  `PaymentGatewayPayPoint` porque ya no existe en JDK modernos.

## Compilar y ejecutar

Requisitos: **JDK 8 (1.8)** y **Apache Ant** en el `PATH`. La compilación debe realizarse con
**Java 8**: `build.xml` define `javac.minVMVersion = 1.8` y todos los `<javac>` usan
`source` y `target` 1.8. Usa un JDK 8 (`JAVA_HOME` apuntando a él) para compilar y ejecutar; no
regreses ese valor a `1.6` (los JDK actuales ya no lo aceptan).

**Dependencias**: las librerías se esperan en `lib/` (ver `.classpath` y `start.bat`), carpeta que
**no está versionada**. Hay una copia de los JAR en `build/jar/lib/` de un build anterior; si `lib/`
no existe, créala a partir de ahí antes de compilar.

```bash
ant jar        # compila beans -> data -> pos -> erp y genera build/jar/openbravopos.jar
ant dist.bin   # zip binario de distribución
```

El target `jar` copia `lib/*.jar` a `build/jar/lib/`, incluye `locales/` dentro del JAR y escribe
`Class-Path` en el manifiesto, así que puede ejecutarse directamente:

```bash
cd build/jar
java -jar openbravopos.jar [ruta/al/openbravopos.properties]
```

Para ejecutar con los scripts (`start.bat`), `openbravopos.jar`, `lib/`, `locales/` y `reports/`
deben estar junto al script. La librería nativa RXTX (puertos serie) se carga de
`lib/Windows/i368-mingw32`.

No hay suite de pruebas. Verifica los cambios compilando con `ant jar` y probando el flujo
afectado en la aplicación contra una base de datos de pruebas, **nunca contra la de producción**.

## Base de datos

- El acceso a datos se hace con las clases de `com.openbravo.data.loader`, no con JDBC directo:
  `new PreparedSentence(session, "SQL ...", SerializerWrite..., SerializerRead...)` y
  `new StaticSentence(...)`. Las operaciones de varios pasos van dentro de `Transaction`.
- Tablas principales: `RECEIPTS`, `TICKETS`, `TICKETLINES`, `PAYMENTS`, `TAXLINES`, `CLOSEDCASH`
  (cortes; `MONEY` es el id del corte, `HOSTSEQUENCE` la secuencia, `DATEEND` nulo = abierto),
  `STOCKDIARY`, `STOCKCURRENT`, `PRODUCTS`, `PEOPLE`, `ROLES`, `RESOURCES`.
- Si un cambio requiere modificar el esquema, actualiza los scripts de `src-pos/com/openbravo/pos/scripts/`
  (al menos el de MySQL) y documenta el `ALTER TABLE` necesario para la base existente. Comprueba
  que las columnas usadas en el SQL de Java existan realmente en el esquema (p. ej. hubo un intento
  de usar `STOCKDIARY.ACTIVECASH` que no existe en los scripts y se revirtió).
- El menú, roles, permisos y plantillas de impresión se leen de la tabla `RESOURCES`; los archivos
  de `templates/` solo son los valores iniciales. Un cambio en `templates/` **no** afecta una
  instalación existente hasta que se actualiza el recurso desde Administración → Recursos.

## Convenciones

- Java 8: el código nuevo puede usar características de Java 8 (lambdas, diamond `<>`,
  try-with-resources, `java.time`), pero **no** APIs ni sintaxis posteriores (`var`, `List.of`,
  records, etc.). Mantén el estilo del archivo que estés editando.
- Archivos con finales de línea **CRLF**; respétalos (`.gitattributes` usa `text=auto`).
- Textos visibles al usuario: usa `AppLocal.getIntString("clave")` y agrega la clave en
  `locales/pos_messages.properties` **y** `locales/pos_messages_es.properties`.
- Registro de errores con `java.util.logging.Logger`; evita `System.out.println` en código nuevo.
- Conserva los encabezados de licencia GPL en los archivos existentes.
- Cuando desactives lógica original, el equipo suele comentarla con `/* ... */` en lugar de borrarla;
  deja un comentario breve en español explicando por qué.
- Mensajes de commit en español, en tono descriptivo (p. ej. "Se permite la edición de un ticket…").

## Precauciones

- **No commitear secretos**: `openbravopos.properties` contiene credenciales de base de datos y
  de correo (`claveCorreoEmisor`). No lo agregues al repositorio ni imprimas su contenido en logs.
- No versionar `build/` ni JARs generados.
- Cambios en `DataLogicSales` (guardar/borrar tickets, inventario, impuestos) y en
  `JPanelCloseMoney*` afectan directamente el dinero y los cortes de caja: describe el impacto
  y pide confirmación antes de cambiar comportamiento ahí.
