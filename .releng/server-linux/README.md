# xOWL Server - Linux #

This folder contains the distribution toolkit for the xOWL Server on Linux:

* `admin.sh`: The administration script for starting and stopping the xOWL Server
* `install-daemon.sh`: Script to install the xOWL Server as a daemon on Ubuntu and compatible distros
* `uninstall-daemon.sh`: Un-install the xOWL server daemon (reverse install-daemon.sh)
* `xowl-server.conf`: The default configuration for the server
* `help.txt`: Short re-distributable documentation for the server

## Use ##

To install the xOWL server as a daemon, simply run (sudo will be asked for):

```
$ ./install-daemon.sh
```

Then, the daemon can be controlled as usual:

```
$ sudo service xowl-server start
$ sudo service xowl-server stop
$ sudo service xowl-server restart
```