# 🏗️ HEXAGONAL ARCHITECTURE - COMPLETE STRUCTURE

## 📁 Project Structure

```
online-khatta/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── khatta/
│   │   │           │
│   │   │           ├── domain/                    # 🔵 DOMAIN LAYER (Core/Hexagon)
│   │   │           │   │                          # Pure Java - NO Spring Dependencies
│   │   │           │   │
│   │   │           │   ├── model/                 # Domain Entities & Value Objects
│   │   │           │   │   ├── entity/
│   │   │           │   │   │   ├── User.java
│   │   │           │   │   │   ├── Group.java
│   │   │           │   │   │   ├── Expense.java
│   │   │           │   │   │   ├── ExpenseSplit.java
│   │   │           │   │   │   └── Settlement.java
│   │   │           │   │   │
│   │   │           │   │   └── valueobject/
│   │   │           │   │       ├── Money.java
│   │   │           │   │       ├── Email.java
│   │   │           │   │       ├── UserId.java
│   │   │           │   │       ├── GroupId.java
│   │   │           │   │       └── ExpenseId.java
│   │   │           │   │
│   │   │           │   ├── service/               # Domain Services (pure business logic)
│   │   │           │   │   ├── BalanceCalculator.java
│   │   │           │   │   ├── ExpenseSplitter.java
│   │   │           │   │   └── SettlementOptimizer.java
│   │   │           │   │
│   │   │           │   ├── port/                  # Port Interfaces
│   │   │           │   │   │
│   │   │           │   │   ├── in/                # INCOMING PORTS (Use Cases)
│   │   │           │   │   │   ├── CreateGroupUseCase.java
│   │   │           │   │   │   ├── AddExpenseUseCase.java
│   │   │           │   │   │   ├── GetGroupBalanceUseCase.java
│   │   │           │   │   │   ├── CalculateSettlementsUseCase.java
│   │   │           │   │   │   └── RegisterUserUseCase.java
│   │   │           │   │   │
│   │   │           │   │   └── out/               # OUTGOING PORTS (Repository interfaces)
│   │   │           │   │       ├── UserRepository.java
│   │   │           │   │       ├── GroupRepository.java
│   │   │           │   │       ├── ExpenseRepository.java
│   │   │           │   │       └── SettlementRepository.java
│   │   │           │   │
│   │   │           │   └── exception/             # Domain Exceptions
│   │   │           │       ├── DomainException.java
│   │   │           │       ├── UserNotFoundException.java
│   │   │           │       ├── GroupNotFoundException.java
│   │   │           │       ├── InsufficientFundsException.java
│   │   │           │       └── InvalidExpenseException.java
│   │   │           │
│   │   │           │
│   │   │           ├── application/               # 🟢 APPLICATION LAYER
│   │   │           │   │                          # Orchestration, Use Case Implementation
│   │   │           │   │
│   │   │           │   ├── service/               # Use Case Implementations
│   │   │           │   │   ├── CreateGroupService.java
│   │   │           │   │   ├── AddExpenseService.java
│   │   │           │   │   ├── GetGroupBalanceService.java
│   │   │           │   │   ├── CalculateSettlementsService.java
│   │   │           │   │   └── RegisterUserService.java
│   │   │           │   │
│   │   │           │   ├── dto/                   # Application DTOs (Input/Output)
│   │   │           │   │   ├── request/
│   │   │           │   │   │   ├── CreateGroupRequest.java
│   │   │           │   │   │   ├── AddExpenseRequest.java
│   │   │           │   │   │   └── RegisterUserRequest.java
│   │   │           │   │   │
│   │   │           │   │   └── response/
│   │   │           │   │       ├── GroupResponse.java
│   │   │           │   │       ├── ExpenseResponse.java
│   │   │           │   │       ├── BalanceResponse.java
│   │   │           │   │       └── SettlementResponse.java
│   │   │           │   │
│   │   │           │   └── mapper/                # DTO ↔ Domain Mappers
│   │   │           │       ├── GroupMapper.java
│   │   │           │       ├── ExpenseMapper.java
│   │   │           │       └── UserMapper.java
│   │   │           │
│   │   │           │
│   │   │           └── infrastructure/            # 🟡 INFRASTRUCTURE LAYER
│   │   │               │                          # Adapters (Input & Output)
│   │   │               │
│   │   │               ├── adapter/
│   │   │               │   │
│   │   │               │   ├── in/                # INPUT ADAPTERS
│   │   │               │   │   │
│   │   │               │   │   └── rest/          # REST Controllers
│   │   │               │   │       ├── GroupController.java
│   │   │               │   │       ├── ExpenseController.java
│   │   │               │   │       ├── UserController.java
│   │   │               │   │       ├── AuthController.java
│   │   │               │   │       └── SettlementController.java
│   │   │               │   │
│   │   │               │   └── out/               # OUTPUT ADAPTERS
│   │   │               │       │
│   │   │               │       ├── persistence/   # Database Adapters
│   │   │               │       │   ├── UserJdbcRepository.java
│   │   │               │       │   ├── GroupJdbcRepository.java
│   │   │               │       │   ├── ExpenseJdbcRepository.java
│   │   │               │       │   ├── SettlementJdbcRepository.java
│   │   │               │       │   │
│   │   │               │       │   ├── mapper/    # Row Mappers
│   │   │               │       │   │   ├── UserRowMapper.java
│   │   │               │       │   │   ├── GroupRowMapper.java
│   │   │               │       │   │   └── ExpenseRowMapper.java
│   │   │               │       │   │
│   │   │               │       │   └── entity/    # JPA-style entities (if needed)
│   │   │               │       │       └── (optional, for complex mappings)
│   │   │               │       │
│   │   │               │       └── external/      # External APIs (future)
│   │   │               │           └── (payment gateways, notifications)
│   │   │               │
│   │   │               │
│   │   │               ├── config/                # Spring Configuration
│   │   │               │   ├── BeanConfig.java
│   │   │               │   ├── DatabaseConfig.java
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   ├── SwaggerConfig.java
│   │   │               │   └── JdbcConfig.java
│   │   │               │
│   │   │               ├── security/              # Security Components
│   │   │               │   ├── JwtTokenProvider.java
│   │   │               │   ├── JwtAuthenticationFilter.java
│   │   │               │   ├── UserDetailsServiceImpl.java
│   │   │               │   └── SecurityUtils.java
│   │   │               │
│   │   │               └── exception/             # Global Exception Handling
│   │   │                   ├── GlobalExceptionHandler.java
│   │   │                   ├── ApiError.java
│   │   │                   └── ErrorCode.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       │
│   │       └── db/
│   │           └── migration/                     # Flyway Migrations
│   │               ├── V1__create_users_table.sql
│   │               ├── V2__create_groups_table.sql
│   │               ├── V3__create_expenses_table.sql
│   │               ├── V4__create_expense_splits_table.sql
│   │               └── V5__create_settlements_table.sql
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── khatta/
│                   ├── domain/                    # Domain Tests
│                   │   ├── service/
│                   │   └── model/
│                   │
│                   ├── application/               # Application Tests
│                   │   └── service/
│                   │
│                   └── infrastructure/            # Integration Tests
│                       ├── adapter/
│                       └── rest/
│
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
│
├── pom.xml
└── README.md
```

---

## 🎯 Layer Responsibilities

### 🔵 DOMAIN LAYER (Core)
- **Purpose**: Pure business logic, NO framework dependencies
- **Contains**: Entities, Value Objects, Domain Services, Port Interfaces
- **Rules**:
    - No Spring annotations
    - No database annotations
    - No HTTP/REST concepts
    - Pure Java objects
    - Framework-agnostic

### 🟢 APPLICATION LAYER
- **Purpose**: Orchestrate domain operations, implement use cases
- **Contains**: Use Case implementations, DTOs, Mappers
- **Rules**:
    - Can depend on Domain layer
    - Implements incoming ports
    - Uses outgoing ports
    - Transaction boundaries defined here

### 🟡 INFRASTRUCTURE LAYER
- **Purpose**: Implement technical details, adapters
- **Contains**: Controllers, Repositories, Config, Security
- **Rules**:
    - Implements outgoing ports (repositories)
    - Adapts external calls to domain
    - All framework code lives here
    - No business logic

---

## 🔄 Dependency Flow

```
Infrastructure (Adapters) 
    ↓ depends on
Application (Use Cases)
    ↓ depends on
Domain (Core)
```

**Key Rule**: Dependencies point INWARD only!
- Domain knows nothing about outer layers
- Application knows Domain but not Infrastructure
- Infrastructure knows everything

---

## 🚀 Key Benefits

1. **Testability**: Domain logic testable without Spring/DB
2. **Flexibility**: Easy to swap databases, frameworks
3. **Maintainability**: Clear separation of concerns
4. **Scalability**: Easy to split into microservices later
5. **Team Collaboration**: Teams can work on different layers independently

---

## 📝 Naming Conventions

### Ports (Interfaces)
- **Incoming Ports**: `*UseCase` (e.g., `CreateGroupUseCase`)
- **Outgoing Ports**: `*Repository`, `*Gateway` (e.g., `UserRepository`)

### Implementations
- **Use Cases**: `*Service` (e.g., `CreateGroupService`)
- **Adapters**: `*Jdbc*`, `*Rest*`, `*Jpa*` (e.g., `UserJdbcRepository`)

### DTOs
- **Requests**: `*Request` (e.g., `CreateGroupRequest`)
- **Responses**: `*Response` (e.g., `GroupResponse`)

---

## ✅ Next Steps

I'll now provide:
1. ✅ Complete **Domain Layer** with all entities and ports
2. ✅ Complete **Application Layer** with use case implementations
3. ✅ Complete **Infrastructure Layer** with JDBC repositories
4. ✅ Complete **Auth Module** with JWT
5. ✅ Database schema and Flyway migrations
6. ✅ Docker setup with PostgreSQL

Ready to build the cleanest backend you've ever seen! 🔥
