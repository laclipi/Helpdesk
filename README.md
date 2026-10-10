# Helpdesk — Ticket Management System

A full-stack helpdesk application built to practise backend development, REST API design, authentication, authorization, and frontend integration.

The application allows users to register, authenticate, and manage their support tickets through a web interface connected to a Spring Boot REST API.

## Demo

Watch the application demo in the [GitHub Release v1.0.0](https://github.com/laclipi/Helpdesk/releases/tag/v1.0.0).

## Features

* **Authentication:** User registration and login.
* **JWT security:** Token-based authentication for protected endpoints.
* **Ticket management:** Create, list, retrieve, update, and delete tickets.
* **Authorization:** Ticket access is restricted according to the application's ownership rules.
* **Frontend integration:** A Next.js interface communicates with the backend API.
* **API documentation and testing:** A Postman collection is included in the repository.

## Tech Stack

### Backend

* Java 21
* Spring Boot 4.1.1
* Spring Web
* Spring Data JPA
* Spring Security
* JSON Web Tokens (JWT)
* H2 Database
* Maven Wrapper
* Lombok
* Jakarta Bean Validation

### Frontend

* Next.js 16.4.0
* React 19.3.0
* TypeScript
* Tailwind CSS v4

## Architecture

The project is organized into two main applications:

* `backend/` — Spring Boot REST API, authentication, business logic, persistence, and security.
* `frontend/` — Next.js web application built with React and TypeScript.
* `postman/` — Postman collection for exercising the API.

The backend exposes REST endpoints consumed by the frontend. Spring Security protects authenticated operations, while JWTs are used to authenticate requests.

## API Endpoints

### Authentication

| Method | Endpoint         | Description         |
| ------ | ---------------- | ------------------- |
| `POST` | `/auth/register` | Register a user     |
| `POST` | `/auth/login`    | Authenticate a user |

### Tickets

| Method   | Endpoint        | Description                                          |
| -------- | --------------- | ---------------------------------------------------- |
| `GET`    | `/tickets`      | Retrieve tickets available to the authenticated user |
| `GET`    | `/tickets/{id}` | Retrieve a ticket by ID                              |
| `POST`   | `/tickets`      | Create a ticket                                      |
| `PUT`    | `/tickets/{id}` | Update a ticket                                      |
| `DELETE` | `/tickets/{id}` | Delete a ticket                                      |

Protected endpoints require a valid JWT in the HTTP `Authorization` header:

```http
Authorization: Bearer <your-token>
```

## Getting Started

### Prerequisites

* Java 21
* Node.js and npm
* Git

The Maven Wrapper is included, so a separate Maven installation is not required.

### 1. Clone the repository

```bash
git clone https://github.com/laclipi/Helpdesk.git
cd Helpdesk
```

### 2. Configure the JWT secret

The backend reads its signing key from the `JWT_SECRET` environment variable. Never commit the actual secret to the repository.

The following instructions use Windows PowerShell and create a local, ignored secret file in the repository root.

Generate a random secret:

```powershell
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[System.IO.File]::WriteAllText(
    "jwt-secret.txt",
    [Convert]::ToBase64String($bytes)
)
```

Load it into the current PowerShell session:

```powershell
$env:JWT_SECRET = (Get-Content .\jwt-secret.txt -Raw).Trim()
```

Keep this PowerShell session open while running the backend and its tests. The secret file is intended for local development only.

### 3. Start the Backend

From the repository root, open a terminal and configure the JWT secret for the current session:

```powershell
$env:JWT_SECRET = (Get-Content .\jwt-secret.txt -Raw).Trim()
```

Start the backend:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The API runs locally at:

`http://localhost:8080`

To run the backend tests, open another PowerShell terminal, navigate to the repository root, load the local JWT secret into that session, and run the tests:

```powershell
cd C:\PROYECTOS\kata-helpdesk
$env:JWT_SECRET = (Get-Content .\jwt-secret.txt -Raw).Trim()
cd backend
.\mvnw.cmd test
```

### 4. Configure and Start the Frontend

Open another terminal and navigate to the frontend directory:

```bash
cd frontend
npm install
```

Create a local `.env.local` file inside `frontend/` with the following configuration:

```dotenv
NEXT_PUBLIC_API_URL=http://localhost:8080
```

Start the frontend development server:

```bash
npm run dev
```

Open the application in your browser:

`http://localhost:3000`

The frontend and backend are configured for local development on the same computer. Make sure the backend is running before using features that require API access.

The H2 database is in-memory, so stored data is lost when the backend stops.


## Database

The application uses an H2 in-memory database for development.

**Important:** Data stored in the in-memory database is not persistent and is lost when the backend application stops.

## API Testing

A Postman collection is included under `postman/collections/`.

Use it to exercise registration, login, and ticket endpoints. For protected requests, authenticate first and provide the JWT returned by the login endpoint.

## Learning Objectives

This project was built to practise:

* Designing and implementing REST APIs with Spring Boot.
* Structuring backend code into models, repositories, services, and controllers.
* Implementing JWT authentication with Spring Security.
* Applying authorization and resource ownership checks.
* Persisting entities with Spring Data JPA.
* Integrating a TypeScript frontend with a backend API.
* Configuring local development environments and protecting secrets.
* Testing, debugging, and managing changes with Git.

## Future Improvements

Potential next steps include:

* Add automated integration and authorization tests.
* Introduce persistent database storage.
* Improve API documentation with OpenAPI/Swagger.
* Add pagination, filtering, and sorting for tickets.
* Improve frontend loading, error, and empty states.
* Add a deployment workflow.

## Author

**Laura Climent Pi**

Full-stack development practice project focused on Java, Spring Boot, REST APIs, JWT security, and Next.js.
