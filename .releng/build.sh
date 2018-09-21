#!/bin/sh

SCRIPT="$(readlink -f "$0")"
RELENG="$(dirname "$SCRIPT")"
ROOT="$(dirname "$RELENG")"

while [ $# -gt 0 ]; do
  case "$1" in
    --repository=*)
      REPOSITORY="${1#*=}"
      ;;
    *)
      printf "***************************\n"
      printf "* Error: Invalid argument.*\n"
      printf "***************************\n"
      exit 1
  esac
  shift
done

# Build
VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -f "$ROOT/pom.xml" -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')
HASH=$(hg -R "$ROOT" --debug id -i)
TIME=$(date +'%d/%m/%Y %T')
echo "Building artifacts for version $VERSION ($HASH)"

# Extract the core products
cp "$ROOT/server/target/xowl-server-$VERSION-jar-with-dependencies.jar" "$RELENG/xowl-server.jar"
rm -f "$RELENG/xowl-server.manifest"
touch "$RELENG/xowl-server.manifest"
echo "version = $VERSION" >> "$RELENG/xowl-server.manifest"
echo "changeset = $HASH" >> "$RELENG/xowl-server.manifest"
echo "build-date = $TIME" >> "$RELENG/xowl-server.manifest"
echo "build-tag = $BUILD_TAG" >> "$RELENG/xowl-server.manifest"
echo "build-user = $USER" >> "$RELENG/xowl-server.manifest"

# Build the server-linux
rm -f "$RELENG/xowl-server-$VERSION.tar.gz"
tar -czf "$RELENG/xowl-server-$VERSION.tar.gz" -C "$ROOT" LICENSE.txt -C "$RELENG" xowl-server.jar xowl-server.manifest -C "$RELENG/server-linux" xowl-server.ini do-run.sh admin.sh install-daemon.sh uninstall-daemon.sh help.md

# Build the server-docker
cp "$RELENG/xowl-server.jar" "$RELENG/server-docker/xowl-server.jar"
cp "$RELENG/xowl-server.manifest" "$RELENG/server-docker/xowl-server.manifest"
cp "$RELENG/server-linux/do-run.sh" "$RELENG/server-docker/do-run.sh"
docker rmi "xowl/xowl-server:$VERSION" || true
docker build --tag "xowl/xowl-server:$VERSION" --rm --label version="$VERSION" --label changeset="$HASH" "$RELENG/server-docker"

# Cleanup
rm "$RELENG/server-docker/xowl-server.jar"
rm "$RELENG/server-docker/xowl-server.manifest"
rm "$RELENG/server-docker/do-run.sh"
rm "$RELENG/xowl-server.jar"
rm "$RELENG/xowl-server.manifest"

# Publish
if [ ! -z "$REPOSITORY" ]; then
  docker tag  "xowl/xowl-server:$VERSION" "$REPOSITORY/xowl/xowl-server:$VERSION"
  docker tag  "$REPOSITORY/xowl/xowl-server:$VERSION" "$REPOSITORY/xowl/xowl-server:latest"
  docker push "$REPOSITORY/xowl/xowl-server:$VERSION"
  docker push "$REPOSITORY/xowl/xowl-server:latest"
  docker rmi  "$REPOSITORY/xowl/xowl-server:$VERSION"
  docker rmi  "$REPOSITORY/xowl/xowl-server:latest"
  docker rmi  "xowl/xowl-server:$VERSION"
fi