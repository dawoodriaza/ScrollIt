# ScrollIt      


##  ERD Diagram
Initital ERD Daigram:  
[![ScrollIt Initia ERD](https://ibb.co/1yWrTYr)](https://ibb.co/1yWrTYr)




# ScrollIt : Live Streaming Platform

A full-stack live streaming platform built with **Spring Boot 4** (backend) and **Next.js** (frontend).  
Users can go live, watch streams, send gifts, comment, and like — all handled safely under heavy concurrent traffic.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Features](#features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Mailtrap Setup](#mailtrap-setup)
- [Seeded Data](#seeded-data)
- [API Reference](#api-reference)
- [Concurrency Design](#concurrency-design)
- [Role Management](#role-management)

---

##  Tech Stack

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Programming language |
| Spring Boot | 4.0.5 | Backend framework |
| Spring Security | 7.0.4 | JWT auth + role management |
| Spring Data JPA | 4.0.4 | Database ORM |
| PostgreSQL | Latest | Relational database |
| Hibernate | 7.2.7 | JPA implementation |
| JWT (jjwt) | 0.11.5 | Token generation |
| Lombok | 1.18.44 | Reduce boilerplate |
| Mailtrap SMTP | — | Email verification (testing) |

### Frontend
| Technology | Purpose |
|---|---|
| Next.js 14 | React framework |
| TypeScript | Type safety |
| localStorage | Auth token storage |

---

##  Features

### User Features
- Register with email verification
- Login with JWT token
- Forgot password and reset via email
- Change password
- Update profile and upload profile picture
- Create live streams with thumbnail
- Go live, end stream
- Join stream as registered user or guest (no login needed)
- Like streams
- Post, edit, delete comments
- Send gifts to streamers using coins
- View coin balance and gift history

### Admin Features
- View platform stats (total users, live streams, total streams)
- View all users with roles and coin balance
- Add coins to any user
- Deactivate admin users (soft delete)
- Delete regular users (hard delete)
- View all streams
- Force end any live stream
- Delete any stream
- Create, edit, deactivate gifts

---

## Project Structure

### Backend
```
livestream-api/
├── src/main/java/com/livestream/livestream_api/
│   ├── config/
│   │   ├── DataSeeder.java          — seeds DB on first run
│   │   ├── JwtAuthFilter.java       — reads JWT from every request
│   │   ├── JwtUtil.java             — generate and validate tokens
│   │   ├── SecurityConfig.java      — route protection rules
│   │   └── StreamConcurrencyManager.java — all concurrent locks
│   ├── controller/
│   │   ├── AdminController.java     — admin only endpoints
│   │   ├── AuthController.java      — register, login, password
│   │   ├── GiftController.java      — gift catalog
│   │   ├── LiveStreamController.java — stream CRUD + join/leave
│   │   ├── StreamInteractionController.java — likes, comments, gifts
│   │   └── UserController.java      — user profile
│   ├── dto/
│   │   ├── request/                 — incoming request bodies
│   │   └── response/                — outgoing response shapes
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java — catches all errors
│   │   └── *.java                   — custom exception classes
│   ├── model/
│   │   ├── User.java
│   │   ├── LiveStream.java
│   │   ├── StreamViewer.java
│   │   ├── Like.java
│   │   ├── Comment.java
│   │   ├── Gift.java
│   │   └── GiftTransaction.java
│   ├── repository/                  — Spring Data JPA interfaces
│   └── service/
│       ├── AuthService.java
│       ├── LiveStreamService.java
│       ├── StreamService.java       — likes, comments, gifts
│       ├── UserService.java
│       ├── GiftService.java
│       ├── FileStorageService.java
│       └── EmailService.java
├── src/main/resources/
│   └── application.properties
└── uploads/                         — uploaded images saved here
```

### Frontend
```
srcollit/
├── app/
│   ├── (auth)/
│   │   ├── login/page.tsx
│   │   ├── register/page.tsx
│   │   └── forgot-password/page.tsx
│   ├── admin/page.tsx               — admin dashboard
│   ├── streams/
│   │   ├── page.tsx                 — all streams grid
│   │   ├── create/page.tsx          — create stream
│   │   └── [id]/page.tsx            — stream detail + chat + gifts
│   ├── profile/page.tsx             — user profile
│   ├── layout.tsx
│   ├── globals.css
│   └── page.tsx                     — redirects to /streams
├── components/
│   ├── Navbar.tsx
│   └── StreamCard.tsx
└── lib/
    ├── api.ts                       — fetch helper
    └── auth.ts                      — token helpers
```

---

##  Getting Started

### Prerequisites
- Java 17+
- Maven
- PostgreSQL
- Node.js 18+

---

### Backend Setup

**Step 1 — Create the database**
```sql
CREATE DATABASE "ScrollIt";
```

**Step 2 — Configure `application.properties`**
```properties
spring.application.name=livestream-api
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/ScrollIt
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=true

app.jwt.secret=C6UlILsE6GJwNqwCTkkvJj9O653yJUoteWMLfYyrc3vaGrrTOrJFAUD1wEBnnposzcQl
app.jwt.expiration=86400000

app.upload.dir=uploads/
spring.web.resources.static-locations=file:uploads/,classpath:/static/
spring.mvc.static-path-pattern=/uploads/**

spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=YOUR_MAILTRAP_USERNAME
spring.mail.password=YOUR_MAILTRAP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Step 3 — Run the backend**
```bash
cd livestream-api
mvn spring-boot:run
```

Backend runs on `http://localhost:8080`  
Database is seeded automatically on first run.

---

### Frontend Setup

**Step 1 — Install dependencies**
```bash
cd srcollit
npm install
```

**Step 2 — Run the frontend**
```bash
npm run dev
```

Frontend runs on `http://localhost:3000`

---

## 📧 Mailtrap Setup

Mailtrap is a fake email inbox for testing. All verification and reset emails go here instead of real inboxes.

**Step 1** — Go to [mailtrap.io](https://mailtrap.io) and create a free account

**Step 2** — Go to **Email Testing → Inboxes → SMTP Settings**

**Step 3** — Select **Spring Boot** from the integrations dropdown

**Step 4** — Copy your credentials and paste into `application.properties`:
```properties
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=PASTE_YOUR_USERNAME
spring.mail.password=PASTE_YOUR_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Step 5** — Restart the backend

**How it works:**
```
User registers → email sent to Mailtrap → open Mailtrap inbox → copy token → paste in Postman → verified ✅
```

---

## Seeded Data

The database is seeded automatically on first run with:

### Users
| Username | Email | Password | Role | Coins |
|---|---|---|---|---|
| admin | admin@livestream.com | admin123 | ADMIN | 99999 |
| Dawood | dawood@livestream.com | Dawood123 | USER | 500 |
| Hamza | hamza@livestream.com | Hamza123 | USER | 300 |
| Shahid | shahid@livestream.com | Shahid123 | USER | 750 |
| AliTheCoder | ali@livestream.com | Ali123 | USER | 1200 |
| OmarStreams | omar@livestream.com | Omar123 | USER | 900 |
| YusufLive | yusuf@livestream.com | Yusuf123 | USER | 600 |
| IbrahimGamer | ibrahim@livestream.com | Ibrahim123 | USER | 1100 |
| AhmedPlays | ahmed@livestream.com | Ahmed123 | USER | 400 |
| KhalidX | khalid@livestream.com | Khalid123 | USER | 850 |

### Gifts
| Name | Cost |
|---|---|
| 🌹 Rose | 10 coins |
| ❤️ Heart | 50 coins |
| 👑 Crown | 200 coins |
| 🚀 Rocket | 500 coins |
| 💎 Diamond | 1000 coins |

### Streams
10 streams seeded with thumbnails — mix of LIVE, SCHEDULED, and ENDED status.

---

## 📡 API Reference

Base URL: `http://localhost:8080`

All protected routes require:
```
Authorization: Bearer YOUR_JWT_TOKEN
```

---

### Auth — No token required

| Method | Endpoint | Body | Description |
|---|---|---|---|
| POST | `/api/auth/register` | `{username, email, password}` | Register new account |
| POST | `/api/auth/login` | `{email, password}` | Login and get token |
| GET | `/api/auth/verify-email?token=` | — | Verify email from Mailtrap link |
| POST | `/api/auth/forgot-password` | `{email}` | Send reset link to email |
| POST | `/api/auth/reset-password` | `{token, newPassword}` | Reset password with token |
| POST | `/api/auth/change-password` | `{currentPassword, newPassword}` | Change password (needs token) |

**Register example:**
```json
POST /api/auth/register
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123"
}
```

**Login response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "user": {
    "userId": 1,
    "username": "john",
    "email": "john@example.com",
    "coinBalance": 100,
    "role": "USER"
  }
}
```

---

###  Users — Token required

| Method | Endpoint | Body | Description |
|---|---|---|---|
| GET | `/api/users/me` | — | Get my profile |
| PUT | `/api/users/me` | `{username}` | Update my username |
| POST | `/api/users/me/profile-picture` | form-data: `file` | Upload profile picture |

---

###  Streams — Mixed access

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/streams` | Public | Get all LIVE streams |
| GET | `/api/streams/all` | Public | Get all streams (all statuses) |
| GET | `/api/streams/scheduled` | Public | Get scheduled streams |
| GET | `/api/streams/ended` | Public | Get ended streams |
| GET | `/api/streams/search?keyword=` | Public | Search streams by title |
| GET | `/api/streams/{id}` | Public | Get single stream |
| GET | `/api/streams/my` | User | Get my own streams |
| POST | `/api/streams` | User | Create stream (JSON) |
| POST | `/api/streams/{id}/thumbnail` | User (host) | Upload thumbnail (form-data: file) |
| PUT | `/api/streams/{id}` | User (host) | Update stream title/description |
| PUT | `/api/streams/{id}/start` | User (host) | Start stream (SCHEDULED → LIVE) |
| PUT | `/api/streams/{id}/end` | User (host) | End stream (LIVE → ENDED) |
| DELETE | `/api/streams/{id}` | User (host) | Delete stream |
| POST | `/api/streams/{id}/join` | User | Join stream as registered user |
| POST | `/api/streams/{id}/join/guest?guestName=` | Public | Join as guest — no login needed |
| POST | `/api/streams/{id}/leave` | User | Leave stream |
| POST | `/api/streams/{id}/leave/guest?viewerId=` | Public | Guest leave stream |
| GET | `/api/streams/{id}/viewers` | Public | Get current viewers |

**Create stream example:**
```json
POST /api/streams
Authorization: Bearer TOKEN
Content-Type: application/json

{
  "title": "My Gaming Stream",
  "description": "Playing Minecraft today!"
}
```

**Stream response:**
```json
{
  "streamId": 5,
  "title": "My Gaming Stream",
  "description": "Playing Minecraft today!",
  "viewerCount": 0,
  "likeCount": 0,
  "status": "SCHEDULED",
  "thumbnailUrl": null,
  "hostId": 2,
  "hostUsername": "Dawood",
  "startedAt": "2026-04-22T12:00:00",
  "endedAt": null
}
```

---

###  Likes
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/streams/{id}/likes` | User | Toggle like or unlike |
| GET | `/api/streams/{id}/likes/count` | Public | Get total like count |

---

### 💬 Comments

| Method | Endpoint | Auth | Body | Description |
|---|---|---|---|---|
| POST | `/api/streams/{id}/comments` | User | `{message}` | Post a comment |
| GET | `/api/streams/{id}/comments` | Public | — | Get all comments |
| PUT | `/api/streams/{id}/comments/{commentId}` | Owner | `{message}` | Edit your comment |
| DELETE | `/api/streams/{id}/comments/{commentId}` | Owner | — | Delete your comment |

---

### Gifts

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/gifts` | Public | Get active gift catalog |
| POST | `/api/streams/{id}/gifts` | User | Send gift to stream |
| GET | `/api/streams/{id}/gifts` | Public | Get gift history for stream |
| GET | `/api/gifts/my-transactions` | User | Get my gift history |

**Send gift example:**
```json
POST /api/streams/5/gifts
Authorization: Bearer TOKEN

{
  "giftId": 1
}
```

**Gift transaction response:**
```json
{
  "transactionId": 10,
  "senderId": 2,
  "senderUsername": "Dawood",
  "streamId": 5,
  "giftId": 1,
  "giftName": "Rose",
  "coinsSpent": 10,
  "createdAt": "2026-04-22T12:05:00"
}
```

---

###  Admin — Admin token required

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/stats` | Platform stats |
| GET | `/api/admin/users` | All users |
| GET | `/api/admin/users/{id}` | Single user |
| POST | `/api/admin/users/{id}/coins?amount=500` | Add coins to user |
| DELETE | `/api/admin/users/{id}/soft` | Deactivate admin user |
| DELETE | `/api/admin/users/{id}` | Delete regular user |
| GET | `/api/admin/streams` | All streams |
| PUT | `/api/admin/streams/{id}/force-end` | Force end any live stream |
| DELETE | `/api/admin/streams/{id}` | Delete any stream |
| GET | `/api/admin/gifts` | All gifts including inactive |
| POST | `/api/admin/gifts` | Create new gift |
| PUT | `/api/admin/gifts/{id}` | Edit gift |
| DELETE | `/api/admin/gifts/{id}` | Deactivate gift |

**Stats response:**
```json
{
  "totalUsers": 14,
  "liveStreams": 5,
  "totalStreams": 10
}
```

**Create gift example:**
```json
POST /api/admin/gifts
Authorization: Bearer ADMIN_TOKEN

{
  "giftName": "Star",
  "coinValue": 100
}
```

---

###  Error Response Format

All errors return this JSON structure:
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Stream not found with id: 99",
  "timestamp": "2026-04-22T12:00:00"
}
```

| Status | Meaning |
|---|---|
| 400 | Bad request — invalid input |
| 401 | Unauthorized — login required or wrong password |
| 402 | Not enough coins |
| 403 | Forbidden — wrong role |
| 404 | Resource not found |
| 409 | Conflict — email taken or concurrent update |
| 500 | Server error |

---

## Concurrency Design

The platform handles thousands of simultaneous users safely using `StreamConcurrencyManager.java`.

### Techniques Used

| Technique | Where Used | Why |
|---|---|---|
| `AtomicInteger` | Viewer count, Like count | Lock-free counting — fastest option |
| `ReentrantLock` | Coin deduction per user | Multi-step operation must be atomic per user |
| `ReadWriteLock` | Stream status changes | Many can read simultaneously, only one writes |
| `Semaphore` | Gift processing | Max 10 concurrent gift senders per stream |
| `ConcurrentHashMap` | Stores all locks | Thread-safe map for multiple threads |
| `@Transactional` | Gift send, join/leave | All DB operations succeed or all rollback |
| `@Version` | User, LiveStream | Optimistic locking — detect conflicting updates |

### How counts work

```
App starts → DataSeeder loads DB counts into AtomicInteger
User joins → incrementAndGet() → count goes up instantly
User leaves → decrementAndGet() → count goes down instantly
Stream ends → final count saved to DB → AtomicInteger cleaned up
```

### Running the concurrency test

```bash
# Make sure a stream is LIVE first
# Then run ConcurrencyTest.java from IntelliJ

# It will:
# 1. Login 10 users automatically
# 2. Fire 500 likes + 500 gifts + 500 viewer joins + 500 comments simultaneously
# 3. Print live progress every 2 seconds
# 4. Show final results
```

Expected output:
```
All 10 users logged in.
Running all tests simultaneously...

[2s] Total=892  Likes=245 Gifts=12 Viewers=487 Comments=148
[4s] Total=1456 Likes=401 Gifts=18 Viewers=812 Comments=223

[DONE] Likes finished:    498
[DONE] Gifts finished:    22 sent (fails = no coins)
[DONE] Viewers finished:  1000 joined
[DONE] Comments finished: 412 posted

DONE in 8432ms — No data corruption. No deadlock.
```

---

##  Role Management

### USER role can
- Create and manage their own streams
- Go live and end their own streams
- Send gifts, like, comment
- Join streams as viewer
- Update their own profile

### ADMIN role can
- Access admin dashboard at `/admin`
- View all users and streams
- Add coins to any user
- Force end any stream
- Delete any stream or user
- Manage gift catalog

### ADMIN role CANNOT
- Create streams (blocked by backend)
- Go live (blocked by backend)
- Send gifts or comment (admin is management only)

---

## 🖼 File Upload

Supported types: **JPEG, PNG, GIF, WEBP**  
Maximum size: **10MB**  
Storage: saved in `uploads/` folder in project root  
Access URL: `http://localhost:8080/uploads/filename.jpg`

Used for:
- Profile pictures — `POST /api/users/me/profile-picture`
- Stream thumbnails — `POST /api/streams/{id}/thumbnail`

---

##  Running Concurrency Test

Place `ConcurrencyTest.java` inside:
```
src/main/java/com/livestream/livestream_api/ConcurrencyTest.java
```

Update the stream ID at the top:
```java
static Long STREAM_ID = 81L;  // change to your live stream ID
```

Start a stream first:
```
PUT http://localhost:8080/api/streams/{id}/start
Authorization: Bearer DAWOOD_TOKEN
```

Then run the test from IntelliJ — right click → Run  
Open `http://localhost:3000/streams/{id}` in browser and watch viewer count change live.

---

## Notes

- Tokens expire after **24 hours** — login again if you get 401
- Guests can join streams and be counted as viewers without any account
- Coin balance is checked before gift sending — insufficient coins returns 402
- Admin soft delete only works on users with ADMIN role — regular users are hard deleted
- Stream viewer and like counts are stored in memory while LIVE for performance, then saved to DB when stream ends
