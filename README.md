# Savasana - Yoga Class Management Application

Full-stack application for managing yoga sessions with user authentication, session booking, and teacher management.

**Stack:** Spring Boot REST API (Java 17) • Angular 14 • MySQL 8.0

---

## Quick Start

```bash
# 1. Start database
docker compose up -d

# 2. Start backend (Terminal 1)
./start-back.sh

# 3. Start frontend (Terminal 2)
./start-front.sh
```

**Application:** http://localhost:4200  
**API:** http://localhost:8080  
**Login:** yoga@studio.com / test!1234

---

## Prerequisites

Ensure you have installed:

- **Node.js v16.x** (required for Angular 14)
- **Java 17** (OpenJDK recommended)
- **Maven 3.9+**
- **Docker & Docker Compose**

> **Note**: If multiple Java/Node versions are installed, the provided scripts automatically manage the correct versions.

---

## Installation

### 1. Clone the Project

```bash
git clone <repository-url>
cd savasana
```

### 2. Install the Database

```bash
docker compose up -d
```

This will create the MySQL database, execute the initialization script (`ressources/sql/script.sql`), and expose MySQL on port 3306.

**Database configuration:**
- Host: `localhost:3306`
- Database: `yoga`
- User: `root`
- Password: `root`

### 3. Install Backend Dependencies

```bash
cd back
mvn clean install
```

### 4. Install Frontend Dependencies

```bash
cd front
npm install
```

---

## Running the Application

### Using Scripts (Recommended)

**Backend:**
```bash
./start-back.sh
```

**Frontend:**
```bash
./start-front.sh
```

### Manual Start

**Backend:**
```bash
cd back
JAVA_HOME=/usr/lib/jvm/java-17-openjdk mvn spring-boot:run
```

**Frontend:**
```bash
cd front
npm run start -- --proxy-config src/proxy.config.json
```

> **Note**: The proxy configuration automatically redirects `/api/*` calls to the backend on port 8080.

---

## Running Tests

### Backend Tests (JUnit + Jacoco)

```bash
cd back
JAVA_HOME=/usr/lib/jvm/java-17-openjdk mvn clean test
```

- **Report**: `./back/target/site/jacoco/index.html`
- **Database**: H2 in-memory (tests only) - No Docker required

> **Notes**: 
> - Integration tests use H2 instead of MySQL for speed and isolation
> - Coverage excludes generated code (DTOs, Models, Payloads, MapStruct implementations) following industry best practices

### Frontend Unit Tests (Jest)

```bash
./run-jest.sh
# or
cd front && npm test -- --coverage
```

**Report**: `./front/coverage/jest/lcov-report/index.html`

### Frontend E2E Tests (Cypress)

**Prerequisites**: Backend AND Frontend must be running.

```bash
./run-cypress.sh
```

**Interactive mode:**
```bash
cd front
npm run start:e2e  # Terminal 1
npm run cypress:open  # Terminal 2
```

**Report**: `./front/coverage/lcov-report/index.html`

### View All Coverage Reports

```bash
./view-coverage.sh
```

---

## Useful Commands

### Docker MySQL

```bash
docker compose up -d          # Start MySQL
docker compose down           # Stop MySQL
docker compose logs mysql     # View logs
docker compose down -v        # Complete reset (deletes data)
```

### Backend

```bash
cd back
mvn clean install             # Compile
mvn test                      # Run tests
mvn spring-boot:run           # Start application
```

### Frontend

```bash
cd front
npm install                   # Install dependencies
npm run start                 # Start dev server
npm test                      # Run Jest tests
npm run cypress:run           # Run Cypress (headless)
npm run cypress:open          # Run Cypress (interactive)
```

---

## Project Structure

```
savasana/
├── back/                       # Spring Boot Backend
│   ├── src/
│   │   ├── main/java/         # Source code
│   │   └── test/java/         # Unit & Integration tests
│   ├── pom.xml                # Maven configuration
│   └── target/site/jacoco/    # Coverage reports
│
├── front/                      # Angular Frontend
│   ├── src/app/               # Components & Services
│   ├── cypress/               # E2E tests
│   ├── coverage/              # Coverage reports
│   └── package.json           # npm dependencies
│
├── ressources/
│   ├── sql/script.sql         # Database initialization
│   └── postman/               # API collection
│
├── docker-compose.yml         # MySQL configuration
├── start-back.sh              # Backend launcher
├── start-front.sh             # Frontend launcher
├── run-jest.sh                # Jest test runner
├── run-cypress.sh             # Cypress test runner
└── view-coverage.sh           # Coverage report viewer
```

---

## Technologies

### Backend
- **Spring Boot** 2.6.1 • **Java** 17 • **Spring Security** + JWT
- **Spring Data JPA** • **MySQL** 8.0
- **Lombok** 1.18.22 • **MapStruct** 1.5.1 • **Maven** 3.9+

### Frontend
- **Angular** 14.2.0 • **Node.js** 16.x • **TypeScript** 4.7.4
- **Angular Material** 14.2.0 • **RxJS** 7.5.6

### Testing
- **JUnit** 5.8.1 • **Mockito** 4.0.0 • **Jacoco** 0.8.11
- **Jest** 28.1.3 • **Cypress** 10.4.0

---

## Additional Resources

- **Postman Collection**: `ressources/postman/yoga.postman_collection.json`
- **SQL Initialization Script**: `ressources/sql/script.sql`

---

## License

This project is developed as part of an OpenClassrooms training program.
