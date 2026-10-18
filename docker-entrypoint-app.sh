#!/bin/bash
# Starts MySQL (importing the prod dump on first run), nginx (webapp) and the Spring Boot service.
set -e

if [ -z "$MYSQL_ROOT_PASSWORD" ]; then
    echo "MYSQL_ROOT_PASSWORD must be set" >&2
    exit 1
fi
export DB_PASSWORD="$MYSQL_ROOT_PASSWORD"
export DB_USERNAME="${DB_USERNAME:-root}"

# The stock MySQL entrypoint initialises the data dir and imports /docker-entrypoint-initdb.d on first run only
/usr/local/bin/docker-entrypoint.sh mysqld --bind-address=127.0.0.1 &
MYSQL_PID=$!

# The temporary init server is socket-only, so a TCP ping only succeeds once the real server is up
until mysqladmin ping -h 127.0.0.1 -uroot -p"$MYSQL_ROOT_PASSWORD" --silent 2>/dev/null; do
    kill -0 $MYSQL_PID 2>/dev/null || { echo "MySQL failed to start" >&2; exit 1; }
    sleep 2
done

nginx -g 'daemon on;'

java ${JAVA_OPTS} -jar /app/tertiary-verify-service.jar &
APP_PID=$!

trap 'kill $APP_PID; nginx -s quit; kill $MYSQL_PID' TERM INT
wait -n $APP_PID $MYSQL_PID
STATUS=$?
kill $APP_PID $MYSQL_PID 2>/dev/null || true
exit $STATUS
