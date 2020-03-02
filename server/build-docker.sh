#!/bin/bash

SCRIPT="$(readlink -f "$0")"
DIR="$(dirname "$SCRIPT")"
ROOT="$(dirname "$DIR")"

source "$ROOT/build-env.sh"

MVNVERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -f "$ROOT/pom.xml" -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')

cp "$DIR/target/xowl-server-$MVNVERSION-jar-with-dependencies.jar" "$DIR/xowl-server.jar"
rm -f "$DIR/xowl-server.manifest"
touch "$DIR/xowl-server.manifest"
echo "version = $MVNVERSION" >> "$DIR/xowl-server.manifest"
echo "changeset = $HASH" >> "$DIR/xowl-server.manifest"
echo "build-date = $TIME" >> "$DIR/xowl-server.manifest"
echo "build-tag = $BUILD_TAG" >> "$DIR/xowl-server.manifest"
echo "build-user = $USER" >> "$DIR/xowl-server.manifest"

docker build --tag "xowl/xowl-server:$DOCKER_TAG" --rm --label version="$VERSION" --label changeset="$HASH" "$DIR"

rm "$DIR/xowl-server.jar"
rm "$DIR/xowl-server.manifest"
