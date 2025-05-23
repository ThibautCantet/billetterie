# Getting Started

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

### Back Office
```bash
cd back-office-billetterie
.././gradlew bootRun
```

### Bank
```bash
cd bank
.././gradlew bootRun
```

## Test the application
To run the tests, use the following command:

```bash
./gradlew :cucumberCli
```


## Run the database
To run the database, use the following command:

```bash
docker-compose up -d
```

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.5/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.5/gradle-plugin/packaging-oci-image.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.5/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.5/reference/web/servlet.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

