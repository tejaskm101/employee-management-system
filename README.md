# Employee Management System

A backend Employee Management System built with **Java, Spring Boot, Spring Security, JWT, PostgreSQL, and Maven**.

The project started as a basic CRUD REST API and was extended with **user authentication, JWT-based security, password hashing, admin provisioning, and role-based access control (RBAC)**.

The project is designed to demonstrate the structure and security concepts commonly used in modern Java backend applications.

---

## 1. Tech Stack

| Technology      | Purpose                        |
| --------------- | ------------------------------ |
| Java 26         | Programming language           |
| Spring Boot     | Backend framework              |
| Spring Web      | REST APIs                      |
| Spring Data JPA | Database persistence           |
| Hibernate       | ORM                            |
| PostgreSQL      | Relational database            |
| Spring Security | Authentication & authorization |
| JWT             | Stateless authentication       |
| BCrypt          | Password hashing               |
| Maven           | Dependency management & build  |
| Postman         | API testing                    |

---

# 2. Project Architecture

The application follows a layered architecture:

Client / Postman
       |
       v
Controller
       |
       v
Service
       |
       v
Repository
       |
       v
PostgreSQL

Security adds another layer before protected controllers:

HTTP Request
     |
     v
JwtAuthenticationFilter
     |
     v
JWT Validation
     |
     v
Spring SecurityContext
     |
     v
Role-Based Authorization
     |
     v
Controller

---

# 3. Package Structure

src/main/java/com/example/CRUD
│
├── config
│   ├── SecurityConfig.java
│   └── AdminSeeder.java
│
├── controller
│   ├── EmployeeController.java
│   └── AuthController.java
│
├── dto
│   ├── EmployeeRequestDTO.java
│   ├── EmployeeResponseDTO.java
│   ├── LoginRequestDTO.java
│   ├── LoginResponseDTO.java
│   ├── RegisterRequestDTO.java
│   └── RegisterResponseDTO.java
│
├── entity
│   ├── Employee.java
│   └── User.java
│
├── exception
│   ├── GlobalExceptionHandler.java
│   └── UsernameAlreadyExistsException.java
│
├── repository
│   ├── EmployeeRepository.java
│   └── UserRepository.java
│
├── security
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtService.java
│
└── service
    ├── EmployeeService.java
    └── AuthenticationService.java

---

# 4. Entity Layer

## Employee.java

Represents an employee stored in the PostgreSQL database.

Responsibilities:

* Defines the employee data model.
* Maps the Java object to the database using JPA.
* Represents an employee record throughout the application.

The entity is persisted using Spring Data JPA.

---

## User.java

Represents an authenticated application user.

Important fields:

id
username
password
role

Example database records:

username    role
-------------------------
tejas       ROLE_USER
admin       ROLE_ADMIN

The password is never stored as plain text. It is stored as a BCrypt hash.

---

# 5. Repository Layer

Repositories provide the database access layer.

## EmployeeRepository.java

Extends Spring Data JPA functionality and provides database operations for `Employee`.

The service layer uses this repository rather than directly interacting with PostgreSQL.

---

## UserRepository.java

Provides database operations for `User`.

One particularly important method is:

```java
findByUsername(...)
```

This is used during:

* Login
* JWT authentication
* Admin seeding
* User registration checks

---

# 6. DTO Layer

DTOs (Data Transfer Objects) control the data exchanged between the client and the API.

They prevent the application from unnecessarily exposing internal entity objects.

## RegisterRequestDTO

Contains registration information supplied by the client:

username
password

---

## RegisterResponseDTO

Represents the response returned after successful registration.

---

## LoginRequestDTO

Contains:

username
password

---

## LoginResponseDTO

Contains the JWT returned after successful authentication.

---

## EmployeeRequestDTO

Contains the data required to create/update an employee.

---

## EmployeeResponseDTO

Represents the employee data returned to the client.

---

# 7. Controller Layer

Controllers expose REST endpoints.

## EmployeeController.java

Handles employee-related HTTP requests.

Typical operations:

GET       /employees
GET       /employees/{id}
POST      /employees
PUT       /employees/{id}
DELETE    /employees/{id}

The controller should remain relatively thin.

It receives the HTTP request and delegates business logic to `EmployeeService`.

---

## AuthController.java

Handles authentication-related endpoints.

Main endpoints:

POST /auth/register
POST /auth/login

### Registration

Client
  |
  | username + password
  v
AuthController
  |
  v
AuthenticationService
  |
  v
BCrypt hashing
  |
  v
UserRepository
  |
  v
PostgreSQL

### Login

Client
  |
  | username + password
  v
AuthController
  |
  v
AuthenticationService
  |
  v
AuthenticationManager
  |
  v
JWT generation
  |
  v
JWT returned to client

---

# 8. Service Layer

## EmployeeService.java

Contains the business logic for employee operations.

The controller does not directly manipulate the repository.

Instead:

EmployeeController
        |
        v
EmployeeService
        |
        v
EmployeeRepository

This separation makes the application easier to maintain and test.

---

## AuthenticationService.java

Responsible for authentication-related business logic.

It contains two important operations:

### Registration

1. Check whether username already exists
2. Create User
3. Hash password using BCrypt
4. Assign ROLE_USER
5. Save user

Every normal registration therefore receives:

ROLE_USER

The client cannot simply choose `ROLE_ADMIN` during registration.

### Login

1. Receive username/password
2. AuthenticationManager authenticates credentials
3. Retrieve authenticated UserDetails
4. Generate JWT
5. Return JWT

---

# 9. Exception Handling

## UsernameAlreadyExistsException.java

Custom exception thrown when someone attempts to register an already-existing username.

Instead of returning a generic Java exception, the application can provide a meaningful API response.

---

## GlobalExceptionHandler.java

Centralizes exception handling using Spring's exception-handling mechanism.

This keeps controllers cleaner and provides consistent error responses.

---

# 10. Spring Security

Spring Security is responsible for protecting the application.

The application uses:

Spring Security
+
JWT
+
Role-Based Access Control

---

# 11. SecurityConfig.java

This is one of the most important classes in the project.

It defines the application's security rules.

Important configuration:

### CSRF

.csrf(csrf -> csrf.disable())

CSRF protection is disabled because this application is designed as a stateless REST API using JWT authentication rather than browser-based session authentication.

### Stateless sessions

SessionCreationPolicy.STATELESS

The server does not maintain an authentication session.

Instead, the client sends the JWT with every protected request.

### Public authentication endpoints

/auth/**

is permitted without authentication.

This allows users to register and log in.

### RBAC

The application implements:

GET /employees/**
    USER or ADMIN

POST /employees/**
    ADMIN only

PUT /employees/**
    ADMIN only

DELETE /employees/**

---

# 12. JwtService.java

Responsible for JWT operations.

Conceptually it performs:

UserDetails
     |
     v
Generate JWT

and:

JWT
 |
 v
Extract username
 |
 v
Validate token

The JWT allows the server to identify the authenticated user without maintaining a server-side session.

---

# 13. JwtAuthenticationFilter.java

This class is responsible for processing JWTs on incoming requests.

It extends:

OncePerRequestFilter

so the filtering logic executes once for each request.

The flow is:

HTTP Request
     |
     v
Read Authorization header
     |
     v
"Bearer <JWT>"
     |
     v
Extract JWT
     |
     v
Extract username
     |
     v
Load UserDetails
     |
     v
Validate JWT
     |
     v
Create Authentication object
     |
     v
SecurityContextHolder

The authentication is then available to Spring Security for authorization decisions.

The filter is registered before:

UsernamePasswordAuthenticationFilter

---

# 14. CustomUserDetailsService.java

Implements Spring Security's `UserDetailsService`.

Its job is to connect our application's `User` entity with Spring Security.

Conceptually:

Spring Security
      |
      | loadUserByUsername()
      v
CustomUserDetailsService
      |
      v
UserRepository
      |
      v
PostgreSQL

It loads the user and converts the application's user information into a Spring Security `UserDetails` object.

The user's role becomes an authority used by Spring Security.

---

# 15. AdminSeeder.java

Creates the initial administrator account when the application starts.

It implements:

CommandLineRunner

which allows code to execute during application startup.

The process is:

Application starts
       |
       v
AdminSeeder.run()
       |
       v
Does admin exist?
   /          \
 YES          NO
 |             |
return       create admin
               |
               v
        BCrypt password
               |
               v
          ROLE_ADMIN
               |
               v
          PostgreSQL

The credentials are configurable through:

app.admin.username=${ADMIN_USERNAME:admin}
app.admin.password=${ADMIN_PASSWORD:admin123}

The environment variables can override the fallback values.

For production use, real credentials should be supplied through environment variables or a proper secrets-management system rather than committed to source control.

---

# 16. Authentication Flow

## Registration

POST /auth/register
        |
        v
AuthController
        |
        v
AuthenticationService
        |
        v
Check username
        |
        v
BCrypt password hashing
        |
        v
Assign ROLE_USER
        |
        v
UserRepository
        |
        v
PostgreSQL

---

## Login

POST /auth/login
        |
        v
AuthenticationManager
        |
        v
Validate username/password
        |
        v
JwtService
        |
        v
Generate JWT
        |
        v
Client

The client then stores the JWT and sends it with protected requests:

Authorization: Bearer <JWT>

---

# 17. Authorization Flow

Consider:

GET /employees
Authorization: Bearer <JWT>

The request goes through:

JWT Filter
    |
    v
Validate token
    |
    v
Identify user
    |
    v
ROLE_USER
    |
    v
SecurityConfig
    |
    v
GET is allowed for USER
    |
    v
EmployeeController

For:

POST /employees

the authorization decision is different:

ROLE_USER
    |
    v
POST /employees
    |
    v
403 Forbidden

An administrator:

ROLE_ADMIN
    |
    v
POST /employees
    |
    v
Allowed

---

# 18. Role Model

The project currently uses two roles.

| Role         | Permissions        |
| ------------ | ------------------ |
| `ROLE_USER`  | Read employees     |
| `ROLE_ADMIN` | Full employee CRUD |

Normal users are assigned:

ROLE_USER

during registration.

The administrator is created by `AdminSeeder`.

---

# 19. API Summary

## Authentication

### Register

POST /auth/register

Request:

{
  "username": "tejas",
  "password": "password"
}

### Login

POST /auth/login

Request:

{
  "username": "tejas",
  "password": "password"
}

Response contains a JWT.

---

## Employees

### Get all employees

GET /employees

Required role:

USER or ADMIN

### Get employee

GET /employees/{id}

Required role:

USER or ADMIN

### Create employee

POST /employees

Required role:

ADMIN

### Update employee

PUT /employees/{id}

Required role:

ADMIN

### Delete employee

DELETE /employees/{id}

Required role:

ADMIN

---

# 20. Running the Project

## Prerequisites

Install:

* Java 26
* Maven
* PostgreSQL

Create the required PostgreSQL database:

employee_management

Update the database configuration in:

src/main/resources/application.properties

Then run:

mvn spring-boot:run

The application starts on:

http://localhost:8080

---

# 21. Testing

The APIs were tested using Postman.

Important authorization tests performed:

USER
GET /employees
→ 200 OK

USER
POST /employees
→ 403 Forbidden

ADMIN
GET /employees
→ allowed

ADMIN
POST /employees
→ allowed

These tests verify that authentication and role-based authorization are actually enforced.

---

# 22. Important Interview Concepts

The following concepts are worth understanding before discussing this project in an interview.

### Why BCrypt?

Passwords should not be stored as plain text.

BCrypt is a password-hashing algorithm designed to be computationally expensive, making brute-force attacks more difficult.

---

### Why JWT?

JWT allows stateless authentication.

Instead of storing a server-side session, the client sends the token with each request.

---

### Authentication vs Authorization

**Authentication** answers:

> Who are you?

Example:

Tejas

**Authorization** answers:

> What are you allowed to do?

Example:

Tejas → ROLE_USER → GET allowed, POST forbidden

---

### What is SecurityContextHolder?

Spring Security uses the `SecurityContext` to store information about the currently authenticated user.

Our JWT filter places the authenticated user into this context after validating the JWT.

---

### Why use a filter?

The JWT needs to be processed before protected controllers execute.

The filter intercepts incoming requests and establishes authentication before Spring Security performs authorization.

---

### Why use `hasRole("ADMIN")` instead of `hasRole("ROLE_ADMIN")`?

Spring Security automatically handles the `ROLE_` prefix for `hasRole()`.

Therefore:

.hasRole("ADMIN")

corresponds to:

ROLE_ADMIN

---

### Why separate Controller, Service and Repository?

Each layer has a different responsibility:

Controller
→ HTTP/API layer

Service
→ Business logic

Repository
→ Database access

This separation improves maintainability, testing and organization.

---

# 23. Potential Future Improvements

The current project is complete for its intended scope, but possible extensions include:

* Unit tests with JUnit 5 and Mockito
* Integration testing
* OpenAPI / Swagger documentation
* Pagination and sorting
* Search/filtering
* Refresh tokens
* JWT expiration handling improvements
* Dockerization
* CI/CD pipeline
* Better secret management
* Production-grade logging and monitoring

---

# 24. Project Learning Outcomes

This project demonstrates practical understanding of:

* Java backend development
* Spring Boot
* REST API design
* Layered architecture
* Spring Data JPA
* Hibernate
* PostgreSQL
* Password hashing
* Spring Security
* JWT authentication
* Stateless authentication
* Role-based access control
* Exception handling
* DTO-based API design
* Git and GitHub

The project intentionally evolved from a basic CRUD application into a more realistic backend by incrementally adding authentication and authorization.

---

## Author

**Tejas Kumar**

IIT Kharagpur
