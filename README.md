<img src="https://www.ga4gh.org/wp-content/themes/ga4gh-theme/gfx/GA-logo-horizontal-tag-RGB.svg" alt="GA4GH Logo" style="width: 400px;"/>

# ga4gh-starter-kit-refget
GA4GH Starter Kit server reference implementation of the refget specification

## Running the Refget service

### Native

Native installations require:
* Java 11+
* Gradle 7.2
* SQLite (for creating the dev database)

First, clone the repository from Github:
```
git clone https://github.com/ga4gh/ga4gh-starter-kit-refget.git
cd ga4gh-starter-kit-refget
```

The service can be run in development mode directly via gradle:

Run with all defaults
```
./gradlew bootRun
```

Run with config file
```
./gradlew bootRun --args="--config path/to/config.yml"
```

Alternatively, the service can be built as a jar and run:

Build jar:
```
./gradlew bootJar
```

Run with all defaults
```
java -jar build/libs/ga4gh-starter-kit-refget-${VERSION}.jar
```

Run with config file
```
java -jar build/libs/ga4gh-starter-kit-refget-${VERSION}.jar --config path/to/config.yml
```

### Confirm server is running

Whether running via docker or natively on a local machine, confirm the Refget API is up running by visiting its `service-info` endpoint, you should receive a valid `ServiceInfo` response.

```
GET http://localhost:4500/ga4gh/refget/v1/sequence/service-info

Response:
{
    "service": {
        "circular_supported": true,
        "algorithms": [
            "md5"
        ],
        "subsequence_limit": 4000000,
        "supported_api_versions": [
            "1.0"
        ]
    }
}
```

## Changelog

### v0.3.1

* Fixed a bug where admin requests to create a controlled access DRS object (i.e. with visas) did not complete successfully

### v0.3.0

* DRS object batch requests
* Passport support - Passport mediated auth to DRS objects (using Starter Kit implementation of Passports)
* Auth info - Discover Passport broker(s) and visa(s) for requested controlled access DRS Objects (single object and bulk request)

### v0.2.2

* patched log4j dependencies to v2.16.0 to avoid [Log4j Vulnerability](https://www.cisa.gov/uscert/apache-log4j-vulnerability-guidance)