/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.denotation.domain;

import fr.cenotelie.commons.utils.Identifiable;
import fr.cenotelie.commons.utils.logging.Logging;
import org.xowl.infra.store.IRIMapper;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.IRINode;
import org.xowl.infra.store.rdf.LiteralNode;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.storage.StoreFactory;
import org.xowl.infra.store.storage.UnsupportedNodeType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Represents the ontological schema or semantic domain of a user's artifact
 *
 * @author Laurent Wouters
 */
public class Domain implements Identifiable {
    /**
     * Creates a new empty schema
     *
     * @param schemaIri   The IRI for the schema
     * @param title       The schema's title
     * @param description The schema's description
     * @return The domain
     */
    public static Domain newEmpty(String schemaIri, String title, String description) {
        IRIMapper mapper = new IRIMapper();
        RepositoryRDF repository = new RepositoryRDF(StoreFactory.create().inMemory().make(), mapper, false);
        IRINode node = repository.getStore().getIRINode(schemaIri);
        try {
            repository.getStore().add(new Quad(node,
                    node,
                    repository.getStore().getIRINode(Vocabulary.rdfType),
                    repository.getStore().getIRINode(Vocabulary.owlOntology)
            ));
            if (title != null)
                repository.getStore().add(new Quad(node,
                        node,
                        repository.getStore().getIRINode(Vocabulary.dcTitle),
                        repository.getStore().getLiteralNode(title, Vocabulary.xsdString, null)
                ));
            if (description != null)
                repository.getStore().add(new Quad(node,
                        node,
                        repository.getStore().getIRINode(Vocabulary.dcDescription),
                        repository.getStore().getLiteralNode(description, Vocabulary.xsdString, null)
                ));
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
        }
        return new Domain(mapper, repository, node);
    }

    /**
     * Loads a domain from the specified resource
     *
     * @param resource The resource to load from
     * @return The domain
     */
    public static Domain loadDomain(String resource) {
        IRIMapper mapper = new IRIMapper();
        mapper.addSimpleMap(resource, resource);
        RepositoryRDF repository = new RepositoryRDF(StoreFactory.create().inMemory().make(), mapper, false);
        try {
            repository.load(Logging.get(), resource);
        } catch (Exception exception) {
            Logging.get().error(exception);
            return null;
        }
        IRINode node = null;
        try {
            Iterator<Quad> iterator = repository.getStore().getAll(
                    null,
                    null,
                    repository.getStore().getIRINode(Vocabulary.rdfType),
                    repository.getStore().getIRINode(Vocabulary.owlOntology));
            if (!iterator.hasNext())
                return null;
            node = (IRINode) iterator.next().getObject();
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
        }
        return new Domain(mapper, repository, node);
    }

    /**
     * The IRI mapper to use
     */
    private final IRIMapper mapper;
    /**
     * The backing repository
     */
    private final RepositoryRDF repository;
    /**
     * The IRI node for the schema
     */
    private final IRINode schemaNode;
    /**
     * The schema's IRI
     */
    private final String schemaIRI;
    /**
     * The schema's title
     */
    private final String title;
    /**
     * The schema's description
     */
    private final String description;

    /**
     * Initializes this domain
     *
     * @param mapper     The mapper to use
     * @param repository The backing repository
     * @param schemaNode The IRI node for the schema
     */
    private Domain(IRIMapper mapper, RepositoryRDF repository, IRINode schemaNode) {
        this.mapper = mapper;
        this.repository = repository;
        this.schemaNode = schemaNode;
        this.schemaIRI = schemaNode.getIRIValue();
        String title = null;
        String description = null;
        try {
            Iterator<Quad> iterator = repository.getStore().getAll(null, schemaNode, null, null);
            while (iterator.hasNext()) {
                Quad quad = iterator.next();
                if (quad.getProperty().getNodeType() == Node.TYPE_IRI) {
                    if (Vocabulary.dcTitle.equals(((IRINode) quad.getProperty()).getIRIValue()))
                        title = ((LiteralNode) quad.getObject()).getLexicalValue();
                    if (Vocabulary.dcDescription.equals(((IRINode) quad.getProperty()).getIRIValue()))
                        description = ((LiteralNode) quad.getObject()).getLexicalValue();
                }
            }
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
        }
        this.title = title;
        this.description = description;
    }

    @Override
    public String getIdentifier() {
        return schemaIRI;
    }

    @Override
    public String getName() {
        return title;
    }

    /**
     * Gets the schema's description
     *
     * @return The schema's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the entities in this schema
     *
     * @return The entities in this schema
     */
    public Collection<DomainEntity> getEntities() {
        try {
            Iterator<Quad> iterator = repository.getStore().getAll(null,
                    null,
                    repository.getStore().getIRINode(Vocabulary.rdfsIsDefinedBy),
                    schemaNode);
            if (!iterator.hasNext())
                return Collections.emptyList();
            Collection<DomainEntity> result = new ArrayList<>();
            while (iterator.hasNext()) {
                result.add(getEntity(((IRINode) iterator.next().getSubject()).getIRIValue()));
            }
            return result;
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
            return Collections.emptyList();
        }
    }

    /**
     * Gets the domain entity for the specified IRI
     *
     * @param iri An IRI
     * @return The corresponding domain entity
     */
    public DomainEntity getEntity(String iri) {
        try {
            Iterator<Quad> iterator = repository.getStore().getAll(null,
                    repository.getStore().getIRINode(iri),
                    null,
                    null);
            if (!iterator.hasNext())
                return null;
            Collection<Quad> quads = new ArrayList<>();
            while (iterator.hasNext())
                quads.add(iterator.next());
            return new DomainEntity(quads);
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
            return null;
        }
    }
}
