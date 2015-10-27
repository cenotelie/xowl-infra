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

package org.xowl.store.storage.persistent;

/**
 * Represents a persisted store of RDF datasets
 * <p/>
 * General layout:
 * - File .header       Header
 * - File .index        Indexes in the content
 * - Files .graph       The content graphs
 * <p/>
 * File header:
 * - Schema version             2 bytes
 * - Flags                      2 bytes
 * - Number of content files    2 bytes
 * - Number of blank nodes      8 bytes
 * - Number of OWL anonymous    8 bytes
 * - Number of opaque exp       8 bytes
 * <p/>
 * File index:
 * <p/>
 * <p/>
 * File graph - Value page:
 * - Entry 0..n: single value
 * <p/>
 * File graph - Graph page:
 * - Entry 0: node for the graph
 * - Entry 1..n: svo parts of the quads
 * <p/>
 * Representation of a value:
 * - [name]         [type ID]   [data]
 * - Raw bytes      char=0      int=counter, int=length, bytes=(the content)
 * - Simple string  char=1      int=counter, int=length, bytes=(encoded string data)
 * - Tagged string  char=2      int=counter, int=data length, bytes=(encoded string data), int=tag length, bytes=(encoded tag data)
 * - Byte-mapped    char=3      int=counter, byte=value, long=(key to datatype), int=length, bytes=(encoded lexical data)
 * - char-mapped    char=4      int=counter, char=value, long=(key to datatype), int=length, bytes=(encoded lexical data)
 * - Int-mapped     char=5      int=counter, int=value, long=(key to datatype), int=length, bytes=(encoded lexical data)
 * - Long-mapped    char=6      int=counter, long=value, long=(key to datatype), int=length, bytes=(encoded lexical data)
 * - Float-mapped   char=7      int=counter, int=value, long=(key to datatype), int=length, bytes=(encoded lexical data)
 * - Double-mapped  char=8      int=counter, long=value, long=(key to datatype), int=length, bytes=(encoded lexical data)
 * - Unmapped lit.  char=9      int=counter, long=(key to datatype), int=length, bytes=(encoded lexical data)
 * <p/>
 * Representation of the nodes (10 bytes each):
 * - [node type]    [type ID]   [data for the node]
 * - IRI            char=0      long=(key to the IRI value)
 * - Blank          char=1      long=(id of the blank node)
 * - Literal        char=2      long=(key to the literal value)
 * - Variable       char=3      long=(key to the variable name)
 * - OWL Anonymous  char=4      long=(id of the anonymous individual)
 * - Dynamic exp    char=5      long=(id of the expression)
 * <p/>
 * Representation of the svo part of a quad (34 bytes):
 * - subject        (10 bytes)
 * - verb           (10 bytes)
 * - object         (10 bytes)
 * - multiplicity   (int)
 *
 * @author Laurent Wouters
 */
class PersistedBackend {

    private FileStoreFile backendHeader;

    private FileStoreFile backendIndex;
}
