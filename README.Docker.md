# Flashcards – Local Docker Development Setup

This guide walks you through running the Flashcards app locally in Docker, with HTTPS enabled for LAN testing, Auth0 authentication, and a PostgreSQL database.

---

## 0. Prerequisites

Before you start, make sure you have:

- **Docker** and **Docker Compose** installed  
  [Install Docker](https://docs.docker.com/get-docker/)
- **mkcert** for generating local HTTPS certificates  
  [Install mkcert](https://github.com/FiloSottile/mkcert#installation)
- **Java 24** and **Maven** (only if you want to run Liquibase from your host)
- An **Auth0** account and application set up

---

## 1. Clone the Repository

```bash
git clone https://github.com/acunil/flashcards.git
cd flashcards
```

---

## 2. Environment Variables

Create a `.env` file in the project root with your local DB and Auth0 settings:

```env
# Database
DB_URL=jdbc:postgresql://db:5432/myapp
DB_USERNAME=myuser
DB_PASSWORD=mypass

# Auth0
AUTH0_DOMAIN=your-tenant.eu.auth0.com
AUTH0_CLIENT_ID=your-client-id
AUTH0_AUDIENCE=your-api-audience
```

> **Note:** Render (or other cloud host) will inject its own environment variables in production — this `.env` is for local Docker only.

---

## 3. Generate HTTPS Certificates for LAN Testing

Get your Mac's LAN IP address:
```bash
ipconfig getifaddr en0
```

If you want to access the frontend from other devices (phones, tablets, PCs) on your network:

```bash
brew install mkcert nss   # macOS
mkcert -install
mkcert 192.168.x.x         # Replace with your Mac's LAN IP
```

Move the generated `.pem` and `-key.pem` files into a `certs/` folder in the project root:

```
flashcards/
  certs/
    192.168.x.x.pem
    192.168.x.x-key.pem
```

---

## 4. Configure Auth0 for Local HTTPS

In the [Auth0 Dashboard](https://manage.auth0.com):

1. Go to **Applications → Applications** and select your app.
2. In **Allowed Web Origins**, add:
   ```
   https://192.168.x.x:5173
   http://localhost:5173
   ```
3. Save changes.

---

## 5. Start the Stack

From flashcards root:

```bash
docker compose up --build
```

This will start:
- **Postgres** (`db`)
- **Backend** (`backend`)
- **Frontend** (`frontend`)

---

## 6. Run Liquibase Migrations

The DB schema is managed by Liquibase.  
Run migrations from your local terminal, ie as normal, no Docker (requires Java + Maven; .env vars will point to the Docker DB):

```bash
cd backend
./mvnw liquibase:update
```

Or run them in a throwaway Maven container:

```bash
docker run --rm \
  --network $(docker network ls --filter name=flashcards_default --format "{{.Name}}") \
  -v "$PWD":/workspace \
  -w /workspace \
  --env-file .env \
  maven:3.9.9-amazoncorretto-24-alpine \
  mvn liquibase:update
```

---

## 7. Accessing the App

- **From your Mac**:  
  [http://localhost:5173](http://localhost:5173) (HTTP)  
  or [https://192.168.x.x:5173](https://192.168.x.x:5173) (HTTPS with cert warning)

- **From other devices on your LAN**:  
  [https://192.168.x.x:5173](https://192.168.x.x:5173)  
  Accept the self‑signed certificate once.

---

## 8. Troubleshooting

**`vite: not found`**
- Don’t override the `command:` in Compose unless you know what you’re doing.
- Use `vite.config.js` to configure HTTPS instead of CLI flags.

**Auth0 says “must run on a secure origin”**
- Use HTTPS for LAN IPs.
- Add the exact origin to **Allowed Web Origins** in Auth0.

**Liquibase says “relation does not exist”**
- Run migrations after starting the DB.
- Make sure `spring.liquibase.enabled=true` in dev or run `liquibase:update` manually.

**Can’t connect from pgAdmin**
- Host: `localhost` (if pgAdmin is on your host)
- Port: `5432`
- Username/Password: from `.env`
- DB name: from `.env`

---

## Notes

- Production deploys on Render are unaffected by this setup — Render uses its own build process and environment variables.
- Local HTTPS is only for development and testing; production should use a real TLS certificate.

---
