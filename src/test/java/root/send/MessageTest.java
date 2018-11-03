/*
 * Copyright 2018-2019 Florian Spieß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package root.send;

import club.minnced.discord.webhook.IOUtil;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private WebhookMessageBuilder builder;

    @Before
    public void setupBuilder() {
        builder = new WebhookMessageBuilder();
    }

    @Test
    public void resetBuilder() {
        builder.setContent("test")
               .setUsername("hello")
               .setTTS(true);
        builder.reset();
        Assert.assertTrue(builder.isEmpty());
    }

    @Test
    public void buildMessage() {
        builder.setContent("Hello World");
        builder.setUsername("Minn");
        builder.build().getBody();
    }

    @Test
    public void buildMessageWithEmbed() {
        List<WebhookEmbed> embedList = Arrays.asList(
                new WebhookEmbedBuilder()
                        .setDescription("Hello World")
                        .build(),
                new WebhookEmbedBuilder()
                        .setDescription("World")
                        .build()
        );
        builder.addEmbeds(embedList.get(0));
        builder.addEmbeds(embedList.subList(1, 2));
        WebhookMessage message = builder.build();
        for (int i = 0; i < 2; i++) {
            Assert.assertEquals(embedList.get(i), message.getEmbeds().get(i));
        }
    }

    @Test
    public void buildMessageWithFiles() throws IOException {
        File tmp = File.createTempFile("message-test", "cat.png");
        builder.addFile(tmp);
        builder.addFile("dog.png", new FileInputStream(tmp));
        builder.addFile("bird.png", IOUtil.readAllBytes(new FileInputStream(tmp)));
        tmp.delete();
        WebhookMessage message = builder.build();
        Assert.assertEquals(tmp.getName(), message.getAttachments()[0].getName());
        Assert.assertEquals("dog.png", message.getAttachments()[1].getName());
        Assert.assertEquals("bird.png", message.getAttachments()[2].getName());
    }

    @Test
    public void factoryEmbeds() {
        WebhookEmbed embed1 = new WebhookEmbedBuilder()
                .setDescription("Hello").build();
        WebhookEmbed embed2 = new WebhookEmbedBuilder()
                .setDescription("World").build();
        WebhookMessage.embeds(embed1, embed2).getBody();
        WebhookMessage.embeds(Arrays.asList(embed1, embed2)).getBody();
    }

    @Test
    public void factoryFiles() throws IOException {
        File tmp = File.createTempFile("message-test", "cat.png");
        WebhookMessage.files(
                "cat.png", tmp,
                "dog.png", new FileInputStream(tmp),
                "bird.png", IOUtil.readAllBytes(new FileInputStream(tmp))).getBody();
        Map<String, Object> files = new HashMap<>();
        files.put("cat.png", tmp);
        files.put("dog.png", new FileInputStream(tmp));
        files.put("bird.png", IOUtil.readAllBytes(new FileInputStream(tmp)));
        WebhookMessage.files(files).getBody();
        tmp.delete();
    }

    @Test
    public void buildEmptyMessage() {
        expectedException.expect(IllegalStateException.class);
        builder.build();
    }
}
