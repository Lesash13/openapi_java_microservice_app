[![Dependencies][dependency-shield]][dependency-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

# Java Rest Microservice

Simple example of rest microservice written on SpringBoot.
Working with DB via Hibernate with the lep of flyway migration.
Includes mapstruct and dmn decision tables example.

### Built With

* [![Java][Java.io]][Java-url]
* [![SpringBoot][SpringBoot.io]][SpringBoot-url]
* [![Hibernate][Hibernate.io]][Hibernate-url]
* [![MapStruct][MapStruct.io]][MapStruct-url]
* [![Flyway][Flyway.io]][Flyway-url]
* [![OpenApi][OpenApi.io]][OpenApi-url]
* [![Wiremock][Wiremock.io]][Wiremock-url]
* [![RestAssured][RestAssured.io]][RestAssured-url]
* [![Junit5][Junit5.io]][Junit5-url]

## Pre-installations

#### Clone the repo:

```sh
git clone https://github.com/Lesash13/openapi_java_microservice_app.git
```

#### Build project:

```sh
gradle clean
```

```sh
gradle build
```

#### Setup endpoints for client and server, set connection to db:

```
resources/application.yaml
```

## Usage

#### Build interface and objects for server PeopleService:

```sh
gradle buildCxfServer
```

#### Build interface and objects for server PeopleService:

```sh
gradle buildCxfClient
```

#### Run service:

```sh
gradle bootRun
```

## API example requests:

API :

```
http://127.0.0.1:4443/integratedPeopleManagement/v1/
```

Get / delete request:

```
http://127.0.0.1:4443/integratedPeopleManagement/v1/people/{id}
```

Find person by status request:

```
http://127.0.0.1:4443/integratedPeopleManagement/v1/people/findByStatus
```

House get request:

```
http://127.0.0.1:4443/integratedPeopleManagement/v1/house/0
```

<!-- MARKDOWN LINKS & IMAGES -->

[dependency-shield]: https://img.shields.io/badge/Dependency_Graph-darkgreen?style=for-the-badge

[dependency-url]: https://github.com/Lesash13/social_network_python_app/network/dependencies

[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=darkblue

[linkedin-url]: https://www.linkedin.com/in/victoriya-mitrofanova-96839278/

[Java.io]: https://img.shields.io/badge/-☕%20Java-blue?style=for-the-badge

[Java-url]: https://www.java.com/ru/

[SpringBoot.io]: https://img.shields.io/badge/-Springboot-green?style=for-the-badge&logo=springboot

[SpringBoot-url]: https://spring.io/projects/spring-boot

[Hibernate.io]: https://img.shields.io/badge/-Hibernate-gray?style=for-the-badge&logo=hibernate

[Hibernate-url]: https://hibernate.org/

[MapStruct.io]: https://img.shields.io/badge/-↩%20MapStruct-orange?style=for-the-badge

[MapStruct-url]: https://mapstruct.org/

[Flyway.io]: https://img.shields.io/badge/-Flyway-white?style=for-the-badge&logo=flyway&logoColor=red

[Flyway-url]: https://flywaydb.org/

[OpenApi.io]: https://img.shields.io/badge/-OpenApi-blueviolet?style=for-the-badge&logo=openapiinitiative

[OpenApi-url]: https://www.openapis.org/

[Wiremock.io]: https://img.shields.io/badge/-🍊%20Wiremock-lightblue?style=for-the-badge

[Wiremock-url]: https://wiremock.org/

[RestAssured.io]: https://img.shields.io/badge/-🪐️%20Rest%20Assured-brightgreen?style=for-the-badge&logo=restAssured

[RestAssured-url]: https://rest-assured.io/

[Junit5.io]: https://img.shields.io/badge/-JUnit5-yellow?style=for-the-badge&logo=JUnit5

[Junit5-url]: https://junit.org/junit5/