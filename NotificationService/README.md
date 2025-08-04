# Spring Security With JWT

A modern Spring Boot application demonstrating how to implement stateless authentication and authorization using Spring Security and JSON Web Tokens (JWT). This repository is ideal for developers looking to learn or bootstrap secure REST APIs with token-based authentication and advanced AOP (Aspect Oriented Programming) features.

---

## 🚀 Features

- **User Registration & Login:**  
  Secure endpoints (`/auth/register`, `/auth/login`) with password hashing using BCrypt.
- **JWT-Based Authentication:**  
  Stateless session management for scalable, secure APIs.
- **Role-Based Authorization:**  
  Simple user roles (USER, ADMIN); easily extendable for custom roles.
- **Spring Boot & Security Best Practices:**  
  Uses Spring Boot dependency injection, REST controllers, and robust configuration.
- **AOP Enhancements:**  
  Annotation-based logging, exception handling, role and token control using custom AOP aspects.
- **Test Endpoints:**  
  `/test/*` endpoints for verifying JWT and role-based access.
- **Docker Support:**  
  Dockerfile provided for easy containerized deployment.

---

## 📁 Project Structure

```
app/
├── src/
│   └── main/
│       ├── java/org/example/
│       │   ├── App.java                      # Spring Boot entry point
│       │   ├── config/
│       │   │   └── SecurityConfig.java       # Spring Security & JWT config
│       │   ├── controller/
│       │   │   └── AuthController.java       # Auth endpoints (register, login)
│       │   │   └── TestController.java       # Protected/test endpoints
│       │   ├── dto/
│       │   │   ├── AuthRequest.java
│       │   │   └── RegisterRequest.java
│       │   ├── model/
│       │   │   └── User.java
│       │   ├── repository/
│       │   │   └── UserRepository.java
│       │   ├── security/
│       │   │   ├── JwtFilter.java
│       │   │   └── JwtUtil.java
│       │   └── service/
│       │       └── CustomUserDetailsService.java
│       │   └── aop/
│       │       ├── exception/
│       │       ├── jwt/
│       │       ├── log/
│       │       └── role/
│       └── resources/
│           └── application.properties
├── build.gradle
├── Dockerfile
├── settings.gradle
└── .gitignore
```

---

## 🛠️ How Authentication Works

1. **User → [POST /auth/login] → AuthController**
2. **AuthController → AuthenticationManager & UserDetailsService:**  
   Authenticate credentials.
3. **AuthController → JwtUtil:**  
   Generates JWT token.
4. **User receives JWT token** and uses it in the Authorization header for protected requests.

5. **For protected endpoints:**  
   **User → [Any request with Authorization: Bearer <token>] → JwtFilter**  
   **JwtFilter → JwtUtil + UserDetailsService:** Validate and parse token.  
   **JwtFilter → SecurityContextHolder:** Authenticates the user for the request context.

---

## ⚡ Quick Start

### Prerequisites

- Java 21+
- Gradle (or use the included wrapper scripts)
- PostgreSQL (or your preferred DB, see configuration)
- Docker (optional)

### Running Locally

```bash
# Clone the repository
git clone https://github.com/ahmetrecayiozturk/SpringBootWith_SpringSecurity_Jwt_AOP.git
cd SpringBootWith_SpringSecurity_Jwt_AOP

# Configure your database in src/main/resources/application.properties

# Build and run with Gradle
./gradlew bootRun
```

The application will start at [http://localhost:8080](http://localhost:8080).

### Using Docker

```bash
docker build -t spring-jwt-app .
docker run -p 8080:8080 spring-jwt-app
```

---

## 🌐 API Endpoints

| Endpoint            | Method | Description                                | Auth Required      |
|---------------------|--------|--------------------------------------------|--------------------|
| /auth/register      | POST   | Register a new user                        | No                 |
| /auth/login         | POST   | Authenticate user, receive JWT             | No                 |
| /test/jwt-test      | GET    | Test JWT-protected endpoint                | Yes (JWT)          |
| /test/user-role-test| GET    | Test endpoint for USER role                | Yes (JWT+USER)     |
| /test/admin-role-test| GET   | Test endpoint for ADMIN role               | Yes (JWT+ADMIN)    |
| /test/exception-test| GET    | Throws and logs an exception (AOP sample)  | Yes (JWT)          |

---

### 📝 Example: Register

```http
POST /auth/register
Content-Type: application/json

{
  "username": "john",
  "password": "secret",
  "role": "USER"
}
```

### 📝 Example: Login

```http
POST /auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "secret"
}
```

**Response:**
```json
{"token": "<JWT Token>"}
```

> Include the JWT token in the Authorization header for all protected endpoints:  
> `Authorization: Bearer <JWT Token>`

---

## 🏗️ Extending the Project

- Add more user roles/privileges by extending the `User` entity and security configuration.
- Integrate with different databases by configuring your datasource in `application.properties`.
- Customize or add more AOP annotations for logging, auditing, or security.

---

## 🛡️ Advanced: Custom AOP Annotations

| Annotation                    | Purpose                                         |
|-------------------------------|-------------------------------------------------|
| `@LogExecutionTime`           | Logs method execution time                      |
| `@Exception`                  | Catches and logs exceptions in annotated methods|
| `@CheckRole("ADMIN"|"USER")`  | Restricts access to methods by role             |
| `@CheckTokenExpirationTime`   | Checks for JWT expiration before method call    |

---

## 📑 Notes

- **Configuration:**  
  All application configuration (database, port, etc.) should be managed in `src/main/resources/application.properties`.
- **Security:**  
  Do not commit sensitive data (e.g., DB credentials, JWT secret) — use environment variables or a secrets manager in production.
- **application.properties** is excluded from version control for security.
- For sharing sample configuration, use `application-example.properties`.

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

<div align="center">
  <img src="https://raw.githubusercontent.com/spring-projects/spring-boot/main/src/main/resources/static/images/spring-boot-logo.png" alt="Spring Boot" width="100"/>
  <br/><br/>
  <b>I hope it would be clear for you</b>
</div>
