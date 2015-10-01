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
import org.xowl.store.Vocabulary;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.UnsupportedNodeType;
import org.xowl.store.storage.cache.CachedNodes;
import org.xowl.utils.Logger;
import org.xowl.utils.collections.Couple;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Represents a structured serializer of RDF data
 *
 * @author Laurent Wouters
 * modified to add DataMap inner class
 * @authors Stephen Creff
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
     * @throws UnsupportedNodeType
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
     * @throws UnsupportedNodeType
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
            return;
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
     */
    protected int getBlankID(BlankNode node) throws IOException {
        long id = node.getBlankID();
        for (int i = 0; i != nextBlank; i++) {
            if (blanks[i] == id)
                return i;
        }
        throw new IOException("Unmapped blank node " + id);
    }
    /**
     * The data to serialize
     * @author Stephen Creff
     */
    protected class DataMap extends HashMap<SubjectNode, List<Quad>>{

        /**
         * Flatten the DataMap structure
         * @return the resulting quad list
         */
        public List<Quad> toList(){
            return this.entrySet().stream().flatMap(entry -> entry.getValue().stream()).collect(Collectors.toList());
        }
        /**
         * Check whether the DataMap contains different graphs
         */
        public boolean containsManyGraphs(){
            return this.entrySet().stream().flatMap(entry -> entry.getValue().stream()).map(quad -> quad.getGraph()).distinct().count()>1;
        }

        /**
         * Get a list of the distinct GraphNodes in the DataMap
         * @return the resulting list
         */
        public List<GraphNode> getGraphGraphNodes(){
            return this.entrySet().stream().flatMap(entry -> entry.getValue().stream()).map(quad -> quad.getGraph()).collect(Collectors.toList());
        }
        /**
         * Get the default graph node of the DataMap (default unnamed graph)
         * @see this.isTheDefaultUnnamedGraph
         * @return a grapnode if exists, null otherwise
         */
        public GraphNode getDefaultGraphNode(){
            List<GraphNode> gNodes = this.toList().stream().filter(quad -> isTheDefaultUnnamedGraph(quad.getGraph())).map(Quad::getGraph).distinct().collect(Collectors.toList());
            return gNodes.isEmpty()? null: gNodes.get(0);
        }
        /**
         * Check whether the given graphNode is a default file one
         * FIXME Very bad way to get the graph name and to test if it correspond to a file
         * @param graphNode the given node
         * @return true if the name ends with a known file extension
         */
        private boolean isTheDefaultUnnamedGraph(GraphNode graphNode){
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
         * @return a list of DataMap(s)
         */
        public List<DataMap> extractGraphs(){
            List<DataMap> result = new ArrayList<>();
            Map<GraphNode,List<Quad>> tempGraphMap = new HashMap<>();
            this.toList().stream().forEach(quad -> {
                if (!tempGraphMap.containsKey(quad.getGraph()))
                    tempGraphMap.put(quad.getGraph(), new LinkedList<>());
                tempGraphMap.get(quad.getGraph()).add(quad);
            });
            result.addAll(tempGraphMap.values().stream().map(this::rebuildDataMap).collect(Collectors.toList()));
            return result;
        }

        /**
         * Extract a list of DataMaps corresponding to graphs other than a specific one (given by the graph node)
         * @param graphNode : the graph node
         * @return the resulting list
         */
        public List<DataMap> extractOtherGraphs(GraphNode graphNode){
            return this.extractGraphs().stream().filter(dataMap -> dataMap.entrySet().stream().flatMap(entry -> entry.getValue().stream()).noneMatch(quad -> quad.getGraph().equals(graphNode))).collect(Collectors.toList());
        }
        /**
         * Extract a DataMap corresponding to a specific graph (given by the graph node)
         * @param graphNode : the graph node
         * @return the resulting DataMap
         */
        public DataMap extractGraph(final GraphNode graphNode){
            return this.extractGraphs().stream().filter(dataMap -> dataMap.entrySet().stream().flatMap(entry -> entry.getValue().stream()).anyMatch(quad -> quad.getGraph().equals(graphNode))).collect(Collectors.toList()).get(0);
        }
        /**
         * Check whether the graph contains any list, </br>
         * (looks for rdf:nil members)
         */
        public boolean containsLists(){
            return this.entrySet().stream().anyMatch(entry -> entry.getValue().stream().anyMatch(quad -> quad.getObject().isNilType() && quad.getProperty().isRdfRest()));
        }
        /**
         * Check whether quads is a list of list, </br>
         */
        public boolean isAListOfLists (List<Quad> quads){
            return this.entrySet().stream().filter(entry -> quads.stream().map(quad -> quad.getObject()).collect(Collectors.toList()).contains(entry.getKey())).anyMatch(entry1 -> entry1.getValue().stream().anyMatch(quad1 -> quad1.getProperty().isRdfFirst()));
        }
        /**
         * Remove graph objects (defining some lists) from the main data map</br>
         * @see this.containsListsToBeConverted
         * @return the removed data
         */
        public DataMap removeSubGraphsFromMainDataMap(List<SubjectNode> subListNodes, List<Node> existingBNs){
            DataMap subMap = new DataMap();
            for (Iterator<Entry<SubjectNode, List<Quad>>> iterator = this.entrySet().iterator(); iterator.hasNext();) {
                Entry<SubjectNode, List<Quad>> entry = iterator.next();
                if (entry.getKey().isBlankNode() && !subListNodes.contains(entry.getKey()) && containsOnlySimpleList(entry.getValue())&& !existingBNs.contains(entry.getKey())){
                    subMap.put(entry.getKey(), this.get(entry.getKey()));
                    iterator.remove();
                }
            }
            return subMap;
        }
        /**
         * Check whether the quads contains only a simple list: <br>
         *     . properties are either rdf:First or rdf:Rest
         *     . and one quad is a nil object
         * @param quads : the list to be checked
         * @return true if conditions are verified
         */
        private boolean containsOnlySimpleList(List<Quad> quads) {
            return quads.stream().allMatch(quad -> (quad.getProperty().isRdfRest() || quad.getProperty().isRdfFirst())) && quads.stream().anyMatch(q -> q.getProperty().isRdfRest() && q.getObject().isNilType());
        }
        /**
         * Check whether the DataMap contains any entry that contains a list node which also contains properties other tha rdf:first and rdf:rest
         * @see this.containsPropertiesOtherThanList(SubjectNode)
         * @return true if conditions are verified
         */
        private boolean containsPropertiesOtherThanList(){
            return this.entrySet().stream().anyMatch(entry -> containsPropertiesOtherThanList(entry.getKey()));
        }
        /**
         * Check whether the given entry in the DataMap contains a list node which also contains properties other tha rdf:first and rdf:rest
         * @see this.containsPropertiesOtherThanList(List<Quad>)
         * @param subjectNode : the given entry key
         * @return true if conditions are verified
         */
        private boolean containsPropertiesOtherThanList(SubjectNode subjectNode){
            return containsPropertiesOtherThanList(this.get(subjectNode));
        }
        /**
         * Check whether the given list of quads in the DataMap contains a list node which also contains properties other tha rdf:first and rdf:rest
         * @param quads : the given list of quads
         * @return true if conditions are verified
         */
        public boolean containsPropertiesOtherThanList(List<Quad> quads){
            return quads.stream().anyMatch(quad -> quad.getProperty().isRdfFirst()||quad.getProperty().isRdfRest())&&
                    quads.stream().anyMatch(quad1 -> !quad1.getProperty().isRdfFirst() && !quad1.getProperty().isRdfRest());
        }

        /**
         * @see this.reorderDataMapMovingUpPropertiesOtherThanList(Entry<SubjectNode, List<Quad>>)
         */
        private void reorderDataMapMovingUpPropertiesOtherThanList(){
            this.entrySet().stream().filter(entry -> containsPropertiesOtherThanList(entry.getKey())).forEach(
                    entry1 -> reorderDataMapMovingUpPropertiesOtherThanList(entry1));
        }

        /**
         * For the given entry, reorder the quads values to make the properties other than rdf:first and rdf:rest be the firsts
         * @param entry the given entry
         */
        private void reorderDataMapMovingUpPropertiesOtherThanList(Entry<SubjectNode, List<Quad>> entry){
            if (entry.getValue().stream().anyMatch(quad -> !(quad.getProperty().isRdfFirst()||quad.getProperty().isRdfRest()))){
                for (int j = 0; j < entry.getValue().size(); j++) {
                    Quad q = entry.getValue().get(j);
                    if (!(q.getProperty().isRdfFirst()||q.getProperty().isRdfRest())) {
                        entry.getValue().remove(q);
                        entry.getValue().add(0, q);
                    }
                }
            }
        }

        /**
         * Resolve blanknode dependencies corresponding to lists and imbrication lists and reconstruct a DataMap
         * FIXME : Some dependencies are resolved and shouldn't according to the test bench
         * @return a new DataMap with resolved dependencies
         */
        public DataMap constructLists(){
            DataMap result;
            List<Quad> tempQuads = new ArrayList<>();
            List<Node> listBlankObjects = this.toList().stream().filter(quad -> quad.getProperty().isRdfRest() || quad.getProperty().isRdfFirst()).map(quad -> quad.getObject()).filter(node -> node.isBlankNode()).collect(Collectors.toList());
            List<SubjectNode> listRootSubjects = this.toList().stream().filter(quad -> !listBlankObjects.contains(quad.getSubject())).map(Quad::getSubject).distinct().collect(Collectors.toList());
            List<Quad> rootQuads = this.toList().stream().filter(quad -> listRootSubjects.contains(quad.getSubject())).collect(Collectors.toList());
            if (rootQuads.isEmpty())
                return this;
            if (this.containsPropertiesOtherThanList())
                this.reorderDataMapMovingUpPropertiesOtherThanList();
            rootQuads.stream().filter(q -> !tempQuads.contains(q)).forEach(q -> stepConstructLists(tempQuads, listRootSubjects, q, q));
            for (int i =0; i<tempQuads.size()-1; i++){ //remove duplicated quads
                if (tempQuads.get(i).equals(tempQuads.get(i+1))){
                    tempQuads.remove(i+1);
                    i--;
                }
            }
            result = rebuildDataMap(tempQuads);
            result.correctDataMap();
            return result;
        }

        /**
         * Move through first, rest properties and blanknodes to construct the resulting ordered list of quads
         * FIXME @see this.constructLists()
         * @param result : the resulting list of quads
         * @param listRootSubjects : the initial root SubjectNodes
         * @param current : the current graph node
         * @param previous : the previous graph node
         */
        private void stepConstructLists(List<Quad> result,List<SubjectNode> listRootSubjects, Quad current, Quad previous){
            Property cProperty = current.getProperty();
            if (current.getObject().isBlankNode()){
                if (!(cProperty.isRdfRest()||cProperty.isRdfFirst())){
                    if (!result.contains(current))
                        result.add(current);
                }
                if (this.toList().stream().filter(quad -> quad.getSubject().equals(current.getObject())).anyMatch(quad1 -> !(quad1.getProperty().isRdfFirst() || quad1.getProperty().isRdfRest()))){
                    if (!result.contains(current))
                        result.add(current);
                }
                if (listRootSubjects.contains(current.getSubject())&&(this.get(current.getObject()).stream().anyMatch(quad2 -> quad2.getObject().isNilType() && quad2.getProperty().isRdfRest()))) {//refer to list
                    if (!result.contains(current))
                        result.add(current);
                }
                for(Quad q : this.toList().stream().filter(quad -> quad.getSubject().equals(current.getObject())).collect(Collectors.toList())){
                    if (this.toList().stream().filter(quad -> quad.getSubject().equals(q.getObject())).anyMatch(quad1 -> !(quad1.getProperty().isRdfFirst() || quad1.getProperty().isRdfRest()))){
                        if (!result.contains(q))
                            result.add(q);
                    }else if (cProperty.isRdfFirst()&&q.getProperty().isRdfFirst()) {
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
                    if (current.getSubject().equals(previous.getSubject())||(!(previous.getProperty().isRdfFirst()||previous.getProperty().isRdfRest()))||(previous.getProperty().isRdfFirst()&&cProperty.isRdfFirst())
                            ||((previous.getProperty().isRdfFirst()||previous.getProperty().isRdfRest())&&(!cProperty.isRdfFirst() && !cProperty.isRdfRest()))){
                        result.add(current);
                    }else{
                        if (cProperty.isRdfFirst()&&(!previous.getProperty().isRdfFirst())){
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
            }
        }

        /**
         * correct the DataMap : replace rdf:First followed by rdf:First in lists (error coming from nested lists)
         * @see this.stepConstructLists()
         */
        private void correctDataMap(){
            for (Iterator<Entry<SubjectNode, List<Quad>>> iterator = this.entrySet().stream().collect(Collectors.toList()).iterator(); iterator.hasNext(); ) {
                Entry<SubjectNode, List<Quad>> entry = iterator.next();
                //Correct
                boolean hasFirst=false;
                for (int j = 0; j < entry.getValue().size(); j++) {
                    Quad q = entry.getValue().get(j);
                    if (q.getProperty().isRdfFirst()) {
                        if (hasFirst) {
                            Quad newQ =new Quad(q.getGraph(), q.getSubject(), new CachedNodes().getIRINode(Vocabulary.rdfRest), q.getObject());
                            int i = entry.getValue().indexOf(q);
                            entry.getValue().remove(i);
                            entry.getValue().add(i, newQ);
                        }else
                            hasFirst = true;
                    }
                }
            }
        }
        /**
         * Get the ObjectNode (Blank) referring to the given Subject node
         * @see this.getPreviousSubject(SubjectNode)
         * @param node : the given subject node
         * @param list
         * @return the corresponding node, self if not referred
         */
        private SubjectNode getReferringNode(SubjectNode node, List<Quad> list) {
            SubjectNode n = node;
            List<SubjectNode> sNodeList = list.stream().map(quad -> quad.getSubject()).collect(Collectors.toList());
            List<Node> bNodeList = list.stream().map(quad -> quad.getObject()).filter(node1 -> node1.isBlankNode()).collect(Collectors.toList());
            while (!(sNodeList.contains(n)||bNodeList.contains(n))){
                SubjectNode tempNode = getPreviousSubject(n);
                if (tempNode !=null ) n = tempNode;
                else break;
            }
            return n;
        }

        /**
         * Get the subject of a given object node
         * @param node
         * @return the resulting subject, null otherwise
         */
        private SubjectNode getPreviousSubject(final SubjectNode node) {
            List<Quad> quads = this.entrySet().stream().flatMap(entry -> entry.getValue().stream()).filter(quad -> quad.getObject().equals(node)).collect(Collectors.toList());
            return quads.isEmpty()? null : quads.get(0).getSubject();
        }

        /**
         * Rebuild a data map (list graphs), from the given temporary quads list
         * @param quads
         */
        private DataMap rebuildDataMap(List<Quad> quads){
            DataMap result = new DataMap();
            for (Quad q : quads){
                if (!result.containsKey(q.getSubject()))
                    result.put(q.getSubject(), new ArrayList<>());
                result.get(q.getSubject()).add(q);
            }
            return result;
        }
    }
}
