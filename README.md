# Backend Developer Test Task

This project implements a REST API for managing Sections and GeologicalClasses. It includes CRUD operations, searching by code, and importing/exporting XLS files. The project is built using Spring Boot, Hibernate, Spring Data, and Apache POI.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Testing](#testing)
- [Postman Collection](#postman-collection)
- [Contributing](#contributing)
- [Contact](#contact)

## Introduction

This project is a backend application that provides APIs for managing Sections and GeologicalClasses, including importing and exporting data from/to XLS files. The application supports asynchronous processing of file operations and basic authentication.

## Features

- CRUD operations for Sections and GeologicalClasses.
- API to search Sections by GeologicalClass code.
- Import Sections and GeologicalClasses from XLS files.
- Export Sections and GeologicalClasses to XLS files.
- Asynchronous processing of import and export operations.
- Basic authentication support.

## Technology Stack

- Spring Boot 3.3.1
- Spring Data JPA
- Hibernate
- Apache POI
- H2 Database (for development and testing)
- Maven

## Installation

### Prerequisites

- Java 21+
- Maven 3.9+

### Steps

1. Clone the repository:
    ```sh
    git clone https://github.com/gawaine1988/Natlex_Task.git
    cd Natlex_Task
    ```

2. Build the project:
    ```sh
    mvn clean install
    ```

3. Run the project:
    ```sh
    mvn spring-boot:run
    ```

## Usage

### Basic Authentication

The APIs are secured using basic authentication and CSRF protection. Use the following credentials for accessing the endpoints:
- **Username:** user
- **Password:** password

For POST requests, you need to include the CSRF token in the header.

### Example

#### Create section with CSRF token:

1. Obtain the CSRF token:
    ```sh
    curl -u user:password -c cookies http://localhost:8080/csrf -o csrf_response.json
    ```
   Save cookies (including JSESSIONID) to a file named `cookies`. 
   Save the JSON response to a file named `csrf_response.json`.

2. Use the token for a POST request:
    ```sh
   csrf_token=$(sed -n 's/.*"token":"\([^"]*\)".*/\1/p' csrf_response.json)
   curl -X POST -u user:password -b cookies -H "X-CSRF-TOKEN: $csrf_token" -H "Content-Type: application/json" -d '{"name": "111", "geologicalClasses": [{"name": "Geo Class 11", "code": "GC11"}]}' http://localhost:8080/sections
    ```
   Use the csrf_token and cookies to request the create sections api.

## API Endpoints

### Sections and GeologicalClasses

- **Create Section**
  ```sh
  POST /sections
  ```
- **Get Section by ID**
  ```sh
  GET /sections/{id}
  ```
- **Update Section**
  ```sh
  PUT /sections/{id}
  ```
- **Delete Section**
  ```sh
  DELETE /sections/{id}
  ```
- **Get Sections by GeologicalClass Code**
  ```sh
  GET /sections/by-code?code={code}
  ```

### Import and Export

- **Import XLS File**
  ```sh
  POST /import
  ```
  Returns the ID of the asynchronous job.

- **Get Import Job Status**
  ```sh
  GET /import/{id}
  ```

- **Export XLS File**
  ```sh
  GET /export
  ```
  Returns the ID of the asynchronous job.

- **Get Export Job Status**
  ```sh
  GET /export/{id}
  ```

- **Download Exported File**
  ```sh
  GET /export/{id}/file
  ```

## Configuration
The application can be configured using the `application.properties` file located in `src/main/resources`.

### Basic Authentication and CSRF Configuration
In your security configuration, you can define the username, password, and CSRF configuration:
```java
@Configuration
@Profile("prod")
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .anyRequest().authenticated()
                )
                .httpBasic()
                .and()
                .csrf() ;

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}
```

### Ignoring Exported Files
To run the tests, use the following command:
```sh
mvn test
```

### Ignoring Security in Tests
To ignore basic authentication and CSRF in your tests, you can create a test security configuration class:
```
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
                .anyRequest().permitAll();

        return http.build();
    }
}
```

## Postman Collection
A Postman collection is provided in the repository to facilitate testing of the API endpoints. You can find the collection file in the `postman` directory.

### Importing the Postman Collection
1. Open Postman.
2. Click on `Import`.
3. Select the `Natlex_Task.postman_collection.json` and `Natlex.postman_environment.json` files from the `postman` directory.
4. The collection will be imported, and you can use it to test the API endpoints.

## Contributing
Contributions are welcome! Please submit a pull request or open an issue to discuss your ideas.

## Contact
For any questions or feedback, please contact Peng Yan at gawaine1988@gmail.com.

