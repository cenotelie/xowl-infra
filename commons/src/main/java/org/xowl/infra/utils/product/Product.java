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
    private final String identifier;
    /**
     * The product's name
     */
    private final String name;
    /**
     * The version information
     */
    private final VersionInfo version;
    /**
     * The product's copyright notice
     */
    private final String copyright;
    /**
     * The product's vendor
     */
    private final String vendor;
    /**
     * A link to the vendor's web site
     */
    private final String vendorLink;
    /**
     * A link to the product's web site
     */
    private final String link;
    /**
     * The license information
     */
    private final License license;

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
        this.version = new VersionInfo(manifest);
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
        this.version = new VersionInfo(manifest);
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
        return "{\"type\": \"" +
                TextUtils.escapeStringJSON(Product.class.getCanonicalName()) +
                "\", \"identifier\": \"" +
                TextUtils.escapeStringJSON(identifier) +
                "\", \"name\": \"" +
                TextUtils.escapeStringJSON(name) +
                "\", \"version\": " +
                version.serializedJSON() +
                ", \"copyright\": \"" +
                (copyright != null ? TextUtils.escapeStringJSON(copyright) : "") +
                "\", \"vendor\": \"" +
                (vendor != null ? TextUtils.escapeStringJSON(vendor) : "") +
                "\", \"vendorLink\": \"" +
                (vendorLink != null ? TextUtils.escapeStringJSON(vendorLink) : "") +
                "\", \"link\": \"" +
                (link != null ? TextUtils.escapeStringJSON(link) : "") +
                "\", \"license\": " +
                (license != null ? license.serializedJSON() : "{}") +
                "}";
    }
}
