# SpringDDDSagaMicroserviceTransactional
I have made a basic spring app that made by saga pattern transactional process kafka event driven design and domain driven design, it is amateurish and simple
# Distributed Order & Notification Microservices System  
**A Professional Microservices Architecture using DDD, Event-Driven Design, Kafka, Saga Pattern, and Outbox Pattern**

---

## Table of Contents

- [Overview](#overview)
- [System Architecture](#system-architecture)
- [Key Concepts & Patterns](#key-concepts--patterns)
- [Technology Stack](#technology-stack)
- [Service Responsibilities](#service-responsibilities)
- [Event Flow & Saga Coordination](#event-flow--saga-coordination)
- [How to Run](#how-to-run)
- [Project Structure](#project-structure)
- [Extensibility & Production Considerations](#extensibility--production-considerations)
- [License](#license)
- [Türkçe Açıklama](#türkçe-açıklama)

---

## Overview

This project demonstrates a highly decoupled, robust, and scalable microservices system for processing orders and sending notifications.  
It leverages:

- **Domain-Driven Design (DDD)**
- **Event-Driven Architecture**
- **Apache Kafka** for asynchronous communication
- **Saga Pattern** for distributed transaction coordination
- **Outbox Pattern** for reliable event delivery

### Business Scenario

- **Order Service** receives and processes customer orders.
- **Notification Service** asynchronously receives order completion events and triggers notifications to end users.

---

## System Architecture

```mermaid
graph TD
    A[API Request: Create Order] -->|REST| B[Order Service]
    B -->|Transactional Save (Order, OutboxEvent)| C[Database]
    C -->|Scheduled Outbox Publisher| D[Kafka: OrderCreatedEvent]
    D -->|Kafka Consumer| B
    B -->|Order Status: COMPLETED, NotificationEvent| E[Kafka: NotificationEvent]
    E -->|Kafka Consumer| F[Notification Service]
    F -->|Notification Logging / Future: Email/SMS| G[User/Log]
```

- **Order Service** manages lifecycle and state of orders, orchestrates saga, ensures ACID properties within its own DB.
- **Notification Service** is completely decoupled; only reacts to events.

---

## Key Concepts & Patterns

### Domain-Driven Design (DDD)

- Clear separation between domain logic, application orchestration, infrastructure, and API layers.
- Aggregates, Value Objects, and Domain Events are used where relevant.

### Event-Driven Architecture

- Services communicate asynchronously via Kafka topics.
- Decouples microservices and improves scalability.

### Saga Pattern

- Orchestrates multi-step, distributed transactions without 2PC (two-phase commit).
- Ensures eventual consistency between microservices.

### Outbox Pattern

- Ensures that domain events are only published if the local transaction commits successfully.
- Scheduled publisher reads unpublished events and sends them to Kafka reliably.

---

## Technology Stack

- **Java 17+**
- **Spring Boot** (`@SpringBootApplication`, Scheduling, JPA, Kafka)
- **Lombok** (for concise model code)
- **Apache Kafka** (event streaming and decoupling)
- **H2/PostgreSQL/MySQL** (pluggable; for demo, any JPA-compatible DB)
- **Docker Compose** (recommended for local Kafka)

---

## Service Responsibilities

### Order Service

- **API Layer:** Exposes REST endpoints for creating and querying orders.
- **Application Layer:** Saga Orchestrator manages transactional workflow and event creation.
- **Domain Layer:** Encapsulates business logic for order lifecycle.
- **Infrastructure Layer:** JPA repositories for persistence, Kafka interaction.
- **Outbox Mechanism:** Persists events as part of the transaction, schedules their publication.

### Notification Service

- **Kafka Consumer:** Listens for `NotificationEvent`s.
- **Deserialization:** Uses a utility for robust JSON mapping.
- **Processing:** Logs notifications; can be extended to integrate with email/SMS/push platforms.

---

## Event Flow & Saga Coordination

1. **Order Creation:**  
   - `POST /orders/create` triggers saga.
   - Order is persisted with status `CREATED` (or PENDING).
   - Corresponding `OrderCreatedEvent` is stored in outbox table.

2. **Outbox Publishing:**  
   - Scheduled task scans unpublished events and publishes them to Kafka.
   - Marks events as published only after successful send.

3. **Order Event Handling:**  
   - Order Service consumes its own `OrderCreatedEvent`.
   - Updates order status to `COMPLETED`.
   - Publishes `NotificationEvent` to Kafka.

4. **Notification:**  
   - Notification Service consumes `NotificationEvent` from Kafka.
   - Logs the notification (future: triggers user notification channels).

**Advantages:**  
- Atomicity is preserved within each service; distributed coordination is event-driven, not locking.
- Outbox pattern prevents event loss and double-publishing.
- Saga pattern prevents distributed transaction inconsistencies.

---

## How to Run

### Prerequisites

- Java 17+
- Kafka (local or via Docker)
- Relational Database (H2 by default; switchable)
- Maven or Gradle

### Setup Steps

1. **Start Kafka Broker**

   - Using Docker:
     ```bash
     docker-compose up -d  # with a suitable docker-compose.yml
     ```

2. **Start Order Service**

   - Build and run `org.example.App` (with scheduling enabled).

3. **Start Notification Service**

   - Build and run NotificationService main class.

4. **Create an Order**

   - Send a POST request to:
     ```
     POST /orders/create
     Content-Type: application/json
     {
         "customerName": "Alice Smith",
         "productName": "Gaming Mouse"
     }
     ```

5. **Observe the Flow**

   - Order Service logs order creation and event publishing.
   - Notification Service logs notification event receipt.

---

## Project Structure

```
order-service/
  ├── api/
  │   └── OrderController.java
  ├── application/
  │   └── OrderSagaOrchestrator.java
  ├── domain/
  │   ├── aggregate/Order.java
  │   └── model/OrderStatus.java
  ├── event/
  │   ├── OrderCreatedEvent.java
  │   └── NotificationEvent.java
  ├── infrastructure/
  │   └── OrderRepository.java
  ├── outbox/
  │   ├── OutboxEvent.java
  │   ├── OutboxEventPublisher.java
  │   └── OutboxEventRepository.java
  └── App.java (main)
notification-service/
  ├── Json/
  │   └── JsonUtil.java
  ├── event/
  │   └── NotificationEvent.java
  └── kafka_consumer/order/
      └── NotificationEventConsumer.java
