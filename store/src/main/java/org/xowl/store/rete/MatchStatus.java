/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/
package org.xowl.store.rete;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the status of a matching RETE rule
 *
 * @author Laurent Wouters
 */
public class MatchStatus {
    /**
     * The match steps
     */
    private final List<MatchStatusStep> steps;

    /**
     * Gets the match steps
     *
     * @return The match steps
     */
    public List<MatchStatusStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    /**
     * Initializes this status
     */
    public MatchStatus() {
        this.steps = new ArrayList<>();
    }

    /**
     * Adds a step
     *
     * @param step The step to add
     */
    protected void addStep(MatchStatusStep step) {
        steps.add(step);
    }

    /**
     * Serializes this matching status in the JSON syntax
     *
     * @param writer The writer to write to
     */
    public void printJSON(Writer writer) throws IOException {
        writer.write("{ \"steps\": [");
        for (int i = 0; i != steps.size(); i++) {
            if (i != 0)
                writer.write(", ");
            steps.get(i).printJSON(writer);
        }
        writer.write("] }");
    }
}
