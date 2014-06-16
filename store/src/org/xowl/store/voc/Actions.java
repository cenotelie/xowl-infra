/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 **********************************************************************/

package org.xowl.store.voc;

/**
 * Defines constants for xOWL language concepts
 *
 * @author Laurent Wouters
 */
public class Actions {
    // Axioms
    public static final String axiomFunctionDefinition = "FunctionDefinition";

    // Ontological entities
    public static final String entityFunction = "Function";

    // Statements
    public static final String stAdd = "Add";
    public static final String stRemove = "Remove";
    public static final String stRemoveAll = "RemoveAll";
    public static final String stAssign = "Assign";
    public static final String stBreak = "Break";
    public static final String stContinue = "Continue";
    public static final String stInvoke = "Invoke";
    // Expressions
    // Multiple classification
    public static final String expInvoke = stInvoke;
    public static final String stExecute = "Execute";
    public static final String expExecute = stExecute;
    public static final String stBlock = "Block";
    public static final String stIf = "If";
    public static final String stFor = "For";
    public static final String stForEach = "ForEach";
    public static final String stWhile = "While";
    public static final String stDeclare = "Declare";
    public static final String stReturn = "Return";
    public static final String expContains = "Contains";
    public static final String expValuesOf = "ValuesOf";
    public static final String expValueOf = "ValueOf";
    public static final String expCodeVariable = "var";
    public static final String expQueryVariable = "qvar";
    public static final String expArrayElement = "ArrayElement";
    // Entity expressions
    public static final String expNewEntity = "NewEntity";
    public static final String expEntityForIRI = "EntityForIRI";
    // Individual expressions
    public static final String expNewIndividual = "NewIndividual";
    // Literal expressions
    public static final String expPlus = "Plus";
    public static final String expMinus = "Minus";
    public static final String expMult = "Mult";
    public static final String expDivide = "Divide";
    public static final String expModulus = "Modulus";
    public static final String expAnd = "And";
    public static final String expOr = "Or";
    public static final String expXOr = "XOr";
    public static final String expNot = "Not";
    public static final String expEqual = "Equal";
    public static final String expDifferent = "Different";
    public static final String expGreater = "Greater";
    public static final String expGreaterEqual = "GreaterEqual";
    public static final String expLesser = "Lesser";
    public static final String expLesserEqual = "LesserEqual";
    public static final String expIRIOf = "IRIOf";
    public static final String expLengthOf = "LengthOf";
    // Executable expressions
    public static final String expLambda = "Lambda";
    // Array expressions
    public static final String expArray = "Array";
    public static final String expConcat = "Concat";
    public static final String expSlice = "Slice";

    // Special query
    public static final String qrySelectAll = "SelectAll";

    // Sequences
    public static final String seqToSeq = "ToSeq";
}
