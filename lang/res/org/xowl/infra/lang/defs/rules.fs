Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)
Prefix(:=<http://xowl.org/infra/lang/rules#>)
Prefix(owl2:=<http://xowl.org/infra/lang/owl2#>)

Ontology(<http://xowl.org/infra/lang/rules>
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

FunctionalObjectProperty(:guard)
ObjectPropertyDomain(:guard :Rule)
ObjectPropertyRange(:guard owl2:LiteralExpression)
)
