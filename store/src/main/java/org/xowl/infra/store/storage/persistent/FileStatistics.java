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
    private final double accessesPerSecond;
    /**
     * The access contention (mean number of tries before an access succeed)
     */
    private final double accessesContention;
    /**
     * The number of loaded blocks
     */
    private final int loadedBlocks;
    /**
     * The number of dirty blocks
     */
    private final int dirtyBlocks;

    /**
     * Initializes this structure
     *
     * @param fileName           The file for which this object represents the statistics
     * @param accessesPerSecond  The number of accesses per second
     * @param accessesContention The access contention (mean number of tries before an access succeed)
     * @param loadedBlocks       The number of loaded blocks
     * @param dirtyBlocks        The number of dirty blocks
     */
    public FileStatistics(String fileName, double accessesPerSecond, double accessesContention, int loadedBlocks, int dirtyBlocks) {
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
                Double.toString(accessesPerSecond) +
                ", \"accessesContention\": " +
                Double.toString(accessesContention) +
                ", \"loadedBlocks\": " +
                Integer.toString(loadedBlocks) +
                ", \"dirtyBlocks\": " +
                Integer.toString(dirtyBlocks) +
                "}";
    }
}
