Prefix( : = <http://example.org/> )

Ontology(
  Declaration( Class( :Boy ) )
  Declaration( Class( :Girl ) )
  Declaration( Class( :Dog ) )

  DisjointClasses( :Boy :Girl :Dog )
  ClassAssertion( :Boy :Stewie )
)