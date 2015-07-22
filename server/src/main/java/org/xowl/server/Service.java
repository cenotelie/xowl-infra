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
 ******************************************************************************/

package org.xowl.server;

import org.xowl.store.AbstractRepository;
import org.xowl.store.writers.NQuadsSerializer;
import org.xowl.store.writers.NTripleSerializer;
import org.xowl.store.writers.RDFSerializer;
import org.xowl.utils.collections.Couple;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.*;

/**
 * Represents a service for the server
 *
 * @author Laurent Wouters
 */
public abstract class Service {

    /**
     * Retrieves the requested syntaxes by order of preference
     *
     * @param request The request
     * @return The syntaxes by order of preference
     */
    protected List<String> getSyntaxes(HttpServletRequest request) {
        String header = request.getHeader("Accept");
        if (header == null || header.isEmpty())
            return Collections.singletonList(AbstractRepository.SYNTAX_NTRIPLES);
        List<Couple<String, Float>> syntaxes = new ArrayList<>();
        String[] parts = header.split(",");
        for (String part : parts) {
            String value = part.trim();
            if (value.contains(";")) {
                String[] subs = value.split(";");
                syntaxes.add(new Couple<>(subs[0], Float.parseFloat(subs[1].substring(2))));
            } else {
                syntaxes.add(new Couple<>(value, 1.0f));
            }
        }
        if (syntaxes.isEmpty())
            return Collections.singletonList(AbstractRepository.SYNTAX_NTRIPLES);
        Collections.sort(syntaxes, new Comparator<Couple<String, Float>>() {
            @Override
            public int compare(Couple<String, Float> c1, Couple<String, Float> c2) {
                return Float.compare(c2.y, c1.y);
            }
        });
        List<String> result = new ArrayList<>(syntaxes.size());
        for (Couple<String, Float> couple : syntaxes)
            result.add(couple.x);
        return result;
    }

    /**
     * Gets the appropriate serializer
     *
     * @param syntaxes The requested syntax
     * @param writer   The target writer
     * @return The corresponding serializer
     */
    protected RDFSerializer getSerializer(List<String> syntaxes, Writer writer) {
        for (String syntax : syntaxes) {
            switch (syntax) {
                case AbstractRepository.SYNTAX_NTRIPLES:
                    return new NTripleSerializer(writer);
                case AbstractRepository.SYNTAX_NQUADS:
                    return new NQuadsSerializer(writer);
            }
        }
        return new NTripleSerializer(writer);
    }

    /**
     * Responds to a GET request
     *
     * @param request  The request
     * @param response The response to build
     */
    public abstract void onGet(HttpServletRequest request, HttpServletResponse response);
}
