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

import org.xowl.infra.utils.Serializable;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.collections.Couple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents the snapshot of a collection of metrics
 *
 * @author Laurent Wouters
 */
public class MetricSnapshot implements Iterable<Couple<String, Object>>, Serializable {
    /**
     * The values in this snapshot
     */
    private final Collection<Couple<String, Object>> values;

    /**
     * Initializes this snapshot
     */
    public MetricSnapshot() {
        this.values = new ArrayList<>();
    }

    @Override
    public Iterator<Couple<String, Object>> iterator() {
        return values.iterator();
    }

    /**
     * Gets the value for a metric
     *
     * @param metric The identifier of a metric
     * @return The value associated to the metric
     */
    public Object get(String metric) {
        for (Couple<String, Object> map : values) {
            if (map.x.equals(metric))
                return map.y;
        }
        return null;
    }

    /**
     * Adds a metric and its value to the snapshot
     *
     * @param metric The identifier of a metric
     * @param value  The value associated to the metric
     */
    public void add(String metric, Object value) {
        this.values.add(new Couple<>(metric, value));
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        boolean first = true;
        for (Couple<String, Object> map : values) {
            if (!first)
                builder.append(", ");
            first = false;
            builder.append("\"");
            builder.append(TextUtils.escapeStringJSON(map.x));
            builder.append("\": \"");
            builder.append(TextUtils.escapeStringJSON(map.y.toString()));
            builder.append("\"");
        }
        builder.append("}");
        return builder.toString();
    }
}
