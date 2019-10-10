# xOWL Infrastructure #

[![Build Status](https://dev.azure.com/cenotelie/cenotelie/_apis/build/status/cenotelie.xowl-infra?branchName=master)](https://dev.azure.com/cenotelie/cenotelie/_build/latest?definitionId=3&branchName=master)

This is the xOWL Infrastructure, a set of components for working with datasets in the semantic web and linked data world:

* Manipulate RDF and OWL2 datasets (N-Triples, N-Quads, Turtle, RDF/XML, JSON-LD, OWL/XML, Functional OWL)
* Query engine with RDF and OWL2 interfaces
* Rule engine with RDF and OWL2 interfaces
* Expression and execution of arbitrary behavior in data using [Clojure](http://clojure.org)
* Deployable triple store server

xOWL is also the name of the language used to express behavior within OWL2 ontologies.
xOWL basically enable the inclusion of snippets of [Clojure](http://clojure.org) code as named entities in ontologies.
In OWL2 ontologies, an entity can be attributed several interpretations: as a class, as an individual (instance), as an object or data property, etc.
With the xOWL extension, an entity can have a new behavioral interpretation defined in a piece of [Clojure](http://clojure.org) code:

```
Prefix(xsd: = <http://www.w3.org/2001/XMLSchema#>)
Prefix( : = <http://xowl.org/infra/engine/tests/Sample#>)
Ontology( <http://xowl.org/infra/engine/tests/Sample>
    FunctionDefinition(:hello (fn [] "Hello World"))
    DataPropertyAssertion(:hasName :hello "A function that says hello.")
)
```

## How do I use this software? ##

### As Java libraries ###

The xOWL Infrastructure can be used as embeddable Java libraries. With Maven, most features can be included with:

```
<dependency>
    <groupId>org.xowl.infra</groupId>
    <artifactId>xowl-store</artifactId>
    <version>2.1.0-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```

For the inclusion of the expression and execution of behavior in ontologies, also include the xowl-engine bundle:

```
#!xml
<artifactId>xowl-engine</artifactId>
```

All libraries in the xOWL Infrastructure and their dependencies are OSGi bundles that can be readily deployed on OSGi platforms such as [Apache Felix](http://felix.apache.org/), or [Eclipse Equinox](http://www.eclipse.org/equinox/).

### Triple Store Server ###

The xOWL Infrastructure also implements a triple store server that can be used through the [downloadable distribution](https://bitbucket.org/cenotelie/xowl-infra/downloads) or by using the Docker image.

To use the [downloadable distribution](https://bitbucket.org/cenotelie/xowl-infra/downloads) (Java 8 required), simply download it.
Then to administer the server, use the provided `admin.sh` script:

```
# Launch the server
$ ./admin.sh start
# Stop the server
$ ./admin.sh stop
```

With a web-browser, go to [https://localhost:3443/web/](https://localhost:3443/web/).
The default administrator login and password are `admin` and `admin`.

To install the xOWL triple store server as a linux daemon, simply run (sudo will be asked for):

```
$ ./install-daemon.sh
```

Then, the daemon can be controlled as usual:

```
$ sudo service xowl-server start
$ sudo service xowl-server stop
$ sudo service xowl-server restart
```

The triple store server is also available as a Docker image at `nexus.cenotelie.fr/xowl/xowl-server:latest`.
To run the latest image:

```
$ docker run -d -p 3443:3443/tcp --name my-xowl-instance -v /path/to/host/data:/xowl-data nexus.cenotelie.fr/xowl/xowl-server:latest
```

Replace the `/path/to/host/data` to a path where to store the databases on your system.
With a web-browser, go to [https://localhost:3443/web/](https://localhost:3443/web/).
The default administrator login and password are `admin` and `admin`.


## How to build ##

To build the artifacts in this repository using Maven:

```
$ mvn clean install -Dgpg.skip=true
```

Then, to build the other redistributable artifacts (redistributable package and Docker image):

```
$ ./.releng/build.sh
```

For this, Docker must be locally installed.


## How can I contribute? ##

The simplest way to contribute is to:

* Fork this repository on [Bitbucket](https://bitbucket.org/cenotelie/xowl-infra).
* Fix [some issue](https://bitbucket.org/cenotelie/xowl-infra/issues?status=new&status=open) or implement a new feature.
* Create a pull request on Bitbucket.

Patches can also be submitted by email, or through the [issue management system](https://bitbucket.org/cenotelie/xowl-infra/issues).

The [isse tracker](https://bitbucket.org/cenotelie/xowl-infra/issues) may contain tickets that are accessible to newcomers. Look for tickets with `[beginner]` in the title. These tickets are good ways to become more familiar with the project and the codebase.


## License ##

This software is licenced under the Lesser General Public License (LGPL) v3.
Refers to the `LICENSE.txt` file at the root of the repository for the full text, or to [the online version](http://www.gnu.org/licenses/lgpl-3.0.html).