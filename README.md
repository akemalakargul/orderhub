# Brokerage Firm Order Management System

This project is a Spring Boot application for a brokerage firm's order management system. It provides endpoints for creating, listing, and canceling stock orders for customers.

## Features

- Create orders for buying or selling assets
- List orders with filtering by customer, date range, and status
- Cancel pending orders
- List customer assets
- Authorization for all endpoints
- Database management for assets and orders

## Prerequisites

- Java 21 (or compatible version)
- Maven

## Running the Application

### Using Maven

1. Clone the repository
2. Navigate to the project directory
3. Run the application using Maven:

```bash
./mvnw spring-boot:run
```

### Using Packaged JAR

1. Build the JAR file:

```bash
./mvnw clean package
```

2. Run the JAR file:

```bash
java -jar target/orderhub-0.0.1-SNAPSHOT.jar
```

## API Documentation

### Authentication

All API endpoints are secured with HTTP Basic Authentication. Use the following credentials:

- Username: `admin`
- Password: `adminPassword`

### Endpoints

#### Create Order

```
POST /api/orders
```

Request Body:
```json
{
  "customerId": "customer1",
  "assetName": "AAPL",
  "orderSide": "BUY",
  "size": 5,
  "price": 150.00
}
```

#### List Orders

```
POST /api/orders/filter
```

Request Body:
```json
{
  "customerId": "customer1",
  "startDate": "2023-01-01",
  "endDate": "2023-12-31",
  "status": "PENDING"
}
```

#### Cancel Order

```
DELETE /api/orders/{orderId}
```

#### List Assets for Customer

```
GET /api/assets/customer/{customerId}
```

#### Get Specific Asset for Customer

```
GET /api/assets/customer/{customerId}/asset/{assetName}
```

## Database

The application uses an H2 in-memory database. You can access the H2 console at:

```
http://localhost:8080/h2-console
```

Use the following connection details:
- JDBC URL: `jdbc:h2:mem:orderdb`
- Username: `sa`
- Password: `password`

## Testing

Run the tests using Maven:

```bash
./mvnw test
```

## Design Decisions

- Used in-memory H2 database for simplicity and ease of development
- Implemented transactions to ensure data consistency when updating balances
- Added comprehensive validation to ensure integrity of data
- Implemented a proper error handling system with standardized error responses
- Used Spring Security for API authentication 