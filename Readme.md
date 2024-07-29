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

- URL: `/iot/event/v1`
- Method: `POST`
- Body: `{ "filepath": "C:/path/to/data.csv" }`
- Response:
    - 200 OK: `{ "description": "data refreshed" }`
    - 404 Not Found: `{ "description": "ERROR: no data file found" }`
    - 500 Internal Server Error: `{ "description": "ERROR: A technical exception occurred." }`

### Get IOT Device Data By ProductId and DateTime

- URL: `/iot/event/v1?ProductId=<productId>&tstmp=<DateTime>`
- Method: `GET`
- Response:
    - 200 OK: `Complete Response Body`
    - 404 Not Found: `{ "description": "ERROR: Id <productId> not found" }`
    - 400 Bad Request: `{ "description": "ERROR: Device could not be located" }`

## Testing

Run `mvn test` to execute unit tests.

## Swagger

Login to Swagger with below endpoint - 

`/swagger-ui/index.html`

Example - `http://localhost:8080/swagger-ui/index.html`
