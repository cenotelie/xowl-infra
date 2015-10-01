package org.xowl.store.writers;

import org.xowl.store.Vocabulary;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.UnsupportedNodeType;
import org.xowl.utils.Logger;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/*******************************************************************************
 * Copyright (c) 2015 stephen.creff
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contributors:
 * Stephen Creff - stephen.creff@gmail.com
 ******************************************************************************/

/**
 * Represents a serializer of RDF data in the JSON-LD format
 *
 * @author stephen creff
 */
public class JSONLDSerializer extends StructuredSerializer {

    /**
     * For debugging purpose
     * Tabulation value making the output human readable
     */
    final static boolean DEBUG = true;//java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp");
    private final String TAB = "\t";

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
    protected List<SubjectNode> listOfList;

    /**
     * For all graphs, the defined BlankNode objects
     */
    protected List<Quad> existingBlankNodeObjectQuads;

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
                if (data.getDefaultGraphNode()==null) {
                    writer.write(Vocabulary.JSONLD.objectBegin);
                    writer.write(System.lineSeparator());
                    serializeNamedGraphContent(data);
                    writer.write(System.lineSeparator());
                    writer.write(Vocabulary.JSONLD.objectEnd);
                }else
                  serializeDefaultGraphContent(); //FIXME Bad way to get the graph
            else {
                existingBlankNodeObjectQuads = data.entrySet().stream().flatMap(entry -> entry.getValue().stream()).filter(quad -> quad.getObject().isBlankNode()).collect(Collectors.toList());
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
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeDefaultGraphContent()throws IOException, UnsupportedNodeType {
        serializeGraphContent(this.data);
    }
    /**
     * Serializes the default graph (this one has sub-graphs and may have others)
     * @param graph : the given graph node
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeSuperGraphContent(GraphNode graph)throws IOException, UnsupportedNodeType{
        namedGraphs =  data.extractOtherGraphs(graph);
        DataMap dm = data.extractGraph(graph);
        for (Iterator<Entry<SubjectNode,List<Quad>>> iterator =  dm.entrySet().iterator(); iterator.hasNext();){
            Entry<SubjectNode,List<Quad>> dmEntry = iterator.next();
            if (DEBUG)tab(1);
            writer.write(Vocabulary.JSONLD.objectBegin);
            writer.write(System.lineSeparator());
            if (DEBUG)tab(2);
            writer.write(Vocabulary.JSONLD.idTag);
            serializeJSONLDObject(dm, dmEntry.getKey());

            if (DEBUG)tab(2);
            List<DataMap> subGraphs = namedGraphs.stream().filter(dataMap -> dataMap.entrySet().stream().flatMap(entry -> entry.getValue().stream()).anyMatch(quad -> quad.getGraph().equals(dmEntry.getKey()))).collect(Collectors.toList());
            if (!subGraphs.isEmpty()){
                writer.write(Vocabulary.JSONLD.separator);
                writer.write(System.lineSeparator());
            }
            for (Iterator<DataMap> it = subGraphs.iterator(); it.hasNext();){
                DataMap subMap = it.next();
                serializeSubGraphContent(subMap);
                if (it.hasNext()) {
                    writer.write(Vocabulary.JSONLD.separator);
                    writer.write(System.lineSeparator());
                }
            }

            writer.write(System.lineSeparator());
            if (DEBUG)tab(1);
            writer.write(Vocabulary.JSONLD.objectEnd);
            if (iterator.hasNext()) {
                writer.write(Vocabulary.JSONLD.separator);
                writer.write(System.lineSeparator());
            }
        }

        List<DataMap> otherGraphs = namedGraphs.stream().filter(dataMap -> dataMap.entrySet().stream().flatMap(entry -> entry.getValue().stream()).noneMatch(quad ->
                dm.entrySet().stream().map(Entry::getKey).anyMatch(subjectNode -> subjectNode.equals(quad.getGraph())))).collect(Collectors.toList());
        if (!otherGraphs.isEmpty()){
            writer.write(Vocabulary.JSONLD.separator);
            writer.write(System.lineSeparator());
        }
        for (Iterator<DataMap> it = otherGraphs.iterator(); it.hasNext();){
            DataMap subMap = it.next();
            writer.write(Vocabulary.JSONLD.objectBegin);
            writer.write(System.lineSeparator());
            serializeNamedGraphContent(subMap) ;
            writer.write(Vocabulary.JSONLD.objectEnd);
            if (it.hasNext()) {
                writer.write(Vocabulary.JSONLD.separator);
                writer.write(System.lineSeparator());
            }
        }
    }

    /**
     * Serializes a named graph provided by the DataMap
     * @param data : the given DataMap
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeNamedGraphContent(DataMap data)throws IOException, UnsupportedNodeType{
        writer.write(Vocabulary.JSONLD.idTag);
        writer.write(data.getGraphGraphNodes().get(0).toString());
        writer.write(Vocabulary.JSONLD.endLabelGoNext);
        writer.write(System.lineSeparator());
        if (DEBUG)tab(2);
        writer.write(Vocabulary.JSONLD.graphTag);
        writer.write(System.lineSeparator());
        serializeGraphContent(data);
        writer.write(System.lineSeparator());
        if (DEBUG)tab(2);
        writer.write(Vocabulary.JSONLD.arrayEnd);
        writer.write(System.lineSeparator());
    }

    /**
     * Serializes many graphs
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeManyGraphsContent() throws IOException, UnsupportedNodeType{
        this.namedGraphs = data.extractGraphs();
        for (Iterator<DataMap> it = this.namedGraphs.iterator(); it.hasNext();) {
            DataMap map = it.next();
            if (DEBUG)tab(1);
            writer.write(Vocabulary.JSONLD.objectBegin);
            writer.write(System.lineSeparator());
            if (DEBUG)tab(2);
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
     * @param dataMap
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeSubGraphContent(DataMap dataMap)throws IOException, UnsupportedNodeType{
        if (DEBUG)tab(2);
        writer.write(Vocabulary.JSONLD.graphTag);
        writer.write(System.lineSeparator());
        serializeGraphContent(dataMap);
        writer.write(System.lineSeparator());
        if (DEBUG)tab(2);
        writer.write(Vocabulary.JSONLD.arrayEnd);
        writer.write(System.lineSeparator());
    }

    /**
     * Serializes the graph content
     * @param data : the given graph
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeGraphContent(DataMap data) throws IOException, UnsupportedNodeType {
        DataMap dt = data;
        if (dt.containsLists()) {
            //dt = data.constructLists(); FIXME
            dt.entrySet().stream().filter(entry -> containsJSONLDLists(entry.getValue())).filter(entry -> dt.isAListOfLists(entry.getValue())).forEach(entry -> entry.getValue().stream().filter(quad -> quad.getObject().isBlankNode()).forEach(quad1 -> listOfList.add((SubjectNode) quad1.getObject())));
            bufferSecondaryData = dt.removeSubGraphsFromMainDataMap(listOfList, existingBlankNodeObjectQuads.stream().filter(q->!q.getGraph().equals(dt.entrySet().iterator().next().getValue().get(0).getGraph())).map(quad -> quad.getObject()).collect(Collectors.toList()));
        }
        for (Iterator<Entry<SubjectNode, List<Quad>>> iterator = dt.entrySet().stream().filter(entry1 -> !listOfList.contains(entry1.getKey())).collect(Collectors.toList()).iterator(); iterator.hasNext(); ) {
            Entry<SubjectNode, List<Quad>> entry = iterator.next();
            if (!entry.getValue().isEmpty()) {
                if (DEBUG)tab(1);
                writer.write(Vocabulary.JSONLD.objectBegin);
                writer.write(System.lineSeparator());
                if (DEBUG)tab(2);
                writer.write(Vocabulary.JSONLD.idTag);
                serializeJSONLDObject(dt, entry.getKey());
                writer.write(System.lineSeparator());
                if (DEBUG)tab(1);
                writer.write(Vocabulary.JSONLD.objectEnd);
                if (iterator.hasNext()) {
                    writer.write(Vocabulary.JSONLD.separator);
                    writer.write(System.lineSeparator());
                }
            }
        }
        if (!listOfList.isEmpty()){
            writer.write(Vocabulary.JSONLD.separator);
            writer.write(System.lineSeparator());
            for (Iterator<SubjectNode> it = listOfList.iterator(); it.hasNext();){
                SubjectNode sn = it.next();
                if (DEBUG)tab(1);
                writer.write(Vocabulary.JSONLD.objectBegin);
                writer.write(System.lineSeparator());
                if (DEBUG)tab(2);
                writer.write(Vocabulary.JSONLD.idTag);
                serializeJSONLDObject(dt, sn);
                writer.write(System.lineSeparator());
                if (DEBUG)tab(1);
                writer.write(Vocabulary.JSONLD.objectEnd);
                if (it.hasNext()){
                    writer.write(Vocabulary.JSONLD.separator);
                    writer.write(System.lineSeparator());
                }
            }
            listOfList.clear();
        }
        bufferSecondaryData.clear();
    }

    /**
     * Serializes a JSON-LD Object
     *
     * @param subject The subject
     //* @param quads   All the quads for its property
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
        if (DEBUG)tab(2);
        serializeProperties(map, quads);
    }

    /**
     * Check whether the list of quads conatins a property redf:rest withan object rdf:Nil
     * @param quads the given list of quads
     * @return true if contains, false otherwise
     */
    private boolean containsJSONLDLists(List<Quad> quads){
        return quads.stream().anyMatch(quad -> quad.getObject().isNilType()&&quad.getProperty().isRdfRest());
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
            if (!bufferPropertyList.containsKey(propertyID)) {
                List<Quad> l = new ArrayList<>(5);
                bufferPropertyList.put(propertyID, l);
            }
            bufferPropertyList.get(propertyID).add(quads.get(i));
        }

        if (containsJSONLDLists(quads)){
            if (map.containsPropertiesOtherThanList(quads)){
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
                        if (DEBUG)tab(2);
                    }
                    //array closed
                    writer.write(Vocabulary.JSONLD.arrayEnd);
                    if (iterator.hasNext()) {
                        writer.write(Vocabulary.JSONLD.separator);
                        writer.write(System.lineSeparator());
                        if (DEBUG)tab(2);
                    }
                }
            }else{
                for (Iterator<Entry<String, List<Quad>>> iterator = bufferPropertyList.entrySet().iterator(); iterator.hasNext(); ) {
                    Entry<String, List<Quad>> propSet = iterator.next();
                    serializePropertySubject(propSet.getValue().get(0));
                    //Default array
                    writer.write(Vocabulary.JSONLD.arrayBegin);
                    if (propSet.getValue().size() == 1) {
                        if (propSet.getValue().get(0).getObject().isNilType()&&propSet.getValue().get(0).getProperty().isRdfRest()){
                            //empty list
                            writer.write(Vocabulary.JSONLD.objectBegin);
                            writer.write(System.lineSeparator());
                            if (DEBUG)tab(3);
                            writer.write(Vocabulary.JSONLD.emptyListTag);
                            writer.write(System.lineSeparator());
                            if (DEBUG)tab(2);
                            writer.write(Vocabulary.JSONLD.objectEnd);
                        }else
                            serializePropertyObject(map, propSet.getValue().get(0));
                    } else {
                        serializePropertyObjects(map, propSet.getValue());
                        writer.write(System.lineSeparator());
                        if (DEBUG)tab(2);
                    }
                    //array closed
                    writer.write(Vocabulary.JSONLD.arrayEnd);
                    if (iterator.hasNext()) {
                        writer.write(Vocabulary.JSONLD.separator);
                        writer.write(System.lineSeparator());
                        if (DEBUG)tab(2);
                    }
                }
            }
        }else{
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
                    if (DEBUG)tab(2);
                }
                //array closed
                writer.write(Vocabulary.JSONLD.arrayEnd);
                if (iterator.hasNext()) {
                    writer.write(Vocabulary.JSONLD.separator);
                    writer.write(System.lineSeparator());
                    if (DEBUG)tab(2);
                }
            }
        }
        bufferPropertyList.clear();
    }

    /**
     * Serializes many properties
     * @param map
     * @param quads
     * @throws IOException
     * @throws UnsupportedNodeType
     */
    private void serializeManyProperties(DataMap map, List<Quad> quads) throws IOException, UnsupportedNodeType {
        for (int i = 0; i < quads.size(); i++) {
            Quad quad = quads.get(i);
            serializePropertyObject(map, quad);
            if ((i + 1 < quads.size()) && (!quads.get(i + 1).getObject().isNilType())) { //has next and next not nil
                writer.write(Vocabulary.JSONLD.separator);
                writer.write(System.lineSeparator());
                if (DEBUG)tab(1);
                if (quads.get(i + 1).getProperty().isRdfRest())
                    if (DEBUG)tab(1);
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
        if (DEBUG)tab(1);
        if (containsJSONLDLists(quads)){
            writer.write(Vocabulary.JSONLD.objectBegin);
            writer.write(System.lineSeparator());
            if (DEBUG)tab(2);
            writer.write(Vocabulary.JSONLD.listTag);
            writer.write(System.lineSeparator());
            if (DEBUG) tab(2);
            serializeManyProperties(map, quads);
            writer.write(System.lineSeparator());
            if (DEBUG)tab(2);
            writer.write(Vocabulary.JSONLD.objectEnd);
        }else{
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
    private void serializePropertySubject(Quad quad) throws IOException, UnsupportedNodeType {
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
                if (quad.getProperty().isRdfType()) {
                    writer.write(Vocabulary.JSONLD.labelTag);
                    writer.write(((IRINode) quad.getObject()).getIRIValue());
                    writer.write(Vocabulary.JSONLD.labelTag);
                } else if (Vocabulary.rdfNil.equals(((IRINode) quad.getObject()).getIRIValue())) {
                    if (quad.getProperty().isRdfRest()) {  //ends list
                        writer.write(System.lineSeparator());
                        if (DEBUG)tab(3);
                        writer.write(Vocabulary.JSONLD.arrayEnd);
                        writer.write(System.lineSeparator());
                        if (DEBUG)tab(2);
                    } else {  //emptylist
                        writer.write(Vocabulary.JSONLD.objectBegin);
                        writer.write(System.lineSeparator());
                        if (DEBUG)tab(3);
                        writer.write(Vocabulary.JSONLD.emptyListTag);
                        writer.write(System.lineSeparator());
                        if (DEBUG)tab(2);
                        writer.write(Vocabulary.JSONLD.objectEnd);
                    }
                } else {
                    writer.write(Vocabulary.JSONLD.idTagWithObject);
                    writer.write(((IRINode) quad.getObject()).getIRIValue());
                    writer.write(Vocabulary.JSONLD.endLabelAndObject);
                }
                break;
            case Node.TYPE_BLANK:
                if (bufferSecondaryData.containsKey(quad.getObject())/*&&(!listOfList.contains(quad.getObject()))*/) { //@list //TODO list and binding
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
     * @param nb : number of tabs
     * @throws IOException
     */
    private void tab(int nb) throws IOException {
        for (int i = nb; --i >= 0; )
            writer.write(TAB);
    }

}
