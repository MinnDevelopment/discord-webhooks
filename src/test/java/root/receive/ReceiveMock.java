/*
 * Copyright 2018-2020 Florian Spieß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package root.receive;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.receive.EntityFactory;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.receive.ReadonlyUser;
import okhttp3.OkHttpClient;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import root.IOTestUtil;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EntityFactory.class)
public class ReceiveMock {
    @Captor
    private ArgumentCaptor<JSONObject> jsonCaptor;

    @Mock
    private OkHttpClient httpClient;

    private WebhookClient client;

    private AutoCloseable mocks;

    @Before
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        PowerMockito.mockStatic(EntityFactory.class);
        client = new WebhookClientBuilder(1234, "token").setWait(true).setHttpClient(httpClient).build();
    }

    @After
    public void cleanup() throws Exception {
        client.close();
        mocks.close();
    }

    @Test
    public void testPassedEntity() throws InterruptedException, ExecutionException, TimeoutException {
        ReadonlyMessage mockMessage = setupFakeResponse(ReceiveMessageTest.getMockMessageJson().toString(), false);
        ReadonlyMessage readMessage = client.send("dummy").get(5, TimeUnit.SECONDS);

        assertNotNull("Returned message is null", readMessage);
        assertSame("Returned message not same as result of EntityFactory.makeMessage", mockMessage, readMessage);
    }

    @Test
    public void testNonGzip() throws InterruptedException, ExecutionException, TimeoutException {
        JSONObject json = ReceiveMessageTest.getMockMessageJson();
        setupFakeResponse(json.toString(), false);
        client.send("dummy").get(5, TimeUnit.SECONDS);

        PowerMockito.verifyStatic(EntityFactory.class, only());
        EntityFactory.makeMessage(any());
        JSONObject value = jsonCaptor.getValue();
        assertNotNull("Null json passed to EntityFactory", value);
        assertEquals("Json passed to EntityFactory is not 1:1 http response", json.toMap(), value.toMap());
    }

    @Test
    public void testGzip() throws InterruptedException, ExecutionException, TimeoutException {
        JSONObject json = ReceiveMessageTest.getMockMessageJson();
        setupFakeResponse(json.toString(), true);
        client.send("dummy").get(5, TimeUnit.SECONDS);

        PowerMockito.verifyStatic(EntityFactory.class, only());
        EntityFactory.makeMessage(any());
        JSONObject value = jsonCaptor.getValue();
        assertNotNull("Null json passed to EntityFactory", value);
        assertEquals("Json passed to EntityFactory is not 1:1 http response", json.toMap(), value.toMap());
    }

    private ReadonlyMessage setupFakeResponse(String json, boolean useGzip) {
        when(httpClient.newCall(any())).thenAnswer(invoc -> IOTestUtil.forgeCall(invoc.getArgument(0), json, useGzip));
        ReadonlyMessage msg = new ReadonlyMessage(1, 2, false, false, 0,
                new ReadonlyUser(3, (short)4, false, "wh", null),
                "content", Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()
        );
        when(EntityFactory.makeMessage(jsonCaptor.capture())).thenReturn(msg);
        return msg;
    }
}
