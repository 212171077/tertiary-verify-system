#!/usr/bin/env bash
# Runs the backend (Spring Boot + devtools) and frontend (ng serve) locally with hot reload.
# Usage: ./run-local.sh [db|backend|frontend|all]   (default: all)
# Optional env: DB_URL, DB_USERNAME (see application.properties for the password variable)
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
SERVICE_DIR="$ROOT/tertiary-verify-service"
WEBAPP_DIR="$ROOT/tertiary-verify-webapp-ng"
API_FILE="$WEBAPP_DIR/src/app/service/tertiary-verify.service.ts"
REMOTE_LINE='  private baseUrl="https://tertiaryverify.dedicated.co.za:8082";'
LOCAL_LINE='  private baseUrl="http://localhost:8081";'

MODE="${1:-all}"
export DB_URL="${DB_URL:-jdbc:mysql://localhost:3306/tertiary_verify_prod}"
export DB_PASSWORD="${DB_PASSWORD:-}"  # set DB_PASSWORD in your environment

# Creates the database and imports the SQL dump (only if the database is missing).
# Uses DB_USERNAME (default root) and DB_PASSWORD; override DB_HOST/DB_PORT/DB_NAME if needed.
setup_db() {
  local host="${DB_HOST:-localhost}" port="${DB_PORT:-3306}" name="${DB_NAME:-tertiary_verify_prod}"
  local user="${DB_USERNAME:-root}"
  local dump="$SERVICE_DIR/src/main/resources/database/tertiary_verify_prod.sql"
  local args=(-h "$host" -P "$port" -u "$user")
  [ -n "${DB_PASSWORD:-}" ] && args+=(-p"$DB_PASSWORD")

  local err
  if ! err=$(mysql "${args[@]}" -e "SELECT 1" 2>&1); then
    echo "Cannot connect to MySQL as '$user'@'$host:$port': $err" >&2
    echo "Set DB_USERNAME / DB_PASSWORD (e.g. DB_PASSWORD=secret ./run-local.sh) and retry." >&2
    return 1
  fi
  if mysql "${args[@]}" -e "USE \`$name\`" 2>/dev/null; then
    echo "Database '$name' already exists, skipping import."
    return
  fi
  echo "Creating database '$name' and importing dump..."
  mysql "${args[@]}" -e "CREATE DATABASE \`$name\` CHARACTER SET utf8mb4"
  mysql "${args[@]}" "$name" < "$dump"
}

run_backend() {
  setup_db
  cd "$SERVICE_DIR"
  exec ./mvnw spring-boot:run
}

# Points the frontend at the local backend; the original file is restored when the frontend exits.
run_frontend() {
  cd "$WEBAPP_DIR"
  [ -d node_modules ] || npm install

  cp "$API_FILE" "$API_FILE.bak"
  trap 'mv -f "$API_FILE.bak" "$API_FILE"' EXIT
  sed -i "s|^  private baseUrl=\"https://tertiaryverify.dedicated.co.za:8082\";|$LOCAL_LINE|" "$API_FILE"

  npx ng serve
}

prefixed() { # prefixed <label> <function>
  "$2" 2>&1 | sed -u "s/^/[$1] /"
}

case "$MODE" in
  db) setup_db ;;
  backend) run_backend ;;
  frontend) run_frontend ;;
  all)
    if [ "$(id -u)" = "0" ]; then
      echo "Don't run this script with sudo; run it as your normal user." >&2
      exit 1
    fi
    for port in 8081 4200; do
      if ss -ltn "sport = :$port" | grep -q LISTEN; then
        echo "Port $port is already in use; stop the process using it first." >&2
        exit 1
      fi
    done

    # Each service runs in its own process group so everything is stopped on Ctrl+C.
    set -m
    prefixed backend run_backend &
    BACKEND_PID=$!
    prefixed frontend run_frontend &
    FRONTEND_PID=$!
    cleanup() {
      trap - INT TERM EXIT
      kill -- -"$BACKEND_PID" -"$FRONTEND_PID" 2>/dev/null || true
      wait 2>/dev/null || true
    }
    trap cleanup INT TERM EXIT
    echo "Starting backend (http://localhost:8081) and frontend (http://localhost:4200). Ctrl+C stops both."
    wait -n "$BACKEND_PID" "$FRONTEND_PID" || true
    echo "A service exited; stopping the other."
    ;;
  *) echo "Usage: $0 [db|backend|frontend|all]"; exit 1 ;;
esac
