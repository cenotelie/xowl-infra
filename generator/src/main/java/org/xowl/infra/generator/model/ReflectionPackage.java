/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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
 ******************************************************************************/

package org.xowl.infra.generator.model;

import org.xowl.infra.lang.owl2.Ontology;
import org.xowl.infra.lang.runtime.Class;
import org.xowl.infra.lang.runtime.Entity;
import org.xowl.infra.lang.runtime.Interpretation;
import org.xowl.infra.lang.runtime.Property;
import org.xowl.infra.store.Vocabulary;

import java.io.File;
import java.io.IOException;

/**
 * Represents the package model for the OWL2 ontology (metamodel)
 *
 * @author Laurent Wouters
 */
public class ReflectionPackage extends PackageModel {
    /**
     * The model for OWL Class
     */
    private ClassModel classClass;

    /**
     * Gets the model for the OWL Class metaclass
     *
     * @return The associated model
     */
    public ClassModel getClassClass() {
        if (classClass != null)
            return classClass;
        Entity entity = parent.getRepository().resolveEntity(Vocabulary.owlClass);
        Class interpretation = parent.getRepository().interpretAsClass(entity);
        addClassClass(interpretation);
        return classClass;
    }

    /**
     * Initializes this package model
     *
     * @param model    The parent model
     * @param ontology The associated ontology
     */
    public ReflectionPackage(Model model, Ontology ontology) {
        super(model, ontology);
    }

    @Override
    public void loadEntities() {
        for (Entity entity : ontology.getAllContains()) {
            for (Interpretation interpretation : entity.getAllInterpretedAs()) {
                if (interpretation instanceof Class) {
                    Class classe = (Class) interpretation;
                    String iri = entity.getHasIRI().getHasValue();
                    if (Vocabulary.owlThing.equals(iri))
                        addClassThing(classe);
                    else if (Vocabulary.owlClass.equals(iri))
                        addClassClass(classe);
                    else
                        classes.put(classe, new ClassModel(this, classe));
                } else if (interpretation instanceof Property) {
                    properties.put((Property) interpretation, new PropertyModel(this, (Property) interpretation));
                }
            }
        }
    }

    /**
     * Adds the model for the metaclass Thing
     *
     * @param classe The metaclass Thing
     */
    private void addClassThing(Class classe) {
        classes.put(classe, new OWLThing(this, classe));
    }

    /**
     * Adds the model for the metaclass Class
     *
     * @param classe The metaclass Class
     */
    private void addClassClass(Class classe) {
        classClass = new OWLClass(this, classe);
        classes.put(classe, classClass);
    }

    @Override
    public void writeInterface(File folder, String header) throws IOException {
        // do nothing
    }

    @Override
    public void writeStandalone(File folder, String header) throws IOException {
        // do nothing
    }

    /**
     * The specialized model for the metaclass Thing
     */
    private static class OWLThing extends ClassModel {
        public OWLThing(PackageModel packageModel, Class classe) {
            super(packageModel, classe);
        }

        @Override
        public String getJavaName(ClassModel from) {
            return "java.lang.Object";
        }
    }

    /**
     * The specialized model for the metaclass Class
     */
    private static class OWLClass extends ClassModel {
        public OWLClass(PackageModel packageModel, Class classe) {
            super(packageModel, classe);
        }

        @Override
        public String getJavaName(ClassModel from) {
            return "java.lang.Class";
        }
    }
}
