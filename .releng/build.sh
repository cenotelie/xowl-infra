#!/bin/sh

SCRIPT="$(readlink -f "$0")"
RELENG="$(dirname "$SCRIPT")"
ROOT="$(dirname "$RELENG")"

# Build
VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -f "$ROOT/pom.xml" -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')
echo "Building artifacts for version $VERSION"
mvn package -f "$ROOT/pom.xml" -DskipTests -Dgpg.skip=true

# Extract the core products
cp "$ROOT/server/target/xowl-server-$VERSION-jar-with-dependencies.jar" "$RELENG/xowl-server.jar"

# Build the server-linux
tar -czf "$RELENG/xowl-server.tar.gz" LICENSE.txt -C "$RELENG" xowl-server.jar -C server-linux xowl-server.conf admin.sh install-daemon.sh uninstall-daemon.sh help.txt

# Build the server-docker
cp "$RELENG/xowl-server.jar" "$RELENG/server-docker/xowl-server.jar"
docker build -t xowl/xowl-server:$VERSION "$RELENG/server-docker"
rm "$RELENG/server-docker/xowl-server.jar"


# Cleanup
rm "$RELENG/xowl-server.jar"
