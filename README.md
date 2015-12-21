# xOWL Infrastructure #

This is the xOWL Infrastructure, a set of components for working with datasets in the semantic web and linked data world:

* Manipulate RDF and OWL2 datasets (N-Triples, N-Quads, Turtle, RDF/XML, JSON-LD, OWL/XML, Functional OWL)
* Query engine with RDF and OWL2 interfaces
* Rule engine with RDF and OWL2 interfaces
* Expression and execution of arbitrary behavior in data using Clojure
* Deployable triple store server

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
$ docker pull xowl/xowl-server:1.0-beta2
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