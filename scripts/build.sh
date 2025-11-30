#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR=$(cd "$(dirname "$0")/.." && pwd)

# Build frontend
cd "$ROOT_DIR/frontend"
npm ci
npm run build
npm run deploy

# Build backend
cd "$ROOT_DIR/backend"
mvn -DskipTests package

echo "Build complete: frontend built and backend packaged."