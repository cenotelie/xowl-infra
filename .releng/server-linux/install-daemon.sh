#!/bin/sh

SCRIPT="$(readlink -f "$0")"
DISTRIB="$(dirname $SCRIPT)"

echo "Installing xOWL Server as daemon ..."
echo "xOWL Server location is $DISTRIB"

rm -f daemon.sh
touch daemon.sh
echo "#!/bin/sh" >> daemon.sh
echo "$DISTRIB/admin.sh \$1" >> daemon.sh

sudo mv daemon.sh /etc/init.d/xowl-server
sudo chmod +x /etc/init.d/xowl-server
sudo update-rc.d xowl-server defaults

echo "OK"
