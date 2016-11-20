This is the distribution for the xOWL Triple Store Server.
The xOWL Triple Store Server is an application for the storage, management, reasoning and serving of semantic web datasets.



# Licenses

This software is Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as
published by the Free Software Foundation, either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

Additional components embedded in this application are:

| Library | Copyright | License | Full text |
|----|----|----|----|
| Java library [`org.xowl.hime:redist:2.0.5`](https://bitbucket.org/laurentw/hime) | Copyright (c) 2016 Laurent Wouters | LGPL v3 | xowl-server.jar$META-INF/licenses/org.xowl.hime.redist |
| Java library [`de.svenkubiak:jBCrypt:0.4`](http://www.mindrot.org/projects/jBCrypt/) | Copyright (c) 2006 Damien Miller | BSD  | xowl-server.jar$META-INF/licenses/de.svenkubiak.jBCrypt |
| Java library [`org.clojure:clojure:1.8.0`](http://clojure.org) | Copyright (c) Rich Hickey | EPL v1.0 | xowl-server.jar$META-INF/licenses/org.clojure.clojure |
| Web Framework [`Bootstrap`](http://getbootstrap.com/) | Copyright (c) 2011-2016 Twitter, Inc. | MIT | xowl-server.jar$META-INF/licenses/bootstrap |
| RDF Schema [`http://www.w3.org/1999/02/22-rdf-syntax-ns`](http://www.w3.org/1999/02/22-rdf-syntax-ns) | Copyright (c) 2009 World Wide Web Consortium, (MIT, ERCIM, Keio, Beihang) | W3C Document License | xowl-server.jar$META-INF/licenses/org.w3c.www.schemas |
| RDF Schema [`http://www.w3.org/2000/01/rdf-schema`](http://www.w3.org/2000/01/rdf-schema) | Copyright (c) 2000 World Wide Web Consortium, (MIT, ERCIM, Keio, Beihang) | W3C Document License | xowl-server.jar$META-INF/licenses/org.w3c.www.schemas |
| RDF Schema [`http://www.w3.org/2002/07/owl`](http://www.w3.org/2002/07/owl) | Copyright (c) 2002 World Wide Web Consortium, (MIT, ERCIM, Keio, Beihang) | W3C Document License | xowl-server.jar$META-INF/licenses/org.w3c.www.schemas |



# Distribution Content

This distribution contains:

* `LICENSE.txt`, the full text of the GNU LGPL v3 license under which this application is provided.
* `xowl-server.manifest`, the manifest file containing information about the version of this application
* `xowl-server.jar`, the main Java application
* `xowl-server.ini`, the configuration file for the application

* `admin.sh`, the administration script for starting and stopping the application
* `do-run.sh`, the helper script used by admin.sh to launch the application
* `install-daemon.sh`, the script to install the application as a Linux service
* `uninstall-daemon.sh`, the script to uninstall the Linux service



# Usage

## Manual Administration

To simply launch the application, run

```
$ sh admin.sh start
```

The web application for the administration of the application is available at: https://localhost:3443/web/
The default administrator is:
* login: admin
* password: admin

The server can be stopped or restart from the web application.
Otherwise, it can be managed from the command line:
```
$ sh admin.sh stop
$ sh admin.sh restart
$ sh admin.sh status
```

## Linux Service

To run the server as a Linux service, first register the service with:

```
$ sh install-daemon.sh
```

The service can then be managed with the usual commands:

```
$ service xowl-server start|stop|status|restart
```

The service can be uninstalled with:

```
$ sh uninstall-daemon.sh
```



# Configuration

TODO: fill this