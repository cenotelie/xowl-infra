#!/bin/bash

VERSION=$(git rev-parse --short HEAD)
HASH=$(git rev-parse HEAD)
TAG=$(git tag -l --points-at HEAD)

BUILD_TARGET=debug
DOCKER_TAG=latest
BASE_URI=
for arg in "$@"
do
    case "$arg" in
    --target=*)
      BUILD_TARGET="${arg#*=}"
      ;;
    --base-uri=*)
      BASE_URI="${arg#*=}"
      ;;
    *)
      printf "***************************\n"
      printf "* Error: Invalid argument: $arg\n"
      printf "***************************\n"
      exit 1
  esac
done

if [ "$BUILD_TARGET" = "production" ]; then
  if [ ! -z "$TAG" -a "$TAG" != "tip" ]; then
    DOCKER_TAG="$TAG"
  else
    printf "***************************\n"
    printf "* Error: version tag is required for production\n"
    printf "***************************\n"
    exit 1
  fi
fi
if [ "$BUILD_TARGET" = "integration" ]; then
  DOCKER_TAG="git-$VERSION"
fi
