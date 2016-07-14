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

package org.xowl.infra.store.storage.persistent;

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.store.IOUtils;
import org.xowl.infra.store.Serializable;

/**
 * Represents the current statistics about a file
 *
 * @author Laurent Wouters
 */
public class FileStatistics implements Serializable {
    /**
     * Refresh period for the statistics (0.5 second)
     */
    public static final long REFRESH_PERIOD = 500000000;

    /**
     * The file for which this object represents the statistics
     */
    private final String fileName;
    /**
     * The number of accesses per second
     */
    private final long accessesPerSecond;
    /**
     * The access contention (mean number of tries before an access succeed)
     */
    private final long accessesContention;
    /**
     * The number of loaded blocks
     */
    private final int loadedBlocks;
    /**
     * The number of dirty blocks
     */
    private final int dirtyBlocks;

    /**
     * Gets the file for which this object represents the statistics
     *
     * @return The file for which this object represents the statistics
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Gets the number of accesses per second
     *
     * @return The number of accesses per second
     */
    public long getAccessesPerSecond() {
        return accessesPerSecond;
    }

    /**
     * Gets the access contention (mean number of tries before an access succeed)
     *
     * @return The access contention (mean number of tries before an access succeed)
     */
    public long getAccessesContention() {
        return accessesContention;
    }

    /**
     * Gets the number of loaded blocks
     *
     * @return The number of loaded blocks
     */
    public int getLoadedBlocks() {
        return loadedBlocks;
    }

    /**
     * Gets the number of dirty blocks
     *
     * @return The number of dirty blocks
     */
    public int getDirtyBlocks() {
        return dirtyBlocks;
    }

    /**
     * Initializes this structure
     *
     * @param fileName           The file for which this object represents the statistics
     * @param accessesPerSecond  The number of accesses per second
     * @param accessesContention The access contention (mean number of tries before an access succeed)
     * @param loadedBlocks       The number of loaded blocks
     * @param dirtyBlocks        The number of dirty blocks
     */
    public FileStatistics(String fileName, long accessesPerSecond, long accessesContention, int loadedBlocks, int dirtyBlocks) {
        this.fileName = fileName;
        this.accessesPerSecond = accessesPerSecond;
        this.accessesContention = accessesContention;
        this.loadedBlocks = loadedBlocks;
        this.dirtyBlocks = dirtyBlocks;
    }

    /**
     * Initializes this structure
     *
     * @param definition The definition
     */
    public FileStatistics(ASTNode definition) {
        String fileName = "";
        long accessesPerSecond = 0;
        long accessesContention = 0;
        int loadedBlocks = 0;
        int dirtyBlocks = 0;
        for (ASTNode member : definition.getChildren()) {
            String name = IOUtils.unescape(member.getChildren().get(0).getValue());
            name = name.substring(1, name.length() - 1);
            switch (name) {
                case "fileName":
                    fileName = IOUtils.unescape(member.getChildren().get(1).getValue());
                    fileName = fileName.substring(1, fileName.length() - 1);
                    break;
                case "accessesPerSecond": {
                    String value = IOUtils.unescape(member.getChildren().get(1).getValue());
                    accessesPerSecond = Long.parseLong(value);
                    break;
                }
                case "accessesContention": {
                    String value = IOUtils.unescape(member.getChildren().get(1).getValue());
                    accessesContention = Long.parseLong(value);
                    break;
                }
                case "loadedBlocks": {
                    String value = IOUtils.unescape(member.getChildren().get(1).getValue());
                    loadedBlocks = Integer.parseInt(value);
                    break;
                }
                case "dirtyBlocks": {
                    String value = IOUtils.unescape(member.getChildren().get(1).getValue());
                    loadedBlocks = Integer.parseInt(value);
                    break;
                }
            }
        }
        this.fileName = fileName;
        this.accessesPerSecond = accessesPerSecond;
        this.accessesContention = accessesContention;
        this.loadedBlocks = loadedBlocks;
        this.dirtyBlocks = dirtyBlocks;
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" +
                IOUtils.escapeStringJSON(FileStatistics.class.getCanonicalName()) +
                "\", \"fileName\": \"" +
                IOUtils.escapeStringJSON(fileName) +
                "\", \"accessesPerSecond\": " +
                Long.toString(accessesPerSecond) +
                ", \"accessesContention\": " +
                Long.toString(accessesContention) +
                ", \"loadedBlocks\": " +
                Integer.toString(loadedBlocks) +
                ", \"dirtyBlocks\": " +
                Integer.toString(dirtyBlocks) +
                "}";
    }
}
