/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.store.http;

/**
 * Constants for using the HTTP protocol
 *
 * @author Laurent Wouters
 */
public interface HttpConstants {
    /**
     * The MIME content type for plain text
     */
    String MIME_TEXT_PLAIN = "text/plain";
    /**
     * The MIME content type for JSON
     */
    String MIME_JSON = "application/json";
    /**
     * Unknown error from the server
     */
    int HTTP_UNKNOWN_ERROR = 520;
}
