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

package org.xowl.store.rete;

import org.xowl.store.rdf.XOWLTriple;
import org.xowl.store.rdf.*;

import java.util.*;
import java.util.Map.Entry;

/**
 * Represents the alpha part of a RETE graph
 *
 * @author Laurent Wouters
 */
public class AlphaGraph {
    /**
     * The internal representation of the graph
     */
    private Map<RDFNode, Map<RDFNode, Map<RDFNode, AlphaMemory>>> map;

    /**
     * Initializes this graph
     */
    public AlphaGraph() {
        map = new IdentityHashMap<>();
    }

    /**
     * Fires the specified fact in this graph
     *
     * @param fact A fact
     */
    public void fire(XOWLTriple fact) {
        RDFNode sub = fact.getSubject();
        RDFNode prop = fact.getProperty();
        RDFNode obj = fact.getObject();
        AlphaMemory mem = match(null, null, null);
        if (mem != null) mem.activateFact(fact);
        mem = match(null, null, obj);
        if (mem != null) mem.activateFact(fact);
        mem = match(null, prop, null);
        if (mem != null) mem.activateFact(fact);
        mem = match(null, prop, obj);
        if (mem != null) mem.activateFact(fact);
        mem = match(sub, null, null);
        if (mem != null) mem.activateFact(fact);
        mem = match(sub, null, obj);
        if (mem != null) mem.activateFact(fact);
        mem = match(sub, prop, null);
        if (mem != null) mem.activateFact(fact);
        mem = match(sub, prop, obj);
        if (mem != null) mem.activateFact(fact);
    }

    /**
     * Unfires the specified fact from this graph
     *
     * @param fact A fact
     */
    public void unfire(XOWLTriple fact) {
        RDFNode sub = fact.getSubject();
        RDFNode prop = fact.getProperty();
        RDFNode obj = fact.getObject();
        AlphaMemory mem = match(null, null, null);
        if (mem != null) mem.deactivateFact(fact);
        mem = match(null, null, obj);
        if (mem != null) mem.deactivateFact(fact);
        mem = match(null, prop, null);
        if (mem != null) mem.deactivateFact(fact);
        mem = match(null, prop, obj);
        if (mem != null) mem.deactivateFact(fact);
        mem = match(sub, null, null);
        if (mem != null) mem.deactivateFact(fact);
        mem = match(sub, null, obj);
        if (mem != null) mem.deactivateFact(fact);
        mem = match(sub, prop, null);
        if (mem != null) mem.deactivateFact(fact);
        mem = match(sub, prop, obj);
        if (mem != null) mem.deactivateFact(fact);
    }

    /**
     * Fires the specified collection of facts in this graph
     *
     * @param facts A collection of facts
     */
    public void fire(Collection<XOWLTriple> facts) {
        Map<AlphaMemory, Collection<XOWLTriple>> dispatch = buildDispatch(facts);
        for (Entry<AlphaMemory, Collection<XOWLTriple>> entry : dispatch.entrySet())
            entry.getKey().activateFacts(new FastBuffer<>(entry.getValue()));
    }

    /**
     * Unfires the specified collection of facts from this graph
     *
     * @param facts A collection of facts
     */
    public void unfire(Collection<XOWLTriple> facts) {
        Map<AlphaMemory, Collection<XOWLTriple>> dispatch = buildDispatch(facts);
        for (Entry<AlphaMemory, Collection<XOWLTriple>> entry : dispatch.entrySet())
            entry.getKey().deactivateFacts(new FastBuffer<>(entry.getValue()));
    }

    /**
     * Builds the dispatching data for the specified collection of facts
     *
     * @param facts A collection of facts
     * @return The dispatching data associating alpha memory to the relevant collections of facts
     */
    private Map<AlphaMemory, Collection<XOWLTriple>> buildDispatch(Collection<XOWLTriple> facts) {
        Map<AlphaMemory, Collection<XOWLTriple>> map = new IdentityHashMap<>();
        for (XOWLTriple fact : facts) {
            AlphaMemory[] mems = getMatches(fact);
            for (int i = 0; i != 8; i++) {
                AlphaMemory mem = mems[i];
                if (mem == null)
                    continue;
                Collection<XOWLTriple> collec = map.get(mem);
                if (collec == null) {
                    collec = new ArrayList<>();
                    map.put(mem, collec);
                }
                collec.add(fact);
            }
        }
        return map;
    }

    /**
     * Gets the alpha memories that match the specified fact
     *
     * @param fact A fact
     * @return The alpha memories that match the fact
     */
    private AlphaMemory[] getMatches(XOWLTriple fact) {
        RDFNode sub = fact.getSubject();
        RDFNode prop = fact.getProperty();
        RDFNode obj = fact.getObject();
        AlphaMemory[] result = new AlphaMemory[8];
        result[0] = match(null, null, null);
        result[1] = match(null, null, obj);
        result[2] = match(null, prop, null);
        result[3] = match(null, prop, obj);
        result[4] = match(sub, null, null);
        result[5] = match(sub, null, obj);
        result[6] = match(sub, prop, null);
        result[7] = match(sub, prop, obj);
        return result;
    }

    /**
     * Gets the alpha memory matching the specified triple data
     *
     * @param sub  The subject node
     * @param prop The property
     * @param obj  The object node
     * @return The matching alpha memory, or null if none is found
     */
    private AlphaMemory match(RDFNode sub, RDFNode prop, RDFNode obj) {
        if (!map.containsKey(sub))
            return null;
        Map<RDFNode, Map<RDFNode, AlphaMemory>> mapProp = map.get(sub);
        if (!mapProp.containsKey(prop))
            return null;
        Map<RDFNode, AlphaMemory> mapObj = mapProp.get(prop);
        if (!mapObj.containsKey(obj))
            return null;
        return mapObj.get(obj);
    }

    /**
     * Resolves the appropriate alpha memory for the specified triple
     *
     * @param triple A triple
     * @param graph  The RDF graph
     * @return The corresponding alpha memory
     */
    public AlphaMemory resolveMemory(RDFTriple triple, RDFGraph graph) {
        RDFNode subj = triple.getSubject();
        RDFNode prop = triple.getProperty();
        RDFNode obj = triple.getObject();
        if (subj.getNodeType() == RDFNodeType.VARIABLE)
            subj = null;
        if (prop.getNodeType() == RDFNodeType.VARIABLE)
            prop = null;
        if (obj.getNodeType() == RDFNodeType.VARIABLE)
            obj = null;
        if (!map.containsKey(subj))
            map.put(subj, new IdentityHashMap<RDFNode, Map<RDFNode, AlphaMemory>>());
        Map<RDFNode, Map<RDFNode, AlphaMemory>> mapProp = map.get(subj);
        if (!mapProp.containsKey(prop))
            mapProp.put(prop, new IdentityHashMap<RDFNode, AlphaMemory>());
        Map<RDFNode, AlphaMemory> mapObj = mapProp.get(prop);
        if (mapObj.containsKey(obj))
            return mapObj.get(obj);

        AlphaMemory mem = new AlphaMemory();
        Collection<XOWLTriple> temp = new ArrayList<>();
        if (mapObj.containsKey(null)) {
            for (XOWLTriple fact : mapObj.get(null).getFacts()) {
                if (fact.getObject() == obj)
                    temp.add(fact);
            }
        } else {
            try {
                Iterator<XOWLTriple> iterator = graph.getAll((RDFSubjectNode) subj, (RDFProperty) prop, obj);
                while (iterator.hasNext())
                    temp.add(iterator.next());
            } catch (UnsupportedNodeType ex) {
                // cannot happen
            }
        }
        if (!temp.isEmpty())
            mem.activateFacts(new FastBuffer<>(temp));
        mapObj.put(obj, mem);
        return mem;
    }

    /**
     * Gets the appropriate alpha memory for the specified triple
     *
     * @param triple A triple
     * @return The corresponding alpha memory
     */
    public AlphaMemory getMemory(RDFTriple triple) {
        RDFNode subj = triple.getSubject();
        RDFNode prop = triple.getProperty();
        RDFNode obj = triple.getObject();
        if (subj.getNodeType() == RDFNodeType.VARIABLE)
            subj = null;
        if (prop.getNodeType() == RDFNodeType.VARIABLE)
            prop = null;
        if (obj.getNodeType() == RDFNodeType.VARIABLE)
            obj = null;
        Map<RDFNode, Map<RDFNode, AlphaMemory>> mapProp = map.get(subj);
        Map<RDFNode, AlphaMemory> mapObj = mapProp.get(prop);
        return mapObj.get(obj);
    }

    /**
     * Removes the alpha memory for the specified triple
     *
     * @param triple A triple
     */
    public void removeMemory(RDFTriple triple) {
        RDFNode subj = triple.getSubject();
        RDFNode prop = triple.getProperty();
        RDFNode obj = triple.getObject();
        if (subj.getNodeType() == RDFNodeType.VARIABLE)
            subj = null;
        if (prop.getNodeType() == RDFNodeType.VARIABLE)
            prop = null;
        if (obj.getNodeType() == RDFNodeType.VARIABLE)
            obj = null;
        Map<RDFNode, Map<RDFNode, AlphaMemory>> mapProp = map.get(subj);
        Map<RDFNode, AlphaMemory> mapObj = mapProp.get(prop);
        mapObj.remove(obj);
        if (mapObj.isEmpty()) {
            mapProp.remove(prop);
            if (mapProp.isEmpty())
                map.remove(subj);
        }
    }

    /**
     * Completely clear this graph
     */
    public void clear() {
        map = new IdentityHashMap<>();
    }
}
