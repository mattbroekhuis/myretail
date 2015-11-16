#!/usr/bin/env bash
docker-machine stop local || true
docker-machine rm local || true
docker-machine create --driver=virtualbox --virtualbox-cpu-count "2" --virtualbox-memory "8000" --virtualbox-cpu-count "4" --virtualbox-disk-size "40000" local
source ~/.bash_profile