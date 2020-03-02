#!/bin/bash

SCRIPT="$(readlink -f "$0")"
ROOT="$(dirname "$SCRIPT")"

"$ROOT/server/build-docker.sh" $@
