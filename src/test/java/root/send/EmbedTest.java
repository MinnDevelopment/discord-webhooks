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

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.OffsetDateTime;

public class EmbedTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private WebhookEmbedBuilder builder;

    @Before
    public void setup() {
        builder = new WebhookEmbedBuilder();
    }

    @Test
    public void resetBuilder() {
        builder.setAuthor(new WebhookEmbed.EmbedAuthor("minn", null, null))
               .setDescription("Hello World!")
               .addField(new WebhookEmbed.EmbedField(false, "name", "value"));
        builder.reset();
        Assert.assertTrue(builder.isEmpty());
    }

    @Test
    public void buildEmbed() {
        builder.setAuthor(new WebhookEmbed.EmbedAuthor("Minn", null, null));
        builder.setColor(0xff00ff);
        builder.setDescription("Hello World!");
        builder.setFooter(new WebhookEmbed.EmbedFooter("Footer text", null));
        builder.setImageUrl(null);
        builder.setThumbnailUrl(null);
        builder.setTitle(new WebhookEmbed.EmbedTitle("Title", null));
        builder.setTimestamp(OffsetDateTime.now());
        builder.build();
    }

    @Test
    public void buildEmptyEmbed() {
        expectedException.expect(IllegalStateException.class);
        builder.build();
    }
}

