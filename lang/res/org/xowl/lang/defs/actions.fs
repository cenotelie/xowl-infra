Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)
Prefix(:=<http://xowl.org/lang/actions#>)
Prefix(owl2:=<http://xowl.org/lang/owl2#>)


Ontology(<http://xowl.org/lang/actions>
Declaration(Class(:DynamicExpression))
SubClassOf(:DynamicExpression owl2:EntityExpression)
SubClassOf(:DynamicExpression owl2:LiteralExpression)

Declaration(Class(:QueryVariable))
SubClassOf(:QueryVariable :DynamicExpression)
Declaration(DataProperty(:name))
FunctionalDataProperty(:name)
DataPropertyDomain(:name :QueryVariable)
DataPropertyRange(:name xsd:string)

Declaration(Class(:OpaqueExpression))
SubClassOf(:OpaqueExpression :DynamicExpression)
Declaration(ObjectProperty(:value))
FunctionalObjectProperty(:value)
ObjectPropertyDomain(:value :OpaqueExpression)
ObjectPropertyRange(:value <http://www.w3.org/2002/07/owl#Thing>)


Declaration(Class(:FunctionExpression))
SubClassOf(:FunctionExpression owl2:Expression)
SubClassOf(owl2:EntityExpression :FunctionExpression)

Declaration(Class(:FunctionDefinitionAxiom))
SubClassOf(:FunctionDefinitionAxiom owl2:Axiom)
Declaration(ObjectProperty(:function))
FunctionalObjectProperty(:function)
ObjectPropertyDomain(:function :FunctionDefinitionAxiom)
ObjectPropertyRange(:function :FunctionExpression)
Declaration(ObjectProperty(:definition))
FunctionalObjectProperty(:definition)
ObjectPropertyDomain(:definition :FunctionDefinitionAxiom)
ObjectPropertyRange(:definition <http://www.w3.org/2002/07/owl#Thing>)
)
