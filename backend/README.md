# Flashcard Backend

## API Documentation
The API documentation is available in OpenAPI format. You can find the latest generated docs here:

- [OpenAPI JSON](target/openapi/openapi.json)

To view the interactive Swagger UI, run the application and visit `/swagger-ui.html`.

- [Swagger UI](http://localhost:8080/swagger-ui.html)

## Building the Application
> All commands should be run from the `backend` (root) directory.
> 
> `cd backend` if you are not already in the root directory.

This project is built using Spring Boot and Maven. To run the application, you need to have Java (JDK 24 or higher) and Maven installed on your machine.

To build the application, you can use the following command:
```bash
mvn clean install
```
This will compile the code, run tests, and create a JAR file in the `build/libs` directory.

## Running the Application

To run the application, you can use the following command:
```bash
mvn spring-boot:run
```
This will start the Spring Boot application, and you can access the API at `http://localhost:8080`.


## Testing
To just run the tests, you can use the following command:
```bash
mvn test
```
This will execute all the unit tests and integration tests defined in the project.


## CSV Upload
The application supports uploading CSV files to create or update flashcards. 

To upload a CSV file, you can use the `/api/upload` endpoint with a POST request. The request should include the CSV file in the body.

The CSV file should have the following format: `front,back`

Where `front` is the text displayed on the front of the flashcard and `back` is the text displayed on the back.

#### Example CSV Content
``` csv
front,back
die Katze,cat
das Haus,house
```


## Database Configuration
The application uses Spring Data JPA for database interactions. It is currently configured to connect to a Neon database by default, which is a PostgreSQL-compatible database.

In order to connect, you must provide a .env file with the necessary database credentials. The `.env` file should be placed in the root directory of the project (ie `backend/.env`) and should contain the following variables:

```dotenv
DB_URL=jdbc:postgresql://<your-database-url>:5432/<your-database-name>
DB_USERNAME=<your-database-username>
DB_PASSWORD=<your-database-password>
```

For using alternative databases, you can configure the database connection in the `application.properties` file located in the `src/main/resources` directory.
You can change the database type, URL, username, and password as needed. For example, to use a MySQL database, you would update the properties as follows:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/flashcards
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
For in memory H2 database, you can use the following configuration:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```