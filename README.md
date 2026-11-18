# roombooking
# RoomBooking API

## Project Overview

RoomBooking API is a RESTful backend application developed with Java and Spring Boot to manage meeting rooms and their reservations. It allows users to register and authenticate using JWT, create and list meeting rooms, and  manage reservations validating room availability to prevent scheduling conflicts.

## Technologies

- Java 17
- Spring Boot 4.1.2
- Maven
- Spring Data JPA / Hibernate
- H2 Database
- Flyway
- Spring Security
- JSON Web Tokens (JWT)
- Jakarta Bean Validation
- SpringDoc OpenAPI / Swagger UI
- JUnit 5, Mockito, and AssertJ

## Getting Started

### Prerequisites

- Java 17 or later.
- Git.

The project includes the Maven Wrapper, so a separate Maven installation is not required.

### 1. Clone the repository

```bash
git clone https://github.com/ariannaleydys/roombooking.git
cd roombooking
```

### 2. Configure the JWT secret

The application requires an environment variable named `JWT_SECRET_KEY`, which contains the secret key used to sign JWT tokens.

On Windows PowerShell, you can set it for the current terminal session:

```powershell
$env:JWT_SECRET_KEY = "YOUR_BASE64_SECRET_KEY"
```

Use a secure Base64-encoded secret key. Never commit real secrets to the repository.

### 3. Run the application

On Windows:
```powershell
.\mvnw.cmd spring-boot:run
```

On Linux or macOS:
```bash
./mvnw spring-boot:run
```

The application will be available at:

`http://localhost:8080`

Flyway automatically applies database migrations when the application starts, and H2 is used as an in-memory database.

### 4. API Documentation

Swagger UI is available at:

`http://localhost:8080/swagger-ui/index.html`

## Design Decisions

The following decisions were made during the development process:

- **Layered architecture:** Responsibilities were separated between controllers, services, and repositories to improve maintainability.
- **Service interfaces and implementations (`service/impl`):** The service layer was designed with separate interfaces and implementation classes to improve scalability. This makes it easier to introduce alternative implementations in the future. For example, if the application evolves to support different reservation hierarchies or reservation types with specific business rules, new implementations could provide different behaviors while preserving the existing service contracts.
- **DTOs:** Data Transfer Objects are used to avoid exposing persistence entities directly through the API.
- **JWT authentication:** Stateless authentication was selected to protect write operations.
- **Flyway:** Database migrations are versioned and managed through Flyway.
- **H2:** An in-memory database was chosen to simplify local setup and execution.
- **JPA Specifications:** Used to build flexible optional filters for reservations by room and date.
- **Reservation validation:** Time interval overlap is checked to prevent conflicting confirmed reservations for the same room.

## Testing

The project includes unit and integration tests using JUnit 5, Mockito, AssertJ, and Spring Boot Test with MockMvc.

**Unit tests**
- Reservation service business logic.
- Successful reservation creation.
- Conflict detection when a room is already reserved.
- Resource validation, including missing rooms, inactive rooms, and missing users.

**Integration tests**
- Retrieve all rooms.
- Retrieve only active rooms.
- Retrieve only inactive rooms.

Integration tests use an H2 in-memory database to verify the interaction between the REST controller, service layer, repository, and database.

To run all tests:
```bash
./mvnw clean test
```
On Windows:
```powershell
.\mvnw.cmd clean test
```
**Test results:** All 8 tests pass successfully (5 unit tests and 3 integration tests).

## What I Would Improve With More Time

- **Integration testing:** Expand test coverage to validate REST endpoints, authentication, and authorization within a full application context.
- **PostgreSQL:** Add a PostgreSQL configuration to provide an environment closer to production.
- **CI/CD:** Configure GitHub Actions to automatically run tests and validations whenever changes are pushed.
- **Security:** Extend role and permission management and consider implementing refresh tokens.
- **Features:** Of course, the features can be increased to achieve a more complete meeting room booking system.

## AI Usage

AI tools were used as development assistants throughout the project, mainly to explore implementation alternatives, troubleshoot technical issues, and review code.

**The following examples illustrate how AI was used:**

- What I asked AI: Help with Spring Security and JWT configuration. 
- What I accepted: The general JWT filter structure and stateless security configuration. 
- What I reviewed, corrected, or adapted: Adjusted exception handling and HTTP responses to correctly distinguish authentication and authorization errors. 


- What I asked AI: Suggestions for unit tests for the reservation service. 
- What I accepted: The test structure using JUnit 5 and Mockito. 
- What I reviewed, corrected, or adapted: Adapted test scenarios to the implemented business rules and verified the results by running the tests. 


- What I asked AI: Review the implementation of optional reservation filters. 
- What I accepted: The use of JPA Specifications as a flexible approach. 
- What I reviewed, corrected, or adapted: Adapted the implementation to combine room and date filters while preserving pagination.

