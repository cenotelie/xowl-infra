# xOWL Infrastructure - Language Implementation #

The commons components implements the abstract syntax (data model) of the xOWL language (OWL2, action language and rule language).
The code of this component is completely generated from the language ontologies.

The ontologies specifying the abstract syntax of the xOWL language are located in the `res` folder, in the package `org.xowl.infra.lang.defs`.
The abstract syntax is split into:

* `owl2.fs` The abstract syntax of the full OWL2.
* `actions.fs` The abstract syntax for the xOWL behavior.
* `rules.fs` The abstract syntax for the xOWL rules.
* `instrumentation.fs` The abstract syntax for the annotation of input ontologies.
* `runtime.fs` The specification of the runtime data model.