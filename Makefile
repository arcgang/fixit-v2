.PHONY: help install test install-fixit-web-frontend test-fixit-web-frontend dev-fixit-web-frontend install-fixit-api-service test-fixit-api-service dev-fixit-api-service up down logs ps db-wait db-schema seed db-reset
# Generated from the repository plan. `make help` lists the targets.
.DEFAULT_GOAL := help

install-fixit-web-frontend: ## install fixit-web-frontend
	cd apps/fixit-web-frontend && npm ci

test-fixit-web-frontend: ## test fixit-web-frontend
	cd apps/fixit-web-frontend && npm test

dev-fixit-web-frontend: ## run fixit-web-frontend locally
	cd apps/fixit-web-frontend && npm run dev

install-fixit-api-service: ## install fixit-api-service
	cd services/fixit-api-service && mvn -q -DskipTests package

test-fixit-api-service: ## test fixit-api-service
	cd services/fixit-api-service && mvn -q test

dev-fixit-api-service: ## run fixit-api-service locally
	cd services/fixit-api-service && mvn spring-boot:run

install: install-fixit-web-frontend install-fixit-api-service ## install every component

test: test-fixit-web-frontend test-fixit-api-service ## test every component

up: ## start the local platform (containers)
	[ -f .env ] || cp .env.example .env
	docker compose up -d

down: ## stop the local platform
	docker compose down

logs: ## follow the platform logs
	docker compose logs -f

ps: ## what is running
	docker compose ps

db-wait:
	@until docker compose exec -T postgres pg_isready -U postgres >/dev/null 2>&1; do sleep 1; done

db-schema: db-wait ## load the LLD schema (db/schema.sql) — until a service ships its own migrations
	docker compose exec -T postgres psql -U postgres -d app -v ON_ERROR_STOP=1 -q < db/schema.sql

seed: db-wait ## load synthetic seed rows (db/seed.sql) — local only
	docker compose exec -T postgres psql -U postgres -d app -v ON_ERROR_STOP=1 -q < db/seed.sql

db-reset: db-wait ## drop and recreate the local database, then schema + seed
	docker compose exec -T postgres psql -U postgres -d postgres -v ON_ERROR_STOP=1 -q -c 'DROP DATABASE IF EXISTS app WITH (FORCE)' -c 'CREATE DATABASE app'
	$(MAKE) db-schema seed

help:
	@grep -E '^[a-zA-Z0-9_-]+:.*?## ' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-32s %s\n", $$1, $$2}'
