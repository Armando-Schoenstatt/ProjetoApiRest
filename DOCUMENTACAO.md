# 📖 Documentação Técnica — Task Manager API

## Índice

1. [Visão Geral](#visão-geral)
2. [Arquitetura](#arquitetura)
3. [Estrutura de Pastas](#estrutura-de-pastas)
4. [Como Rodar](#como-rodar)
5. [Autenticação JWT](#autenticação-jwt)
6. [Endpoints — Auth](#endpoints--auth)
7. [Endpoints — Tasks](#endpoints--tasks)
8. [Modelos de Dados](#modelos-de-dados)
9. [Erros e Códigos HTTP](#erros-e-códigos-http)
10. [Testando com Swagger](#testando-com-swagger)
11. [Rodando os Testes](#rodando-os-testes)
12. [Variáveis de Ambiente](#variáveis-de-ambiente)

---

## Visão Geral

API REST para gerenciamento de tarefas pessoais. Cada usuário se registra, faz login e gerencia suas próprias tarefas com título, descrição, status e prioridade.

**Base URL:** `http://localhost:8080`

---

## Arquitetura

```
Request → JwtAuthFilter → Controller → Service → Repository → MySQL
```

- **JwtAuthFilter** — intercepta toda requisição e valida o token JWT
- **Controller** — recebe a requisição HTTP e chama o Service
- **Service** — contém a lógica de negócio
- **Repository** — acessa o banco de dados via JPA
- **MySQL** — armazena usuários e tarefas

---

## Estrutura de Pastas

```
src/main/java/com/taskmanager/api/
│
├── config/
│   ├── SecurityConfig.java       → Configuração do Spring Security (rotas públicas/privadas, JWT filter)
│   └── SwaggerConfig.java        → Configuração do Swagger com suporte a Bearer token
│
├── controller/
│   ├── AuthController.java       → Endpoints de registro e login (/api/auth/*)
│   └── TaskController.java       → Endpoints CRUD de tarefas (/api/tasks/*)
│
├── dto/
│   ├── AuthDTO.java              → Records de request/response de autenticação
│   └── TaskDTO.java              → Records de request/response de tarefas
│
├── entity/
│   ├── User.java                 → Entidade usuário (tabela: users)
│   └── Task.java                 → Entidade tarefa (tabela: tasks) com enums Status e Priority
│
├── exception/
│   └── GlobalExceptionHandler.java → Trata erros globalmente e retorna JSON padronizado
│
├── repository/
│   ├── UserRepository.java       → Queries de usuário (findByEmail, existsByEmail)
│   └── TaskRepository.java       → Queries de tarefa com filtros por status e prioridade
│
├── security/
│   ├── JwtService.java           → Gera e valida tokens JWT
│   ├── JwtAuthFilter.java        → Filtro que lê o token do header Authorization
│   └── UserDetailsServiceImpl.java → Carrega usuário do banco para o Spring Security
│
└── service/
    ├── AuthService.java          → Lógica de registro e login
    └── TaskService.java          → Lógica CRUD de tarefas com isolamento por usuário
```

---

## Como Rodar

### Pré-requisitos
- Java 17
- Maven 3.10+
- MySQL (WAMP, XAMPP ou instalação direta)

### 1. Inicia o MySQL
Abre o WAMP e garante que o MySQL está rodando (ícone verde).

### 2. Abre o terminal no VS Code
**Ctrl + `** dentro da pasta do projeto.

### 3. Define o JAVA_HOME (apenas se necessário)
```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
```

### 4. Roda a aplicação
```powershell
mvn spring-boot:run
```

### 5. Acessa
- Swagger UI: http://localhost:8080/swagger-ui.html
- API: http://localhost:8080/api

---

## Autenticação JWT

A API usa **JWT (JSON Web Token)** para autenticação stateless.

### Como funciona:
1. Usuário faz **registro** ou **login** → recebe um `token`
2. Em toda requisição protegida, envia o token no header:
   ```
   Authorization: Bearer SEU_TOKEN_AQUI
   ```
3. O servidor valida o token e identifica o usuário

### Características do token:
- Algoritmo: **HMAC-SHA256**
- Expiração: **24 horas**
- Payload contém: email do usuário

---

## Endpoints — Auth

### POST /api/auth/register
Registra um novo usuário e retorna o token JWT.

**Não requer autenticação.**

**Body:**
```json
{
  "email": "usuario@email.com",
  "name": "Nome do Usuário",
  "password": "minimo6caracteres"
}
```

**Resposta 201:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "email": "usuario@email.com",
  "name": "Nome do Usuário"
}
```

**Erros:**
- `400` — E-mail já cadastrado
- `422` — Campos inválidos (email mal formatado, senha curta, etc.)

---

### POST /api/auth/login
Autentica um usuário existente e retorna o token JWT.

**Não requer autenticação.**

**Body:**
```json
{
  "email": "usuario@email.com",
  "password": "suasenha"
}
```

**Resposta 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "email": "usuario@email.com",
  "name": "Nome do Usuário"
}
```

**Erros:**
- `401` — Credenciais inválidas

---

## Endpoints — Tasks

> Todos os endpoints abaixo **requerem autenticação** via Bearer token.
> Cada usuário acessa **somente suas próprias tarefas**.

---

### GET /api/tasks
Lista todas as tarefas do usuário autenticado.

**Parâmetros opcionais (query string):**

| Parâmetro | Valores | Exemplo |
|---|---|---|
| `status` | `PENDING`, `IN_PROGRESS`, `DONE` | `?status=PENDING` |
| `priority` | `LOW`, `MEDIUM`, `HIGH` | `?priority=HIGH` |

**Exemplos:**
```
GET /api/tasks
GET /api/tasks?status=PENDING
GET /api/tasks?priority=HIGH
```

**Resposta 200:**
```json
[
  {
    "id": 1,
    "title": "Estudar Spring Boot",
    "description": "Ver documentação oficial",
    "status": "IN_PROGRESS",
    "priority": "HIGH",
    "dueDate": "2025-12-31",
    "createdAt": "2025-01-01T10:00:00",
    "updatedAt": "2025-01-01T10:00:00"
  }
]
```

---

### GET /api/tasks/{id}
Busca uma tarefa específica pelo ID.

**Exemplo:**
```
GET /api/tasks/1
```

**Resposta 200:**
```json
{
  "id": 1,
  "title": "Estudar Spring Boot",
  "description": "Ver documentação oficial",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "dueDate": "2025-12-31",
  "createdAt": "2025-01-01T10:00:00",
  "updatedAt": "2025-01-01T10:00:00"
}
```

**Erros:**
- `404` — Tarefa não encontrada ou não pertence ao usuário

---

### POST /api/tasks
Cria uma nova tarefa.

**Body:**
```json
{
  "title": "Minha Tarefa",
  "description": "Descrição opcional",
  "status": "PENDING",
  "priority": "HIGH",
  "dueDate": "2025-12-31"
}
```

> `status` padrão: `PENDING`
> `priority` padrão: `MEDIUM`
> `description` e `dueDate` são opcionais

**Resposta 201:** retorna a tarefa criada com `id`, `createdAt` e `updatedAt`.

**Erros:**
- `422` — `title` vazio ou maior que 150 caracteres

---

### PUT /api/tasks/{id}
Atualiza uma tarefa existente.

**Exemplo:**
```
PUT /api/tasks/1
```

**Body:** mesmo formato do POST.

**Resposta 200:** retorna a tarefa atualizada.

**Erros:**
- `404` — Tarefa não encontrada
- `422` — Dados inválidos

---

### DELETE /api/tasks/{id}
Deleta uma tarefa.

**Exemplo:**
```
DELETE /api/tasks/1
```

**Resposta 204:** sem corpo.

**Erros:**
- `404` — Tarefa não encontrada

---

## Modelos de Dados

### User (tabela: users)

| Campo | Tipo | Descrição |
|---|---|---|
| id | BIGINT | Chave primária auto-incremento |
| email | VARCHAR(100) | Único, usado para login |
| name | VARCHAR(100) | Nome do usuário |
| password | VARCHAR | Senha criptografada com BCrypt |

### Task (tabela: tasks)

| Campo | Tipo | Descrição |
|---|---|---|
| id | BIGINT | Chave primária auto-incremento |
| title | VARCHAR(150) | Título da tarefa (obrigatório) |
| description | TEXT | Descrição (opcional) |
| status | ENUM | `PENDING`, `IN_PROGRESS`, `DONE` |
| priority | ENUM | `LOW`, `MEDIUM`, `HIGH` |
| dueDate | DATE | Data de vencimento (opcional) |
| createdAt | DATETIME | Preenchido automaticamente |
| updatedAt | DATETIME | Atualizado automaticamente |
| user_id | BIGINT | Chave estrangeira para users |

---

## Erros e Códigos HTTP

| Código | Significado | Quando ocorre |
|---|---|---|
| 200 | OK | Requisição bem-sucedida |
| 201 | Created | Recurso criado com sucesso |
| 204 | No Content | Deletado com sucesso |
| 400 | Bad Request | Regra de negócio violada (ex: email duplicado) |
| 401 | Unauthorized | Token ausente, inválido ou expirado |
| 403 | Forbidden | Sem permissão para o recurso |
| 404 | Not Found | Recurso não encontrado |
| 422 | Unprocessable Entity | Validação falhou (campos inválidos) |

### Formato padrão de erro:
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Tarefa não encontrada: 99",
  "timestamp": "2025-01-01T10:00:00"
}
```

### Formato de erro de validação (422):
```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": {
    "title": "não deve estar em branco",
    "email": "deve ser um endereço de e-mail bem formado"
  },
  "timestamp": "2025-01-01T10:00:00"
}
```

---

## Testando com Swagger

1. Acessa **http://localhost:8080/swagger-ui.html**
2. Expande **POST /api/auth/register**
3. Clica em **Try it out**
4. Preenche o body com seus dados e clica **Execute**
5. Copia o valor do campo `token` na resposta
6. Clica no botão **Authorize 🔒** no topo da página
7. No campo **Value**, digita: `Bearer SEU_TOKEN` e clica **Authorize**
8. Agora todos os endpoints de tarefas estão liberados para uso

---

## Rodando os Testes

Os testes usam banco H2 em memória — **não precisa do MySQL rodando**.

```powershell
mvn test
```

### Testes disponíveis:

**AuthControllerTest** (5 testes):
- Registro com sucesso retorna token
- Rejeita e-mail duplicado
- Login com credenciais válidas
- Rejeita credenciais inválidas
- Rejeita formato de e-mail inválido

**TaskControllerTest** (6 testes):
- Cria tarefa com sucesso
- Lista tarefas do usuário
- Atualiza tarefa existente
- Deleta tarefa e confirma remoção
- Retorna 401 sem token
- Filtra tarefas por status

**JwtServiceTest** (3 testes):
- Gera e valida token
- Extrai e-mail do token
- Rejeita token com usuário errado

---

## Variáveis de Ambiente

Em produção, defina as variáveis abaixo para não expor credenciais no código:

| Variável | Descrição | Padrão (dev) |
|---|---|---|
| `JWT_SECRET` | Chave secreta para assinar o JWT | valor hardcoded no properties |
| `JWT_EXPIRATION` | Expiração do token em ms | `86400000` (24h) |

**Exemplo no Linux/Mac:**
```bash
export JWT_SECRET=minha-chave-super-secreta-de-producao
mvn spring-boot:run
```

**Exemplo no Windows PowerShell:**
```powershell
$env:JWT_SECRET = "minha-chave-super-secreta-de-producao"
mvn spring-boot:run
```
