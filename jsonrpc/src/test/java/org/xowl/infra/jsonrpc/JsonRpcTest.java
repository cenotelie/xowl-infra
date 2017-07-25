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
import org.xowl.infra.utils.api.ReplySuccess;
import org.xowl.infra.utils.json.SerializedUnknown;

import java.util.Collection;

/**
 * Test suite for the Json-Rcp protocol
 *
 * @author Laurent Wouters
 */
public class JsonRpcTest {

    @Test
    public void testPositionalParameters1() {
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

    @Test
    public void testPositionalParameters2() {
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

        JsonRpcRequest request = new JsonRpcRequest("1", "subtract", new Object[]{23, 42});
        Reply reply = client.send(request);
        Assert.assertTrue(reply.isSuccess());
        JsonRpcResponse response = ((ReplyResult<JsonRpcResponse>) reply).getData();
        Assert.assertTrue(response instanceof JsonRpcResponseResult);
        JsonRpcResponseResult<Integer> result = (JsonRpcResponseResult<Integer>) response;
        Assert.assertEquals("1", response.getIdentifier());
        Assert.assertEquals((Integer) (-19), result.getResult());
    }

    @Test
    public void testNamedParameters() {
        JsonRpcServer server = new JsonRpcServerBase() {
            @Override
            public JsonRpcResponse handle(JsonRpcRequest request) {
                if (!"subtract".equals(request.getMethod()))
                    return JsonRpcResponseError.newMethodNotFound(request.getIdentifier());
                Object params = request.getParams();
                if (params == null || !(params instanceof SerializedUnknown))
                    return JsonRpcResponseError.newInvalidParameters(request.getIdentifier());
                Object subtrahend = ((SerializedUnknown) params).getValueFor("subtrahend");
                Object minuend = ((SerializedUnknown) params).getValueFor("minuend");
                if (subtrahend == null || !(subtrahend instanceof Integer)
                        || minuend == null || !(minuend instanceof Integer))
                    return JsonRpcResponseError.newInvalidParameters(request.getIdentifier());
                int result = ((Integer) minuend) - (Integer) subtrahend;
                return new JsonRpcResponseResult<>(request.getIdentifier(), result);
            }
        };
        JsonRpcClient client = new TestClient(server);

        SerializedUnknown parameters = new SerializedUnknown();
        parameters.addProperty("subtrahend", 23);
        parameters.addProperty("minuend", 42);
        JsonRpcRequest request = new JsonRpcRequest("1", "subtract", parameters);
        Reply reply = client.send(request);
        Assert.assertTrue(reply.isSuccess());
        JsonRpcResponse response = ((ReplyResult<JsonRpcResponse>) reply).getData();
        Assert.assertTrue(response instanceof JsonRpcResponseResult);
        JsonRpcResponseResult<Integer> result = (JsonRpcResponseResult<Integer>) response;
        Assert.assertEquals("1", response.getIdentifier());
        Assert.assertEquals((Integer) 19, result.getResult());
    }

    @Test
    public void testSimpleNotification1() {
        JsonRpcServer server = new JsonRpcServerBase() {
            @Override
            public JsonRpcResponse handle(JsonRpcRequest request) {
                Assert.assertTrue(request.isNotification());
                if (!"update".equals(request.getMethod()))
                    return JsonRpcResponseError.newMethodNotFound(request.getIdentifier());
                Object params = request.getParams();
                if (params == null || !(params instanceof Collection))
                    return JsonRpcResponseError.newInvalidParameters(request.getIdentifier());
                return null;
            }
        };
        JsonRpcClient client = new TestClient(server);

        JsonRpcRequest request = new JsonRpcRequest(null, "update", new Object[]{1, 2, 3, 4, 5});
        Assert.assertTrue(request.isNotification());
        Reply reply = client.send(request);
        Assert.assertEquals(ReplySuccess.instance(), reply);
    }

    @Test
    public void testSimpleNotification2() {
        JsonRpcServer server = new JsonRpcServerBase() {
            @Override
            public JsonRpcResponse handle(JsonRpcRequest request) {
                Assert.assertTrue(request.isNotification());
                if (!"foobar".equals(request.getMethod()))
                    return JsonRpcResponseError.newMethodNotFound(request.getIdentifier());
                Object params = request.getParams();
                if (params != null)
                    return JsonRpcResponseError.newInvalidParameters(request.getIdentifier());
                return null;
            }
        };
        JsonRpcClient client = new TestClient(server);

        JsonRpcRequest request = new JsonRpcRequest(null, "foobar", null);
        Assert.assertTrue(request.isNotification());
        Reply reply = client.send(request);
        Assert.assertEquals(ReplySuccess.instance(), reply);
    }

    @Test
    public void testMissingMethod() {
        JsonRpcServer server = new JsonRpcServerBase() {
            @Override
            public JsonRpcResponse handle(JsonRpcRequest request) {
                if (!"myMethod".equals(request.getMethod()))
                    return JsonRpcResponseError.newMethodNotFound(request.getIdentifier());
                return new JsonRpcResponseResult<>(request.getIdentifier(), "ok");
            }
        };
        JsonRpcClient client = new TestClient(server);

        JsonRpcRequest request = new JsonRpcRequest("1", "foobar", null);
        Reply reply = client.send(request);
        Assert.assertTrue(reply.isSuccess());
        JsonRpcResponse response = ((ReplyResult<JsonRpcResponse>) reply).getData();
        Assert.assertTrue(response instanceof JsonRpcResponseError);
        JsonRpcResponseError error = (JsonRpcResponseError) response;
        Assert.assertEquals("1", error.getIdentifier());
        Assert.assertEquals(-32601, error.getCode());
        Assert.assertEquals("Method not found", error.getMessage());
    }
}
