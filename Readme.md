# IoT Tracker

This is a Spring Boot application to track IoT devices' locations from a CSV file.

## Requirements

- Java 17
- Maven

## Build and Run

1. Clone the repository.
2. Navigate to the project directory.
3. Run `mvn clean install` to build the project.
4. Run `mvn spring-boot:run` to start the application.

## API Endpoints

### Load CSV Data

- URL: `/iot/event/v1/`
- Method: `POST`
- Body: `{ "filepath": "C:/path/to/data.csv" }`
- Response:
    - 200 OK: `{ "description": "data refreshed" }`
    - 404 Not Found: `{ "description": "ERROR: no data file found" }`
    - 500 Internal Server Error: `{ "description": "ERROR: A technical exception occurred." }`

## Testing

Run `mvn test` to execute unit tests.