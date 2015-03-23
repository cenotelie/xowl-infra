Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)
Prefix(:=<http://xowl.org/lang/rules#>)
Prefix(owl2:=<http://xowl.org/lang/owl2#>)
Prefix(actions:=<http://xowl.org/lang/actions#>)

Ontology(<http://xowl.org/lang/rules>
FunctionalDataProperty(:isPositive)
DataPropertyDomain(:isPositive :Assertion)
DataPropertyRange(:isPositive xsd:boolean)

FunctionalDataProperty(:isMeta)
DataPropertyDomain(:isMeta :Assertion)
DataPropertyRange(:isMeta xsd:boolean)

ObjectPropertyDomain(:axioms :Assertion)
ObjectPropertyRange(:axioms owl2:Axiom)

FunctionalObjectProperty(:hasIRI)
ObjectPropertyDomain(:hasIRI :Rule)
ObjectPropertyRange(:hasIRI owl2:IRI)

ObjectPropertyDomain(:antecedents :Rule)
ObjectPropertyRange(:antecedents :Assertion)

ObjectPropertyDomain(:consequents :Rule)
ObjectPropertyRange(:consequents :Assertion)

ObjectPropertyDomain(:guards :Rule)
ObjectPropertyRange(:guards owl2:LiteralExpression)
)