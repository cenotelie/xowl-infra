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

package org.xowl.infra.store.storage;

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.store.IOUtils;
import org.xowl.infra.store.Serializable;
import org.xowl.infra.store.storage.persistent.FileStatistics;

/**
 * Represents the statistics of a store
 *
 * @author Laurent Wouters
 */
public class StoreStatistics implements Serializable {
    /**
     * The file statistics, if any
     */
    private final FileStatistics[] fileStatistics;

    /**
     * Initializes this structure
     *
     * @param fileStatistics The file statistics, if any
     */
    public StoreStatistics(FileStatistics[] fileStatistics) {
        this.fileStatistics = fileStatistics;
    }

    /**
     * Initializes this structure
     *
     * @param definition The definition
     */
    public StoreStatistics(ASTNode definition) {
        FileStatistics[] fileStatistics = null;
        for (ASTNode member : definition.getChildren()) {
            String name = IOUtils.unescape(member.getChildren().get(0).getValue());
            name = name.substring(1, name.length() - 1);
            switch (name) {
                case "files": {
                    fileStatistics = new FileStatistics[member.getChildren().get(1).getChildren().size()];
                    int i = 0;
                    for (ASTNode child : member.getChildren().get(1).getChildren()) {
                        fileStatistics[i] = new FileStatistics(child);
                    }
                }
            }
        }
        this.fileStatistics = fileStatistics;
    }

    /**
     * Gets the associated file statistics
     *
     * @return The associated file statistics
     */
    public FileStatistics[] getFileStatistics() {
        return fileStatistics;
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"type\": \"");
        builder.append(IOUtils.escapeStringJSON(StoreStatistics.class.getCanonicalName()));
        builder.append("\", \"files\": [");
        if (fileStatistics != null) {
            for (int i = 0; i != fileStatistics.length; i++) {
                if (i != 0)
                    builder.append(", ");
                builder.append(fileStatistics[i].serializedJSON());
            }
        }
        builder.append("]}");
        return builder.toString();
    }
}
