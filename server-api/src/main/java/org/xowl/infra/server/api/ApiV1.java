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

package org.xowl.infra.server.api;

import org.xowl.infra.utils.ApiError;

/**
 * Constants for the xOWL Triple Store Server API, version 1
 *
 * @author Laurent Wouters
 */
public interface ApiV1 {
    /**
     * The URI prefix for the API
     */
    String URI_PREFIX = "/api/v1";
    /**
     * The name of the cookie for the authentication token
     */
    String AUTH_TOKEN = "__Secure-xOWL-Token-v1";

    /**
     * The resource that contains the definition for this version of the API
     */
    String RESOURCE_DEFINITION = "/org/xowl/infra/server/api/v1/definition.raml";
    /**
     * The resources that contains schema definitions for this version of the API
     */
    String[] RESOURCE_SCHEMAS = new String[]{
            "org/xowl/infra/server/api/v1/schemaUtils.json",
            "org/xowl/infra/server/api/v1/SchemaServerAPI.json"
    };


    /**
     * The prefix for the help links
     */
    String ERROR_HELP_PREFIX = "http://xowl.org/support/server/errors/v1/";
    /**
     * API error - Failed to parse the content of the request.
     */
    ApiError ERROR_CONTENT_PARSING_FAILED = new ApiError(1,
            "Failed to parse the content of the request.",
            ERROR_HELP_PREFIX + "0x0001.html");
    /**
     * API error - The serialization of data failed on the client side.
     */
    ApiError ERROR_SERIALIZATION_FAILED = new ApiError(2,
            "The serialization of data failed on the client side.",
            ERROR_HELP_PREFIX + "0x0002.html");
    /**
     * API error - This user already exists.
     */
    ApiError ERROR_USER_ALREADY_EXIST = new ApiError(3,
            "This user already exists.",
            ERROR_HELP_PREFIX + "0x0003.html");
    /**
     * API error - User name does not meet requirements
     */
    ApiError ERROR_USER_NAME_REQUIREMENT_FAILED = new ApiError(4,
            "User name does not meet requirements: [_a-zA-Z0-9]+",
            ERROR_HELP_PREFIX + "0x0004.html");
    /**
     * API error - Password does not meet length requirements
     */
    ApiError ERROR_USER_PASSWORD_REQUIREMENT_FAILED = new ApiError(5,
            "Password does not meet length requirements.",
            ERROR_HELP_PREFIX + "0x0005.html");
    /**
     * API error - The database already exists
     */
    ApiError ERROR_DB_ALREADY_EXIST = new ApiError(6,
            "This database already exists.",
            ERROR_HELP_PREFIX + "0x0006.html");
    /**
     * API error - Database name does not match requirements
     */
    ApiError ERROR_DB_NAME_REQUIREMENT_FAILED = new ApiError(7,
            "Database name does not match requirements: [_a-zA-Z0-9]+",
            ERROR_HELP_PREFIX + "0x0007.html");
    /**
     * API error - The rule is not active
     */
    ApiError ERROR_RULE_NOT_ACTIVE = new ApiError(8,
            "The rule is not active.",
            ERROR_HELP_PREFIX + "0x0008.html");
    /**
     * API error - The specification of default graphs is not supported
     */
    ApiError ERROR_DEFAULT_GRAPH_NOT_SUPPORTED = new ApiError(9,
            "The specification of default graphs is not supported.",
            ERROR_HELP_PREFIX + "0x0009.html");
    /**
     * API error - The specification of named graphs is not supported.
     */
    ApiError ERROR_NAMED_GRAPH_NOT_SUPPORTED = new ApiError(10,
            "The specification of named graphs is not supported.",
            ERROR_HELP_PREFIX + "0x000A.html");
}
