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

package org.xowl.infra.server.xsp;

import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.utils.ApiError;
import org.xowl.infra.utils.TextUtils;

/**
 * Implements a reply to a xOWL server protocol request when the request failed with an API error
 *
 * @author Laurent Wouters
 */
public class XSPReplyApiError implements XSPReply {
    /**
     * The API error to report
     */
    private final ApiError error;

    /**
     * Initializes this reply
     *
     * @param error The API error to report
     */
    public XSPReplyApiError(ApiError error) {
        this.error = error;
    }

    /**
     * Gets the API error to report
     *
     * @return The API error to report
     */
    public ApiError getError() {
        return error;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public String getMessage() {
        return error.getMessage();
    }

    @Override
    public String serializedString() {
        return "ERROR: " + error.getMessage();
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" +
                TextUtils.escapeStringJSON(XSPReply.class.getCanonicalName()) +
                "\", \"kind\": \"" +
                TextUtils.escapeStringJSON(XSPReplyApiError.class.getSimpleName()) +
                "\", \"isSuccess\": false" +
                ", \"message\": \"\"" +
                ", \"payload\": " + error.serializedJSON() + "}";
    }

    /**
     * Loads an API error from its AST definition
     *
     * @param root The root of the AST definition
     * @return The API error
     */
    public static ApiError parseApiError(ASTNode root) {
        int code = 0;
        String message = "";
        String helpLink = "";
        for (ASTNode child : root.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            switch (name) {
                case "code": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    String value = TextUtils.unescape(nodeValue.getValue());
                    code = Integer.parseInt(value);
                    break;
                }
                case "message": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    message = TextUtils.unescape(nodeValue.getValue());
                    message = message.substring(1, message.length() - 1);
                    break;
                }
                case "helpLink": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    helpLink = TextUtils.unescape(nodeValue.getValue());
                    helpLink = helpLink.substring(1, helpLink.length() - 1);
                    break;
                }
            }
        }
        return new ApiError(code, message, helpLink);
    }
}
