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

package org.xowl.infra.utils.metrics;

import org.xowl.infra.utils.TextUtils;

/**
 * Implements a metric snapshot with an integer value
 *
 * @author Laurent Wouters
 */
public class MetricSnapshotInt implements MetricSnapshot<Integer> {
    /**
     * The timestamp for this snapshot
     */
    private final long timestamp;
    /**
     * The value of the metric
     */
    private final int value;

    /**
     * Initializes this snapshot
     *
     * @param value The value of the metric
     */
    public MetricSnapshotInt(int value) {
        this.timestamp = System.nanoTime();
        this.value = value;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String serializedString() {
        return Integer.toString(value);
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" +
                TextUtils.escapeStringJSON(MetricSnapshot.class.getCanonicalName()) +
                "\", \"value\": \"" +
                TextUtils.escapeStringJSON(Integer.toString(value)) +
                "\"}";
    }
}
