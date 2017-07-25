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

package org.xowl.infra.jsonrpc;

import org.junit.Assert;
import org.junit.Test;
import org.xowl.infra.utils.api.Reply;
import org.xowl.infra.utils.api.ReplyResult;

import java.util.Collection;

/**
 * Test suite for the Json-Rcp protocol
 *
 * @author Laurent Wouters
 */
public class JsonRcpTest {

    @Test
    public void testSubtract1() {
        JsonRpcServer server = new JsonRpcServerBase() {
            @Override
            public JsonRpcResponse handle(JsonRpcRequest request) {
                if (!"subtract".equals(request.getMethod()))
                    return JsonRpcResponseError.newMethodNotFound(request.getIdentifier());
                Object params = request.getParams();
                if (params == null || !(params instanceof Collection))
                    return JsonRpcResponseError.newInvalidParameters(request.getIdentifier());
                Object[] values = ((Collection) params).toArray();
                if (values.length != 2 || !(values[0] instanceof Integer) || !(values[1] instanceof Integer))
                    return JsonRpcResponseError.newInvalidParameters(request.getIdentifier());
                int result = (Integer) values[0] - (Integer) values[1];
                return new JsonRpcResponseResult<>(request.getIdentifier(), result);
            }
        };
        JsonRpcClient client = new TestClient(server);

        JsonRpcRequest request = new JsonRpcRequest("1", "subtract", new Object[]{42, 23});
        Reply reply = client.send(request);
        Assert.assertTrue(reply.isSuccess());
        JsonRpcResponse response = ((ReplyResult<JsonRpcResponse>) reply).getData();
        Assert.assertTrue(response instanceof JsonRpcResponseResult);
        JsonRpcResponseResult<Integer> result = (JsonRpcResponseResult<Integer>) response;
        Assert.assertEquals("1", response.getIdentifier());
        Assert.assertEquals((Integer) 19, result.getResult());
    }
}
