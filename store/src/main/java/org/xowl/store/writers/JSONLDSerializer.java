/*******************************************************************************
 * Copyright (c) 2015 stephen.creff
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
 *     Stephen Creff - stephen.creff@gmail.com
 ******************************************************************************/

package org.xowl.store.writers;

import org.xowl.store.RDFUtils;
import org.xowl.store.Vocabulary;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.UnsupportedNodeType;
import org.xowl.utils.Logger;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.Map.Entry;

/**
 * Represents a serializer of RDF data in the JSON-LD format
 *
 * @author Stephen Creff
 */
public class JSONLDSerializer extends StructuredSerializer {

    /**
     * For debugging purpose
     * Tabulation value making the output human readable
     */
    private final static boolean DEBUG = true;//java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp");

    /**
     * The writer to use
     */
    private final Writer writer;

    /**
     * A buffer of serialized property
     */
    protected final Map<String, List<Quad>> bufferPropertyList;

    /**
     * A list of named graphs in the DataMap format
     */
    protected List<DataMap> namedGraphs;

    /**
     * For each graph, The data to serialize into a list
     */
    protected DataMap bufferSecondaryData;

    /**
     * For each graph, The data to serialize into a list
     */
    protected final List<SubjectNode> listOfList;

    /**
     * For all graphs, the defined BlankNode objects
     */
    protected final List<Quad> existingBlankNodeObjectQuads;

    /**
     * Initializes this serializer
     *
     * @param writer The writer to use
     */
    public JSONLDSerializer(Writer writer) {
        super();
        this.writer = writer;
        this.bufferPropertyList = new LinkedHashMap<>();
        this.bufferSecondaryData = new DataMap();
        this.listOfList = new ArrayList<>();
        this.existingBlankNodeObjectQuads = new ArrayList<>();
    }

    /**
     * Serializes the specified quads
     *
     * @param logger The logger to use
     * @param quads  The quads to serialize
     */
    @Override
    public void serialize(Logger logger, Iterator<Quad> quads) {
        try {
            while (quads.hasNext()) {
                enqueue(quads.next());
            }
        } catch (UnsupportedNodeType exception) {
            logger.error(exception);
        }
        try {
            serialize();
        } catch (IOException | UnsupportedNodeType exception) {
            logger.error(exception);
        }
    }

    /**
     * Serializes the data
     */
    private void serialize() throws IOException, UnsupportedNodeType {
        writer.write(Vocabulary.JSONLD.arrayBegin); //Expanded graph -> starts with default unnamed array
        if (DEBUG) writer.write(System.lineSeparator());

        if (!data.entrySet().isEmpty()) {
            if (!data.containsManyGraphs()) //Serialize default unnamed graph
                if (data.getDefaultGraphNode() == null) {
                    writer.write(Vocabulary.JSONLD.objectBegin);
                    writer.write(System.lineSeparator());
                    serializeNamedGraphContent(data);
                    writer.write(System.lineSeparator());
                    writer.write(Vocabulary.JSONLD.objectEnd);
                } else
                    serializeDefaultGraphContent(); //FIXME Bad way to get the graph
            else {
                for (Entry<SubjectNode, List<Quad>> entry : data.entrySet())
                    for (Quad quad : entry.getValue())
                        if (RDFUtils.isBlankNode(quad.getObject()))
                            existingBlankNodeObjectQuads.add(quad);
                GraphNode defaultRootGN = data.getDefaultGraphNode();
                if (defaultRootGN != null) //Serialize Default and other named graphs
                    serializeSuperGraphContent(defaultRootGN);
                else
                    serializeManyGraphsContent();
            }
        }
        writer.write(System.lineSeparator());
        writer.write(Vocabulary.JSONLD.arrayEnd);
    }

    /**
     * Serializes the default unnamed graph
     *
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeDefaultGraphContent() throws IOException, UnsupportedNodeType {
        serializeGraphContent(this.data);
    }

    /**
     * Serializes the default graph (this one has sub-graphs and may have others)
     *
     * @param graph : the given graph node
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeSuperGraphContent(GraphNode graph) throws IOException, UnsupportedNodeType {
        namedGraphs = data.extractOtherGraphs(graph);
        DataMap dm = data.extractGraph(graph);
        for (Iterator<Entry<SubjectNode, List<Quad>>> iterator = dm.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<SubjectNode, List<Quad>> dmEntry = iterator.next();
            if (DEBUG) tab(1);
            writer.write(Vocabulary.JSONLD.objectBegin);
            writer.write(System.lineSeparator());
            if (DEBUG) tab(2);
            writer.write(Vocabulary.JSONLD.idTag);
            serializeJSONLDObject(dm, dmEntry.getKey());
            if (DEBUG) tab(2);
            List<DataMap> subGraphs = getSubGraphs(dmEntry.getKey());
            if (!subGraphs.isEmpty()) {
                writer.write(Vocabulary.JSONLD.separator);
                writer.write(System.lineSeparator());
                for (Iterator<DataMap> it = subGraphs.iterator(); it.hasNext(); ) {
                    DataMap subMap = it.next();
                    serializeSubGraphContent(subMap);
                    if (it.hasNext()) {
                        writer.write(Vocabulary.JSONLD.separator);
                        writer.write(System.lineSeparator());
                    }
                }
            }
            writer.write(System.lineSeparator());
            if (DEBUG) tab(1);
            writer.write(Vocabulary.JSONLD.objectEnd);
            if (iterator.hasNext()) {
                writer.write(Vocabulary.JSONLD.separator);
                writer.write(System.lineSeparator());
            }
        }
        List<DataMap> otherGraphs = getOtherGraphs(dm);
        if (!otherGraphs.isEmpty()) {
            writer.write(Vocabulary.JSONLD.separator);
            writer.write(System.lineSeparator());
            for (Iterator<DataMap> it = otherGraphs.iterator(); it.hasNext(); ) {
                DataMap subMap = it.next();
                writer.write(Vocabulary.JSONLD.objectBegin);
                writer.write(System.lineSeparator());
                serializeNamedGraphContent(subMap);
                writer.write(Vocabulary.JSONLD.objectEnd);
                if (it.hasNext()) {
                    writer.write(Vocabulary.JSONLD.separator);
                    writer.write(System.lineSeparator());
                }
            }
        }
    }

    /**
     * Filter the NamedGraph list to return the DataMap whose graph is another subject node
     *
     * @param subjectNode : the given subject node
     * @return the sub list
     * @see this.namedGraphs
     */
    private List<DataMap> getSubGraphs(SubjectNode subjectNode) {
        List<DataMap> subGraphs = new ArrayList<>();
        for (DataMap dataMap : namedGraphs) {
            boolean isFromGraph = false;
            for (Entry<SubjectNode, List<Quad>> entry : dataMap.entrySet())
                for (Quad quad : entry.getValue())
                    if (quad.getGraph().equals(subjectNode)) {
                        isFromGraph = true;
                        break;
                    }
            if (isFromGraph)
                subGraphs.add(dataMap);
        }
        return subGraphs;
    }

    /**
     * Filter the NamedGraph list to return the DataMap whose graph is another subject node
     * than the ones from the given default dATAmAP
     *
     * @param defaultDataMap : the given dataMap containing the subject nodes
     * @return the sub list
     * @see this.namedGraphs
     */
    private List<DataMap> getOtherGraphs(DataMap defaultDataMap) {
        List<DataMap> otherGraphs = new ArrayList<>();
        for (DataMap dataMap : namedGraphs) {
            boolean graphIsASubjectOfDefault = false;
            for (Entry<SubjectNode, List<Quad>> entry : dataMap.entrySet()) {
                for (Quad quad : entry.getValue()) {
                    if (defaultDataMap.keySet().contains(quad.getGraph()))
                        graphIsASubjectOfDefault = true;
                }
            }
            if (!graphIsASubjectOfDefault)
                otherGraphs.add(dataMap);
        }
        return otherGraphs;
    }

    /**
     * Serializes a named graph provided by the DataMap
     *
     * @param data : the given DataMap
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeNamedGraphContent(DataMap data) throws IOException, UnsupportedNodeType {
        writer.write(Vocabulary.JSONLD.idTag);
        writer.write(data.getGraphGraphNodes().get(0).toString());
        writer.write(Vocabulary.JSONLD.endLabelGoNext);
        writer.write(System.lineSeparator());
        if (DEBUG) tab(2);
        writer.write(Vocabulary.JSONLD.graphTag);
        writer.write(System.lineSeparator());
        serializeGraphContent(data);
        writer.write(System.lineSeparator());
        if (DEBUG) tab(2);
        writer.write(Vocabulary.JSONLD.arrayEnd);
        writer.write(System.lineSeparator());
    }

    /**
     * Serializes many graphs
     *
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeManyGraphsContent() throws IOException, UnsupportedNodeType {
        this.namedGraphs = data.extractGraphs();
        for (Iterator<DataMap> it = this.namedGraphs.iterator(); it.hasNext(); ) {
            DataMap map = it.next();
            if (DEBUG) tab(1);
            writer.write(Vocabulary.JSONLD.objectBegin);
            writer.write(System.lineSeparator());
            if (DEBUG) tab(2);
            serializeNamedGraphContent(map);
            writer.write(Vocabulary.JSONLD.objectEnd);
            if (it.hasNext()) {
                writer.write(Vocabulary.JSONLD.separator);
                writer.write(System.lineSeparator());
            }
        }
    }

    /**
     * Serializes a graph, subgraph a the default one
     *
     * @param dataMap : the given DataMap
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeSubGraphContent(DataMap dataMap) throws IOException, UnsupportedNodeType {
        if (DEBUG) tab(2);
        writer.write(Vocabulary.JSONLD.graphTag);
        writer.write(System.lineSeparator());
        serializeGraphContent(dataMap);
        writer.write(System.lineSeparator());
        if (DEBUG) tab(2);
        writer.write(Vocabulary.JSONLD.arrayEnd);
        writer.write(System.lineSeparator());
    }

    /**
     * Serializes the graph content
     *
     * @param data : the given graph
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeGraphContent(DataMap data) throws IOException, UnsupportedNodeType {
        DataMap dt = data;
        if (dt.containsLists()) {
            //dt = data.constructLists(); FIXME
            for (Entry<SubjectNode, List<Quad>> entry : dt.entrySet())
                if (containsJSONLDLists(entry.getValue()) && dt.isAListOfLists(entry.getValue()))
                    for (Quad quad : entry.getValue())
                        if (RDFUtils.isBlankNode(quad.getObject()))
                            listOfList.add((SubjectNode) quad.getObject());
            bufferSecondaryData = dt.removeSubGraphsFromMainDataMap(listOfList, getBlankNodesNotFromTheGraph(dt.entrySet().iterator().next().getValue().get(0).getGraph()));
        }
        for (Iterator<Entry<SubjectNode, List<Quad>>> iterator = dt.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<SubjectNode, List<Quad>> entry = iterator.next();
            if (!listOfList.contains(entry.getKey()) && !entry.getValue().isEmpty()) {
                if (DEBUG) tab(1);
                writer.write(Vocabulary.JSONLD.objectBegin);
                writer.write(System.lineSeparator());
                if (DEBUG) tab(2);
                writer.write(Vocabulary.JSONLD.idTag);
                serializeJSONLDObject(dt, entry.getKey());
                writer.write(System.lineSeparator());
                if (DEBUG) tab(1);
                writer.write(Vocabulary.JSONLD.objectEnd);
                if (iterator.hasNext()) {
                    writer.write(Vocabulary.JSONLD.separator);
                    writer.write(System.lineSeparator());
                }
            }
        }
        if (!listOfList.isEmpty()) {
            writer.write(Vocabulary.JSONLD.separator);
            writer.write(System.lineSeparator());
            for (Iterator<SubjectNode> it = listOfList.iterator(); it.hasNext(); ) {
                SubjectNode sn = it.next();
                if (DEBUG) tab(1);
                writer.write(Vocabulary.JSONLD.objectBegin);
                writer.write(System.lineSeparator());
                if (DEBUG) tab(2);
                writer.write(Vocabulary.JSONLD.idTag);
                serializeJSONLDObject(dt, sn);
                writer.write(System.lineSeparator());
                if (DEBUG) tab(1);
                writer.write(Vocabulary.JSONLD.objectEnd);
                if (it.hasNext()) {
                    writer.write(Vocabulary.JSONLD.separator);
                    writer.write(System.lineSeparator());
                }
            }
            listOfList.clear();
        }
        bufferSecondaryData.clear();
    }

    /**
     * Filters the existing blank node list to get the only ones related to the given graph
     *
     * @param graphNode the given graph
     * @return the resulting list
     * @see this.existingBlankNodeObjectQuads
     */
    private List<Node> getBlankNodesNotFromTheGraph(GraphNode graphNode) {
        List<Node> result = new ArrayList<>();
        for (Quad quad : existingBlankNodeObjectQuads)
            if (!quad.getGraph().equals(graphNode))
                result.add(quad.getObject());
        return result;
    }

    /**
     * Serializes a JSON-LD Object
     *
     * @param subject The subject
     *                //* @param quads   All the quads for its property
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeJSONLDObject(DataMap map, SubjectNode subject) throws IOException, UnsupportedNodeType {
        List<Quad> quads = map.get(subject);
        if (subject.getNodeType() == Node.TYPE_IRI) {
            writer.write(((IRINode) subject).getIRIValue());
            writer.write(Vocabulary.JSONLD.endLabelGoNext);

        } else {
            //Blank nodes subject are not reduced unless pertaining to a @list
            writer.write(subject.toString());
            writer.write(Vocabulary.JSONLD.endLabelGoNext);
        }
        writer.write(System.lineSeparator());
        if (DEBUG) tab(2);
        serializeProperties(map, quads);
    }

    /**
     * Check whether the list of quads contains a property redf:rest with an object rdf:Nil
     *
     * @param quads the given list of quads
     * @return true if contains, false otherwise
     */
    private boolean containsJSONLDLists(List<Quad> quads) {
        for (Quad quad : quads)
            if (RDFUtils.isRdfNil(quad.getObject()) && RDFUtils.isRdfRest(quad.getProperty()))
                return true;
        return false;
    }

    /**
     * Serializes a property from a node
     *
     * @param quads The quads representing the property
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeProperties(DataMap map, List<Quad> quads) throws IOException, UnsupportedNodeType {
        for (int i = 0; i != quads.size(); i++) {
            Property property = quads.get(i).getProperty();
            String propertyID = property.toString();
            if (!bufferPropertyList.containsKey(propertyID))
                bufferPropertyList.put(propertyID, new ArrayList<Quad>(5));
            bufferPropertyList.get(propertyID).add(quads.get(i));
        }

        if (containsJSONLDLists(quads)) {
            if (map.containsPropertiesOtherThanList(quads)) {
                for (Iterator<Entry<String, List<Quad>>> iterator = bufferPropertyList.entrySet().iterator(); iterator.hasNext(); ) {
                    Entry<String, List<Quad>> propSet = iterator.next();
                    serializePropertySubject(propSet.getValue().get(0));
                    //Default array
                    writer.write(Vocabulary.JSONLD.arrayBegin);
                    if (propSet.getValue().size() == 1) {
                        serializePropertyObject(map, propSet.getValue().get(0));
                    } else {
                        serializePropertyObjects(map, propSet.getValue());
                        writer.write(System.lineSeparator());
                        if (DEBUG) tab(2);
                    }
                    //array closed
                    writer.write(Vocabulary.JSONLD.arrayEnd);
                    if (iterator.hasNext()) {
                        writer.write(Vocabulary.JSONLD.separator);
                        writer.write(System.lineSeparator());
                        if (DEBUG) tab(2);
                    }
                }
            } else {
                for (Iterator<Entry<String, List<Quad>>> iterator = bufferPropertyList.entrySet().iterator(); iterator.hasNext(); ) {
                    Entry<String, List<Quad>> propSet = iterator.next();
                    serializePropertySubject(propSet.getValue().get(0));
                    //Default array
                    writer.write(Vocabulary.JSONLD.arrayBegin);
                    if (propSet.getValue().size() == 1) {
                        if (RDFUtils.isRdfNil(propSet.getValue().get(0).getObject()) && RDFUtils.isRdfRest(propSet.getValue().get(0).getProperty())) {
                            //empty list
                            writer.write(Vocabulary.JSONLD.objectBegin);
                            writer.write(System.lineSeparator());
                            if (DEBUG) tab(3);
                            writer.write(Vocabulary.JSONLD.emptyListTag);
                            writer.write(System.lineSeparator());
                            if (DEBUG) tab(2);
                            writer.write(Vocabulary.JSONLD.objectEnd);
                        } else
                            serializePropertyObject(map, propSet.getValue().get(0));
                    } else {
                        serializePropertyObjects(map, propSet.getValue());
                        writer.write(System.lineSeparator());
                        if (DEBUG) tab(2);
                    }
                    //array closed
                    writer.write(Vocabulary.JSONLD.arrayEnd);
                    if (iterator.hasNext()) {
                        writer.write(Vocabulary.JSONLD.separator);
                        writer.write(System.lineSeparator());
                        if (DEBUG) tab(2);
                    }
                }
            }
        } else {
            for (Iterator<Entry<String, List<Quad>>> iterator = bufferPropertyList.entrySet().iterator(); iterator.hasNext(); ) {
                Entry<String, List<Quad>> propSet = iterator.next();
                serializePropertySubject(propSet.getValue().get(0));
                //Default array
                writer.write(Vocabulary.JSONLD.arrayBegin);
                if (propSet.getValue().size() == 1) {
                    serializePropertyObject(map, propSet.getValue().get(0));
                } else {
                    serializePropertyObjects(map, propSet.getValue());
                    writer.write(System.lineSeparator());
                    if (DEBUG) tab(2);
                }
                //array closed
                writer.write(Vocabulary.JSONLD.arrayEnd);
                if (iterator.hasNext()) {
                    writer.write(Vocabulary.JSONLD.separator);
                    writer.write(System.lineSeparator());
                    if (DEBUG) tab(2);
                }
            }
        }
        bufferPropertyList.clear();
    }

    /**
     * Serializes many properties
     *
     * @param map   the current DataMap
     * @param quads the current quads
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeManyProperties(DataMap map, List<Quad> quads) throws IOException, UnsupportedNodeType {
        for (int i = 0; i < quads.size(); i++) {
            Quad quad = quads.get(i);
            serializePropertyObject(map, quad);
            if ((i + 1 < quads.size()) && (!RDFUtils.isRdfNil(quads.get(i + 1).getObject()))) { //has next and next not nil
                writer.write(Vocabulary.JSONLD.separator);
                writer.write(System.lineSeparator());
                if (DEBUG) tab(1);
                if (RDFUtils.isRdfRest(quads.get(i + 1).getProperty()))
                    if (DEBUG) tab(1);
            }
        }
    }

    /**
     * Serializes a property from a node (object parts)
     *
     * @param quads The quads representing the property
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializePropertyObjects(DataMap map, List<Quad> quads) throws IOException, UnsupportedNodeType {
        writer.write(System.lineSeparator());
        if (DEBUG) tab(1);
        if (containsJSONLDLists(quads)) {
            writer.write(Vocabulary.JSONLD.objectBegin);
            writer.write(System.lineSeparator());
            if (DEBUG) tab(2);
            writer.write(Vocabulary.JSONLD.listTag);
            writer.write(System.lineSeparator());
            if (DEBUG) tab(2);
            serializeManyProperties(map, quads);
            writer.write(System.lineSeparator());
            if (DEBUG) tab(2);
            writer.write(Vocabulary.JSONLD.objectEnd);
        } else {
            serializeManyProperties(map, quads);
        }
    }

    /**
     * Serializes a property from a node (subject part)
     *
     * @param quad The quad representing the property
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializePropertySubject(Quad quad) throws IOException {
        String property = ((IRINode) quad.getProperty()).getIRIValue();
        if (Vocabulary.rdfType.equals(property))
            writer.write(Vocabulary.JSONLD.typeTag);
        else {
            writer.write(Vocabulary.JSONLD.labelTag);
            writer.write(property);
            writer.write(Vocabulary.JSONLD.endLabelGoDesc);
        }
    }

    /**
     * Serializes a property from a node (object parts)
     *
     * @param quad The quad representing the property
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializePropertyObject(DataMap map, Quad quad) throws IOException, UnsupportedNodeType {
        switch (quad.getObject().getNodeType()) {
            case Node.TYPE_IRI:
                if (RDFUtils.isRdfType(quad.getProperty())) {
                    writer.write(Vocabulary.JSONLD.labelTag);
                    writer.write(((IRINode) quad.getObject()).getIRIValue());
                    writer.write(Vocabulary.JSONLD.labelTag);
                } else if (Vocabulary.rdfNil.equals(((IRINode) quad.getObject()).getIRIValue())) {
                    if (RDFUtils.isRdfRest(quad.getProperty())) {  //ends list
                        writer.write(System.lineSeparator());
                        if (DEBUG) tab(3);
                        writer.write(Vocabulary.JSONLD.arrayEnd);
                        writer.write(System.lineSeparator());
                        if (DEBUG) tab(2);
                    } else {  //emptylist
                        writer.write(Vocabulary.JSONLD.objectBegin);
                        writer.write(System.lineSeparator());
                        if (DEBUG) tab(3);
                        writer.write(Vocabulary.JSONLD.emptyListTag);
                        writer.write(System.lineSeparator());
                        if (DEBUG) tab(2);
                        writer.write(Vocabulary.JSONLD.objectEnd);
                    }
                } else {
                    writer.write(Vocabulary.JSONLD.idTagWithObject);
                    writer.write(((IRINode) quad.getObject()).getIRIValue());
                    writer.write(Vocabulary.JSONLD.endLabelAndObject);
                }
                break;
            case Node.TYPE_BLANK:
                if (bufferSecondaryData.containsKey(quad.getObject())) { //@list
                    serializePropertyObjects(map, bufferSecondaryData.get(quad.getObject()));
                } else {
                    writer.write(Vocabulary.JSONLD.idTagWithObject);
                    writer.write(quad.getObject().toString());
                    writer.write(Vocabulary.JSONLD.endLabelAndObject);
                }
                break;
            case Node.TYPE_LITERAL:
                String lexicalValue = ((LiteralNode) quad.getObject()).getLexicalValue();
                String dataType = ((LiteralNode) quad.getObject()).getDatatype();
                String language = ((LiteralNode) quad.getObject()).getLangTag();
                //
                writer.write(Vocabulary.JSONLD.valueTag);
                writer.write(lexicalValue);
                writer.write(Vocabulary.JSONLD.labelTag);
                if (language != null) {
                    writer.write(Vocabulary.JSONLD.nextLanguageTag);
                    writer.write(language);
                    writer.write(Vocabulary.JSONLD.labelTag);
                } else if (dataType != null) {
                    if (!Vocabulary.xsdString.equals(dataType)) {
                        writer.write(Vocabulary.JSONLD.nextTypeTag);
                        writer.write(dataType);
                        writer.write(Vocabulary.JSONLD.labelTag);
                    }
                }
                writer.write(Vocabulary.JSONLD.objectEnd);
                break;
            default:
                throw new UnsupportedNodeType(quad.getObject(), "RDF serialization only support IRI, Blank and Literal nodes");
        }
    }

    /**
     * Write nb tabulations into the writer
     *
     * @param nb : number of tabs
     * @throws IOException
     */
    private void tab(int nb) throws IOException {
        for (int i = nb; --i >= 0; )
            writer.write("\t");
    }

}
