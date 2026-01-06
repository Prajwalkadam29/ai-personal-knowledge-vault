# 🧠 AI Personal Knowledge Vault

An intelligent **"Second Brain"** application built with **Spring Boot 3.5.9** and **Java 21**, featuring automated technical insights and a multi-database architecture. This system doesn't just store notes; it understands them using **Llama 3.3 (Groq)** and creates a navigable knowledge graph in **Neo4j**.

## 🚀 Key Features

* **AI-Powered Summarization:** Automatically generates concise technical summaries for every note using Groq's Llama 3.3 model.


* **Automated Tagging:** Extracts relevant technical tags (e.g., #Java, #Microservices) without manual input.


* **Semantic Search:** Finds notes based on meaning and concepts rather than exact keywords using **Vector Embeddings**.


* **Concept Linking:** Automatically detects and creates physical relationships (`RELATED_TO`) between conceptually similar notes.


* **Graph Visualization API:** Exposes a complex node-edge data structure to render interactive knowledge maps.


* **Hybrid Search:** Combines traditional PostgreSQL keyword matching with Neo4j vector similarity for high-precision retrieval.


* **Smart Recommendations:** Suggests new technical topics based on existing knowledge clusters using Cypher graph traversal.
---

## 🛠 Tech Stack

### Backend
* **Java 21** (Leveraging Virtual Threads for AI I/O).
* **Spring Boot 3.5.9**.
* **Spring AI 1.1.2** (Orchestrating LLM and Vector Store interactions).
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


### Configuration

Update `src/main/resources/application.properties`:

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/knowledge_vault
spring.datasource.username=your_user
spring.datasource.password=your_pass

# Neo4j
spring.neo4j.uri=bolt://localhost:7687
spring.neo4j.authentication.password=your_pass

# Groq (AI)
spring.ai.openai.api-key=your_groq_key
spring.ai.openai.base-url=https://api.groq.com/openai
```

---

## 📡 API Endpoints (Core)

| Method     |             Endpoint                | Description                              |
|------------|-------------------------------------|------------------------------------------|
| `POST`     | `/api/notes`                        | Create a note (triggers AI & Graph Sync) |
| `GET`      | `/api/search/hybrid`                | Perform semantic + keyword search        |
| `GET`      | `/api/graph`                        | Get node-edge data for visualization     |
| `GET`      | `/api/recommendations/topics/{id}`  | Get graph-based topic suggestions        |

---

## 📝 Future Roadmap

* OAuth2 & JWT User Authentication.
* Multi-user data isolation.
* Real-time knowledge graph rendering in Flutter.

---
