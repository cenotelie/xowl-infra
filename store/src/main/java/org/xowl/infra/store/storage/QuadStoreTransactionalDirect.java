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

import fr.cenotelie.commons.storage.ConcurrentWriteException;
import fr.cenotelie.commons.storage.NoTransactionException;
import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.store.rdf.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.WeakHashMap;

/**
 * Implements a transactional quad store with direct use of transactions coming from the backing storage system
 *
 * @author Laurent Wouters
 */
public class QuadStoreTransactionalDirect extends QuadStoreTransactional {

    @Override
    public QuadStoreTransaction newTransaction(boolean writable, boolean autocommit) {
        return null;
    }

    @Override
    public QuadStoreTransaction getTransaction() throws NoTransactionException {
        return null;
    }

    @Override
    public void setExecutionManager(ExecutionManager executionManager) {

    }

    @Override
    public void close() throws Exception {

    }
}
