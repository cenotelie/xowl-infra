#!/bin/bash

SCRIPT="$(readlink -f "$0")"
DIR="$(dirname "$SCRIPT")"
ROOT="$(dirname "$DIR")"

source "$ROOT/build-env.sh"

docker push "xowl/xowl-server:$DOCKER_TAG"
