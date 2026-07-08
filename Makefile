.PHONY: help build test package up up-core up-full down logs logs-banking logs-enterprise logs-playground reset-lab

help:
	@echo "API Testing Playground"
	@echo "  make build             - Compile all modules"
	@echo "  make test              - Run unit/integration tests"
	@echo "  make package           - Build runnable JARs"
	@echo "  make up                - PostgreSQL + platform-api (foundation profile)"
	@echo "  make up-core           - PostgreSQL + platform-api + banking-api (core profile)"
	@echo "  make up-full           - PostgreSQL + all APIs including test lab (full profile)"
	@echo "  make down              - Stop all Compose services"
	@echo "  make logs              - Tail platform-api logs"
	@echo "  make logs-banking      - Tail banking-api logs"
	@echo "  make logs-enterprise   - Tail enterprise-api logs"
	@echo "  make logs-playground   - Tail playground-api logs"
	@echo "  make reset-lab         - Reset all lab data (requires playground-api on :8083)"

build:
	mvn -q clean compile

test:
	mvn -q test

package:
	mvn -q clean package -DskipTests

up: package
	docker compose --profile foundation up --build -d

up-core: package
	docker compose --profile core up --build -d

up-full: package
	docker compose --profile full up --build -d

down:
	docker compose --profile foundation --profile core --profile full down

logs:
	docker compose logs -f platform-api

logs-banking:
	docker compose logs -f banking-api

logs-enterprise:
	docker compose logs -f enterprise-api

logs-playground:
	docker compose logs -f playground-api

reset-lab:
	curl -s -u $${PLAYGROUND_API_USER:-learner}:$${PLAYGROUND_API_PASSWORD:-learner} \
		-X POST http://localhost:$${PLAYGROUND_LAB_PORT:-8083}/v1/playground/reset \
		-H 'Content-Type: application/json' \
		-d '{"scope":"ALL"}' | jq
