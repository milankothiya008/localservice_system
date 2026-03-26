# LocalService – Local Service Booking Platform

A Spring Boot REST API platform that connects users with local service providers. Users can discover providers, book services, and track their bookings — while providers can manage incoming requests and update booking statuses.

---

## Project Overview

**LocalService** is a backend REST API that facilitates a marketplace between **users** (customers) and **providers** (service workers like plumbers, electricians, cleaners, etc.). The platform supports:

- Role-based registration and JWT authentication
- Service discovery and provider search
- Booking creation, tracking, and lifecycle management
- Dashboards for both users and providers

---

## Features

### Core Functionality

| Feature | Description |
|---|---|
| **JWT Authentication** | Stateless token-based auth using JJWT library |
| **Role-Based Access** | `USER` and `PROVIDER` roles with separate permissions |
| **Service Discovery** | Browse available service categories |
| **Provider Search** | Search providers by service type or name/keyword |
| **Booking Management** | Full booking lifecycle from creation to completion |
| **Dashboards** | Aggregated stats for users and providers |
| **Global Exception Handling** | Consistent JSON error responses across all endpoints |

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Core programming language |
| Spring Boot | 4.0.4 | Application framework |
| Spring Data JPA | — | ORM and database access |
| Spring Security | — | Authentication and authorization |
| JJWT | 0.11.5 | JWT token generation and validation |
| MySQL | 8.0+ | Relational database |
| Lombok | — | Reduce boilerplate code |
| Maven | 3.9+ | Build and dependency management |
| H2 | — | In-memory DB for testing |

---

## Project Structure

```
com.example.localservice
├── controller          # REST API endpoints
│   ├── AuthController
│   ├── BookingController
│   ├── ProviderController
│   ├── ServiceController
│   └── UserController
│
├── service             # Business logic layer
│   ├── AuthService / AuthServiceImpl
│   ├── BookingService / BookingServiceImpl
│   ├── ProviderService / ProviderServiceImpl
│   └── ServiceItemService / ServiceItemServiceImpl
│
├── repository          # Data access layer (JPA)
│   ├── UserRepository
│   ├── ServiceProviderRepository
│   ├── ServiceItemRepository
│   └── BookingRepository
│
├── entity              # JPA entities
│   ├── User
│   ├── Role (enum)
│   ├── ServiceItem
│   ├── ServiceProvider
│   ├── Booking
│   └── BookingStatus (enum)
│
├── dto                 # Data Transfer Objects
│   ├── RegistrationDto / LoginDto / AuthResponseDto
│   ├── BookingRequestDto / BookingResponseDto / BookingStatusUpdateDto
│   ├── ProviderResponseDto / ProviderDashboardResponse
│   ├── UserDto / UserDashboardResponse
│   └── ServiceDto / ServiceProviderDto
│
├── security            # JWT and Spring Security
│   ├── JwtUtil
│   ├── JwtFilter
│   ├── CustomUserDetails
│   └── CustomUserDetailsService
│
├── config
│   └── SecurityConfig
│
└── exception
    ├── GlobalExceptionHandler
    ├── ResourceNotFoundException
    ├── BadRequestException
    ├── DuplicateBookingException
    ├── InvalidBookingStateException
    └── ProviderNotAvailableException
```

---

## Entity Relationship Overview

```
User (role: USER or PROVIDER)
 │
 ├──► ServiceProvider (one-to-one with User of role PROVIDER)
 │         └──► ServiceItem (many-to-one)
 │
 └──► Booking (many-to-one: user)
           ├──► ServiceProvider (many-to-one: provider)
           └──► ServiceItem (many-to-one)
```

**Key Relationships:**
- A `User` with role `PROVIDER` has one `ServiceProvider` profile
- A `ServiceProvider` is linked to one `ServiceItem` (e.g., Plumbing, Cleaning)
- A `Booking` ties a `User`, a `ServiceProvider`, and a `ServiceItem` together on a specific date
- Booking status flows: `PENDING → ACCEPTED / REJECTED / CANCELLED`, then `ACCEPTED → COMPLETED`

---

## Booking Status Flow

```
                     ┌─────────────┐
                     │   PENDING   │ ← Created by USER
                     └──────┬──────┘
           ┌────────────────┼────────────────┐
           ▼                ▼                ▼
      ACCEPTED           REJECTED        CANCELLED
    (by PROVIDER)      (by PROVIDER)    (by USER)
           │
           ▼
       COMPLETED
     (by PROVIDER)
```

**Rules:**
- Only `PENDING` bookings can be cancelled (by USER)
- Only `PROVIDER` can accept, reject, or complete bookings
- A provider cannot have two `ACCEPTED` bookings on the same date (availability check)
- Duplicate bookings (same user + provider + date) are blocked

---

## API Endpoints

### Authentication — `/api/auth`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Register as USER or PROVIDER | ❌ No |
| POST | `/api/auth/login` | Login and receive JWT token | ❌ No |

---

### Services — `/api/services`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/services` | Get all service categories | ✅ Yes |
| GET | `/api/services?name=plumb` | Search services by name | ✅ Yes |

---

### Providers — `/api/providers`

| Method | Endpoint | Description | Auth | Role |
|---|---|---|---|---|
| GET | `/api/providers` | Search providers (by serviceId, keyword, name) | ✅ Yes | Any |
| GET | `/api/providers/{providerId}/dashboard` | Get provider dashboard stats | ✅ Yes | PROVIDER |

---

### Bookings — `/api/bookings`

| Method | Endpoint | Description | Auth | Role |
|---|---|---|---|---|
| POST | `/api/bookings` | Create a new booking | ✅ Yes | USER |
| GET | `/api/bookings/user/{userId}` | Get all bookings for a user | ✅ Yes | USER |
| GET | `/api/bookings/provider/{providerId}` | Get bookings for a provider (paginated) | ✅ Yes | PROVIDER |
| PUT | `/api/bookings/{id}/status` | Update booking status (ACCEPT/REJECT) | ✅ Yes | PROVIDER |
| PUT | `/api/bookings/{id}/complete` | Mark booking as COMPLETED | ✅ Yes | PROVIDER |
| PUT | `/api/bookings/{id}/cancel` | Cancel a PENDING booking | ✅ Yes | USER |

---

### Users — `/api/users`

| Method | Endpoint | Description | Auth | Role |
|---|---|---|---|---|
| GET | `/api/users/{userId}/dashboard` | Get user dashboard stats | ✅ Yes | USER |

---

## Request & Response Examples

### 1. Register as a USER

**POST** `/api/auth/register`

```json
{
  "name": "Ravi Patel",
  "email": "ravi@example.com",
  "password": "ravi@123",
  "role": "USER"
}
```

**Response (201 Created):**
```json
{
  "message": "Registration successful",
  "token": null,
  "user": {
    "id": 1,
    "name": "Ravi Patel",
    "email": "ravi@example.com",
    "role": "USER"
  },
  "provider": null
}
```

---

### 2. Register as a PROVIDER

**POST** `/api/auth/register`

```json
{
  "name": "Suresh Kumar",
  "email": "suresh@example.com",
  "password": "suresh@123",
  "role": "PROVIDER",
  "serviceId": 1,
  "experience": 5
}
```

> ⚠️ `serviceId` and `experience` are **required** for PROVIDER registration.

**Response (201 Created):**
```json
{
  "message": "Registration successful",
  "token": null,
  "user": {
    "id": 2,
    "name": "Suresh Kumar",
    "email": "suresh@example.com",
    "role": "PROVIDER"
  },
  "provider": {
    "id": 1,
    "userId": 2,
    "name": "Suresh Kumar",
    "service": {
      "id": 1,
      "name": "Plumbing"
    },
    "experience": 5
  }
}
```

---

### 3. Login

**POST** `/api/auth/login`

```json
{
  "email": "ravi@example.com",
  "password": "ravi@123"
}
```

**Response (200 OK):**
```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "name": "Ravi Patel",
    "email": "ravi@example.com",
    "role": "USER"
  },
  "provider": null
}
```

> 🔑 **Copy the `token` value** — you must include it in the `Authorization` header for all protected endpoints:
> ```
> Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
> ```

---

### 4. Get All Services

**GET** `/api/services`

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  { "id": 1, "name": "Plumbing" },
  { "id": 2, "name": "Electrical" },
  { "id": 3, "name": "Cleaning" }
]
```

**Search by name:**
```
GET /api/services?name=plumb
```

---

### 5. Search Providers

**GET** `/api/providers?serviceId=1`

Search by service:
```
GET /api/providers?serviceId=1
```

Search by keyword (matches provider name or service name):
```
GET /api/providers?keyword=suresh
```

Combined:
```
GET /api/providers?serviceId=1&keyword=suresh
```

**Response (200 OK):**
```json
[
  {
    "providerId": 1,
    "providerName": "Suresh Kumar",
    "serviceName": "Plumbing",
    "experience": 5
  }
]
```

---

### 6. Create a Booking

**POST** `/api/bookings`

**Headers:**
```
Authorization: Bearer <USER token>
Content-Type: application/json
```

**Body:**
```json
{
  "userId": 1,
  "providerId": 1,
  "serviceId": 1,
  "serviceDate": "2026-04-15"
}
```

**Response (201 Created):**
```json
{
  "bookingId": 1,
  "serviceName": "Plumbing",
  "providerName": "Suresh Kumar",
  "status": "PENDING",
  "serviceDate": "2026-04-15"
}
```

**Validation Rules:**
- `serviceDate` cannot be in the past
- All fields are required
- The provider must offer the requested service
- Duplicate booking (same user + provider + date) is rejected

---

### 7. Get Bookings for a User

**GET** `/api/bookings/user/{userId}`

**Headers:**
```
Authorization: Bearer <USER token>
```

**Response (200 OK):**
```json
[
  {
    "bookingId": 1,
    "serviceName": "Plumbing",
    "providerName": "Suresh Kumar",
    "status": "PENDING",
    "serviceDate": "2026-04-15"
  }
]
```

---

### 8. Get Bookings for a Provider (Paginated)

**GET** `/api/bookings/provider/{providerId}`

**Headers:**
```
Authorization: Bearer <PROVIDER token>
```

**Optional query params:**
```
?status=PENDING&page=0&size=5&sort=createdAt,desc
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "bookingId": 1,
      "serviceName": "Plumbing",
      "providerName": "Suresh Kumar",
      "status": "PENDING",
      "serviceDate": "2026-04-15"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 5,
  "number": 0
}
```

---

### 9. Update Booking Status (Provider)

**PUT** `/api/bookings/{id}/status`

**Headers:**
```
Authorization: Bearer <PROVIDER token>
Content-Type: application/json
```

**Body (Accept):**
```json
{ "status": "ACCEPTED" }
```

**Body (Reject):**
```json
{ "status": "REJECTED" }
```

**Response (200 OK):**
```json
{
  "bookingId": 1,
  "serviceName": "Plumbing",
  "providerName": "Suresh Kumar",
  "status": "ACCEPTED",
  "serviceDate": "2026-04-15"
}
```

> ⚠️ A provider cannot ACCEPT a booking if they already have an ACCEPTED booking on the same date.

---

### 10. Complete a Booking

**PUT** `/api/bookings/{id}/complete`

**Headers:**
```
Authorization: Bearer <PROVIDER token>
```

**Response (200 OK):**
```json
{
  "bookingId": 1,
  "serviceName": "Plumbing",
  "providerName": "Suresh Kumar",
  "status": "COMPLETED",
  "serviceDate": "2026-04-15"
}
```

---

### 11. Cancel a Booking (User)

**PUT** `/api/bookings/{id}/cancel`

**Headers:**
```
Authorization: Bearer <USER token>
```

**Response (200 OK):**
```json
{
  "bookingId": 1,
  "serviceName": "Plumbing",
  "providerName": "Suresh Kumar",
  "status": "CANCELLED",
  "serviceDate": "2026-04-15"
}
```

> ⚠️ Only `PENDING` bookings can be cancelled.

---

### 12. User Dashboard

**GET** `/api/users/{userId}/dashboard`

**Headers:**
```
Authorization: Bearer <USER token>
```

**Response (200 OK):**
```json
{
  "totalBookings": 5,
  "pendingBookings": 1,
  "acceptedBookings": 2,
  "completedBookings": 1,
  "cancelledBookings": 1,
  "rejectedBookings": 0
}
```

---

### 13. Provider Dashboard

**GET** `/api/providers/{providerId}/dashboard`

**Headers:**
```
Authorization: Bearer <PROVIDER token>
```

**Response (200 OK):**
```json
{
  "totalRequests": 8,
  "pendingRequests": 2,
  "acceptedRequests": 3,
  "completedRequests": 2,
  "cancelledRequests": 1,
  "rejectedRequests": 0
}
```

---

## Exception Handling

All errors return a consistent JSON format:

```json
{
  "error": "Error description here"
}
```

| Exception | HTTP Status | When Thrown |
|---|---|---|
| `ResourceNotFoundException` | 404 Not Found | User, provider, service, or booking not found |
| `BadRequestException` | 400 Bad Request | Invalid input (e.g., provider doesn't offer service) |
| `DuplicateBookingException` | 409 Conflict | Same user + provider + date already exists |
| `ProviderNotAvailableException` | 409 Conflict | Provider already ACCEPTED on that date |
| `InvalidBookingStateException` | 400 Bad Request | Invalid status transition |
| Validation errors | 400 Bad Request | `@NotNull`, `@Email`, `@FutureOrPresent` violations |
| Generic exceptions | 500 Internal Server Error | Unexpected errors |

**Example error responses:**

```json
// 404
{ "error": "User not found" }

// 409
{ "error": "Duplicate booking exists for this user, provider, and date" }

// 400 (validation)
{
  "serviceDate": "Service date cannot be in the past",
  "userId": "User ID is required"
}
```

---

## Security & Authorization

| Endpoint Pattern | Allowed Role |
|---|---|
| `/api/auth/**` | Public (no auth) |
| `POST /api/bookings` | USER only |
| `GET /api/bookings/user/**` | USER only |
| `PUT /api/bookings/*/cancel` | USER only |
| `GET /api/users/**` | USER only |
| `PUT /api/bookings/*/status` | PROVIDER only |
| `PUT /api/bookings/*/complete` | PROVIDER only |
| `GET /api/bookings/provider/**` | PROVIDER only |
| `GET /api/providers/*/dashboard` | PROVIDER only |
| All other endpoints | Any authenticated user |

JWT tokens are generated at login and expire after **10 hours**.

---

## Complete Test Workflow

Here is a step-by-step flow to fully test the platform:

### Step 1 — Add Service Data (directly via MySQL)

Since there is no admin endpoint to create services, seed the database manually:

```sql
INSERT INTO services (name) VALUES ('Plumbing');
INSERT INTO services (name) VALUES ('Electrical');
INSERT INTO services (name) VALUES ('House Cleaning');
```

---

### Step 2 — Register a User

```
POST /api/auth/register
Body: { "name": "Ravi", "email": "ravi@test.com", "password": "pass123", "role": "USER" }
```

---

### Step 3 — Register a Provider

```
POST /api/auth/register
Body: { "name": "Suresh", "email": "suresh@test.com", "password": "pass123", "role": "PROVIDER", "serviceId": 1, "experience": 4 }
```

---

### Step 4 — Login as User, Get Token

```
POST /api/auth/login
Body: { "email": "ravi@test.com", "password": "pass123" }
→ Save the token as USER_TOKEN
```

---

### Step 5 — Login as Provider, Get Token

```
POST /api/auth/login
Body: { "email": "suresh@test.com", "password": "pass123" }
→ Save the token as PROVIDER_TOKEN
→ Note the provider.id from the response
```

---

### Step 6 — Search Providers (as User)

```
GET /api/providers?serviceId=1
Authorization: Bearer USER_TOKEN
→ Note the providerId
```

---

### Step 7 — Create a Booking (as User)

```
POST /api/bookings
Authorization: Bearer USER_TOKEN
Body: { "userId": 1, "providerId": 1, "serviceId": 1, "serviceDate": "2026-05-01" }
→ Note the bookingId
```

---

### Step 8 — View User Bookings

```
GET /api/bookings/user/1
Authorization: Bearer USER_TOKEN
→ Should show the booking with status PENDING
```

---

### Step 9 — View Provider Bookings

```
GET /api/bookings/provider/1
Authorization: Bearer PROVIDER_TOKEN
→ Should show the incoming booking
```

---

### Step 10 — Accept the Booking (as Provider)

```
PUT /api/bookings/1/status
Authorization: Bearer PROVIDER_TOKEN
Body: { "status": "ACCEPTED" }
```

---

### Step 11 — Complete the Booking (as Provider)

```
PUT /api/bookings/1/complete
Authorization: Bearer PROVIDER_TOKEN
```

---

### Step 12 — Check Dashboards

```
GET /api/users/1/dashboard        → Authorization: Bearer USER_TOKEN
GET /api/providers/1/dashboard    → Authorization: Bearer PROVIDER_TOKEN
```

---

## How to Run the Project

### Prerequisites

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### Setup Steps

**1. Clone the repository**
```bash
git clone <repository-url>
cd localservice
```

**2. Create the MySQL database**
```sql
CREATE DATABASE localservice;
```

**3. Configure `application.properties`**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/localservice?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
```

**4. Run the application**
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

---

## Testing in Postman

1. Create a **Postman Collection** called `LocalService`
2. Set a **collection variable** `baseUrl = http://localhost:8080`
3. After login, copy the token and set it as a variable: `token = <JWT>`
4. For protected endpoints, set header: `Authorization: Bearer {{token}}`
5. Follow the **Complete Test Workflow** above step by step

---

## Troubleshooting

| Problem | Solution |
|---|---|
| Application fails to start | Check MySQL is running and credentials in `application.properties` are correct |
| `401 Unauthorized` | Ensure `Authorization: Bearer <token>` header is set correctly |
| `403 Forbidden` | You're using the wrong role's token for the endpoint |
| `404 Not Found` | Verify the resource ID exists in the database |
| `409 Conflict` | Duplicate booking or provider unavailable on that date |
| `400 Bad Request` | Check validation rules — past dates, missing fields, wrong status transition |
| Port 8080 in use | Add `server.port=8081` to `application.properties` |
| PROVIDER registration fails | Make sure `serviceId` and `experience` are included in the request body |

---

## Future Enhancements

- [ ] Admin role with service/category management endpoints
- [ ] Rating and review system for completed bookings
- [ ] Provider availability scheduling
- [ ] Email/SMS notifications on booking status changes
- [ ] Pagination support for user bookings
- [ ] WebSocket support for real-time status updates
- [ ] Payment integration

---

Built with ❤️ using Spring Boot
