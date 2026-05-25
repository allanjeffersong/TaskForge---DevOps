# ⚡ TaskForge

Sistema de gerenciamento de tarefas pessoais e colaborativas desenvolvido como projeto final da disciplina de DevOps — Centro Universitário UNIESP.

## 👥 Equipe

| Desenvolvedor | Responsabilidade |
|---|---|
| Allan Jefferson | DevOps, Back-end (Controller), Pipeline CI/CD |
| Aucemi Santos | Back-end (Entidade, Service, Testes) |
| Aluilson Bezerra | Front-end (React, CRUD, Estilização) |

---

## 🛠️ Tecnologias

| Camada | Tecnologia |
|---|---|
| Back-end | Java 17 + Spring Boot 3.2.5 |
| Front-end | React 18 |
| Banco de dados | PostgreSQL 15 |
| Containerização | Docker + Docker Compose |
| Controle de versão | Git + GitFlow |
| CI/CD | GitHub Actions |
| Qualidade de código | SonarCloud + JaCoCo |

---

## 📋 Funcionalidades

- ✅ Cadastro e listagem de tarefas
- ✅ Edição de tarefas existentes
- ✅ Remoção de tarefas com confirmação
- ✅ Definição de prioridade (Baixa, Média, Alta)
- ✅ Controle de status (Pendente, Em Andamento, Concluída)
- ✅ Filtros por status e prioridade
- ✅ Cards de estatística em tempo real
- ✅ Interface responsiva

---

## 🚀 Como executar

### Pré-requisitos

- [Docker](https://www.docker.com/products/docker-desktop) e Docker Compose instalados
- Ou: Java 17 + Node.js 24 + PostgreSQL instalados localmente

---

### Opção 1 — Com Docker (recomendado)

Clone o repositório:

```bash
git clone https://github.com/allanjeffersong/TaskForge---DevOps.git
cd TaskForge---DevOps
```

Suba todos os serviços:

```bash
docker-compose up --build
```

Acesse:
- **Front-end:** http://localhost:5173
- **Back-end (API):** http://localhost:8080/api/tasks

---

### Opção 2 — Sem Docker (local)

#### 1. Banco de dados

Instale o PostgreSQL e crie o banco:

```sql
CREATE USER taskforge WITH PASSWORD 'taskforge';
CREATE DATABASE taskforge OWNER taskforge;
GRANT ALL PRIVILEGES ON DATABASE taskforge TO taskforge;
```

#### 2. Back-end

```bash
cd backend
./mvnw spring-boot:run
```

O servidor sobe em: http://localhost:8080

#### 3. Front-end

```bash
cd frontend
npm install
npm start
```

A aplicação abre em: http://localhost:3000

---

## 🔗 Endpoints da API

| Método | Endpoint | Descrição |
|---|---|---|
| GET | /api/tasks | Lista todas as tarefas |
| GET | /api/tasks/{id} | Busca tarefa por ID |
| POST | /api/tasks | Cria nova tarefa |
| PUT | /api/tasks/{id} | Atualiza tarefa |
| DELETE | /api/tasks/{id} | Remove tarefa |

### Filtros disponíveis

```
GET /api/tasks?status=PENDENTE
GET /api/tasks?priority=ALTA
GET /api/tasks?status=EM_ANDAMENTO&priority=MEDIA
```

### Exemplo de payload (POST/PUT)

```json
{
  "title": "Estudar para a prova",
  "description": "Revisar os conteúdos de DevOps",
  "priority": "ALTA",
  "status": "PENDENTE",
  "dueDate": "2026-06-01"
}
```

---

## 🧪 Rodando os testes

```bash
cd backend
./mvnw test
```

O relatório de cobertura é gerado em:
```
backend/target/site/jacoco/index.html
```

---

## 🌿 GitFlow — Estrutura de branches

```
main          ← versão de produção (tag v1.0.0)
develop       ← integração contínua
├── feature/backend-task-entity     (Aucemi)
├── feature/backend-controller      (Allan)
├── feature/backend-tests           (Aucemi)
├── feature/frontend-crud           (Aluilson)
├── feature/devops-pipeline         (Allan)
├── fix/sonar-code-duplication      (Allan)
└── release/v1.0.0                  (Allan)
```

---

## ⚙️ Pipeline CI/CD

A pipeline é disparada automaticamente em todo push e pull request para `main` e `develop`.

**Jobs executados:**

1. **Build, Test e SonarCloud**
    - Compila o projeto Java
    - Roda os testes unitários e de integração
    - Gera relatório de cobertura com JaCoCo
    - Envia análise para o SonarCloud

2. **Build do Front-end**
    - Instala dependências npm
    - Executa o build do React

---

## 📊 Qualidade de código

Análise disponível em:
**https://sonarcloud.io/project/overview?id=allanjeffersong_TaskForge---DevOps**

- ✅ Quality Gate: aprovado
- ✅ Bugs: 0
- ✅ Vulnerabilidades: 0
- ✅ Security Rating: A

---

## 📁 Estrutura do projeto

```
TaskForge---DevOps/
├── .github/
│   └── workflows/
│       └── ci.yml
├── backend/
│   ├── src/
│   │   ├── main/java/com/taskforge/backend/
│   │   │   ├── controller/TaskController.java
│   │   │   ├── dto/TaskDTO.java
│   │   │   ├── entity/Task.java
│   │   │   ├── exception/
│   │   │   ├── repository/TaskRepository.java
│   │   │   └── service/TaskService.java
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── App.js
│   │   ├── App.css
│   │   └── taskService.js
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml
└── sonar-project.properties
```