# Makefile to build and run the project
.PHONY: build run clean

build:
	./scripts/build.sh

run:
	./scripts/run-local.sh

clean:
	cd backend && mvn clean
	cd frontend && rm -rf dist

echo:
	@echo "Use 'make build' to build frontend and backend, 'make run' to start local backend."