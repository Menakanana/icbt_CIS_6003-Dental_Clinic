# Sequence Specification: Staff User Authentication

## 1. Document Control & Overview

| Field | Detail |
|---|---|
| **Diagram Name** | `dental-clinic-auth-sequence.puml` |
| **Module / Task** | CIS6003 - Advanced Programming (Task A - 20 Marks) |
| **Workflow** | Staff User Authentication (`POST /api/auth/login`) |

---

## 2. Lifelines & Participants

- **`Staff User`**: Human actor submitting credentials.
- **`AuthApiController`**: REST Web API controller handling `POST /api/auth/login`.
- **`AuthService`**: Service orchestrating BCrypt verification and token generation.
- **`UserRepository`**: Spring Data JPA repository managing `User` entities.
- **`BCryptPasswordEncoder`**: Security encoder verifying salted password hashes (factor 12).

---

## 3. Message Sequence & Conditional Logic

1. **HTTP Request:** Client posts `LoginRequestDTO` (username, password).
2. **User Lookup:** Service calls `UserRepository.findByUsername(username)`.
   - **`alt` Branch 1 (User Not Found):** Throws `BadCredentialsException`. Returns `401 Unauthorized`.
3. **Password Hashing Check:** Service invokes `BCryptPasswordEncoder.matches(rawPassword, encodedPassword)`.
   - **`alt` Branch 2 (Password Mismatch):** Throws `BadCredentialsException`. Returns `401 Unauthorized`.
   - **`else` Branch 3 (Success Path):** Returns `200 OK` with JWT Bearer Token and User Role.
