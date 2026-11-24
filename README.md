# Food Donation System

A robust Spring Boot REST API for managing food donations, connecting food donors (restaurants, individuals, caterers) with NGOs and charitable organizations to reduce food waste and feed those in need.

---

## 🛠️ Tech Stack

- **Java**: 17 LTS
- **Framework**: Spring Boot 3.3.5
- **ORM / Persistence**: Spring Data JPA / Hibernate
- **Database**: 
  - **Production / Dev**: MySQL 8.x
  - **Testing**: H2 In-Memory Database
- **Build Tool**: Maven Wrapper (`./mvnw`)

---

## 🚀 Getting Started

### 1. Prerequisites
- **JDK 17** or later
- **MySQL 8.0+** (Optional if running tests with H2)

### 2. Configure Database (Optional)
By default, the application connects to MySQL on `localhost:3306` with database `food_donation` (which is created automatically if it doesn't exist). You can customize database parameters in `src/main/resources/application.properties` or via environment variables:

```bash
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/food_donation?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=YourPassword
PORT=8080
```

### 3. Run the Application
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

### 4. Run Automated Tests
```bash
.\mvnw.cmd test
```

### 5. Build Executable JAR
```bash
.\mvnw.cmd clean package
# The runnable JAR is created at target/food-donation-system-0.0.1-SNAPSHOT.jar
java -jar target/food-donation-system-0.0.1-SNAPSHOT.jar
```

---

## 📖 API Documentation

### 👤 Users API (`/api/users`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/users/register` | Register a new user (`DONOR`, `NGO`, or `ADMIN`) |
| `GET` | `/api/users` | List all users |
| `GET` | `/api/users/{id}` | Get user by ID |
| `GET` | `/api/users/email/{email}` | Find user by email |
| `GET` | `/api/users/role/{role}` | List users filtered by role (`DONOR`, `NGO`, `ADMIN`) |
| `DELETE` | `/api/users/{id}` | Delete user by ID |

#### Register User Example:
`POST /api/users/register`
```json
{
  "name": "Feed The Hungry Foundation",
  "email": "contact@feedhungry.org",
  "password": "SecurePassword123",
  "role": "NGO"
}
```
*(Note: Passwords are write-only and never exposed in JSON responses)*

---

### 🍲 Donations API (`/api/donations`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/donations/{donorId}` | Create donation for specific donor |
| `POST` | `/api/donations` | Create donation (donor ID in body or query param) |
| `GET` | `/api/donations` | List all donations |
| `GET` | `/api/donations/available` | List all available (unexpired) donations |
| `GET` | `/api/donations/donor/{donorId}` | List donations by donor ID |
| `GET` | `/api/donations/{id}` | Get donation by ID |
| `PATCH` | `/api/donations/{id}/status?status={status}` | Update status (`AVAILABLE`, `REQUESTED`, `APPROVED`, `COMPLETED`, `REJECTED`, `EXPIRED`) |
| `DELETE` | `/api/donations/{id}` | Delete donation (safely cascades requests) |

#### Create Donation Example:
`POST /api/donations/1`
```json
{
  "foodName": "Fresh Meals (Rice, Dal, Veggies)",
  "description": "Leftover packed meals from corporate event",
  "quantity": "50 boxes",
  "pickupLocation": "123 Main Street, Downtown",
  "safeUntil": "2026-11-25T18:00:00"
}
```

---

### 📦 Requests API (`/api/requests`)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/requests` | Request food (accepts JSON body or query parameters) |
| `GET` | `/api/requests/{requestId}` | Get request by ID |
| `GET` | `/api/requests/ngo/{receiverId}` | Get all requests submitted by an NGO |
| `GET` | `/api/requests/donation/{donationId}` | Get all requests for a specific donation |
| `PATCH` | `/api/requests/{requestId}/status?status={status}` | Approve or Reject request (`APPROVED`, `REJECTED`, `COMPLETED`) |

#### Create Request Example:
`POST /api/requests`
```json
{
  "donationId": 1,
  "receiverId": 2,
  "message": "We can pick this up immediately for our local shelter."
}
```

---

## 🌐 Steps to Push Online to GitHub

To push this repository to GitHub:

```bash
# 1. Initialize git repository
git init

# 2. Add all files
git add .

# 3. Commit changes
git commit -m "Initial commit: Food Donation System API"

# 4. Create repository on GitHub and link remote
git branch -M main
git remote add origin https://github.com/<your-username>/<repo-name>.git

# 5. Push to GitHub
git push -u origin main
```
