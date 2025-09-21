# Flashcards

Flashcards app for subject revision

## Overview
This application allows users to create, manage, and review flashcards for various subjects. 
It supports user authentication, CSV file uploads for bulk card creation, and exporting flashcards to CSV files.

## Tech Stack
- **Frontend**: React, Vite, Tailwind CSS
- **Backend**: Java 24 with Spring Boot
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **Containerization**: Docker
- **Database Migrations**: Liquibase
- **Authentication**: Spring Security with JWT, OAuth2
- **API Documentation**: Swagger/OpenAPI
- **Testing**: JUnit, Mockito, Spring Boot Test


# Database Migrations
Once a new Postgres database is created, you must replace the login details saved locally in your .env file.

Run the following command to create the database tables using the provided custom script:
```bash
./liquibase-update.sh
```

In order to have multiple users, you must also create new users in the database.

The liquibase update script will run the migrations and create the tables.

However, new users will not be able to access or edit the database until they are granted permission.

Use pgAdmin (or other database console) to grant permissions to the new user.

1. Connect to the database in pgAdmin using details provided on the database portal
2. Run the following sql commands to grant access to the new user:

```sql
GRANT USAGE ON SCHEMA public TO newuser1;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO newuser1;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA public TO newuser1;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO newuser1;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE ON SEQUENCES TO newuser1;
```
Replace `newuser1` with the username you created.

# Setting up and running the Application
Refer to the README.md files in the `frontend` and `backend` directories for instructions on how to run the frontend and backend services respectively.

For docker setup, refer to the README.Docker.md file in the root directory.