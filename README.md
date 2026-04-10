# Getting Started

## Requirements
To run this application, you can run the application using the following version of Java:
- Java 17 : drop commits "java 21" and "java 25"
- Java 21 : drop commits "java 25"
- Java 25 (default)

## Build the application
To build the application, use the following command:

```bash
./gradlew build
```

## Configure the smtp server
Create an account on [Mailtrap](https://mailtrap.io/) and configure the SMTP server by adding the following environment variables:

```bash
USERNAME=your_username
PASSWORD=your_password
````

## Run the applications
To run the applications, use the following command:

### Billetterie
```bash
./gradlew bootRun
```
The application will be available at `http://localhost:8080`.

### Back Office
```bash
cd back-office-billetterie
.././gradlew bootRun
```
The application will be available at `http://localhost:8081.

### Bank
```bash
cd bank
.././gradlew bootRun
```
The application will be available at `http://localhost:8082.

## Unit tests the application
To run the units tests, use the following command:
```bash
./gradlew :test
```

## Integration tests the application
To run the integration tests, use the following command:
```bash
./gradlew :cucumberCli
```

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.5/gradle-plugin)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.5/reference/web/servlet.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

