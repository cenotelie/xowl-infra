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

import fr.cenotelie.commons.utils.collections.Couple;
import fr.cenotelie.commons.utils.logging.SinkLogger;
import org.junit.Assert;
import org.junit.Test;
import org.xowl.infra.store.IRIs;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.rdf.Quad;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * Basic tests for the on-disk store
 *
 * @author Laurent Wouters
 */
public class QuadStoreOnDiskSimpleTest {

    @Test
    public void testInsert() throws Exception {
        Path p = Files.createTempDirectory("testInsert");
        SinkLogger logger = new SinkLogger();
        try (RepositoryRDF repositoryRdf = new RepositoryRDF(StoreFactory.newPersisted(p.toFile(), false))) {
            repositoryRdf.runAsTransaction((repository, transaction) -> {
                repository.load(logger, IRIs.RDF);
                return null;
            }, true);
        }
        Assert.assertFalse("Failed to load", logger.isOnError());

        try (RepositoryRDF repositoryRdf = new RepositoryRDF(StoreFactory.newPersisted(p.toFile(), false))) {
            repositoryRdf.runAsTransaction((repository, transaction) -> {
                Iterator<? extends Quad> iterator = transaction.getDataset().getAll();
                while (iterator.hasNext()) {
                    Quad quad = iterator.next();
                    //System.out.println(quad);
                }
                return null;
            }, false);
        }
    }

    @Test
    public void testInsert2() throws Exception {
        Path p = Files.createTempDirectory("testInsert");
        try (RepositoryRDF repositoryRdf = new RepositoryRDF(StoreFactory.newPersisted(p.toFile(), false))) {
            Couple<Quad, Quad> result = repositoryRdf.runAsTransaction((repository, transaction) -> {
                Quad q1 = new Quad(
                        transaction.getDataset().getIRINode("http://xowl.org/infra/tests/g"),
                        transaction.getDataset().getIRINode("http://xowl.org/infra/tests/x"),
                        transaction.getDataset().getIRINode("http://xowl.org/infra/tests/p"),
                        transaction.getDataset().getIRINode("http://xowl.org/infra/tests/y1")
                );
                Quad q2 = new Quad(
                        transaction.getDataset().getIRINode("http://xowl.org/infra/tests/g"),
                        transaction.getDataset().getIRINode("http://xowl.org/infra/tests/x"),
                        transaction.getDataset().getIRINode("http://xowl.org/infra/tests/p"),
                        transaction.getDataset().getIRINode("http://xowl.org/infra/tests/y2")
                );
                return new Couple<>(q1, q2);
            }, false);
            final Quad quad1 = result.x;
            final Quad quad2 = result.y;

            repositoryRdf.runAsTransaction((repository, transaction) -> {
                transaction.getDataset().add(quad1);
                transaction.getDataset().add(quad2);
                return null;
            }, true);

            repositoryRdf.runAsTransaction((repository, transaction) -> {
                Iterator<? extends Quad> iterator = transaction.getDataset().getAll(quad1.getSubject(), quad1.getProperty(), null);
                while (iterator.hasNext()) {
                    Quad quad = iterator.next();
                    //System.out.println(quad);
                }
                return null;
            }, false);
        }
    }
}
