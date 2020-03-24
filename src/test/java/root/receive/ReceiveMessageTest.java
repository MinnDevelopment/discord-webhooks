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

import club.minnced.discord.webhook.receive.EntityFactory;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.receive.ReadonlyUser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ReceiveMessageTest {
    public static final String WEBHOOK_ID = "350595946677594378";

    public static final JSONObject MOCK_MESSAGE_USER_JSON =
            new JSONObject()
                    .put("id", WEBHOOK_ID)
                    .put("username", "Captain Hook")
                    .put("discriminator", "0000")
                    .put("bot", true)
                    .put("avatar", JSONObject.NULL);

    public static JSONObject getMockMessageJson() {
        return new JSONObject()
                .put("id", "2")
                .put("content", "Dummy content")
                .put("channel_id", "1234")
                .put("author", MOCK_MESSAGE_USER_JSON)
                .put("tts", true)
                .put("attachments", new JSONArray())
                .put("embeds", new JSONArray())
                .put("mention_everyone", true)
                .put("mentions", new JSONArray())
                .put("mention_roles", new JSONArray());
                /*not parsed:
                .put("pinned", true)
                .put("webhook_id", WEBHOOK_ID)
                .put("timestamp", OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .put("edited_timestamp", OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .put("nonce", JSONObject.NULL)
                .put("type", 0);
                */
    }

    @Test
    public void parseMessage() {
        ReadonlyMessage message = EntityFactory.makeMessage(getMockMessageJson());
        assertEquals("Message id mismatches", 2L, message.getId());
        assertEquals("Message content mismatches", "Dummy content", message.getContent());
        assertEquals("Channel id mismatches", 1234L, message.getChannelId());
        assertTrue("TTS mismatches", message.isTTS());
        assertTrue("Attachments are not empty", message.getAttachments().isEmpty());
        assertTrue("Embeds are not empty", message.getEmbeds().isEmpty());
        assertTrue("Does not mention everyone", message.isMentionsEveryone());
        assertTrue("User mentions not empty", message.getMentionedUsers().isEmpty());
        assertTrue("Role mentions not empty", message.getMentionedRoles().isEmpty());
    }

    @Test
    public void parseEmbed() {
        JSONObject json = getMockMessageJson();
        json.getJSONArray("embeds").put(ReceiveEmbedTest.MOCK_EMBED_JSON);
        ReadonlyMessage message = EntityFactory.makeMessage(json);
        assertEquals("Embeds are empty", 1, message.getEmbeds().size());
        assertEquals("Embed json incorrect/incomplete",
                ReceiveEmbedTest.MOCK_EMBED_JSON.toMap(),
                new JSONObject(message.getEmbeds().get(0).toJSONString()).toMap()
        );
    }

    @Test
    public void parseMentions() {
        JSONObject json = getMockMessageJson();
        json.getJSONArray("mentions").put(
                new JSONObject()
                        .put("username", "Lucky Winner")
                        .put("discriminator", "1234")
                        .put("id", "2222")
                        .put("avatar", "abc")
        );
        json.getJSONArray("mention_roles").put("654").put("321");
        ReadonlyMessage message = EntityFactory.makeMessage(json);
        assertEquals("User mentions are empty", 1, message.getMentionedUsers().size());
        assertEquals("Role mentions are empty", 2, message.getMentionedRoles().size());

        assertEquals("Role ids incorrect", Arrays.asList(654L, 321L), message.getMentionedRoles());

        ReadonlyUser user = message.getMentionedUsers().get(0);
        assertEquals("Username mismatches", "Lucky Winner", user.getName());
        assertEquals("Discriminator mismatches", "1234", user.getDiscriminator());
        assertEquals("User Id mismatches", 2222L, user.getId());
        assertEquals("Avatar mismatches", "abc", user.getAvatarId());
    }
}
