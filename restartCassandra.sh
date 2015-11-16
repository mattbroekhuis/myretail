#!/usr/bin/env bash
docker rm -f cassandra || true
docker run -d --name cassandra  -p 9042:9042 cassandra:2.2.3
docker ps