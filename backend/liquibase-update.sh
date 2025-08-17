#!/usr/bin/env bash
set -euo pipefail

ENV_FILE=".env"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "ERROR: $ENV_FILE not found"
  exit 1
fi

# Read each variable from .env
DB_URL=$(grep -v '^#' "$ENV_FILE" | grep '^DB_URL=' | cut -d '=' -f2-)
DB_USERNAME=$(grep -v '^#' "$ENV_FILE" | grep '^DB_USERNAME=' | cut -d '=' -f2-)
DB_PASSWORD=$(grep -v '^#' "$ENV_FILE" | grep '^DB_PASSWORD=' | cut -d '=' -f2-)

# Debug
echo "Using database URL: $DB_URL"
echo "Using database username: $DB_USERNAME"

# Clear checksums to ensure Liquibase picks up any modified/added changesets
echo "Clearing Liquibase checksums..."
./mvnw liquibase:clearCheckSums \
  -DDB_URL="$DB_URL" \
  -DDB_USERNAME="$DB_USERNAME" \
  -DDB_PASSWORD="$DB_PASSWORD"

# Run Liquibase update (only applies pending changesets)
echo "Running Liquibase update..."
./mvnw liquibase:update \
  -DDB_URL="$DB_URL" \
  -DDB_USERNAME="$DB_USERNAME" \
  -DDB_PASSWORD="$DB_PASSWORD"

  echo "Liquibase update complete."
