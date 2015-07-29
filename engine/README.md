# README #

This component extends the store component by adding the capabilities of evaluating and executing Clojure code embedded into xOWL datasets.
It relies on the standard Clojure interpreter (in dependency).
This component is made separate from store so that applications that use the store without using behaviors in their data do not have to pull Clojure as a dependency.
