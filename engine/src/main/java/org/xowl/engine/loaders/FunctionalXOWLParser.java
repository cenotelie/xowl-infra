/*
 * WARNING: this file has been generated by
 * Hime Parser Generator 1.3.2.0
 */

package org.xowl.engine.loaders;

import org.xowl.hime.redist.Symbol;
import org.xowl.hime.redist.parsers.InitializationException;
import org.xowl.hime.redist.parsers.RNGLRAutomaton;
import org.xowl.hime.redist.parsers.RNGLRParser;

/**
 * Represents a parser
 */
class FunctionalXOWLParser extends RNGLRParser {
    /**
     * The automaton for this parser
     */
    private static final RNGLRAutomaton automaton = RNGLRAutomaton.find(FunctionalXOWLParser.class, "FunctionalXOWLParser.bin");

    /**
     * Contains the constant IDs for the variables and virtuals in this parser
     */
    public static class ID {
        /**
         * The unique identifier for variable document
         */
        public static final int document = 0x0014;
        /**
         * The unique identifier for variable prefixes
         */
        public static final int prefixes = 0x0015;
        /**
         * The unique identifier for variable prefix
         */
        public static final int prefix = 0x0016;
        /**
         * The unique identifier for variable ontology
         */
        public static final int ontology = 0x0017;
        /**
         * The unique identifier for variable ontologyIRI
         */
        public static final int ontologyIRI = 0x0018;
        /**
         * The unique identifier for variable iri
         */
        public static final int iri = 0x0019;
        /**
         * The unique identifier for variable imports
         */
        public static final int imports = 0x001A;
        /**
         * The unique identifier for variable import
         */
        public static final int _import = 0x001B;
        /**
         * The unique identifier for variable elements
         */
        public static final int elements = 0x001C;
        /**
         * The unique identifier for variable element
         */
        public static final int element = 0x001D;
        /**
         * The unique identifier for variable annotations
         */
        public static final int annotations = 0x001E;
        /**
         * The unique identifier for variable annotation
         */
        public static final int annotation = 0x001F;
        /**
         * The unique identifier for variable annotationSubject
         */
        public static final int annotationSubject = 0x0020;
        /**
         * The unique identifier for variable annotationProperty
         */
        public static final int annotationProperty = 0x0021;
        /**
         * The unique identifier for variable annotationValue
         */
        public static final int annotationValue = 0x0022;
        /**
         * The unique identifier for variable entity
         */
        public static final int entity = 0x0023;
        /**
         * The unique identifier for variable individual
         */
        public static final int individual = 0x0024;
        /**
         * The unique identifier for variable namedIndividual
         */
        public static final int namedIndividual = 0x0025;
        /**
         * The unique identifier for variable anonymousIndividual
         */
        public static final int anonymousIndividual = 0x0026;
        /**
         * The unique identifier for variable literal
         */
        public static final int literal = 0x0027;
        /**
         * The unique identifier for variable literalTyped
         */
        public static final int literalTyped = 0x0028;
        /**
         * The unique identifier for variable literalString
         */
        public static final int literalString = 0x0029;
        /**
         * The unique identifier for variable literalLang
         */
        public static final int literalLang = 0x002A;
        /**
         * The unique identifier for variable expObjectProperty
         */
        public static final int expObjectProperty = 0x002B;
        /**
         * The unique identifier for variable expObjectPropertyChain
         */
        public static final int expObjectPropertyChain = 0x002C;
        /**
         * The unique identifier for variable expInverseObjectProperty
         */
        public static final int expInverseObjectProperty = 0x002D;
        /**
         * The unique identifier for variable expDataProperty
         */
        public static final int expDataProperty = 0x002E;
        /**
         * The unique identifier for variable expDatarange
         */
        public static final int expDatarange = 0x002F;
        /**
         * The unique identifier for variable expDataIntersectionOf
         */
        public static final int expDataIntersectionOf = 0x0030;
        /**
         * The unique identifier for variable expDataUnionOf
         */
        public static final int expDataUnionOf = 0x0031;
        /**
         * The unique identifier for variable expDataComplementOf
         */
        public static final int expDataComplementOf = 0x0032;
        /**
         * The unique identifier for variable expDataOneOf
         */
        public static final int expDataOneOf = 0x0033;
        /**
         * The unique identifier for variable expDatatypeRestriction
         */
        public static final int expDatatypeRestriction = 0x0034;
        /**
         * The unique identifier for variable expRestriction
         */
        public static final int expRestriction = 0x0035;
        /**
         * The unique identifier for variable expClass
         */
        public static final int expClass = 0x0036;
        /**
         * The unique identifier for variable expObjectInterfactionOf
         */
        public static final int expObjectInterfactionOf = 0x0037;
        /**
         * The unique identifier for variable expObjectUnionOf
         */
        public static final int expObjectUnionOf = 0x0038;
        /**
         * The unique identifier for variable expObjectComplementOf
         */
        public static final int expObjectComplementOf = 0x0039;
        /**
         * The unique identifier for variable expObjectOneOf
         */
        public static final int expObjectOneOf = 0x003A;
        /**
         * The unique identifier for variable expObjectSomeValuesFrom
         */
        public static final int expObjectSomeValuesFrom = 0x003B;
        /**
         * The unique identifier for variable expObjectAllValuesFrom
         */
        public static final int expObjectAllValuesFrom = 0x003C;
        /**
         * The unique identifier for variable expObjectHasValue
         */
        public static final int expObjectHasValue = 0x003D;
        /**
         * The unique identifier for variable expObjectHasSelf
         */
        public static final int expObjectHasSelf = 0x003E;
        /**
         * The unique identifier for variable expObjectMinCardinality
         */
        public static final int expObjectMinCardinality = 0x003F;
        /**
         * The unique identifier for variable expObjectMaxCardinality
         */
        public static final int expObjectMaxCardinality = 0x0040;
        /**
         * The unique identifier for variable expObjectExactCardinality
         */
        public static final int expObjectExactCardinality = 0x0041;
        /**
         * The unique identifier for variable expDataSomeValuesFrom
         */
        public static final int expDataSomeValuesFrom = 0x0042;
        /**
         * The unique identifier for variable expDataAllValuesFrom
         */
        public static final int expDataAllValuesFrom = 0x0043;
        /**
         * The unique identifier for variable expDataHasValue
         */
        public static final int expDataHasValue = 0x0044;
        /**
         * The unique identifier for variable expDataMinCardinality
         */
        public static final int expDataMinCardinality = 0x0045;
        /**
         * The unique identifier for variable expDataMaxCardinality
         */
        public static final int expDataMaxCardinality = 0x0046;
        /**
         * The unique identifier for variable expDataExactCardinality
         */
        public static final int expDataExactCardinality = 0x0047;
        /**
         * The unique identifier for variable axiom
         */
        public static final int axiom = 0x0048;
        /**
         * The unique identifier for variable axiomDeclaration
         */
        public static final int axiomDeclaration = 0x0049;
        /**
         * The unique identifier for variable axiomClass
         */
        public static final int axiomClass = 0x004A;
        /**
         * The unique identifier for variable axiomSubClassOf
         */
        public static final int axiomSubClassOf = 0x004B;
        /**
         * The unique identifier for variable axiomEquivalentClasses
         */
        public static final int axiomEquivalentClasses = 0x004C;
        /**
         * The unique identifier for variable axiomDisjointClasses
         */
        public static final int axiomDisjointClasses = 0x004D;
        /**
         * The unique identifier for variable axiomDisjointUnion
         */
        public static final int axiomDisjointUnion = 0x004E;
        /**
         * The unique identifier for variable axiomObjectProperty
         */
        public static final int axiomObjectProperty = 0x004F;
        /**
         * The unique identifier for variable axiomSubOjectPropertyOf
         */
        public static final int axiomSubOjectPropertyOf = 0x0050;
        /**
         * The unique identifier for variable axiomEquivalentObjectProperties
         */
        public static final int axiomEquivalentObjectProperties = 0x0051;
        /**
         * The unique identifier for variable axiomDisjointObjectProperties
         */
        public static final int axiomDisjointObjectProperties = 0x0052;
        /**
         * The unique identifier for variable axiomInverseObjectProperties
         */
        public static final int axiomInverseObjectProperties = 0x0053;
        /**
         * The unique identifier for variable axiomObjectPropertyDomain
         */
        public static final int axiomObjectPropertyDomain = 0x0054;
        /**
         * The unique identifier for variable axiomObjectPropertyRange
         */
        public static final int axiomObjectPropertyRange = 0x0055;
        /**
         * The unique identifier for variable axiomFunctionalObjectProperty
         */
        public static final int axiomFunctionalObjectProperty = 0x0056;
        /**
         * The unique identifier for variable axiomInverseFunctionalObjectProperty
         */
        public static final int axiomInverseFunctionalObjectProperty = 0x0057;
        /**
         * The unique identifier for variable axiomReflexiveObjectProperty
         */
        public static final int axiomReflexiveObjectProperty = 0x0058;
        /**
         * The unique identifier for variable axiomIrreflexiveObjectProperty
         */
        public static final int axiomIrreflexiveObjectProperty = 0x0059;
        /**
         * The unique identifier for variable axiomSymmetricObjectProperty
         */
        public static final int axiomSymmetricObjectProperty = 0x005A;
        /**
         * The unique identifier for variable axiomAsymmetricObjectProperty
         */
        public static final int axiomAsymmetricObjectProperty = 0x005B;
        /**
         * The unique identifier for variable axiomTransitiveObjectProperty
         */
        public static final int axiomTransitiveObjectProperty = 0x005C;
        /**
         * The unique identifier for variable axiomDataProperty
         */
        public static final int axiomDataProperty = 0x005D;
        /**
         * The unique identifier for variable axiomSubDataPropertyOf
         */
        public static final int axiomSubDataPropertyOf = 0x005E;
        /**
         * The unique identifier for variable axiomEquivalentDataProperties
         */
        public static final int axiomEquivalentDataProperties = 0x005F;
        /**
         * The unique identifier for variable axiomDisjointDataProperties
         */
        public static final int axiomDisjointDataProperties = 0x0060;
        /**
         * The unique identifier for variable axiomDataPropertyDomain
         */
        public static final int axiomDataPropertyDomain = 0x0061;
        /**
         * The unique identifier for variable axiomDataPropertyRange
         */
        public static final int axiomDataPropertyRange = 0x0062;
        /**
         * The unique identifier for variable axiomFunctionalDataProperty
         */
        public static final int axiomFunctionalDataProperty = 0x0063;
        /**
         * The unique identifier for variable axiomDatatype
         */
        public static final int axiomDatatype = 0x0064;
        /**
         * The unique identifier for variable axiomHasKey
         */
        public static final int axiomHasKey = 0x0065;
        /**
         * The unique identifier for variable axiomHasKeyObjectProperties
         */
        public static final int axiomHasKeyObjectProperties = 0x0066;
        /**
         * The unique identifier for variable axiomHasKeyDataProperties
         */
        public static final int axiomHasKeyDataProperties = 0x0067;
        /**
         * The unique identifier for variable axiomAssertion
         */
        public static final int axiomAssertion = 0x0068;
        /**
         * The unique identifier for variable axiomSameIndividual
         */
        public static final int axiomSameIndividual = 0x0069;
        /**
         * The unique identifier for variable axiomDifferentIndividuals
         */
        public static final int axiomDifferentIndividuals = 0x006A;
        /**
         * The unique identifier for variable axiomClassAssertion
         */
        public static final int axiomClassAssertion = 0x006B;
        /**
         * The unique identifier for variable axiomObjectPropertyAssertion
         */
        public static final int axiomObjectPropertyAssertion = 0x006C;
        /**
         * The unique identifier for variable axiomNegativeObjectPropertyAssertion
         */
        public static final int axiomNegativeObjectPropertyAssertion = 0x006D;
        /**
         * The unique identifier for variable axiomDataPropertyAssertion
         */
        public static final int axiomDataPropertyAssertion = 0x006E;
        /**
         * The unique identifier for variable axiomNegativeDataPropertyAssertion
         */
        public static final int axiomNegativeDataPropertyAssertion = 0x006F;
        /**
         * The unique identifier for variable axiomAnnotation
         */
        public static final int axiomAnnotation = 0x0070;
        /**
         * The unique identifier for variable axiomAnnotationAssertion
         */
        public static final int axiomAnnotationAssertion = 0x0071;
        /**
         * The unique identifier for variable axiomSubAnnotationPropertyOf
         */
        public static final int axiomSubAnnotationPropertyOf = 0x0072;
        /**
         * The unique identifier for variable axiomAnnotationPropertyDomain
         */
        public static final int axiomAnnotationPropertyDomain = 0x0073;
        /**
         * The unique identifier for variable axiomAnnotationPropertyRange
         */
        public static final int axiomAnnotationPropertyRange = 0x0074;
        /**
         * The unique identifier for variable literalInteger
         */
        public static final int literalInteger = 0x00DC;
        /**
         * The unique identifier for variable rule
         */
        public static final int rule = 0x00DD;
        /**
         * The unique identifier for variable ruleAntecedents
         */
        public static final int ruleAntecedents = 0x00DE;
        /**
         * The unique identifier for variable ruleConsequents
         */
        public static final int ruleConsequents = 0x00DF;
        /**
         * The unique identifier for variable assertion
         */
        public static final int assertion = 0x00E0;
        /**
         * The unique identifier for variable axiomPattern
         */
        public static final int axiomPattern = 0x00E1;
        /**
         * The unique identifier for variable _Axiom_
         */
        public static final int _Axiom_ = 0x00EB;
    }

    /**
     * The collection of variables matched by this parser
     * <p/>
     * The variables are in an order consistent with the automaton,
     * so that variable indices in the automaton can be used to retrieve the variables in this table
     */
    private static final Symbol[] variables = {
            new Symbol(0x0014, "document"),
            new Symbol(0x0015, "prefixes"),
            new Symbol(0x0016, "prefix"),
            new Symbol(0x0017, "ontology"),
            new Symbol(0x0018, "ontologyIRI"),
            new Symbol(0x0019, "iri"),
            new Symbol(0x001A, "imports"),
            new Symbol(0x001B, "import"),
            new Symbol(0x001C, "elements"),
            new Symbol(0x001D, "element"),
            new Symbol(0x001E, "annotations"),
            new Symbol(0x001F, "annotation"),
            new Symbol(0x0020, "annotationSubject"),
            new Symbol(0x0021, "annotationProperty"),
            new Symbol(0x0022, "annotationValue"),
            new Symbol(0x0023, "entity"),
            new Symbol(0x0024, "individual"),
            new Symbol(0x0025, "namedIndividual"),
            new Symbol(0x0026, "anonymousIndividual"),
            new Symbol(0x0027, "literal"),
            new Symbol(0x0028, "literalTyped"),
            new Symbol(0x0029, "literalString"),
            new Symbol(0x002A, "literalLang"),
            new Symbol(0x002B, "expObjectProperty"),
            new Symbol(0x002C, "expObjectPropertyChain"),
            new Symbol(0x002D, "expInverseObjectProperty"),
            new Symbol(0x002E, "expDataProperty"),
            new Symbol(0x002F, "expDatarange"),
            new Symbol(0x0030, "expDataIntersectionOf"),
            new Symbol(0x0031, "expDataUnionOf"),
            new Symbol(0x0032, "expDataComplementOf"),
            new Symbol(0x0033, "expDataOneOf"),
            new Symbol(0x0034, "expDatatypeRestriction"),
            new Symbol(0x0035, "expRestriction"),
            new Symbol(0x0036, "expClass"),
            new Symbol(0x0037, "expObjectInterfactionOf"),
            new Symbol(0x0038, "expObjectUnionOf"),
            new Symbol(0x0039, "expObjectComplementOf"),
            new Symbol(0x003A, "expObjectOneOf"),
            new Symbol(0x003B, "expObjectSomeValuesFrom"),
            new Symbol(0x003C, "expObjectAllValuesFrom"),
            new Symbol(0x003D, "expObjectHasValue"),
            new Symbol(0x003E, "expObjectHasSelf"),
            new Symbol(0x003F, "expObjectMinCardinality"),
            new Symbol(0x0040, "expObjectMaxCardinality"),
            new Symbol(0x0041, "expObjectExactCardinality"),
            new Symbol(0x0042, "expDataSomeValuesFrom"),
            new Symbol(0x0043, "expDataAllValuesFrom"),
            new Symbol(0x0044, "expDataHasValue"),
            new Symbol(0x0045, "expDataMinCardinality"),
            new Symbol(0x0046, "expDataMaxCardinality"),
            new Symbol(0x0047, "expDataExactCardinality"),
            new Symbol(0x0048, "axiom"),
            new Symbol(0x0049, "axiomDeclaration"),
            new Symbol(0x004A, "axiomClass"),
            new Symbol(0x004B, "axiomSubClassOf"),
            new Symbol(0x004C, "axiomEquivalentClasses"),
            new Symbol(0x004D, "axiomDisjointClasses"),
            new Symbol(0x004E, "axiomDisjointUnion"),
            new Symbol(0x004F, "axiomObjectProperty"),
            new Symbol(0x0050, "axiomSubOjectPropertyOf"),
            new Symbol(0x0051, "axiomEquivalentObjectProperties"),
            new Symbol(0x0052, "axiomDisjointObjectProperties"),
            new Symbol(0x0053, "axiomInverseObjectProperties"),
            new Symbol(0x0054, "axiomObjectPropertyDomain"),
            new Symbol(0x0055, "axiomObjectPropertyRange"),
            new Symbol(0x0056, "axiomFunctionalObjectProperty"),
            new Symbol(0x0057, "axiomInverseFunctionalObjectProperty"),
            new Symbol(0x0058, "axiomReflexiveObjectProperty"),
            new Symbol(0x0059, "axiomIrreflexiveObjectProperty"),
            new Symbol(0x005A, "axiomSymmetricObjectProperty"),
            new Symbol(0x005B, "axiomAsymmetricObjectProperty"),
            new Symbol(0x005C, "axiomTransitiveObjectProperty"),
            new Symbol(0x005D, "axiomDataProperty"),
            new Symbol(0x005E, "axiomSubDataPropertyOf"),
            new Symbol(0x005F, "axiomEquivalentDataProperties"),
            new Symbol(0x0060, "axiomDisjointDataProperties"),
            new Symbol(0x0061, "axiomDataPropertyDomain"),
            new Symbol(0x0062, "axiomDataPropertyRange"),
            new Symbol(0x0063, "axiomFunctionalDataProperty"),
            new Symbol(0x0064, "axiomDatatype"),
            new Symbol(0x0065, "axiomHasKey"),
            new Symbol(0x0066, "axiomHasKeyObjectProperties"),
            new Symbol(0x0067, "axiomHasKeyDataProperties"),
            new Symbol(0x0068, "axiomAssertion"),
            new Symbol(0x0069, "axiomSameIndividual"),
            new Symbol(0x006A, "axiomDifferentIndividuals"),
            new Symbol(0x006B, "axiomClassAssertion"),
            new Symbol(0x006C, "axiomObjectPropertyAssertion"),
            new Symbol(0x006D, "axiomNegativeObjectPropertyAssertion"),
            new Symbol(0x006E, "axiomDataPropertyAssertion"),
            new Symbol(0x006F, "axiomNegativeDataPropertyAssertion"),
            new Symbol(0x0070, "axiomAnnotation"),
            new Symbol(0x0071, "axiomAnnotationAssertion"),
            new Symbol(0x0072, "axiomSubAnnotationPropertyOf"),
            new Symbol(0x0073, "axiomAnnotationPropertyDomain"),
            new Symbol(0x0074, "axiomAnnotationPropertyRange"),
            new Symbol(0x0075, "_gen_V117"),
            new Symbol(0x007B, "_gen_V123"),
            new Symbol(0x007D, "_gen_V125"),
            new Symbol(0x007E, "_gen_V126"),
            new Symbol(0x0082, "_gen_V130"),
            new Symbol(0x0085, "_gen_V133"),
            new Symbol(0x0087, "_gen_V135"),
            new Symbol(0x008A, "_gen_V138"),
            new Symbol(0x008C, "_gen_V140"),
            new Symbol(0x008E, "_gen_V142"),
            new Symbol(0x0090, "_gen_V144"),
            new Symbol(0x0093, "_gen_V147"),
            new Symbol(0x009C, "_gen_V156"),
            new Symbol(0x009E, "_gen_V158"),
            new Symbol(0x00AC, "_gen_V172"),
            new Symbol(0x00AE, "_gen_V174"),
            new Symbol(0x00B0, "_gen_V176"),
            new Symbol(0x00B3, "_gen_V179"),
            new Symbol(0x00B5, "_gen_V181"),
            new Symbol(0x00C2, "_gen_V194"),
            new Symbol(0x00C4, "_gen_V196"),
            new Symbol(0x00CA, "_gen_V202"),
            new Symbol(0x00CB, "_gen_V203"),
            new Symbol(0x00CD, "_gen_V205"),
            new Symbol(0x00CF, "_gen_V207"),
            new Symbol(0x00DC, "literalInteger"),
            new Symbol(0x00DD, "rule"),
            new Symbol(0x00DE, "ruleAntecedents"),
            new Symbol(0x00DF, "ruleConsequents"),
            new Symbol(0x00E0, "assertion"),
            new Symbol(0x00E1, "axiomPattern"),
            new Symbol(0x00E4, "_gen_V228"),
            new Symbol(0x00E6, "_gen_V230"),
            new Symbol(0x00E7, "_gen_V231"),
            new Symbol(0x00E8, "_gen_V232"),
            new Symbol(0x00E9, "_gen_V233"),
            new Symbol(0x00EA, "_gen_V234"),
            new Symbol(0x00EB, "_Axiom_")};
    /**
     * The collection of virtuals matched by this parser
     * <p/>
     * The virtuals are in an order consistent with the automaton,
     * so that virtual indices in the automaton can be used to retrieve the virtuals in this table
     */
    private static final Symbol[] virtuals = {
    };

    /**
     * Initializes a new instance of the parser
     *
     * @param lexer The input lexer
     */
    public FunctionalXOWLParser(FunctionalXOWLLexer lexer) throws InitializationException {
        super(automaton, variables, virtuals, null, lexer);
    }
}
