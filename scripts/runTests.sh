#!/usr/bin/env bash
source .env.test
docker-compose up --exit-code-from api --abort-on-container-exit