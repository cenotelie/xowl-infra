/**********************************************************************
 * Copyright (c) 2015 Laurent Wouters and others
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

package org.xowl.engine.loaders;

import org.xowl.lang.owl2.Annotation;
import org.xowl.lang.owl2.Axiom;
import org.xowl.lang.rules.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents the data de-serialized by a loader
 */
public class LoaderResult {
    /**
     * The iri of the containing ontology
     */
    private String iri;
    /**
     * The version iri of the ontology
     */
    private String version;
    /**
     * The axioms contained in the ontology
     */
    private List<Axiom> axioms;
    /**
     * The rules contained in the ontology
     */
    private List<Rule> rules;
    /**
     * The ontology-level annotations
     */
    private List<Annotation> annotations;
    /**
     * The imported IRIs
     */
    private List<String> imports;

    /**
     * Gets the iri of the loaded ontology
     *
     * @return The iri of the loaded ontology
     */
    public String getIri() {
        return iri;
    }

    /**
     * Gets the version iri of the loaded ontology
     *
     * @return The version iri of the loaded ontology
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the loaded axioms
     *
     * @return The loaded axioms
     */
    public Collection<Axiom> getAxioms() {
        return axioms;
    }

    /**
     * Gets the loaded rules
     *
     * @return The loaded rules
     */
    public Collection<Rule> getRules() {
        return rules;
    }

    /**
     * Gets the annotations on the loaded ontology
     *
     * @return The annotations on the loaded ontology
     */
    public Collection<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * Gets the imported documents from the loaded ontology
     *
     * @return The imported documents
     */
    public Collection<String> getImports() {
        return imports;
    }

    /**
     * Initialized an empty result
     *
     * @param iri     The iri of the loaded ontology
     * @param version The version iri of the loaded ontology
     */
    public LoaderResult(String iri, String version) {
        this.iri = iri;
        this.version = version;
        this.axioms = new ArrayList<>();
        this.rules = new ArrayList<>();
        this.annotations = new ArrayList<>();
        this.imports = new ArrayList<>();
    }

    /**
     * Adds an axiom to this result
     *
     * @param axiom The axiom to add
     */
    public void addAxiom(Axiom axiom) {
        axioms.add(axiom);
    }

    /**
     * Adds a rule to this result
     *
     * @param rule The rule to add
     */
    public void addRule(Rule rule) {
        rules.add(rule);
    }

    /**
     * Adds an annotation to this result
     *
     * @param annotation The ontology-level annotation to add
     */
    public void addAnnotation(Annotation annotation) {
        annotations.add(annotation);
    }

    /**
     * Adds the iri of an imported document
     *
     * @param iri The iri of an import document
     */
    public void addImport(String iri) {
        imports.add(iri);
    }
}
