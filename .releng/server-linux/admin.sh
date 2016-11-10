#!/bin/sh

# number of seconds to wait after launching the daemon before checking it has started correctly
WAIT=4

SCRIPT="$(readlink -f "$0")"
DISTRIBUTION="$(dirname "$SCRIPT")"

init () {
  PROCESS_ID=
  IS_RUNNING=false

  if [ -f "xowl-server.pid" ];
  then
    # pid file exists, is the server still running?
    PROCESS_ID=`cat "$DISTRIBUTION/xowl-server.pid"`
    PROCESS=`ps -p "$PROCESS_ID"`
    TARGET=sh
    if test "${PROCESS#*$TARGET}" != "$PROCESS"
    then
      IS_RUNNING=true
    fi
  fi
}

start () {
  if [ "$IS_RUNNING" = "true" ]; then
    echo "xOWL Server is already running!"
    exit 1
  else
    doStart
    if [ "$IS_RUNNING" = "true" ]; then
      exit 0
    else
      exit 1
    fi
  fi
}

doStart () {
  echo "==== xOWL Server Startup ====" >> log.txt
  echo "xOWL Server starting ..."
  sh "$DISTRIBUTION/do-run.sh" "$DISTRIBUTION" &
  PROCESS_ID="$!"
  echo "$PROCESS_ID" > "$DISTRIBUTION/xowl-server.pid"
  sleep "$WAIT"
  PROCESS=`ps -p "$PROCESS_ID"`
  TARGET=sh
  if test "${PROCESS#*$TARGET}" != "$PROCESS"
  then
    IS_RUNNING=true
    echo "xOWL Server started."
  else
    IS_RUNNING=false
    echo "xOWL Server failed to start!"
  fi
}

stop () {
  if [ "$IS_RUNNING" = "true" ]; then
    doStop
    exit 0
  else
    echo "xOWL Server is not running."
    exit 0
  fi
}

doStop () {
  echo "xOWL Server stopping ..."
  GROUP_ID=`ps -o pgid= -p "$PROCESS_ID" | tr -d ' '`
  CHILDREN=`ps -o pid= "-$GROUP_ID" | tr -d ' '`
  kill -TERM "-$GROUP_ID"
  while [ -n "$CHILDREN" ]; do
    sleep "$WAIT"
    CHILDREN=`ps -o pid= "-$GROUP_ID" | tr -d ' '`
  done
  rm "$DISTRIBUTION/xowl-server.pid"
  echo "xOWL Server stopped."
}

restart () {
  if [ "$IS_RUNNING" = "true" ]; then
    doStop
  fi
  doStart
  if [ "$IS_RUNNING" = "true" ]; then
    exit 0
  else
    exit 1
  fi
}

status () {
  if [ "$IS_RUNNING" = "true" ]; then
    echo "xOWL Server is running on PID $PROCESS_ID."
  else
    echo "xOWL Server is not running."
  fi
  exit 0
}

## Main script
# set the current directory to the script's location
cd "$(dirname "$0")"
# initialize
init
# do we have exactly one argument?
if [ "$#" -ne 1 ]; then
  echo "admin.sh start|stop|restart|status"
  exit 1
fi
# OK, branch on the command
if [ "$1" = "start" ]; then
  start
elif [ "$1" = "stop" ]; then
  stop
elif [ "$1" = "restart" ]; then
  restart
elif [ "$1" = "status" ]; then
  status
elif [ "$1" = "help" ]; then
  echo "admin.sh start|stop|restart|status"
  exit 0
else
  echo "Unknown command!"
  exit 1
fi
