#!/bin/sh

echo "Uninstalling xOWL Triple Store Server as daemon ..."

sudo rm /etc/init.d/xowl-server
sudo update-rc.d -f xowl-server remove

echo "OK"
