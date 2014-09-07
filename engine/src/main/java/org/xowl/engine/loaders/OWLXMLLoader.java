/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters and others
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
package org.xowl.engine.loaders;

import org.xowl.hime.redist.ParseResult;
import org.xowl.lang.owl2.Axiom;
import org.xowl.utils.Logger;

import java.io.Reader;
import java.util.List;

/**
 * Represents a loader for OWL/XML data
 *
 * @author Laurent Wouters
 */
public class OWLXMLLoader implements Loader {
    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Axiom> loadAxioms(Logger logger, Reader reader, String uri) {
        return null;
    }
}
