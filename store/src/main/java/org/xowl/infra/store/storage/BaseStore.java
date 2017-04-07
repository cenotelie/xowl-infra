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

import org.xowl.infra.store.execution.EvaluableFactory;
import org.xowl.infra.utils.metrics.Metric;
import org.xowl.infra.utils.metrics.MetricComposite;
import org.xowl.infra.utils.metrics.MetricSnapshot;
import org.xowl.infra.utils.metrics.MetricSnapshotComposite;

/**
 * Represents the public API of a data store (a dataset and a node manager)
 *
 * @author Laurent Wouters
 */
public abstract class BaseStore implements Dataset, NodeManager, AutoCloseable {
    /**
     * The composite metric for this store
     */
    protected final MetricComposite metricStore;

    /**
     * Initializes this store
     */
    protected BaseStore() {
        this.metricStore = new MetricComposite(BaseStore.class.getCanonicalName() + "@" + Integer.toString(hashCode()),
                "Store",
                1000000000);
    }

    /**
     * Gets the composite metric for this store
     *
     * @return The metric for this store
     */
    public Metric getMetric() {
        return metricStore;
    }

    /**
     * Gets a snapshot of the metrics for this store
     *
     * @param timestamp The timestamp to use
     * @return The snapshot
     */
    public MetricSnapshot getMetricSnapshot(long timestamp) {
        return new MetricSnapshotComposite(timestamp);
    }

    /**
     * Sets the evaluable factory for this node manager
     *
     * @param evaluableFactory The evaluable factory
     */
    public void setEvaluableFactory(EvaluableFactory evaluableFactory) {
    }

    /**
     * Commits the outstanding changes to this store
     *
     * @return Whether the operation succeeded
     */
    public boolean commit() {
        // do nothing
        return true;
    }

    /**
     * Rollback the outstanding changes to this store
     *
     * @return Whether the operation succeeded
     */
    public boolean rollback() {
        // do nothing
        return true;
    }

    @Override
    public void close() throws Exception {
        // do nothing
    }
}
