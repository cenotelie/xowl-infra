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

import java.io.IOException;
import java.util.jar.Manifest;

/**
 * A descriptors for a product at runtime
 *
 * @author Laurent Wouters
 */
public class Product implements Serializable {
    /**
     * The unique identifier of this product
     */
    protected String identifier;
    /**
     * The product's name
     */
    protected String name;
    /**
     * The product's description
     */
    protected String description;
    /**
     * The version information
     */
    protected VersionInfo version;
    /**
     * The product's copyright notice
     */
    protected String copyright;
    /**
     * The name of the icon for the product
     */
    protected String iconName;
    /**
     * The content of the icon (in Base64) for the product
     */
    protected String iconContent;
    /**
     * The product's vendor
     */
    protected String vendor;
    /**
     * A link to the vendor's web site
     */
    protected String vendorLink;
    /**
     * A link to the product's web site
     */
    protected String link;
    /**
     * The license information
     */
    protected License license;

    /**
     * Initializes an empty product description
     */
    protected Product() {
    }

    /**
     * Initializes this version info
     *
     * @param identifier The unique identifier of this product
     * @param name       The product's name
     * @param type       The type that is contained in the bundle for the product
     * @throws IOException When a resource cannot be read
     */
    public Product(String identifier, String name, Class<?> type) throws IOException {
        Manifest manifest = ManifestUtils.getManifest(type);
        this.identifier = identifier;
        this.name = name;
        this.description = manifest.getMainAttributes().getValue("Bundle-Description");
        this.version = new VersionInfo(manifest);
        this.iconName = null;
        this.iconContent = null;
        this.vendor = manifest.getMainAttributes().getValue("Bundle-Vendor");
        this.copyright = "Copyright (c) " + vendor;
        this.vendorLink = manifest.getMainAttributes().getValue("Bundle-DocURL");
        this.link = manifest.getMainAttributes().getValue("XOWL-Product-Link");
        this.license = new LicenseEmbedded(
                manifest.getMainAttributes().getValue("XOWL-License-Name"),
                type,
                manifest.getMainAttributes().getValue("XOWL-License-Resource")
        );
    }

    /**
     * Initializes this version info
     *
     * @param type The type that is contained in the bundle for the product
     * @throws IOException When a resource cannot be read
     */
    public Product(Class<?> type) throws IOException {
        Manifest manifest = ManifestUtils.getManifest(type);
        this.identifier = manifest.getMainAttributes().getValue("Bundle-SymbolicName");
        this.name = manifest.getMainAttributes().getValue("Bundle-Name");
        this.description = manifest.getMainAttributes().getValue("Bundle-Description");
        this.version = new VersionInfo(manifest);
        this.iconName = null;
        this.iconContent = null;
        this.vendor = manifest.getMainAttributes().getValue("Bundle-Vendor");
        this.copyright = "Copyright (c) " + vendor;
        this.vendorLink = manifest.getMainAttributes().getValue("Bundle-DocURL");
        this.link = manifest.getMainAttributes().getValue("XOWL-Product-Link");
        this.license = new LicenseEmbedded(
                manifest.getMainAttributes().getValue("XOWL-License-Name"),
                type,
                manifest.getMainAttributes().getValue("XOWL-License-Resource")
        );
    }

    /**
     * The unique identifier of this product
     *
     * @return The unique identifier of this product
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * The product's name
     *
     * @return The product's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the version information
     *
     * @return The version information
     */
    public VersionInfo getVersion() {
        return version;
    }

    /**
     * Gets the product's copyright notice
     *
     * @return The product's copyright notice
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * Gets the product's vendor
     *
     * @return The product's vendor
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Gets a link to the vendor's web site
     *
     * @return A link to the vendor's web site
     */
    public String getVendorLink() {
        return vendorLink;
    }

    /**
     * Gets a link to the product's web site
     *
     * @return A link to the product's web site
     */
    public String getLink() {
        return link;
    }

    /**
     * Gets the license information
     *
     * @return The license information
     */
    public License getLicense() {
        return license;
    }

    @Override
    public String serializedString() {
        return identifier;
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder("{");
        serializedJSONBase(builder);
        builder.append("}");
        return builder.toString();
    }

    /**
     * Serializes the base fields for the product
     *
     * @param builder The string builder to use
     */
    protected void serializedJSONBase(StringBuilder builder) {
        builder.append("\"type\": \"");
        builder.append(TextUtils.escapeStringJSON(Product.class.getCanonicalName()));
        builder.append("\", \"identifier\": \"");
        builder.append(TextUtils.escapeStringJSON(identifier));
        builder.append("\", \"name\": \"");
        builder.append(TextUtils.escapeStringJSON(name));
        builder.append("\", \"description\": \"");
        builder.append(TextUtils.escapeStringJSON(description));
        builder.append("\", \"version\": ");
        builder.append(version.serializedJSON());
        builder.append(", \"copyright\": \"");
        builder.append((copyright != null ? TextUtils.escapeStringJSON(copyright) : ""));
        builder.append("\", \"iconName\": \"");
        builder.append((iconName != null ? TextUtils.escapeStringJSON(iconName) : ""));
        builder.append("\", \"iconContent\": \"");
        builder.append((iconContent != null ? TextUtils.escapeStringJSON(iconContent) : ""));
        builder.append("\", \"vendor\": \"");
        builder.append((vendor != null ? TextUtils.escapeStringJSON(vendor) : ""));
        builder.append("\", \"vendorLink\": \"");
        builder.append((vendorLink != null ? TextUtils.escapeStringJSON(vendorLink) : ""));
        builder.append("\", \"link\": \"");
        builder.append((link != null ? TextUtils.escapeStringJSON(link) : ""));
        builder.append("\", \"license\": ");
        builder.append((license != null ? license.serializedJSON() : "{}"));
    }
}
