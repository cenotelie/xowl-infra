Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)
Prefix(:=<http://xowl.org/infra/lang/runtime#>)
Prefix(owl2:=<http://xowl.org/infra/lang/owl2#>)
Prefix(actions:=<http://xowl.org/infra/lang/actions#>)

Ontology(<http://xowl.org/infra/lang/runtime>
SubClassOf(owl2:AnonymousIndividual :Individual)
SubClassOf(owl2:AnonymousIndividual :Value)
Declaration(Class(:AnnotationProperty))
SubClassOf(:AnnotationProperty :Interpretation)
Declaration(Class(:CardinalityRestriction))
SubClassOf(:CardinalityRestriction :ClassRestriction)
Declaration(Class(:Class))
SubClassOf(:Class :Interpretation)
Declaration(Class(:ClassRestriction))
Declaration(Class(:DataAllValuesFrom))
SubClassOf(:DataAllValuesFrom :NAryDataPropertyRestriction)
Declaration(Class(:DataCardinalityRestriction))
SubClassOf(:DataCardinalityRestriction :CardinalityRestriction)
SubClassOf(:DataCardinalityRestriction :DataPropertyRestriction)
Declaration(Class(:DataExactCardinality))
SubClassOf(:DataExactCardinality :DataCardinalityRestriction)
Declaration(Class(:DataHasValue))
SubClassOf(:DataHasValue :DataPropertyRestriction)
Declaration(Class(:DataMaxCardinality))
SubClassOf(:DataMaxCardinality :DataCardinalityRestriction)
Declaration(Class(:DataMinCardinality))
SubClassOf(:DataMinCardinality :DataCardinalityRestriction)
Declaration(Class(:DataProperty))
SubClassOf(:DataProperty :Property)
SubClassOf(:DataProperty ObjectAllValuesFrom(:propertyDisjointWith :DataProperty))
SubClassOf(:DataProperty ObjectAllValuesFrom(:propertyEquivalentTo :DataProperty))
SubClassOf(:DataProperty ObjectAllValuesFrom(:range :Datatype))
SubClassOf(:DataProperty ObjectAllValuesFrom(:subPropertyOf :DataProperty))
SubClassOf(:DataProperty ObjectAllValuesFrom(:superPropertyOf :DataProperty))
Declaration(Class(:DataPropertyAssertion))
SubClassOf(:DataPropertyAssertion :PropertyAssertion)
SubClassOf(:DataPropertyAssertion ObjectAllValuesFrom(:property :DataProperty))
Declaration(Class(:DataPropertyRestriction))
SubClassOf(:DataPropertyRestriction :ClassRestriction)
Declaration(Class(:DataSomeValuesFrom))
SubClassOf(:DataSomeValuesFrom :NAryDataPropertyRestriction)
Declaration(Class(:Datatype))
SubClassOf(:Datatype :Interpretation)
Declaration(Class(:DatatypeRestriction))
Declaration(Class(:Entity))
SubClassOf(:Entity :Value)
Declaration(Class(:Function))
SubClassOf(:Function :Interpretation)
Declaration(Class(:Individual))
Declaration(Class(:Interpretation))
Declaration(Class(:Literal))
SubClassOf(:Literal :Value)
Declaration(Class(:NAryDataPropertyRestriction))
SubClassOf(:NAryDataPropertyRestriction :ClassRestriction)
Declaration(Class(:NamedIndividual))
SubClassOf(:NamedIndividual :Individual)
SubClassOf(:NamedIndividual :Interpretation)
Declaration(Class(:ObjectAllValuesFrom))
SubClassOf(:ObjectAllValuesFrom :ObjectPropertyRestriction)
Declaration(Class(:ObjectCardinalityRestriction))
SubClassOf(:ObjectCardinalityRestriction :CardinalityRestriction)
SubClassOf(:ObjectCardinalityRestriction :ObjectPropertyRestriction)
Declaration(Class(:ObjectExactCardinality))
SubClassOf(:ObjectExactCardinality :ObjectCardinalityRestriction)
Declaration(Class(:ObjectHasSelf))
SubClassOf(:ObjectHasSelf :ObjectPropertyRestriction)
Declaration(Class(:ObjectHasValue))
SubClassOf(:ObjectHasValue :ObjectPropertyRestriction)
Declaration(Class(:ObjectMaxCardinality))
SubClassOf(:ObjectMaxCardinality :ObjectCardinalityRestriction)
Declaration(Class(:ObjectMinCardinality))
SubClassOf(:ObjectMinCardinality :ObjectCardinalityRestriction)
Declaration(Class(:ObjectProperty))
SubClassOf(:ObjectProperty :Property)
SubClassOf(:ObjectProperty ObjectAllValuesFrom(:propertyDisjointWith :ObjectProperty))
SubClassOf(:ObjectProperty ObjectAllValuesFrom(:propertyEquivalentTo :ObjectProperty))
SubClassOf(:ObjectProperty ObjectAllValuesFrom(:range :Class))
SubClassOf(:ObjectProperty ObjectAllValuesFrom(:subPropertyOf :ObjectProperty))
SubClassOf(:ObjectProperty ObjectAllValuesFrom(:superPropertyOf :ObjectProperty))
Declaration(Class(:ObjectPropertyAssertion))
SubClassOf(:ObjectPropertyAssertion :PropertyAssertion)
SubClassOf(:ObjectPropertyAssertion ObjectAllValuesFrom(:property :ObjectProperty))
Declaration(Class(:ObjectPropertyRestriction))
SubClassOf(:ObjectPropertyRestriction :ClassRestriction)
Declaration(Class(:ObjectSomeValuesFrom))
SubClassOf(:ObjectSomeValuesFrom :ObjectPropertyRestriction)
Declaration(Class(:Property))
SubClassOf(:Property :Interpretation)
Declaration(Class(:PropertyAssertion))
Declaration(Class(:Value))
Declaration(ObjectProperty(:asserts))
ObjectPropertyDomain(:asserts :Individual)
ObjectPropertyRange(:asserts :PropertyAssertion)
Declaration(ObjectProperty(:chains))
ObjectPropertyDomain(:chains :ObjectProperty)
ObjectPropertyRange(:chains :ObjectProperty)
Declaration(ObjectProperty(:classComplementOf))
InverseObjectProperties(:classComplementOf :classComplementOf)
FunctionalObjectProperty(:classComplementOf)
InverseFunctionalObjectProperty(:classComplementOf)
ObjectPropertyDomain(:classComplementOf :Class)
ObjectPropertyRange(:classComplementOf :Class)
Declaration(ObjectProperty(:classDisjointWith))
InverseObjectProperties(:classDisjointWith :classDisjointWith)
ObjectPropertyDomain(:classDisjointWith :Class)
ObjectPropertyRange(:classDisjointWith :Class)
Declaration(ObjectProperty(:classEquivalentTo))
InverseObjectProperties(:classEquivalentTo :classEquivalentTo)
ObjectPropertyDomain(:classEquivalentTo :Class)
ObjectPropertyRange(:classEquivalentTo :Class)
Declaration(ObjectProperty(:classIntersectionOf))
ObjectPropertyDomain(:classIntersectionOf :Class)
ObjectPropertyRange(:classIntersectionOf :Class)
Declaration(ObjectProperty(:classOneOf))
ObjectPropertyDomain(:classOneOf :Class)
ObjectPropertyRange(:classOneOf :Individual)
Declaration(ObjectProperty(:classRestrictions))
ObjectPropertyDomain(:classRestrictions :Class)
ObjectPropertyRange(:classRestrictions :ClassRestriction)
Declaration(ObjectProperty(:classUnionOf))
ObjectPropertyDomain(:classUnionOf :Class)
ObjectPropertyRange(:classUnionOf :Class)
Declaration(ObjectProperty(:classe))
FunctionalObjectProperty(:classe)
ObjectPropertyDomain(:classe ObjectUnionOf(:ObjectAllValuesFrom :ObjectCardinalityRestriction :ObjectSomeValuesFrom))
ObjectPropertyRange(:classe :Class)
Declaration(ObjectProperty(:classifiedBy))
InverseObjectProperties(:classifies :classifiedBy)
ObjectPropertyDomain(:classifiedBy :Individual)
ObjectPropertyRange(:classifiedBy :Class)
Declaration(ObjectProperty(:classifies))
InverseObjectProperties(:classifies :classifiedBy)
ObjectPropertyDomain(:classifies :Class)
ObjectPropertyRange(:classifies :Individual)
Declaration(ObjectProperty(:containedBy))
InverseObjectProperties(:containedBy :contains)
FunctionalObjectProperty(:containedBy)
ObjectPropertyDomain(:containedBy :Entity)
ObjectPropertyRange(:containedBy owl2:Ontology)
Declaration(ObjectProperty(:contains))
InverseObjectProperties(:containedBy :contains)
InverseFunctionalObjectProperty(:contains)
ObjectPropertyDomain(:contains owl2:Ontology)
ObjectPropertyRange(:contains :Entity)
Declaration(ObjectProperty(:dataBase))
FunctionalObjectProperty(:dataBase)
ObjectPropertyDomain(:dataBase :Datatype)
ObjectPropertyRange(:dataBase :Datatype)
Declaration(ObjectProperty(:dataComplementOf))
InverseObjectProperties(:dataComplementOf :dataComplementOf)
FunctionalObjectProperty(:dataComplementOf)
ObjectPropertyDomain(:dataComplementOf :Datatype)
ObjectPropertyRange(:dataComplementOf :Datatype)
Declaration(ObjectProperty(:dataIntersectionOf))
ObjectPropertyDomain(:dataIntersectionOf :Datatype)
ObjectPropertyRange(:dataIntersectionOf :Datatype)
Declaration(ObjectProperty(:dataOneOf))
ObjectPropertyDomain(:dataOneOf :Datatype)
ObjectPropertyRange(:dataOneOf :Literal)
Declaration(ObjectProperty(:dataProperties))
ObjectPropertyDomain(:dataProperties :NAryDataPropertyRestriction)
ObjectPropertyRange(:dataProperties :DataProperty)
Declaration(ObjectProperty(:dataProperty))
FunctionalObjectProperty(:dataProperty)
ObjectPropertyDomain(:dataProperty :DataPropertyRestriction)
ObjectPropertyRange(:dataProperty :DataProperty)
Declaration(ObjectProperty(:dataRestrictions))
ObjectPropertyDomain(:dataRestrictions :Datatype)
ObjectPropertyRange(:dataRestrictions :DatatypeRestriction)
Declaration(ObjectProperty(:dataUnionOf))
ObjectPropertyDomain(:dataUnionOf :Datatype)
ObjectPropertyRange(:dataUnionOf :Datatype)
Declaration(ObjectProperty(:datatype))
FunctionalObjectProperty(:datatype)
ObjectPropertyDomain(:datatype ObjectUnionOf(:NAryDataPropertyRestriction :DataCardinalityRestriction))
ObjectPropertyRange(:datatype :Datatype)
Declaration(ObjectProperty(:definedAs))
FunctionalObjectProperty(:definedAs)
ObjectPropertyDomain(:definedAs :Function)
ObjectPropertyRange(:definedAs actions:OpaqueExpression)
Declaration(ObjectProperty(:differentFrom))
InverseObjectProperties(:differentFrom :differentFrom)
ObjectPropertyDomain(:differentFrom :Individual)
ObjectPropertyRange(:differentFrom :Individual)
Declaration(ObjectProperty(:domain))
InverseObjectProperties(:domainOf :domain)
FunctionalObjectProperty(:domain)
ObjectPropertyDomain(:domain :Property)
ObjectPropertyRange(:domain :Class)
Declaration(ObjectProperty(:domainOf))
InverseObjectProperties(:domainOf :domain)
ObjectPropertyDomain(:domainOf :Class)
ObjectPropertyRange(:domainOf :Property)
Declaration(ObjectProperty(:facet))
FunctionalObjectProperty(:facet)
ObjectPropertyDomain(:facet :DatatypeRestriction)
ObjectPropertyRange(:facet owl2:IRI)
Declaration(ObjectProperty(:hasIRI))
FunctionalObjectProperty(:hasIRI)
ObjectPropertyDomain(:hasIRI :Entity)
ObjectPropertyRange(:hasIRI owl2:IRI)
Declaration(ObjectProperty(:individual))
FunctionalObjectProperty(:individual)
ObjectPropertyDomain(:individual :ObjectHasValue)
ObjectPropertyRange(:individual :Individual)
Declaration(ObjectProperty(:interpretationOf))
InverseObjectProperties(:interpretedAs :interpretationOf)
FunctionalObjectProperty(:interpretationOf)
ObjectPropertyDomain(:interpretationOf :Interpretation)
ObjectPropertyRange(:interpretationOf :Entity)
Declaration(ObjectProperty(:interpretedAs))
InverseObjectProperties(:interpretedAs :interpretationOf)
InverseFunctionalObjectProperty(:interpretedAs)
ObjectPropertyDomain(:interpretedAs :Entity)
ObjectPropertyRange(:interpretedAs :Interpretation)
Declaration(ObjectProperty(:inverseOf))
InverseObjectProperties(:inverseOf :inverseOf)
FunctionalObjectProperty(:inverseOf)
ObjectPropertyDomain(:inverseOf :ObjectProperty)
ObjectPropertyRange(:inverseOf :ObjectProperty)
Declaration(ObjectProperty(:literal))
FunctionalObjectProperty(:literal)
ObjectPropertyDomain(:literal :DataHasValue)
ObjectPropertyRange(:literal :Literal)
Declaration(ObjectProperty(:memberOf))
FunctionalObjectProperty(:memberOf)
ObjectPropertyDomain(:memberOf :Literal)
ObjectPropertyRange(:memberOf :Datatype)
Declaration(ObjectProperty(:objectProperty))
FunctionalObjectProperty(:objectProperty)
ObjectPropertyDomain(:objectProperty :ObjectPropertyRestriction)
ObjectPropertyRange(:objectProperty :ObjectProperty)
Declaration(ObjectProperty(:property))
FunctionalObjectProperty(:property)
ObjectPropertyDomain(:property :PropertyAssertion)
ObjectPropertyRange(:property :Property)
Declaration(ObjectProperty(:propertyDisjointWith))
InverseObjectProperties(:propertyDisjointWith :propertyDisjointWith)
ObjectPropertyDomain(:propertyDisjointWith :Property)
ObjectPropertyRange(:propertyDisjointWith :Property)
Declaration(ObjectProperty(:propertyEquivalentTo))
InverseObjectProperties(:propertyEquivalentTo :propertyEquivalentTo)
ObjectPropertyDomain(:propertyEquivalentTo :Property)
ObjectPropertyRange(:propertyEquivalentTo :Property)
Declaration(ObjectProperty(:range))
FunctionalObjectProperty(:range)
ObjectPropertyDomain(:range :Property)
ObjectPropertyRange(:range ObjectUnionOf(:Datatype :Class))
Declaration(ObjectProperty(:sameAs))
InverseObjectProperties(:sameAs :sameAs)
ObjectPropertyDomain(:sameAs :Individual)
ObjectPropertyRange(:sameAs :Individual)
Declaration(ObjectProperty(:subAnnotProperty))
InverseObjectProperties(:superAnnotProperty :subAnnotProperty)
ObjectPropertyDomain(:subAnnotProperty :AnnotationProperty)
ObjectPropertyRange(:subAnnotProperty :AnnotationProperty)
Declaration(ObjectProperty(:subClassOf))
InverseObjectProperties(:subClassOf :superClassOf)
ObjectPropertyDomain(:subClassOf :Class)
ObjectPropertyRange(:subClassOf :Class)
Declaration(ObjectProperty(:subPropertyOf))
InverseObjectProperties(:subPropertyOf :superPropertyOf)
ObjectPropertyDomain(:subPropertyOf :Property)
ObjectPropertyRange(:subPropertyOf :Property)
Declaration(ObjectProperty(:superAnnotProperty))
InverseObjectProperties(:superAnnotProperty :subAnnotProperty)
ObjectPropertyDomain(:superAnnotProperty :AnnotationProperty)
ObjectPropertyRange(:superAnnotProperty :AnnotationProperty)
Declaration(ObjectProperty(:superClassOf))
InverseObjectProperties(:subClassOf :superClassOf)
ObjectPropertyDomain(:superClassOf :Class)
ObjectPropertyRange(:superClassOf :Class)
Declaration(ObjectProperty(:superPropertyOf))
InverseObjectProperties(:subPropertyOf :superPropertyOf)
ObjectPropertyDomain(:superPropertyOf :Property)
ObjectPropertyRange(:superPropertyOf :Property)
Declaration(ObjectProperty(:valueIndividual))
FunctionalObjectProperty(:valueIndividual)
ObjectPropertyDomain(:valueIndividual :ObjectPropertyAssertion)
ObjectPropertyRange(:valueIndividual :Individual)
Declaration(ObjectProperty(:valueLiteral))
FunctionalObjectProperty(:valueLiteral)
ObjectPropertyDomain(:valueLiteral ObjectUnionOf(:DatatypeRestriction :DataPropertyAssertion))
ObjectPropertyRange(:valueLiteral :Literal)
Declaration(DataProperty(:cardinality))
FunctionalDataProperty(:cardinality)
DataPropertyDomain(:cardinality :CardinalityRestriction)
DataPropertyRange(:cardinality xsd:integer)
Declaration(DataProperty(:isAsymmetric))
FunctionalDataProperty(:isAsymmetric)
DataPropertyDomain(:isAsymmetric :ObjectProperty)
DataPropertyRange(:isAsymmetric xsd:boolean)
Declaration(DataProperty(:isFunctional))
FunctionalDataProperty(:isFunctional)
DataPropertyDomain(:isFunctional :Property)
DataPropertyRange(:isFunctional xsd:boolean)
Declaration(DataProperty(:isInverseFunctional))
FunctionalDataProperty(:isInverseFunctional)
DataPropertyDomain(:isInverseFunctional :ObjectProperty)
DataPropertyRange(:isInverseFunctional xsd:boolean)
Declaration(DataProperty(:isIrreflexive))
FunctionalDataProperty(:isIrreflexive)
DataPropertyDomain(:isIrreflexive :ObjectProperty)
DataPropertyRange(:isIrreflexive xsd:boolean)
Declaration(DataProperty(:isNegative))
FunctionalDataProperty(:isNegative)
DataPropertyDomain(:isNegative :PropertyAssertion)
DataPropertyRange(:isNegative xsd:boolean)
Declaration(DataProperty(:isReflexive))
FunctionalDataProperty(:isReflexive)
DataPropertyDomain(:isReflexive :ObjectProperty)
DataPropertyRange(:isReflexive xsd:boolean)
Declaration(DataProperty(:isSymmetric))
FunctionalDataProperty(:isSymmetric)
DataPropertyDomain(:isSymmetric :ObjectProperty)
DataPropertyRange(:isSymmetric xsd:boolean)
Declaration(DataProperty(:isTransitive))
FunctionalDataProperty(:isTransitive)
DataPropertyDomain(:isTransitive :ObjectProperty)
DataPropertyRange(:isTransitive xsd:boolean)
Declaration(DataProperty(:lexicalValue))
FunctionalDataProperty(:lexicalValue)
DataPropertyDomain(:lexicalValue :Literal)
DataPropertyRange(:lexicalValue xsd:string)
Declaration(DataProperty(:langTag))
FunctionalDataProperty(:langTag)
DataPropertyDomain(:langTag :Literal)
DataPropertyRange(:langTag xsd:string)
)
