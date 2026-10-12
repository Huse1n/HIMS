# HIMS - Customer Management System

HIMS is a full-stack customer management application built with
Spring Boot, Angular, and PostgreSQL.

The project demonstrates modern software development practices,
including REST API design, layered architecture, authentication,
authorization, automated testing, containerization, and
Continuous Integration (CI).

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- Spring Security
- Maven
- PostgreSQL

### Frontend
- Angular
- TypeScript
- HTML / SCSS
- Angular Router
- Angular HttpClient

### Testing
- JUnit 5
- Mockito
- Spring Boot Test / MockMvc
- Testcontainers
- Vitest

### DevOps
- Docker
- Docker Compose
- Nginx
- GitHub Actions (CI)

## Features

- Create, read, update, and delete customers (CRUD)
- Customer email uniqueness validation
- RESTful API with appropriate HTTP status codes
- Authentication using HTTP Basic
- Role-based authorization (ADMIN / USER)
- Protected Angular routes
- Responsive user interface
- Automated backend and frontend tests
- Containerized application deployment
- Automated CI pipeline

## Architecture

HIMS follows a layered architecture on the backend and a component-based architecture on the frontend.

### Backend Architecture

```text
Angular Frontend
       |
       | HTTP / REST
       v
Spring Security
       |
       v
REST Controller
       |
       v
Service Layer
       |
       v
Repository Layer
       |
       v
PostgreSQL Database
```

**Backend Layers:**

- **Controller:** Handles HTTP requests and returns responses.
- **Service:** Contains business logic and validation.
- **Repository:** Provides database access using Spring Data JPA.
- **Entity:** Represents database tables.
- **DTO:** Transfers data between the API and its clients.
- **Security:** Handles authentication and authorization.

### Frontend Architecture

The Angular frontend uses a component-based architecture.

- **Components:** Display the user interface and handle user interactions.
- **Services:** Communicate with the backend REST API.
- **Models:** Define TypeScript data structures.
- **Guards:** Protect routes based on authentication and roles.
- **Interceptors:** Attach authentication credentials to HTTP requests.

## Project Structure

```text
HIMS/
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── backend/
│   └── customer-service/
│       ├── src/
│       ├── pom.xml
│       └── Dockerfile
│
├── frontend/
│   └── hims-web/
│       ├── src/
│       ├── package.json
│       ├── angular.json
│       └── Dockerfile
│
├── docs/
│   └── ER-Diagram.md
│
├── compose.yaml
└── README.md
```

## Database Design

HIMS uses PostgreSQL to store customer information.

The current database model contains a `customers` table with the following fields:

| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | Primary Key |
| first_name | VARCHAR(255) | NOT NULL |
| last_name | VARCHAR(255) | NOT NULL |
| email | VARCHAR(255) | NOT NULL, UNIQUE |

The entity relationship diagram is available in `docs/ER-Diagram.md`.

## Getting Started

### Prerequisites

Make sure the following tools are installed:

- Git
- Docker
- Docker Compose

### 1. Clone the Repository

```bash
git clone https://github.com/Huse1n/HIMS.git
cd HIMS
```

### 2. Configure Environment Variables

Create a `.env` file in the project root directory:

```env
DB_PASSWORD=your_database_password
HIMS_ADMIN_PASSWORD=your_admin_password
HIMS_USER_PASSWORD=your_user_password
```

The `.env` file is excluded from Git to protect sensitive information.

### 3. Start the Application

Build and start all services:

```bash
docker compose up --build -d
```

Docker Compose starts three services:

- PostgreSQL database
- Spring Boot backend
- Angular frontend served by Nginx

### 4. Access the Application

Open the application in your browser:

http://localhost:4200

Log in using one of the configured accounts:

- **ADMIN:** Full customer management access.
- **USER:** Read-only access to customer information.

### 5. Stop the Application

```bash
docker compose down
```

To view container logs:

```bash
docker compose logs -f
```

## Testing

HIMS includes automated tests for both the backend and frontend.

### Backend Tests

The backend uses JUnit 5, Mockito, MockMvc, and Testcontainers.

Tests cover:
- Customer CRUD operations
- Business logic and validation
- REST API endpoints and HTTP status codes
- Database integration
- Spring application context

Run backend tests:

```bash
cd backend/customer-service
./mvnw clean verify
```

### Frontend Tests

The Angular frontend uses Vitest and Angular TestBed.

Tests cover:
- CustomerService HTTP requests
- Customer CRUD operations
- HTTP error handling
- Angular components

Run frontend tests:

```bash
cd frontend/hims-web
npm ci
npm test -- --watch=false
```

### Continuous Integration (CI)

HIMS uses GitHub Actions for automated testing and building.

The CI pipeline runs automatically on:
- Pushes to the `main` branch
- Pull requests targeting the `main` branch

The pipeline contains two independent jobs:

**Backend Tests**
1. Set up Java 21.
2. Start a PostgreSQL 16 service.
3. Execute Maven tests.

**Frontend Tests and Build**
1. Set up Node.js 24.
2. Install dependencies using `npm ci`.
3. Execute Angular tests.
4. Build the Angular application.

The CI configuration is located at `.github/workflows/ci.yml`.

## Future Improvements

Potential future enhancements include:

- JWT-based authentication
- Database migrations with Flyway
- Pagination and sorting
- Additional entities and relationships
- Automated deployment (CD)

## Author

Developed as a full-stack software engineering portfolio project.
