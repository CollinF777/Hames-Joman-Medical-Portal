# Hames Joman Medical Patient Portal

A full-stack medical appointment management system built with a Spring Boot REST API backend and a JavaFX desktop frontend. Three user roles — **Admin**, **Doctor**, and **Patient** — each get their own dashboard with role-appropriate functionality.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
  - [Database Setup](#database-setup)
  - [Backend Setup](#backend-setup)
  - [Frontend Setup](#frontend-setup)
- [API Overview](#api-overview)
- [Running Tests](#running-tests)
- [Team](#team)

---

## Tech Stack

**Backend**
- Java 17
- Spring Boot 4.x (Web, Data JPA, Security Crypto)
- MySQL
- BCrypt password hashing
- JUnit 5 + Mockito

**Frontend**
- Java 17
- JavaFX 17
- Jackson (JSON parsing)
- TestFX (UI testing)
- Mockito

---

## Project Structure

```
patient-portal/
├── Backend/                  # Spring Boot REST API
│   └── src/
│       └── main/java/com/HamesJoman/patient_portal/
│           ├── controllers/  # REST endpoints (Auth, User, Appointment)
│           ├── dto/          # Request/response data transfer objects
│           ├── models/       # JPA entities (User, Patient, Doctor, Admin, Appointment)
│           ├── repositories/ # Spring Data JPA repositories
│           └── services/     # Business logic
└── Frontend/                 # JavaFX desktop application
    └── src/
        └── main/java/com/HamesJoman/patientportalguiapp/
            ├── controllers/  # JavaFX controllers by role (Admin, Doctor, Patient, Shared)
            ├── ApiClient.java    # HTTPS HTTP client
            └── SessionManager.java  # Singleton session state
```

---

## Prerequisites

- **Java 17** (required for both backend and frontend)
- **Maven** (or use the included `mvnw` wrapper)
- **MySQL** — download from [https://dev.mysql.com/downloads/](https://dev.mysql.com/downloads/)
  - Windows: use the installer at the bottom of the page
  - Linux: install via your package manager (e.g. `sudo apt install mysql-server`)

---

## Getting Started

### Database Setup

1. Install MySQL and start the server.
2. Create a database named `patient_portal`:
   ```sql
   CREATE DATABASE patient_portal;
   ```
3. Note your MySQL username, password, and port (default is `3306`; the template uses `3308` — adjust as needed).

### Backend Setup

1. Navigate to `patient-portal/Backend/`.
2. Copy the properties template and fill in your database credentials:
   ```
   src/main/resources/application.properties.template  →  src/main/resources/application.properties
   ```
   Update these fields:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/patient_portal
   spring.datasource.username=YOUR_USERNAME
   spring.datasource.password=YOUR_PASSWORD
   ```
3. Run the backend:
   ```bash
   ./mvnw spring-boot:run
   ```
   The server starts on **port 8443** (HTTPS). Spring will auto-create the database schema on first run.

4. Verify the API is running by hitting `https://localhost:8443/api/users` in Postman. If you get a 200 (empty array), you're good.

> **Note:** The backend uses a self-signed SSL certificate. You'll need to accept the cert in Postman (`Settings → SSL certificate verification → OFF`) or your HTTP client.

### Frontend Setup

1. Navigate to `patient-portal/Frontend/`.
2. Run the desktop application:
   ```bash
   ./mvnw javafx:run
   ```
3. Log in with any user account you've created through the API. The app will redirect you to the correct dashboard based on your role.

---

## API Overview

All endpoints are prefixed with `/api`.

### Auth

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/login` | Authenticate and get user info |

### Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/users` | Get all users |
| `GET` | `/api/users/{id}` | Get user by ID |
| `POST` | `/api/users` | Create a user (`role`: `patient`, `doctor`, or `admin`) |
| `PUT` | `/api/users/{id}` | Update a user (leave password blank to keep unchanged) |
| `PATCH` | `/api/users/{id}/change-password` | Change a user's password |
| `DELETE` | `/api/users/{id}` | Delete a user (cancels their active appointments) |

### Appointments

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/appointments` | Get all appointments |
| `GET` | `/api/appointments/{id}` | Get appointment by ID |
| `GET` | `/api/appointments/patient/{id}` | Get appointments for a patient |
| `GET` | `/api/appointments/doctor/{id}` | Get appointments for a doctor |
| `POST` | `/api/appointments` | Create an appointment |
| `PUT` | `/api/appointments/{id}` | Update an appointment (ACTIVE only) |
| `PUT` | `/api/appointments/{id}/cancel` | Cancel an appointment |

**Appointment request body:**
```json
{
  "date": "YYYY-MM-DD",
  "startTime": "HH:mm",
  "endTime": "HH:mm",
  "patientId": 1,
  "doctorId": 2
}
```

**Appointment statuses:** `ACTIVE` → `CANCELLED` or `FINISHED` (auto-set when the end time passes)

---

## Running Tests

### Backend
```bash
cd patient-portal/Backend
./mvnw test
```
Tests use an in-memory H2 database and Mockito mocks — no MySQL required.

### Frontend
```bash
cd patient-portal/Frontend
./mvnw test
```
Includes unit tests (logic + SessionManager) and UI tests (TestFX). UI tests require a display; on headless servers, use a virtual display like Xvfb.

> **Note:** Frontend tests must be run on **Java 17**. Other versions will not work.

---

## Team

**Team Hames Joman**

- Collin Fair — Lead Dev
- Nathan Amidon — AppointmentService, AppointmentController, backend tests
- Liam Callahan — Frontend UI, DeleteUserController, PasswordChangeController
- Corey Suhr — Cancel/Update appointment controllers, confirmation flow
- Mohamed Musa — ViewAppointmentsController, ViewUsersController, backend unit tests
- Ali Beheshti — Backend unit tests
