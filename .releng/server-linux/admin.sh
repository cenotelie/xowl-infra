#!/bin/sh

# number of seconds to wait after launching the daemon before checking it has started correctly
STARTUP_WAIT=4

SCRIPT="$(readlink -f "$0")"
DISTRIB="$(dirname $SCRIPT)"

init () {
  PID=
  ISRUNNING=false

  if [ -f "xowl-server.pid" ];
  then
    # pid file exists, is the server still running?
    PID=`cat xowl-server.pid`
    PROCESS=`ps -p $PID`
    TARGET=sh
    if test "${PROCESS#*$TARGET}" != "$PROCESS"
    then
      ISRUNNING=true
    fi
  fi
}

start () {
  if [ "$ISRUNNING" = "true" ]; then
    echo "xOWL Server is already running ..."
    exit 1
  else
    doStart
    if [ "$ISRUNNING" = "true" ]; then
      exit 0
    else
      exit 1
    fi
  fi
}

doStart () {
  echo "==== xOWL Server Startup ====" >> log.txt
  sh do-run.sh "$DISTRIB"  &
  PID="$!"
  echo $PID > xowl-server.pid
  sleep $STARTUP_WAIT
  PROCESS=`ps -p $PID`
  TARGET=sh
  if test "${PROCESS#*$TARGET}" != "$PROCESS"
  then
    ISRUNNING=true
    echo "xOWL Server started ..."
  else
    ISRUNNING=false
    echo "xOWL Server failed to start"
  fi
}

stop () {
  if [ "$ISRUNNING" = "true" ]; then
    doStop
    exit 0
  else
    echo "xOWL Server is not running ..."
    exit 0
  fi
}

doStop () {
  echo "xOWL Server stopping ..."
  sudo kill $PID
  rm -f xowl-server.pid
  echo "xOWL Server stopped ..."
}

restart () {
  if [ "$ISRUNNING" = "true" ]; then
    doStop
  fi
  doStart
  if [ "$ISRUNNING" = "true" ]; then
    exit 0
  else
    exit 1
  fi
}

status () {
  if [ "$ISRUNNING" = "true" ]; then
    echo "xOWL Server is running on PID $PID"
  else
    echo "xOWL Server is not running"
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
  echo "Unknown command"
  exit 1
fi
