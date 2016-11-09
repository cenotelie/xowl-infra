#!/bin/sh

SCRIPT="$(readlink -f "$0")"
RELENG="$(dirname "$SCRIPT")"
ROOT="$(dirname "$RELENG")"

# Build
VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -f "$ROOT/pom.xml" -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')
HASH=$(hg -R "$ROOT" --debug id -i)
echo "Building artifacts for version $VERSION ($HASH)"
mvn clean install -f "$ROOT/pom.xml" -Dgpg.skip=true

# Extract the core products
cp "$ROOT/server/target/xowl-server-$VERSION-jar-with-dependencies.jar" "$RELENG/xowl-server.jar"
rm -f "$RELENG/xowl-server.version"
touch "$RELENG/xowl-server.version"
echo "version = $VERSION" >> "$RELENG/xowl-server.version"
echo "changeset = $HASH" >> "$RELENG/xowl-server.version"

# Build the server-linux
rm -f "$RELENG/xowl-server-$VERSION.tar.gz"
tar -czf "$RELENG/xowl-server-$VERSION.tar.gz" -C "$ROOT" LICENSE.txt -C "$RELENG" xowl-server.jar xowl-server.version -C "$RELENG/server-linux" xowl-server.ini do-run.sh admin.sh install-daemon.sh uninstall-daemon.sh help.txt

# Build the server-docker
cp "$RELENG/xowl-server.jar" "$RELENG/server-docker/xowl-server.jar"
cp "$RELENG/xowl-server.version" "$RELENG/server-docker/xowl-server.version"
cp "$RELENG/server-linux/xowl-server.ini" "$RELENG/server-docker/xowl-server.ini"
cp "$RELENG/server-linux/do-run.sh" "$RELENG/server-docker/do-run.sh"
docker rmi "xowl/xowl-server:$VERSION" || true
docker build -t "xowl/xowl-server:$VERSION" "$RELENG/server-docker"
rm "$RELENG/server-docker/xowl-server.jar"
rm "$RELENG/server-docker/xowl-server.version"
rm "$RELENG/server-docker/xowl-server.ini"
rm "$RELENG/server-docker/do-run.sh"

# Cleanup
rm "$RELENG/xowl-server.jar"
rm "$RELENG/xowl-server.version"