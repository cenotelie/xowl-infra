#!/bin/bash

SCRIPT="$(readlink -f "$0")"
ROOT="$(dirname "$SCRIPT")"

source "$ROOT/build-env.sh"

"$ROOT/build-src.sh" "$BUILD_TARGET" "$BASE_URI"

"$ROOT/build-docker.sh" $@
