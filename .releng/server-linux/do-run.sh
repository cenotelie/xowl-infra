#!/bin/sh

# Runs an instance of the xOWL server
# This scripts looks for a special return value to indicate that the server requested a restart
# Expected parameter:
#   1: Path to the startup directory for the server

SCRIPT="$(readlink -f "$0")"
DISTRIBUTION="$(dirname "$SCRIPT")"
TARGET=$1

# custom path to java, if any
# if left empty, the script will use 'which' to locate java
JAVA=

if [ -z "$JAVA" ]
  then
    JAVA=`which java`
fi
if [ -z "$JAVA" ]
  then
    echo "Cannot find java"
    exit 1
fi

if [ -z "$TARGET" ]
  then
    # No startup directory provided, uses the installation directory
    TARGET="$DISTRIBUTION"
fi

# while exit code is 5 (restart) relaunch the process
CODE=5
while [ "$CODE" -eq 5 ]
  do
    "$JAVA" -jar "$DISTRIBUTION/xowl-server.jar" "$TARGET"
    CODE=$?
    echo "Exit code is $CODE"
done