# Real-Time Notification Service

Event-driven notification engine consuming Kafka topics to deliver real-time alerts via WebSocket, email, and SMS channels. Achieves sub-second delivery latency with dead-letter queue handling and retry mechanisms for fault tolerance.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.x**
- **Apache Kafka** - Event streaming
- **WebSocket (STOMP)** - Real-time push
- **PostgreSQL 15** - Persistence
- **Docker** - Containerization

## Architecture

```
                    +------------------+
                    |   REST API       |
                    |  (Publish/Query) |
                    +--------+---------+
                             |
                             v
                    +------------------+
                    |  Kafka Topic     |
                    |  "notifications" |
                    +--------+---------+
                             |
              +--------------+--------------+
              |                             |
              v                             v
    +------------------+          +------------------+
    | Notification     |          | Dead Letter      |
    | Consumer         |          | Consumer         |
    | (3 retries)      |  ---X---> | "notifications   |
    +--------+---------+          |  .DLT"           |
              |                    +--------+---------+
              |                             |
              v                             v
    +------------------+          +------------------+
    | Dispatcher       |          | Update DB:       |
    | (route by        |          | status=FAILED    |
    |  channel)        |          +------------------+
    +--------+---------+
              |
    +---------+---------+---------+
    |         |         |         |
    v         v         v         v
+--------+ +--------+ +--------+
|WebSocket| | Email  | |  SMS   |
|/topic/  | |JavaMail| |(log)   |
|notif/{id}| |        | |        |
+--------+ +--------+ +--------+
```

**Flow:**
1. Events published to `notifications` topic via REST API or upstream producers
2. Consumer processes events, persists to DB, routes to channel-specific services
3. On failure: 3 retries with 1s backoff, then forwarded to `notifications.DLT`
4. DLT consumer logs failed events and updates DB status to FAILED

## Prerequisites

- Docker & Docker Compose
- Java 17 (for local development)
- Maven 3.8+

## How to Run

### Full Stack (Docker Compose)

```bash
docker-compose up -d
```

Services:
- **App**: http://localhost:8080
- **Kafka**: localhost:9092
- **PostgreSQL**: localhost:5432
- **Zookeeper**: localhost:2181

### Local Development

1. Start infrastructure:
```bash
docker-compose up -d zookeeper kafka postgres
```

2. Run the application:
```bash
mvn spring-boot:run
```

### Build JAR

```bash
mvn clean package
java -jar target/notification-service-1.0.0.jar
```

## API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/v1/notifications | Publish notification event to Kafka |
| GET | /api/v1/notifications/{userId} | Get notification history for user |
| GET | /api/v1/notifications/{id}/status | Get delivery status by ID |

### Publish Notification Example

```bash
curl -X POST http://localhost:8080/api/v1/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user-123",
    "channel": "WEBSOCKET",
    "title": "Alert",
    "message": "Your order has shipped",
    "metadata": {}
  }'
```

### WebSocket Subscription

Connect to `ws://localhost:8080/ws` (SockJS) and subscribe to `/topic/notifications/{userId}` to receive real-time notifications.

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| spring.kafka.bootstrap-servers | localhost:9092 | Kafka brokers |
| spring.kafka.consumer.group-id | notification-consumer-group | Consumer group |
| notification.kafka.topic | notifications | Main topic |
| notification.kafka.dead-letter-topic | notifications.DLT | DLT topic |
| notification.kafka.retry.max-attempts | 3 | Retries before DLT |
| notification.kafka.retry.backoff-ms | 1000 | Backoff between retries |

## Email & SMS

- **Email**: Configure `MAIL_USERNAME` and `MAIL_PASSWORD` env vars. Include `email` in event metadata.
- **SMS**: Placeholder implementation (logs only). Add `phone` to metadata for future integration.
