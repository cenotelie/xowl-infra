#!/bin/bash

SCRIPT="$(readlink -f "$0")"
ROOT="$(dirname "$SCRIPT")"

HASH=$(git rev-parse HEAD)
TAG=$(git tag -l --points-at HEAD)

(cd "$DIR"; mvn clean verify)
