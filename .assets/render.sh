#!/bin/sh

# width and height of the files to produce
WIDTH=256
HEIGHT=256

SCRIPT="$(readlink -f "$0")"
ASSETS="$(dirname "$SCRIPT")"

inkscape "-e=$ASSETS/xowl-$WIDTH.png" -C -w=$WIDTH -h=$HEIGHT "$ASSETS/xowl.svg"

ANGLE=0
ANGLE_INC=2
ANGLE_MAX=30
COUNTER=0
while [ $ANGLE -lt $ANGLE_MAX ]
do
  sed "s/rotate(0/rotate($ANGLE/g" "$ASSETS/xowl.svg" > "$ASSETS/xowl_temp.svg"
  NAME=$COUNTER
  if [ $COUNTER -lt 10 ]
  then
    NAME="0$COUNTER"
  fi
  inkscape "-e=$ASSETS/xowl_temp_$NAME.png" -C -w=$WIDTH -h=$HEIGHT "$ASSETS/xowl_temp.svg"
  rm "$ASSETS/xowl_temp.svg"
  ANGLE=$(($ANGLE+$ANGLE_INC))
  COUNTER=$(($COUNTER+1))
done

convert -delay 5 -loop 0 -dispose Background -antialias "$ASSETS/xowl_temp_*.png" "$ASSETS/xowl.gif"

rm "$ASSETS/"xowl_temp_*.png
