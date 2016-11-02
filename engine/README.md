# README #

This component extends the store component by adding the capabilities of evaluating and executing Clojure code embedded into xOWL datasets.
It relies on the standard Clojure interpreter (in dependency).
This component is made separate from store so that applications that use the store without using behaviors in their data do not have to pull Clojure as a dependency.

## Dependencies

This component has the following external dependencies.

### Clojure

* Sources available at [https://github.com/clojure/clojure/](https://github.com/clojure/clojure/)
* Licenced under [Eclipse Public License 1.0](https://opensource.org/licenses/eclipse-1.0.php)