# mednetExternalPACS

Receives PACS ORU HL7, maps it per vendor, and forwards the report to mednetLab.

## 1. Run the jar

```bash
java -jar mednetExternalPACS.jar
```

Build it first with `mvn clean package`, which produces `target/mednetExternalPACS.jar`.

It starts on port `9991` with context path `/mednetExternalPACS`, so the receive endpoint is:

```
POST http://<host>:9991/mednetExternalPACS/api/v1/report/receive/{pacsName}
```

## 2. Set the properties file

Create `MednetExternalPACS.properties` at:

```
/usr/local/mednet/mednetFiles/config/propertiesFile/MednetExternalPACS.properties
```

```properties
mednet.lab.internal.url=http://localhost:9999/mednetLab
externalpacs.cors.allowed-origins=*
server.port=9991
logging.level.com.mednet.externalpacs=INFO
```

| Key | Default | Description |
| --- | --- | --- |
| `mednet.lab.internal.url` | `http://localhost:9999/mednetLab` | mednetLab base URL; `/api/v1/pacsreport/process` is appended |
| `externalpacs.cors.allowed-origins` | `*` | Allowed browser origins |
| `server.port` | `9991` | HTTP port |

The copy of this file under `src/main/resources` is only a template — Spring reads the one at the
path above.
