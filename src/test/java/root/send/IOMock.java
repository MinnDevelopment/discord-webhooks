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

package root.send;

import club.minnced.discord.webhook.LibraryInfo;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class IOMock {

    @Captor
    private ArgumentCaptor<Request> requestCaptor;

    @Mock
    private OkHttpClient httpClient;

    private WebhookClient client;

    private AutoCloseable mocks;

    @Before
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        when(httpClient.newCall(any())).thenReturn(null);   //will make WebhookClient code throw NPE internally, which we don't care about
        client = new WebhookClientBuilder(1234, "token").setWait(false).setHttpClient(httpClient).build();
    }

    @After
    public void cleanup() throws Exception {
        client.close();
        mocks.close();
    }

    @Test
    public void testUrl() {
        client.send("Hello World");

        verify(httpClient, timeout(1000).only()).newCall(requestCaptor.capture());
        Request req = requestCaptor.getValue();
        Assert.assertEquals("POST", req.method());
        String expectedUrl = String.format("https://discord.com/api/v%d/webhooks/%d/%s", LibraryInfo.DISCORD_API_VERSION, 1234, "token");
        Assert.assertEquals(expectedUrl, req.url().toString());
    }

    @Test
    public void messageBodyUsed() {
        //implicitly checks json sent due to json (requestbody) being checked in MessageTest testclass
        RequestBody body = new WebhookMessageBuilder()
                .setContent("CONTENT!")
                .setUsername("MrWebhook")
                .setAvatarUrl("linkToImage")
                .setTTS(true)
                .addEmbeds(new WebhookEmbedBuilder().setDescription("embed").build())
                .build().getBody();

        WebhookMessage mock = mock(WebhookMessage.class);
        when(mock.getBody()).thenReturn(body);

        client.send(mock);

        verify(httpClient, timeout(1000).only()).newCall(requestCaptor.capture());
        Request req = requestCaptor.getValue();
        Assert.assertSame(body, req.body());
    }
}
