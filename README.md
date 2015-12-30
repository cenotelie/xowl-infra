# xOWL Infrastructure #

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
Prefix( : = <http://xowl.org/engine/tests/Sample#>)
Ontology( <http://xowl.org/engine/tests/Sample>
    FunctionDefinition(:hello (fn [] "Hello World"))
    DataPropertyAssertion(:hasName :hello "A function that says hello.")
)
```

## Java libraries ##

The xOWL Infrastructure can be used as embeddable Java libraries. With Maven, most features can be included with:

```
#!xml
<dependency>
    <groupId>org.xowl.infra</groupId>
    <artifactId>xowl-store</artifactId>
    <version>1.0-beta2</version>
    <scope>compile</scope>
</dependency>
```

For the inclusion of the expression of execution of behavior in ontologies, also include the xowl-engine bundle:

```
#!xml
<artifactId>xowl-engine</artifactId>
```


## Triple Store Server ##

To use the xOWL Infrastructure as a triple store server, either use the downloadable distribution, or the Docker image.

### Downloadable distribution ###

```
$ java -jar xowl-server.jar
```

With a web-browser, go to [https://localhost:3443/web/](https://localhost:3443/web/).
The default administrator login and password are `admin` and `admin`.

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

### Docker image ###

```
$ docker pull xowl/xowl-server:latest
$ docker run -d -p 3443:3443/tcp --name my-xowl-instance -v /path/to/host/data:/xowl-data xowl-server:latest
```

Replace the `/path/to/host/data` to a path where to store the databases on your system.
With a web-browser, go to [https://localhost:3443/web/](https://localhost:3443/web/).
The default administrator login and password are `admin` and `admin`.

## License ##

This software is licenced under the Lesser General Public License (LGPL) v3.
Refers to the `LICENSE.txt` file at the root of the repository for the full text, or to [the online version](http://www.gnu.org/licenses/lgpl-3.0.html).


## How to build ##

### Build Java libraries ###

To only build the xOWL libraries as a set of Java libraries, use maven:

```
$ mvn clean install
```

### Build redistributable artifacts ###

To build the redistributable artifacts (server, client, Docker image, etc.):

```
$ ./.releng/build.sh
```

For this, Docker must be locally installed.