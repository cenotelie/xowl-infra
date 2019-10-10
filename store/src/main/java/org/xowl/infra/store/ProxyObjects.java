/*******************************************************************************
 * Copyright (c) 2019 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.store;

import fr.cenotelie.commons.utils.collections.AdaptingIterator;
import fr.cenotelie.commons.utils.collections.SingleIterator;
import fr.cenotelie.commons.utils.collections.SkippableIterator;
import fr.cenotelie.commons.utils.logging.Logging;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.UnsupportedNodeType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Collection of proxy objects
 *
 * @author Laurent Wouters
 */
public class ProxyObjects {
    /**
     * The current dataset
     */
    private final Dataset dataset;
    /**
     * The map of known proxies
     */
    private final Map<String, ProxyObject> proxies;

    /**
     * Initializes this collection
     *
     * @param dataset The current dataset
     */
    public ProxyObjects(Dataset dataset) {
        this.dataset = dataset;
        this.proxies = new HashMap<>();
    }

    /**
     * Gets a proxy on the existing entity having the specified IRI
     *
     * @param iri The IRI of an entity
     * @return The proxy, or null if the entity does not exist
     */
    public ProxyObject getProxy(GraphNode graph, String iri) {
        ProxyObject candidate = proxies.get(iri);
        if (candidate != null)
            return candidate;
        IRINode node = dataset.getIRINode(iri);
        if (node == null)
            return null;
        candidate = new ProxyObject(this, dataset, graph, node);
        proxies.put(iri, candidate);
        return candidate;
    }

    /**
     * Gets a proxy on the existing entity having the specified subject node in the specified ontology
     *
     * @param subject The subject node
     * @return The proxy, or null if the entity does not exist
     */
    public ProxyObject getProxy(GraphNode graph, SubjectNode subject) {
        if (subject.getNodeType() == Node.TYPE_IRI) {
            return getProxy(graph, ((IRINode) subject).getIRIValue());
        }
        return new ProxyObject(this, dataset, graph, subject);
    }

    /**
     * Gets the proxy objects on entities defined in the specified ontology
     * An entity is defined within an ontology if at least one of its property is asserted in this ontology.
     * The IRI of this entity may or may not start with the IRI of the ontology, although it easier if it does.
     *
     * @param ontology An ontology
     * @return An iterator over the proxy objects
     */
    public Iterator<ProxyObject> getProxiesIn(String ontology) {
        try {
            GraphNode graph = dataset.getIRINode(ontology);
            Iterator<? extends Quad> quads = dataset.getAll(graph);
            final HashSet<SubjectNode> known = new HashSet<>();
            return new SkippableIterator<>(new AdaptingIterator<>(quads, element -> {
                SubjectNode subject = element.getSubject();
                if (subject.getNodeType() != Node.TYPE_IRI && subject.getNodeType() != Node.TYPE_BLANK)
                    return null;
                if (known.contains(subject))
                    return null;
                known.add(subject);
                return getProxy(graph, subject);
            }));
        } catch (UnsupportedNodeType exception) {
            Logging.get().error(exception);
            return new SingleIterator<>(null);
        }
    }

    /**
     * Creates a new object and gets a proxy on it
     *
     * @param ontology The containing ontology for the new object
     * @return A proxy on the new object
     */
    public ProxyObject newObject(String ontology) {
        GraphNode graph = dataset.getIRINode(ontology);
        IRINode entity = dataset.getIRINode(graph);
        return getProxy(graph, entity);
    }
}
