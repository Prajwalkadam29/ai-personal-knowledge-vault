# 🧠 AI Personal Knowledge Vault

An intelligent **"Second Brain"** application built with **Spring Boot 3.5.9** and **Java 21**, featuring automated technical insights and a multi-database architecture. This system doesn't just store notes; it understands them using **Llama 3.3 (Groq)** and creates a navigable knowledge graph in **Neo4j**.

## 🚀 Current Implementation (Phase 1: The Engine)

* **Multi-Tenant Security:** Full data isolation using JWT and Google OAuth2. User A can never see, search, or link to User B's knowledge.


* **Three-Brain Storage:**

    - **Relational (PostgreSQL):** Handles structured data, user profiles, and note metadata.

    - **Graph (Neo4j):** Maps conceptual links between notes and tags.

    - **Vector (Neo4j Vector Store):** Enables "Semantic Search" using AI embeddings.


* **AI Orchestration:** Integrated with Groq (Llama 3.3) for automated note summarization and tag generation.


* **Hybrid Search:** Combines SQL keyword matching with Vector-based similarity search.


* **Automated Testing:** A robust suite of 8 tests covering Unit, Service-Integration, and Security-Isolation layers.

---

## 🛠 Tech Stack

### Backend
* **Java 21** (Leveraging Virtual Threads for AI I/O).
* **Spring Boot 3.5.9**.
* **Spring AI 1.1.2** (Orchestrating LLM and Vector Store interactions).
* **Security:** Spring Security, JWT, Google OAuth2
* **PostgreSQL** (Source of Truth for raw data and metadata).
* **Neo4j** (Graph Database & Vector Store for relationships and embeddings).
* **Groq API** (Llama 3.3-70b-versatile for high-speed inference).
* **Local ONNX Transformers** (`all-MiniLM-L6-v2` for local embedding generation).

### Mobile (Upcoming)
* **Flutter** (Material 3 UI).
* **Riverpod** (State Management).
---

## 🏗 Architecture & Data Flow
The system operates on a "Three-Brain" Architecture:

1. **Memory (PostgreSQL):** Handles ACID-compliant storage of raw markdown and AI summaries.
2. **Context (Neo4j Graph):** Maps how concepts connect using a specialized knowledge graph.
3. **Intuition (Vector Index):** Uses mathematical vectors to provide human-like search capabilities.

---

## 🚦 Getting Started

### Prerequisites
* JDK 21+
* PostgreSQL 16+
* Neo4j 5.x+
* Groq API Key
* Jwt Secret Key
* Google Client Id and Google Client Secret

### Configuration

Update `src/main/resources/application.properties`:

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/knowledge_vault
spring.datasource.username=${POSTGRES_USERNAME}
spring.datasource.password=${POSTGRES_PASSWORD}

# Neo4j
spring.neo4j.uri=bolt://localhost:7687
spring.neo4j.authentication.password=${NEO4J_PASSWORD}

# Groq (AI)
spring.ai.openai.api-key=${GROQ_API_KEY}
spring.ai.openai.base-url=https://api.groq.com/openai

# JWT Security
# You can generate a random 256-bit key online or use a long string
application.security.jwt.secret-key=${JWT_SECRET_KEY}
application.security.jwt.expiration=86400000

# Add your Google credentials
spring.security.oauth2.client.registration.google.client-id=${YOUR_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${YOUR_CLIENT_SECRET}
```

### Configure Google Cloud Console

Steps to get a **Client ID** and **Secret** from Google.

1. Go to the [Google Cloud Console](https://console.cloud.google.com).

2. Create a new project named `AI-Knowledge-Vault`.

3. Go to **APIs & Services > OAuth consent screen** and configure it (External).

4. Go to Credentials > Create Credentials > OAuth client ID. 
    - **Application type:** Web application.

    - **Authorized redirect URIs:** `http://localhost:8080/login/oauth2/code/google`

5. Copy your **Client ID** and **Client Secret**.

---

## 📡 API Documentation

### 1. Authentication

| Method       | Endpoint                           | Body (JSON)                         | Description                       |
|--------------|------------------------------------|-------------------------------------|-----------------------------------|
| `POST`       | `/api/auth/register`               | `{"fullName", "email", "password"}` | Creates a new user & returns JWT. |
| `POST`       | `/api/auth/login`                  | `{"email", "password"}`             | Validates user & returns JWT.     |
| `GET`        | `/oauth2/authorization/google`     | N/A                                 | Redirects to Google Login flow.   |


### 2. Note Management (Requires Bearer Token)

| Method      | Endpoint              | Body (JSON)            | Description                                                    |
|-------------|-----------------------|------------------------|----------------------------------------------------------------|
| `POST`      | `/api/notes`          | `{"title", "content"}` | Creates note, triggers AI analysis, and syncs to Graph/Vector. |
| `GET`       | `/api/notes`          | N/A                    | Returns all notes belonging only to the logged-in user.        |
| `GET`       | `/api/notes/{id}`     | N/A                    | Returns a specific note by ID.                                 |


### 3. Knowledge & Search (Requires Bearer Token)

| Method | Endpoint              | Query Param    | Description                                                   |
|--------|-----------------------|----------------|---------------------------------------------------------------|
| `GET`  | `/api/search/hybrid`  | `?query=...`   | Returns combined Semantic and Keyword results.                |
| `GET`  | `/api/graph`          | N/A            | Returns JSON nodes and edges for private graph visualization. |

---

## 🧪 Testing
The system is guarded by a comprehensive test suite to ensure stability during future updates.

### How to run tests:
```bash
./mvnw test
```

### Test Coverage:
1. **JwtServiceTest:** Validates token generation and parsing logic.

2. **AuthenticationServiceTest:** Mocks the database to test registration business logic.

3. **NoteServiceIntegrationTest:** Verifies the full flow from Note creation to PostgreSQL persistence with DB rollbacks.

4. **AuthControllerIntegrationTest:** Ensures the Spring Security Filter Chain correctly blocks/permits endpoints.

5. **SearchSecurityIntegrationTest:** Programmatically proves that User B cannot find User A's private data.

---

## 📝 Roadmap (Phase 2: The Interface)

* **Flutter App:** Cross-platform mobile client with real-time sync.

* **Interactive Graph:** D3.js or Force-directed graph visualization in the UI.

* **Agentic Chat:** A "Vault Assistant" that can answer questions based only on your private notes using RAG.

* **Markdown Support:** Full rich-text rendering for notes.

---
