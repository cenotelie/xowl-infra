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
 * Represents a transactional quad store.
 * Accessing quads within this store requires the creation of a transaction.
 * This store provides the following guarantees:
 * - Atomicity: either a transaction full commits to the store, or nothing is changed
 * - Consistency: the store is in a consistent state before a transaction begins and after the transaction commits
 * - Isolation: transactions can only see the state of the store as of the time of the transaction's launch, with the changes of all fully committed previous transactions (snapshot isolation).
 * Transactions cannot see the changes of other transactions started after itself.
 * - Durability: the store guarantees that a transaction is durable after the transaction has been fully committed (the commit function returned), as long as the base store provides such guarantee.
 *
 * @author Laurent Wouters
 */
public class QuadStoreTransactional implements QuadStore {
    /**
     * The original store to be protected by this interface
     */
    private final QuadStore base;
    /**
     * The currently running transactions
     */
    private volatile QuadStoreTransaction[] transactions;
    /**
     * The currently running transactions by thread
     */
    private final WeakHashMap<Thread, QuadStoreTransaction> transactionsByThread;
    /**
     * The number of running transactions
     */
    private volatile int transactionsCount;
    /**
     * The index of transaction data currently in the log
     */
    private volatile DatasetDiff[] index;
    /**
     * The number of transaction data in the index
     */
    private volatile int indexLength;


    /**
     * Starts a new transaction
     * The transaction must be ended by a call to the transaction's close method.
     * The transaction will NOT automatically commit when closed, the commit method should be called before closing.
     *
     * @param writable Whether the transaction shall support writing
     * @return The new transaction
     */
    public QuadStoreTransaction newTransaction(boolean writable) {
        return newTransaction(writable, false);
    }

    /**
     * Starts a new transaction
     * The transaction must be ended by a call to the transaction's close method.
     *
     * @param writable   Whether the transaction shall support writing
     * @param autocommit Whether this transaction should commit when being closed
     * @return The new transaction
     */
    public QuadStoreTransaction newTransaction(boolean writable, boolean autocommit) {
        QuadStoreTransaction transaction = new QuadStoreTransaction(writable, autocommit) {
            @Override
            protected void doCommit() throws ConcurrentWriteException {
                // do nothing
            }

            @Override
            protected void onClose() {
                transactionsByThread.remove(Thread.currentThread());
            }
        };
        synchronized (transactionsByThread) {
            transactionsByThread.put(Thread.currentThread(), transaction);
        }
        return transaction;
    }

    /**
     * Gets the currently running transactions for the current thread
     *
     * @return The current transaction
     * @throws NoTransactionException when the current thread does not use a transaction
     */
    public QuadStoreTransaction getTransaction() throws NoTransactionException {
        return transactionsByThread.get(Thread.currentThread());
    }


    @Override
    public void setExecutionManager(ExecutionManager executionManager) {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void addListener(ChangeListener listener) {

    }

    @Override
    public void removeListener(ChangeListener listener) {

    }

    @Override
    public long getMultiplicity(Quad quad) {
        return 0;
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return 0;
    }

    @Override
    public Iterator<? extends Quad> getAll() {
        return null;
    }

    @Override
    public Iterator<? extends Quad> getAll(GraphNode graph) {
        return null;
    }

    @Override
    public Iterator<? extends Quad> getAll(SubjectNode subject, Property property, Node object) {
        return null;
    }

    @Override
    public Iterator<? extends Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return null;
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public long count(GraphNode graph) {
        return 0;
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) {
        return 0;
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return 0;
    }

    @Override
    public void insert(Changeset changeset) {

    }

    @Override
    public void add(Quad quad) {

    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) {

    }

    @Override
    public void remove(Quad quad) {

    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void clear(GraphNode graph) {

    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) {

    }

    @Override
    public void move(GraphNode origin, GraphNode target) {

    }

    @Override
    public IRINode getIRINode(GraphNode graph) {
        return null;
    }

    @Override
    public IRINode getIRINode(String iri) {
        return null;
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        return null;
    }

    @Override
    public BlankNode getBlankNode() {
        return null;
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        return null;
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        return null;
    }

    @Override
    public DynamicNode getDynamicNode(EvaluableExpression evaluable) {
        return null;
    }
}
