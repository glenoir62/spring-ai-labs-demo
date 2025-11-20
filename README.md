# Spring AI Demo - RAG Application with Ollama & pgvector

Spring Boot application demonstrating RAG (Retrieval-Augmented Generation) using Spring AI, Ollama LLM, and PostgreSQL pgvector for vector storage, with a Chainlit web UI.

## Features

- **RAG System**: Query PDF documents with semantic context
- **Spring AI**: AI integration framework for Spring Boot
- **Ollama**: Local LLM (Llama 3.1)
- **pgvector**: PostgreSQL vector database for semantic search
- **REST API**: Endpoints for chatbot interaction
- **Chainlit UI**: Modern web interface for chatbot conversations

## Prerequisites

- **Java 21+**
- **Python 3.8+** (for Chainlit frontend)
- **Docker & Docker Compose**
- **Ollama** installed locally

### Install Ollama

**Linux/WSL:**
```bash
curl -fsSL https://ollama.com/install.sh | sh
```

**macOS:**
```bash
brew install ollama
```

**Windows:** Download from [ollama.com](https://ollama.com/download)

### Download Required Models

```bash
# Chat model (~4.7 GB)
ollama pull llama3.1:8b

# Embedding model (~274 MB)
ollama pull nomic-embed-text

# Verify installation
ollama list
```

## Quick Start

### 1. Start PostgreSQL with pgvector

```bash
docker compose up -d
```

### 2. Ensure Ollama is Running

```bash
# Linux/WSL
ollama serve

# macOS/Windows runs automatically
```

Verify Ollama is accessible:
```bash
curl http://localhost:11434/api/tags
```

### 3. Start the Spring Boot Application

```bash
# Linux/macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

The application starts on port **8080**.

### 4. Start the Chainlit Frontend (Optional)

```bash
cd src/main/resources/front

# Create virtual environment
python -m venv venv

# Activate virtual environment
# Linux/macOS:
source venv/bin/activate
# Windows:
venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Start Chainlit
chainlit run app.py
```

The Chainlit UI will be available at **http://localhost:8000**

## API Usage

### Load PDF Documents

```bash
curl -X POST http://localhost:8080/api/v1/ollama/load-documents
```

### Ask a Question (with RAG)

```bash
curl -X POST http://localhost:8080/api/v1/ollama/ask \
  -H "Content-Type: application/json" \
  -d '{"message": "What is the refund policy?"}'
```

### Simple Chat (without RAG)

```bash
curl -X POST http://localhost:8080/api/v1/ollama/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello, how are you?"}'
```

## Using the Chainlit Interface

1. Open your browser to **http://localhost:8000**
2. Type your question in the chat input
3. The system automatically maintains conversation context with a unique session ID
4. Responses are generated using the Spring Boot backend with RAG capabilities

### Chainlit Features

- **Session Management**: Each conversation has a unique context ID
- **Real-time Streaming**: See responses as they're generated
- **Modern UI**: Clean, responsive interface
- **Error Handling**: Clear error messages for troubleshooting

## Configuration

### Spring Boot (application.yml)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: postgres
  ai:
    ollama:
      base-url: http://localhost:11434
      embedding:
        options:
          model: nomic-embed-text
      chat:
        options:
          model: llama3.1:8b
          temperature: 0.7
```

### Chainlit (app.py)

The Chainlit app connects to the Spring Boot backend:

```python
endpoint = 'http://localhost:8080/ai/callWithContext'
```

Modify this endpoint to match your API route if different.

## Project Structure

```
spring-ai-demo/
├── src/main/
│   ├── java/com/gleo/labs/
│   │   ├── controller/          # REST API endpoints
│   │   ├── service/             # Business logic
│   │   └── config/              # Configuration classes
│   └── resources/
│       ├── application.yml      # Spring Boot config
│       └── front/               # Chainlit frontend
│           ├── app.py           # Chainlit application
│           ├── requirements.txt # Python dependencies
│           └── public/          # UI assets (logos)
├── compose.yaml                 # Docker Compose for pgvector
├── pom.xml                      # Maven dependencies
└── README.md
```

## Troubleshooting

**Ollama connection refused:**
```bash
curl http://localhost:11434/api/tags
ollama serve  # if not running
```

**Database connection failed:**
```bash
docker compose ps
docker compose restart
```

**Model not found:**
```bash
ollama list
ollama pull llama3.1:8b
ollama pull nomic-embed-text
```

**Port 8080 already in use:**
Add to `application.yml`:
```yaml
server:
  port: 8081
```

Then update the Chainlit `app.py` endpoint accordingly.

**Chainlit not starting:**
```bash
# Ensure you're in the correct directory
cd src/main/resources/front

# Reinstall dependencies
pip install --upgrade chainlit

# Check Python version (needs 3.8+)
python --version
```

**Chainlit can't connect to backend:**
- Ensure Spring Boot is running on port 8080
- Check the endpoint URL in `app.py`
- Verify no firewall is blocking localhost connections

## Stop Services

```bash
# Stop Spring Boot: Ctrl+C in Spring Boot terminal

# Stop Chainlit: Ctrl+C in Chainlit terminal

# Stop PostgreSQL
docker compose down

# Stop Ollama (Linux/WSL)
pkill ollama
```

## Build JAR

```bash
./mvnw clean package
java -jar target/spring-ai-labs-demo-0.0.1-SNAPSHOT.jar
```

## Tech Stack

### Backend
- Spring Boot 3.5.7
- Spring AI 1.0.0-M6
- Ollama (Llama 3.1)
- PostgreSQL + pgvector
- Java 21

### Frontend
- Chainlit 2.1.1
- Python 3.8+

## Resources

- [Spring AI Docs](https://docs.spring.io/spring-ai/reference/)
- [Chainlit Docs](https://docs.chainlit.io/)
- [Ollama](https://github.com/ollama/ollama)
- [pgvector](https://github.com/pgvector/pgvector)