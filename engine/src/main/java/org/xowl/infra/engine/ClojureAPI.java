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

package org.xowl.infra.engine;

import org.xowl.infra.lang.owl2.IRI;
import org.xowl.infra.lang.owl2.Owl2Factory;
import org.xowl.infra.store.EvaluatorContext;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.loaders.SPARQLLoader;
import org.xowl.infra.store.rdf.IRINode;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.sparql.Command;
import org.xowl.infra.store.sparql.Result;
import org.xowl.infra.store.sparql.ResultFailure;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.utils.logging.BufferedLogger;
import org.xowl.infra.utils.logging.Logging;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Provides a convenient API for accessing data from Clojure
 *
 * @author Laurent Wouters
 */
public class ClojureAPI {
    /**
     * Executes a SPARQL query against the current repository
     *
     * @param query The SPARQL query to execute
     * @return The result
     */
    public static Result sparql(String query) {
        EvaluatorContext context = EvaluatorContext.get(null);
        if (context == null)
            return new ResultFailure("No current evaluation context");
        if (!(context.getRepository() instanceof RepositoryRDF))
            return new ResultFailure("The current repository is not a RDF repository");

        NodeManager nodeManager = ((RepositoryRDF) context.getRepository()).getStore();
        BufferedLogger logger = new BufferedLogger();
        SPARQLLoader loader = new SPARQLLoader(nodeManager);
        Command command = loader.load(logger, new StringReader(query));
        if (command == null)
            return new ResultFailure(logger.getErrorsAsString());
        if (!logger.getErrorMessages().isEmpty())
            return new ResultFailure(logger.getErrorsAsString());
        return command.execute(((RepositoryRDF) context.getRepository()));
    }

    /**
     * Gets the object value associated by a property to an entity
     *
     * @param entity   The entity
     * @param property The property
     * @return The first associated object value
     */
    public static IRI getObjectValue(IRI entity, String property) {
        EvaluatorContext context = EvaluatorContext.get(null);
        if (context == null)
            return null;
        if (!(context.getRepository() instanceof RepositoryRDF))
            return null;

        RepositoryRDF repositoryRDF = (RepositoryRDF) context.getRepository();
        IRINode nodeSubject = repositoryRDF.getStore().getIRINode(entity.getHasValue());
        IRINode nodeProperty = repositoryRDF.getStore().getIRINode(property);
        try {
            Iterator<Quad> iterator = repositoryRDF.getStore().getAll(nodeSubject, nodeProperty, null);
            if (!iterator.hasNext())
                return null;
            while (iterator.hasNext()) {
                Quad quad = iterator.next();
                if (quad.getObject().getNodeType() == Node.TYPE_IRI) {
                    IRI iri = Owl2Factory.newIRI();
                    iri.setHasValue(((IRINode) quad.getObject()).getIRIValue());
                    return iri;
                }
            }
            return null;
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
            return null;
        }
    }

    /**
     * Gets the object values associated by a property to an entity
     *
     * @param entity   The entity
     * @param property The property
     * @return The associated object values
     */
    public static Collection<IRI> getObjectValues(IRI entity, String property) {
        Collection<IRI> result = new ArrayList<>();
        EvaluatorContext context = EvaluatorContext.get(null);
        if (context == null)
            return result;
        if (!(context.getRepository() instanceof RepositoryRDF))
            return result;

        RepositoryRDF repositoryRDF = (RepositoryRDF) context.getRepository();
        IRINode nodeSubject = repositoryRDF.getStore().getIRINode(entity.getHasValue());
        IRINode nodeProperty = repositoryRDF.getStore().getIRINode(property);
        try {
            Iterator<Quad> iterator = repositoryRDF.getStore().getAll(nodeSubject, nodeProperty, null);
            while (iterator.hasNext()) {
                Quad quad = iterator.next();
                if (quad.getObject().getNodeType() == Node.TYPE_IRI) {
                    IRI iri = Owl2Factory.newIRI();
                    iri.setHasValue(((IRINode) quad.getObject()).getIRIValue());
                    result.add(iri);
                }
            }
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
        }
        return result;
    }

    /**
     * Gets the data value associated by a property to an entity
     *
     * @param entity   The entity
     * @param property The property
     * @return The first associated data value
     */
    public static Object getDataValue(IRI entity, String property) {
        EvaluatorContext context = EvaluatorContext.get(null);
        if (context == null)
            return null;
        if (!(context.getRepository() instanceof RepositoryRDF))
            return null;

        RepositoryRDF repositoryRDF = (RepositoryRDF) context.getRepository();
        IRINode nodeSubject = repositoryRDF.getStore().getIRINode(entity.getHasValue());
        IRINode nodeProperty = repositoryRDF.getStore().getIRINode(property);
        try {
            Iterator<Quad> iterator = repositoryRDF.getStore().getAll(nodeSubject, nodeProperty, null);
            if (!iterator.hasNext())
                return null;
            while (iterator.hasNext()) {
                Quad quad = iterator.next();
                if (quad.getObject().getNodeType() == Node.TYPE_LITERAL) {
                    return RDFUtils.getNative(quad.getObject());
                }
            }
            return null;
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
            return null;
        }
    }

    /**
     * Gets the data values associated by a property to an entity
     *
     * @param entity   The entity
     * @param property The property
     * @return The associated data values
     */
    public static Collection<Object> getDataValues(IRI entity, String property) {
        Collection<Object> result = new ArrayList<>();
        EvaluatorContext context = EvaluatorContext.get(null);
        if (context == null)
            return result;
        if (!(context.getRepository() instanceof RepositoryRDF))
            return result;

        RepositoryRDF repositoryRDF = (RepositoryRDF) context.getRepository();
        IRINode nodeSubject = repositoryRDF.getStore().getIRINode(entity.getHasValue());
        IRINode nodeProperty = repositoryRDF.getStore().getIRINode(property);
        try {
            Iterator<Quad> iterator = repositoryRDF.getStore().getAll(nodeSubject, nodeProperty, null);
            while (iterator.hasNext()) {
                Quad quad = iterator.next();
                if (quad.getObject().getNodeType() == Node.TYPE_LITERAL) {
                    result.add(RDFUtils.getNative(quad.getObject()));
                }
            }
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
        }
        return result;
    }
}
