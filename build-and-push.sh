#!/bin/bash
# Builds the tertiary-verify image (webapp + service + MySQL) from the repo root and pushes it to Docker Hub.
# Usage: ./build-and-push.sh [tag]   (default tag: latest)

#./build-and-push.sh          # builds and pushes 212171077/tertiary-verify:latest
#./build-and-push.sh v1.0     # use a specific tag instead of latest

set -e

cd "$(dirname "$0")"

IMAGE="212171077/tertiary-verify"
TAG="${1:-latest}"

echo "🔹 Building $IMAGE:$TAG..."
docker build -f Dockerfile -t "$IMAGE:$TAG" .

echo "🔹 Pushing $IMAGE:$TAG..."
docker push "$IMAGE:$TAG"

echo "🚀 Done. On the server run ./deploy.sh to pull and start it."
