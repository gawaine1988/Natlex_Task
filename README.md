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
- [Contributing](#contributing)
- [License](#license)

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

The APIs are secured using basic authentication. Use the following credentials for accessing the endpoints:
- **Username:** user
- **Password:** password

### Example

#### Importing a file:
```sh
curl -X POST -u user:password -F 'file=@path/to/yourfile.xlsx' http://localhost:8080/import
