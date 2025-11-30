#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR=$(cd "$(dirname "$0")/.." && pwd)

# Start frontend dev server if configured (optional)
# cd "$ROOT_DIR/frontend"
# npm ci
# npm run watch &

# Start backend
cd "$ROOT_DIR/backend"
mvn spring-boot:run

# Note: Keep frontend dev server running in background if needed.