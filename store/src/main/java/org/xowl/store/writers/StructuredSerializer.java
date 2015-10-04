/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 *     Stephen Creff - stephen.creff@gmail.com
 ******************************************************************************/

package org.xowl.store.writers;

import org.xowl.store.AbstractRepository;
import org.xowl.store.RDFUtils;
import org.xowl.store.Vocabulary;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.UnsupportedNodeType;
import org.xowl.store.storage.cache.CachedNodes;
import org.xowl.utils.Logger;
import org.xowl.utils.collections.Couple;

import java.io.IOException;
import java.util.*;


/**
 * Represents a structured serializer of RDF data
 *
 * @author Laurent Wouters
 *         modified to add DataMap inner class
 * @author Stephen Creff
 */
public abstract class StructuredSerializer implements RDFSerializer {
    /**
     * Initial size of the buffer for the blank node map
     */
    private static final int BLANKS_MAP_INIT_SIZE = 256;
    /**
     * Prefix for the generated namespaces
     */
    private static final String NAMESPACE_PREFIX = "nm";

    /**
     * The namespaces in this document
     */
    protected final Map<String, String> namespaces;
    /**
     * The data to serialize
     */
    protected final DataMap data;
    /**
     * Buffer for renaming blank nodes
     */
    private long[] blanks;
    /**
     * Index of the next blank node slot
     */
    private int nextBlank;
    /**
     * The index of the next namespace
     */
    private int nextNamespace;
    /**
     * A buffer of serialized property
     */
    protected final List<Property> bufferProperties;

    /**
     * Initializes this serializer
     */
    public StructuredSerializer() {
        this.namespaces = new HashMap<>();
        this.namespaces.put(Vocabulary.rdf, "rdf");
        this.namespaces.put(Vocabulary.rdfs, "rdfs");
        this.namespaces.put(Vocabulary.xsd, "xsd");
        this.namespaces.put(Vocabulary.owl, "owl");
        this.data = new DataMap();
        this.blanks = new long[BLANKS_MAP_INIT_SIZE];
        this.nextBlank = 0;
        this.nextNamespace = 0;
        this.bufferProperties = new ArrayList<>(5);
    }

    /**
     * Serializes the specified quads
     *
     * @param logger The logger to use
     * @param quads  The quads to serialize
     */
    public abstract void serialize(Logger logger, Iterator<Quad> quads);

    /**
     * Enqueue a quad into the dataset to serialize
     *
     * @param quad A quad
     * @throws UnsupportedNodeType whenever occurs
     */
    protected void enqueue(Quad quad) throws UnsupportedNodeType {
        // map the nodes
        mapNode(quad.getGraph());
        mapNode(quad.getSubject());
        mapNode(quad.getProperty());
        mapNode(quad.getObject());
        // register the quad as a property of its subject
        List<Quad> properties = data.get(quad.getSubject());
        if (properties == null) {
            properties = new ArrayList<>();
            data.put(quad.getSubject(), properties);
        }
        properties.add(quad);
    }

    /**
     * Maps the specified node
     *
     * @param node The node
     * @throws UnsupportedNodeType whenever occurs
     */
    private void mapNode(Node node) throws UnsupportedNodeType {
        switch (node.getNodeType()) {
            case Node.TYPE_IRI:
                mapIRI(node, ((IRINode) node).getIRIValue());
                break;
            case Node.TYPE_BLANK:
                mapBlank((BlankNode) node);
                break;
            case Node.TYPE_LITERAL:
                String datatype = ((LiteralNode) node).getDatatype();
                mapIRI(node, datatype);
                break;
            default:
                throw new UnsupportedNodeType(node, "RDF serialization only support IRI, Blank and Literal nodes");
        }
    }

    /**
     * Maps an IRI to its namespace
     *
     * @param node The containing node
     * @param iri  The IRI to map
     */
    private void mapIRI(Node node, String iri) throws UnsupportedNodeType {
        int index = iri.indexOf("#");
        if (index != -1) {
            mapNamespace(iri.substring(0, index + 1));
        }
        //mapNamespace(iri);
        //Modified -> no # means direct mapping
        // FIXME
        // throw new UnsupportedNodeType(node, "IRI does not contain #");
    }

    /**
     * Maps the specified namespace
     *
     * @param namespace A namespace
     */
    private void mapNamespace(String namespace) {
        String compact = namespaces.get(namespace);
        if (compact == null) {
            compact = NAMESPACE_PREFIX + nextNamespace;
            nextNamespace++;
            namespaces.put(namespace, compact);
        }
    }

    /**
     * Maps this blank node for renaming
     *
     * @param node The blank node
     */
    private void mapBlank(BlankNode node) {
        long id = node.getBlankID();
        for (int i = 0; i != nextBlank; i++) {
            if (blanks[i] == id)
                return;
        }
        if (nextBlank == blanks.length)
            blanks = Arrays.copyOf(blanks, blanks.length + BLANKS_MAP_INIT_SIZE);
        blanks[nextBlank] = id;
        nextBlank++;
    }

    /**
     * Gets the compact IRI corresponding to the specified IRI
     *
     * @param node The containing node
     * @param iri  The IRI to compact
     * @return The corresponding compact IRI as couple (prefix, suffix) where prefix:suffix expands to the original IRI
     * @throws UnsupportedNodeType whenever occurs
     */
    protected Couple<String, String> getCompactIRI(Node node, String iri) throws UnsupportedNodeType {
        int index = iri.indexOf("#");
        if (index != -1) {
            String prefix = namespaces.get(iri.substring(0, index + 1));
            if (prefix == null)
                throw new UnsupportedNodeType(node, "Unmapped IRI node");
            return new Couple<>(prefix, index == iri.length() - 1 ? "" : iri.substring(index + 1));
        }
        throw new UnsupportedNodeType(node, "IRI does not contain #");
    }

    /**
     * Gets the remapped identifier for the specified blank node
     *
     * @param node A blank node
     * @return The corresponding identifier
     * @throws IOException whenever occurs
     */
    protected int getBlankID(BlankNode node) throws IOException {
        long id = node.getBlankID();
        for (int i = 0; i != nextBlank; i++)
            if (blanks[i] == id)
                return i;
        throw new IOException("Unmapped blank node " + id);
    }

    /**
     * The data to serialize
     *
     * @author Stephen Creff
     */
    protected class DataMap extends HashMap<SubjectNode, List<Quad>> {

        /**
         * Flatten the DataMap structure
         *
         * @return the resulting quad list
         */
        public List<Quad> toList() {
            List<Quad> result = new ArrayList<>();
            for (Map.Entry<SubjectNode, List<Quad>> entry : this.entrySet())
                result.addAll(entry.getValue());
            return result;
        }

        /**
         * Rebuild a data map (list graphs), from the given temporary quads list
         *
         * @param quads the given list
         */
        private DataMap toDataMap(List<Quad> quads) {
            DataMap result = new DataMap();
            for (Quad q : quads) {
                if (!result.containsKey(q.getSubject()))
                    result.put(q.getSubject(), new ArrayList<Quad>());
                result.get(q.getSubject()).add(q);
            }
            return result;
        }

        /**
         * Check whether the DataMap contains different graphs
         *
         * @return true whether it does
         */
        public boolean containsManyGraphs() {
            GraphNode gnRef = null;
            for (Map.Entry<SubjectNode, List<Quad>> entry : this.entrySet())
                for (Quad quad : entry.getValue()) {
                    if (gnRef == null)
                        gnRef = quad.getGraph();
                    if (!gnRef.equals(quad.getGraph()))
                        return false;
                }
            return true;
        }

        /**
         * Get a list of the distinct GraphNodes in the DataMap
         *
         * @return the resulting list
         */
        public List<GraphNode> getGraphGraphNodes() {
            List<GraphNode> result = new ArrayList<>();
            for (Map.Entry<SubjectNode, List<Quad>> entry : this.entrySet())
                for (Quad quad : entry.getValue())
                    if (!result.contains(quad.getGraph()))
                        result.add(quad.getGraph());
            return result;
        }

        /**
         * Get the default graph node of the DataMap (default unnamed graph)
         *
         * @return a grapnode if exists, null otherwise
         * @see <code>this.isTheDefaultUnnamedGraph()</code>
         */
        public GraphNode getDefaultGraphNode() {
            for (Map.Entry<SubjectNode, List<Quad>> entry : this.entrySet())
                for (Quad quad : entry.getValue())
                    if (isTheDefaultUnnamedGraph(quad.getGraph()))
                        return quad.getGraph();
            return null;
        }

        /**
         * Check whether the given graphNode is a default file one
         * FIXME Very bad way to get the graph name and to test if it correspond to a file
         *
         * @param graphNode the given node
         * @return true if the name ends with a known file extension
         */
        private boolean isTheDefaultUnnamedGraph(GraphNode graphNode) {
            return graphNode.toString().endsWith(AbstractRepository.EXT_JSON_LD) ||
                    graphNode.toString().endsWith(AbstractRepository.EXT_OWLXML_B) ||
                    graphNode.toString().endsWith(AbstractRepository.EXT_OWLXML_A) ||
                    graphNode.toString().endsWith(AbstractRepository.EXT_OWL2_B) ||
                    graphNode.toString().endsWith(AbstractRepository.EXT_OWL2_A) ||
                    graphNode.toString().endsWith(AbstractRepository.EXT_NQUADS) ||
                    graphNode.toString().endsWith(AbstractRepository.EXT_NTRIPLES) ||
                    graphNode.toString().endsWith(AbstractRepository.EXT_RDFT) ||
                    graphNode.toString().endsWith(AbstractRepository.EXT_RDFXML) ||
                    graphNode.toString().endsWith(AbstractRepository.EXT_TURTLE) ||
                    graphNode.toString().endsWith(AbstractRepository.EXT_XOWL);
        }

        /**
         * Extract the distinct DataMaps corresponding to different Graphs
         *
         * @return a list of DataMap(s)
         */
        public List<DataMap> extractGraphs() {
            List<DataMap> result = new ArrayList<>();
            Map<GraphNode, List<Quad>> tempGraphMap = new HashMap<>();
            for (Map.Entry<SubjectNode, List<Quad>> entry : this.entrySet())
                for (Quad quad : entry.getValue()) {
                    if (!tempGraphMap.containsKey(quad.getGraph()))
                        tempGraphMap.put(quad.getGraph(), new LinkedList<Quad>());
                    tempGraphMap.get(quad.getGraph()).add(quad);
                }
            for (Map.Entry<GraphNode, List<Quad>> entry : tempGraphMap.entrySet())
                result.add(toDataMap(entry.getValue()));
            return result;
        }

        /**
         * Extract a list of DataMaps corresponding to graphs other than a specific one (given by the graph node)
         *
         * @param graphNode : the graph node
         * @return the resulting list
         */
        public List<DataMap> extractOtherGraphs(GraphNode graphNode) {
            List<DataMap> result = new ArrayList<>();
            for (DataMap dm : this.extractGraphs()) {
                boolean match = false;
                for (Map.Entry<SubjectNode, List<Quad>> entry : dm.entrySet()) {
                    for (Quad quad : entry.getValue())
                        if (quad.getGraph().equals(graphNode)) {
                            match = true;
                            break;
                        }
                    if (match)
                        break;
                }
                if (!match)
                    result.add(dm);
            }
            return result;
        }

        /**
         * Extract a DataMap corresponding to a specific graph (given by the graph node)
         *
         * @param graphNode : the graph node
         * @return the resulting DataMap
         */
        public DataMap extractGraph(final GraphNode graphNode) {
            for (DataMap dm : this.extractGraphs()) {
                boolean match = true;
                for (Map.Entry<SubjectNode, List<Quad>> entry : dm.entrySet()) {
                    for (Quad quad : entry.getValue())
                        if (!quad.getGraph().equals(graphNode)) {
                            match = false;
                            break;
                        }
                    if (!match)
                        break;
                }
                if (match)
                    return dm;
            }
            return null;
        }

        /**
         * Check whether the graph contains any list,
         * (looks for rdf:nil members)
         *
         * @return true whether it does
         */
        public boolean containsLists() {
            for (Map.Entry<SubjectNode, List<Quad>> entry : this.entrySet())
                for (Quad quad : entry.getValue())
                    if (RDFUtils.isRdfNil(quad.getObject()) && RDFUtils.isRdfRest(quad.getProperty()))
                        return true;
            return false;
        }

        /**
         * Check whether quads is a list of list,
         *
         * @param quads the given list
         * @return true whether it is
         */
        public boolean isAListOfLists(List<Quad> quads) {
            List<Node> objects = new ArrayList<>();
            for (Quad quad : quads)
                objects.add(quad.getObject());
            for (Map.Entry<SubjectNode, List<Quad>> entry : this.entrySet())
                if (objects.contains(entry.getKey()))
                    for (Quad quad : entry.getValue())
                        if (RDFUtils.isRdfFirst(quad.getProperty()))
                            return true;
            return false;
        }

        /**
         * Remove graph objects (defining some lists) from the main data map
         *
         * @param subListNodes the sub-list of nodes
         * @param existingBNs  the existing blank nodes
         * @return the removed data
         * @see #containsOnlySimpleList(List)
         */
        public DataMap removeSubGraphsFromMainDataMap(List<SubjectNode> subListNodes, List<Node> existingBNs) {
            DataMap subMap = new DataMap();
            for (Iterator<Map.Entry<SubjectNode, List<Quad>>> iterator = this.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<SubjectNode, List<Quad>> entry = iterator.next();
                if (RDFUtils.isBlankNode(entry.getKey()) && !subListNodes.contains(entry.getKey()) && containsOnlySimpleList(entry.getValue()) && !existingBNs.contains(entry.getKey())) {
                    subMap.put(entry.getKey(), this.get(entry.getKey()));
                    iterator.remove();
                }
            }
            return subMap;
        }

        /**
         * Check whether the quads contains only a simple list:
         * . properties are either rdf:First or rdf:Rest
         * . and one quad is a nil object
         *
         * @param quads : the list to be checked
         * @return true if conditions are verified
         */
        private boolean containsOnlySimpleList(List<Quad> quads) {
            boolean hasNilProperty = false;
            for (Quad quad : quads) {
                if (!(RDFUtils.isRdfRest(quad.getProperty()) || RDFUtils.isRdfFirst(quad.getProperty())))
                    return false;
                if (RDFUtils.isRdfRest(quad.getProperty()) && RDFUtils.isRdfNil(quad.getObject()))
                    hasNilProperty = true;
            }
            return hasNilProperty;
        }

        /**
         * Check whether the DataMap contains any entry that contains a list node which also contains properties other tha rdf:first and rdf:rest
         *
         * @return true if conditions are verified
         * @see #containsPropertiesOtherThanList(SubjectNode)
         */
        private boolean containsPropertiesOtherThanList() {
            boolean doContain = false;
            for (Map.Entry<SubjectNode, List<Quad>> entry : this.entrySet())
                if (containsPropertiesOtherThanList(entry.getKey())) {
                    doContain = true;
                    break;
                }
            return doContain;
        }

        /**
         * Check whether the given entry in the DataMap contains a list node which also contains properties other tha rdf:first and rdf:rest
         *
         * @param subjectNode : the given entry key
         * @return true if conditions are verified
         * @see <code>this.containsPropertiesOtherThanList(List<Quad>)</code>
         */
        private boolean containsPropertiesOtherThanList(SubjectNode subjectNode) {
            return containsPropertiesOtherThanList(this.get(subjectNode));
        }

        /**
         * Check whether the given list of quads in the DataMap contains a list node which also contains properties other tha rdf:first and rdf:rest
         *
         * @param quads : the given list of quads
         * @return true if conditions are verified
         */
        public boolean containsPropertiesOtherThanList(List<Quad> quads) {
            boolean hasListProperty = false;
            boolean hasNonListProperty = false;
            for (Quad quad : quads)
                if (RDFUtils.isRdfFirst(quad.getProperty()) || RDFUtils.isRdfRest(quad.getProperty()))
                    hasListProperty = true;
                else if (!RDFUtils.isRdfFirst(quad.getProperty()) && !RDFUtils.isRdfRest(quad.getProperty()))
                    hasNonListProperty = true;
            return hasListProperty && hasNonListProperty;
        }

        /**
         * @see <code>this.reorderDataMapMovingUpPropertiesOtherThanList(Map.Entry<SubjectNode, List<Quad>>)</code>
         */
        private void reorderDataMapMovingUpPropertiesOtherThanList() {
            for (Map.Entry<SubjectNode, List<Quad>> entry : this.entrySet())
                if (containsPropertiesOtherThanList(entry.getKey()))
                    reorderDataMapMovingUpPropertiesOtherThanList(entry);
        }

        /**
         * For the given entry, reorder the quads values to make the properties other than rdf:first and rdf:rest be the firsts
         *
         * @param entry the given entry
         */
        private void reorderDataMapMovingUpPropertiesOtherThanList(Map.Entry<SubjectNode, List<Quad>> entry) {
            for (int i = 0; i < entry.getValue().size(); i++) {
                Quad quad = entry.getValue().get(i);
                if (!(RDFUtils.isRdfFirst(quad.getProperty()) || RDFUtils.isRdfRest(quad.getProperty())))
                    for (int j = 0; j < entry.getValue().size(); j++) {
                        Quad q = entry.getValue().get(j);
                        if (!(RDFUtils.isRdfFirst(q.getProperty()) || RDFUtils.isRdfRest(q.getProperty()))) {
                            entry.getValue().remove(q);
                            entry.getValue().add(0, q);
                        }
                    }
            }
        }

        /**
         * Resolve blanknode dependencies corresponding to lists and imbrication lists and reconstruct a DataMap
         * FIXME : Some dependencies are resolved and shouldn't according to the test bench
         *
         * @return a new DataMap with resolved dependencies
         */
        public DataMap constructLists() {
            DataMap result;
            List<Quad> tempQuads = new ArrayList<>();
            List<Node> listBlankObjects = new ArrayList<>();
            List<SubjectNode> listRootSubjects = new ArrayList<>();
            List<Quad> rootQuads = new ArrayList<>();
            for (Quad quad : this.toList()) {
                if (RDFUtils.isRdfRest(quad.getProperty()) || RDFUtils.isRdfFirst(quad.getProperty()))
                    if (RDFUtils.isBlankNode(quad.getObject()))
                        listBlankObjects.add(quad.getObject());
            }
            for (Quad quad : this.toList()) {
                if (!listBlankObjects.contains(quad.getSubject()))
                    if (!listRootSubjects.contains(quad.getSubject()))
                        listRootSubjects.add(quad.getSubject());
            }
            for (SubjectNode sn : listRootSubjects)
                rootQuads.addAll(this.get(sn));
            if (rootQuads.isEmpty())
                return this;
            if (this.containsPropertiesOtherThanList())
                this.reorderDataMapMovingUpPropertiesOtherThanList();
            for (Quad q : rootQuads)
                if (!tempQuads.contains(q))
                    stepConstructLists(tempQuads, listRootSubjects, q, q);
            for (int i = 0; i < tempQuads.size() - 1; i++) { //remove duplicated quads
                if (tempQuads.get(i).equals(tempQuads.get(i + 1))) {
                    tempQuads.remove(i + 1);
                    i--;
                }
            }
            result = toDataMap(tempQuads);
            result.correctDataMap();
            return result;
        }

        /**
         * Move through first, rest properties and blanknodes to construct the resulting ordered list of quads
         * FIXME @see this.constructLists()
         *
         * @param result           : the resulting list of quads
         * @param listRootSubjects : the initial root SubjectNodes
         * @param current          : the current graph node
         * @param previous         : the previous graph node
         */
        private void stepConstructLists(List<Quad> result, List<SubjectNode> listRootSubjects, Quad current, Quad previous) {
        /*    Property cProperty = current.getProperty();
            if (RDFUtils.isBlankNode(current.getObject())){
                if (!(RDFUtils.isRdfRest(cProperty)|| RDFUtils.isRdfFirst(cProperty))){
                    if (!result.contains(current))
                        result.add(current);
                }
                if (this.toList().stream().filter(quad -> quad.getSubject().equals(current.getObject())).anyMatch(quad1 -> !(RDFUtils.isRdfFirst(quad1.getProperty()) || RDFUtils.isRdfRest(quad1.getProperty())))){
                    if (!result.contains(current))
                        result.add(current);
                }
                if (listRootSubjects.contains(current.getSubject())&&(this.get(current.getObject()).stream().anyMatch(quad2 -> RDFUtils.isRdfNil(quad2.getObject()) && RDFUtils.isRdfRest(quad2.getProperty())))) {//refer to list
                    if (!result.contains(current))
                        result.add(current);
                }
                for(Quad q : this.toList().stream().filter(quad -> quad.getSubject().equals(current.getObject())).collect(Collectors.toList())){
                    if (this.toList().stream().filter(quad -> quad.getSubject().equals(q.getObject())).anyMatch(quad1 -> !(RDFUtils.isRdfFirst(quad1.getProperty()) || RDFUtils.isRdfRest(quad1.getProperty())))){
                        if (!result.contains(q))
                            result.add(q);
                    }else if (RDFUtils.isRdfFirst(cProperty)&& RDFUtils.isRdfFirst(q.getProperty())) {
                        if (!result.contains(current))
                            if (result.stream().map(Quad::getObject).anyMatch(node -> node.equals(current.getSubject())))
                                result.add(current);
                            else {
                                Quad newQ = new Quad(current.getGraph(), getReferringNode(previous.getSubject(), result), cProperty, current.getObject());
                                if (!result.contains(newQ))
                                    result.add(newQ);
                            }
                    }
                    stepConstructLists(result, listRootSubjects, q, current);
                }

            }else{
                if (!result.contains(current)){
                    if (current.getSubject().equals(previous.getSubject())||(!(RDFUtils.isRdfFirst(previous.getProperty())|| RDFUtils.isRdfRest(previous.getProperty())))||(RDFUtils.isRdfFirst(previous.getProperty())&& RDFUtils.isRdfFirst(cProperty))
                            ||((RDFUtils.isRdfFirst(previous.getProperty())|| RDFUtils.isRdfRest(previous.getProperty()))&&(!RDFUtils.isRdfFirst(cProperty) && !RDFUtils.isRdfRest(cProperty)))){
                        result.add(current);
                    }else{
                        if (RDFUtils.isRdfFirst(cProperty)&&(!RDFUtils.isRdfFirst(previous.getProperty()))){
                            if (result.contains(previous)&&!result.contains(current))
                                result.add(current);
                            else{
                            Quad newQ = new Quad(current.getGraph(), getReferringNode(previous.getSubject(), result), new CachedNodes().getIRINode(Vocabulary.rdfRest), current.getObject());
                            if (!result.contains(newQ))
                                result.add(newQ);
                            }
                        }else{
                            if (result.stream().anyMatch(quad -> quad.getSubject().equals(previous.getSubject()))) {
                                Quad newQ =new Quad(current.getGraph(), getReferringNode(previous.getSubject(), result), cProperty, current.getObject());
                                if (!result.contains(newQ))
                                    result.add(newQ);
                            }else {
                                Quad newQ = new Quad(current.getGraph(), getReferringNode(previous.getSubject(), result), cProperty, current.getObject());
                                if (!result.contains(newQ))
                                    result.add(newQ);
                            }
                        }
                    }
                }
            }*/
        }

        /**
         * correct the DataMap : replace rdf:First followed by rdf:First in lists (error coming from nested lists)
         *
         * @see #stepConstructLists(List, List, Quad, Quad)
         */
        private void correctDataMap() {
            for (Map.Entry<SubjectNode, List<Quad>> entry : this.entrySet()) {
                boolean hasFirst = false;
                for (int j = 0; j < entry.getValue().size(); j++) {
                    Quad q = entry.getValue().get(j);
                    if (RDFUtils.isRdfFirst(q.getProperty())) {
                        if (hasFirst) {
                            Quad newQ = new Quad(q.getGraph(), q.getSubject(), new CachedNodes().getIRINode(Vocabulary.rdfRest), q.getObject());
                            int i = entry.getValue().indexOf(q);
                            entry.getValue().remove(i);
                            entry.getValue().add(i, newQ);
                        } else
                            hasFirst = true;
                    }
                }
            }
        }

        /**
         * Get the ObjectNode (Blank) referring to the given Subject node
         *
         * @param node : the given subject node
         * @param list : the given list
         * @return the corresponding node, self if not referred
         * @see #getPreviousSubject(SubjectNode)
         */
        private SubjectNode getReferringNode(SubjectNode node, List<Quad> list) {
            SubjectNode n = node;
            List<SubjectNode> sNodeList = new ArrayList<>();
            for (Quad quad : list)
                sNodeList.add(quad.getSubject());
            List<Node> bNodeList = new ArrayList<>();
            for (Quad quad : list)
                if (RDFUtils.isBlankNode(quad.getObject()))
                    bNodeList.add(quad.getObject());
            while (!(sNodeList.contains(n) || bNodeList.contains(n))) {
                SubjectNode tempNode = getPreviousSubject(n);
                if (tempNode != null) n = tempNode;
                else break;
            }
            return n;
        }

        /**
         * Get the subject of a given object node
         *
         * @param node : the given node
         * @return the resulting subject, null otherwise
         */
        private SubjectNode getPreviousSubject(final SubjectNode node) {
            List<Quad> quads = new ArrayList<>();
            for (Map.Entry<SubjectNode, List<Quad>> entry : this.entrySet())
                for (Quad quad : entry.getValue())
                    if (quad.getObject().equals(node))
                        quads.add(quad);
            return quads.isEmpty() ? null : quads.get(0).getSubject();
        }
    }
}
