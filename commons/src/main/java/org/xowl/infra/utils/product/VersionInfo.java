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

package org.xowl.infra.utils.product;

import org.xowl.infra.utils.Serializable;
import org.xowl.infra.utils.TextUtils;

import java.util.jar.Manifest;

/**
 * Represents the information about the version of a product or a component
 *
 * @author Laurent Wouters
 */
public class VersionInfo implements Serializable {
    /**
     * The version number
     */
    private final String number;
    /**
     * The SCM tag indicating the source version
     */
    private final String scmTag;
    /**
     * The name of the user that performed the build for this version
     */
    private final String buildUser;
    /**
     * The tag for the build that produced this version
     */
    private final String buildTag;
    /**
     * The timestamp for the build that produced this version
     */
    private final String buildTimestamp;

    /**
     * Initializes this version info
     *
     * @param manifest The original manifest
     */
    public VersionInfo(Manifest manifest) {
        this.number = manifest.getMainAttributes().getValue("Bundle-Version");
        this.scmTag = manifest.getMainAttributes().getValue("XOWL-SCM-Tag");
        this.buildUser = manifest.getMainAttributes().getValue("Built-By");
        this.buildTag = manifest.getMainAttributes().getValue("XOWL-Build-Tag");
        this.buildTimestamp = manifest.getMainAttributes().getValue("XOWL-Build-Timestamp");
    }

    /**
     * Gets the version number
     *
     * @return The version number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Gets the SCM tag indicating the source version
     *
     * @return The SCM tag indicating the source version
     */
    public String getScmTag() {
        return scmTag;
    }

    /**
     * Gets the name of the user that performed the build for this version
     *
     * @return The name of the user that performed the build for this version
     */
    public String getBuildUser() {
        return buildUser;
    }

    /**
     * Gets the tag for the build that produced this version
     *
     * @return The tag for the build that produced this version
     */
    public String getBuildTag() {
        return buildTag;
    }

    /**
     * Gets the timestamp for the build that produced this version
     *
     * @return The timestamp for the build that produced this version
     */
    public String getBuildTimestamp() {
        return buildTimestamp;
    }

    @Override
    public String serializedString() {
        return number;
    }

    @Override
    public String serializedJSON() {
        return "{\"number\": \"" +
                TextUtils.escapeStringJSON(number) +
                "\", \"scmTag\": \"" +
                (scmTag != null ? TextUtils.escapeStringJSON(scmTag) : "") +
                "\", \"buildUser\": \"" +
                (buildUser != null ? TextUtils.escapeStringJSON(buildUser) : "") +
                "\", \"buildTag\": \"" +
                (buildTag != null ? TextUtils.escapeStringJSON(buildTag) : "") +
                "\", \"buildTimestamp\": \"" +
                (buildTimestamp != null ? TextUtils.escapeStringJSON(buildTimestamp) : "") +
                "\"}";
    }
}
