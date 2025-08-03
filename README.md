# Integration Demo Project

This project demonstrates integration between different persistence layers using Apache Camel, JPA (Hibernate), and MongoDB.

## Project Structure

- **orm-module**: Contains JPA entities and repositories (H2 database)
- **odm-module**: Contains MongoDB document models and repositories
- **integration-module**: Implements the integration logic using Apache Camel

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- MongoDB 4.4 or higher
- H2 Database (embedded)

## Getting Started

1. **Start MongoDB**
   Make sure MongoDB is running on localhost:27017

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   cd integration-module
   mvn spring-boot:run
   ```

## API Endpoints

- `GET /api/customers/sync` - Trigger manual sync between databases
- `GET /h2-console` - H2 Database Console (JDBC URL: jdbc:h2:mem:customersdb)
- `GET /camel/*` - Apache Camel routes and endpoints

## Integration Flow

1. The application polls the H2 database for new customers
2. When new customers are found, they are automatically synced to MongoDB
3. The sync process transforms the JPA entity to a MongoDB document
4. A manual sync can be triggered via the REST API

## Data Model

### JPA Entity (H2 Database)
```java
public class CustomerEntity {
    private Long id;
    private String name;
    private String email;
    private String status;
}
```

### MongoDB Document
```java
@Document(collection = "customers")
public class CustomerDocument {
    private String id;
    private String name;
    private String email;
    private String status;
    private String sourceSystem;
}
```

## Configuration

- H2 Database: `jdbc:h2:mem:customersdb` (in-memory)
- MongoDB: `mongodb://localhost:27017/integration`
- Server Port: 8080

## Monitoring

- H2 Console: http://localhost:8080/h2-console
- Camel Routes: Check application logs for route activity


