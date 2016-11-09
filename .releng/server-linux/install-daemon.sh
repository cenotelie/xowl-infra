#!/bin/sh

SCRIPT="$(readlink -f "$0")"
DISTRIBUTION="$(dirname "$SCRIPT")"

echo "Installing xOWL Server as daemon ..."
echo "xOWL Server location is $DISTRIBUTION"

rm -f daemon.sh
touch daemon.sh
echo "#!/bin/sh" >> daemon.sh
echo "" >> daemon.sh
echo "### BEGIN INIT INFO" >> daemon.sh
echo "# Provides:          xowl-server" >> daemon.sh
echo "# Required-Start:    $remote_fs $syslog $time" >> daemon.sh
echo "# Required-Stop:     $remote_fs $syslog $time" >> daemon.sh
echo "# Default-Start:     2 3 4 5" >> daemon.sh
echo "# Default-Stop:      0 1 6" >> daemon.sh
echo "# Short-Description: Daemon for the xOWL Triple Store Server" >> daemon.sh
echo "# Description:       Daemon for the xOWL Triple Store Server" >> daemon.sh
echo "### END INIT INFO" >> daemon.sh
echo "" >> daemon.sh
echo "$DISTRIBUTION/admin.sh \$1" >> daemon.sh

sudo mv daemon.sh /etc/init.d/xowl-server
sudo chmod +x /etc/init.d/xowl-server
sudo update-rc.d xowl-server defaults

echo "OK"
