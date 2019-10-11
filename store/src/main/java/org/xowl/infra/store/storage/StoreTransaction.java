/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

import fr.cenotelie.commons.storage.Access;
import fr.cenotelie.commons.storage.Transaction;
import org.xowl.infra.store.ProxyObjects;
import org.xowl.infra.store.rdf.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a transaction for an interaction with a store of RDF quads
 *
 * @author Laurent Wouters
 */
public abstract class StoreTransaction extends Transaction implements DatasetProvider {
    /**
     * The current listeners on this store transaction
     */
    private final Collection<ChangeListener> listeners;
    /**
     * The proxy listener specific to this transaction
     */
    private final ChangeListener proxyListener;
    /**
     * Whether listeners are currently in use
     */
    private boolean useListeners;
    /**
     * The query engine for this transaction, if any
     */
    private RDFQueryEngine queryEngine;
    /**
     * The proxy objects ORM, if any
     */
    private ProxyObjects proxyObjects;

    /**
     * Initializes this transaction
     *
     * @param writable   Whether this transaction allows writing
     * @param autocommit Whether this transaction should commit when being closed
     */
    public StoreTransaction(boolean writable, boolean autocommit) {
        super(writable, autocommit);
        this.listeners = new ArrayList<>();
        this.proxyListener = new ChangeListener() {
            @Override
            public void onIncremented(Quad quad) {
                for (ChangeListener listener : listeners) {
                    listener.onIncremented(quad);
                }
            }

            @Override
            public void onDecremented(Quad quad) {
                for (ChangeListener listener : listeners) {
                    listener.onDecremented(quad);
                }
            }

            @Override
            public void onAdded(Quad quad) {
                for (ChangeListener listener : listeners) {
                    listener.onAdded(quad);
                }
            }

            @Override
            public void onRemoved(Quad quad) {
                for (ChangeListener listener : listeners) {
                    listener.onRemoved(quad);
                }
            }

            @Override
            public void onChange(Changeset changeset) {
                for (ChangeListener listener : listeners) {
                    listener.onChange(changeset);
                }
            }
        };
        this.useListeners = false;
        this.queryEngine = null;
        this.proxyObjects = null;
    }

    /**
     * Gets the dataset to use for this transaction
     *
     * @return The dataset to use for this transaction
     */
    public abstract Dataset getDataset();

    /**
     * Adds the specified listener to this store
     *
     * @param listener A listener
     */
    public void addListener(ChangeListener listener) {
        listeners.add(listener);
        if (!useListeners) {
            this.useListeners = true;
            ((DatasetImpl) getDataset()).addListener(proxyListener);
        }
    }

    /**
     * Removes the specified listener from this store
     *
     * @param listener A listener
     */
    public void removeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Gets the query engine
     *
     * @return The query engine
     */
    public RDFQueryEngine getQueryEngine() {
        if (queryEngine == null) {
            queryEngine = new RDFQueryEngine(this);
        }
        return queryEngine;
    }

    /**
     * Gets the proxy objects
     *
     * @return The proxy objects
     */
    public ProxyObjects getProxyObjects() {
        if (proxyObjects == null) {
            proxyObjects = new ProxyObjects(getDataset());
        }
        return proxyObjects;
    }

    @Override
    protected Access newAccess(long index, int length, boolean writable) {
        // do not allow direct access to the storage
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onClose() {
        if (useListeners) {
            ((DatasetImpl) getDataset()).removeListener(proxyListener);
        }
    }
}