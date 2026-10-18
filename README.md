# 📋 Task Manager API

> API REST completa para gerenciamento de tarefas com autenticação JWT, construída com Spring Boot 3 + MySQL.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green?style=flat-square&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Auth-black?style=flat-square&logo=jsonwebtokens)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?style=flat-square&logo=swagger)
![Tests](https://img.shields.io/badge/Tests-MockMvc-success?style=flat-square)

---

## ✨ Funcionalidades

- 🔐 **Autenticação JWT** — Registro e login com token Bearer
- ✅ **CRUD de Tarefas** — Criar, listar, atualizar e deletar tarefas
- 🔍 **Filtros** — Filtrar tarefas por `status` (PENDING, IN_PROGRESS, DONE) e `priority` (LOW, MEDIUM, HIGH)
- 🛡️ **Isolamento por usuário** — Cada usuário acessa apenas suas próprias tarefas
- 📄 **Swagger UI** — Documentação interativa da API
- 🧪 **Testes automatizados** — Testes de integração e unitários com MockMvc e H2

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.2.5 | Framework web |
| Spring Security | 6 | Autenticação e autorização |
| Spring Data JPA | 3.2 | Persistência de dados |
| MySQL | 8.0 | Banco de dados principal |
| JJWT | 0.12.5 | Geração e validação de tokens JWT |
| SpringDoc OpenAPI | 2.5 | Documentação Swagger |
| Lombok | latest | Redução de boilerplate |
| H2 | latest | Banco em memória para testes |
| JUnit 5 + MockMvc | latest | Testes automatizados |

---

## 🚀 Como rodar

### Pré-requisitos

- Java 17+
- Maven 3.8+
- MySQL 8.0 rodando localmente

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/task-manager-api.git
cd task-manager-api
```

### 2. Configure o banco de dados

Crie o banco (ou deixe o Spring criar automaticamente):

```sql
CREATE DATABASE taskmanager;
```

Edite `src/main/resources/application.properties` com suas credenciais:

```properties
spring.datasource.username=root
spring.datasource.password=sua_senha
```

### 3. Execute a aplicação

```bash
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

### 4. Acesse o Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

## 🧪 Rodando os testes

Os testes usam banco H2 em memória — não precisa de MySQL rodando.

```bash
mvn test
```

---

## 📡 Endpoints

### Auth

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Registrar novo usuário | ❌ |
| POST | `/api/auth/login` | Login e obter token JWT | ❌ |

### Tasks

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| GET | `/api/tasks` | Listar tarefas (filtros opcionais) | ✅ |
| GET | `/api/tasks/{id}` | Buscar tarefa por ID | ✅ |
| POST | `/api/tasks` | Criar nova tarefa | ✅ |
| PUT | `/api/tasks/{id}` | Atualizar tarefa | ✅ |
| DELETE | `/api/tasks/{id}` | Deletar tarefa | ✅ |

**Filtros disponíveis em GET /api/tasks:**
- `?status=PENDING` | `IN_PROGRESS` | `DONE`
- `?priority=LOW` | `MEDIUM` | `HIGH`

---

## 📦 Exemplos de uso

### Registrar usuário

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@email.com","name":"João Silva","password":"senha123"}'
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "email": "user@email.com",
  "name": "João Silva"
}
```

### Criar tarefa

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Estudar Spring Boot","priority":"HIGH","status":"IN_PROGRESS","dueDate":"2025-12-31"}'
```

**Resposta:**
```json
{
  "id": 1,
  "title": "Estudar Spring Boot",
  "description": null,
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "dueDate": "2025-12-31",
  "createdAt": "2025-01-01T10:00:00",
  "updatedAt": "2025-01-01T10:00:00"
}
```

---

## 🗂️ Estrutura do projeto

```
src/
├── main/java/com/taskmanager/api/
│   ├── config/          # SecurityConfig, SwaggerConfig
│   ├── controller/      # AuthController, TaskController
│   ├── dto/             # AuthDTO, TaskDTO (Records)
│   ├── entity/          # User, Task
│   ├── exception/       # GlobalExceptionHandler
│   ├── repository/      # UserRepository, TaskRepository
│   ├── security/        # JwtService, JwtAuthFilter
│   └── service/         # AuthService, TaskService
└── test/java/com/taskmanager/api/
    ├── AuthControllerTest.java
    ├── TaskControllerTest.java
    └── JwtServiceTest.java
```

---

## 🔒 Segurança

- Senhas armazenadas com **BCrypt**
- Tokens JWT assinados com **HMAC-SHA256**
- Expiração do token: **24 horas**
- Cada usuário acessa **somente suas próprias tarefas**
- Rotas protegidas retornam **401** sem token válido

---

## 📝 Licença

MIT License — sinta-se livre para usar e modificar.
