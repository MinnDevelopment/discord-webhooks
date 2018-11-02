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

package club.minnced.discord.webhook.receive;

import club.minnced.discord.webhook.send.WebhookEmbed;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class EntityFactory { //TODO: Write Tests, Documentation
    public static ReadonlyUser makeUser(JSONObject json) {
        final long id = Long.parseUnsignedLong(json.getString("id"));
        final String name = json.getString("username");
        final String avatar = json.optString("avatar", null);
        final short discriminator = Short.parseShort(json.getString("discriminator"));
        final boolean bot = !json.isNull("bot") && json.getBoolean("bot");

        return new ReadonlyUser(id, discriminator, bot, name, avatar);
    }

    public static ReadonlyAttachment makeAttachment(JSONObject json) {
        final String url = json.getString("url");
        final String proxy = json.getString("proxy_url");
        final String name = json.getString("filename");
        final int size = json.getInt("size");
        final int width = json.optInt("width", -1);
        final int height = json.optInt("height", -1);
        final long id = Long.parseUnsignedLong(json.getString("id"));
        return new ReadonlyAttachment(url, proxy, name, width, height, size, id);
    }

    public static WebhookEmbed.EmbedField makeEmbedField(JSONObject json) {
        if (json == null)
            return null;
        final String name = json.getString("name");
        final String value = json.getString("value");
        final boolean inline = !json.isNull("inline") && json.getBoolean("inline");
        return new WebhookEmbed.EmbedField(inline, name, value);
    }

    public static WebhookEmbed.EmbedAuthor makeEmbedAuthor(JSONObject json) {
        if (json == null)
            return null;
        final String name = json.getString("name");
        final String url = json.optString("url", null);
        final String icon = json.optString("icon_url", null);
        return new WebhookEmbed.EmbedAuthor(name, icon, url);
    }

    public static WebhookEmbed.EmbedFooter makeEmbedFooter(JSONObject json) {
        if (json == null)
            return null;
        final String text = json.getString("text");
        final String icon = json.getString("icon_url");
        return new WebhookEmbed.EmbedFooter(text, icon);
    }

    public static WebhookEmbed.EmbedTitle makeEmbedTitle(JSONObject json) {
        final String text = json.optString("title", null);
        if (text == null)
            return null;
        final String url = json.optString("url", null);
        return new WebhookEmbed.EmbedTitle(text, url);
    }

    public static ReadonlyEmbed.EmbedImage makeEmbedImage(JSONObject json) {
        if (json == null)
            return null;
        final String url = json.getString("url");
        final String proxyUrl = json.getString("proxy_url");
        final int width = json.getInt("width");
        final int height = json.getInt("height");
        return new ReadonlyEmbed.EmbedImage(url, proxyUrl, width, height);
    }

    public static ReadonlyEmbed.EmbedProvider makeEmbedProvider(JSONObject json) {
        if (json == null)
            return null;
        final String url = json.getString("url");
        final String name = json.getString("name");
        return new ReadonlyEmbed.EmbedProvider(name, url);
    }

    public static ReadonlyEmbed makeEmbed(JSONObject json) { //TODO: ReadonlyEmbed
        final String description = json.optString("description", null);
        final Integer color = json.isNull("color") ? null : json.getInt("color");
        final ReadonlyEmbed.EmbedImage image = makeEmbedImage(json.optJSONObject("image"));
        final ReadonlyEmbed.EmbedImage thumbnail = makeEmbedImage(json.optJSONObject("thumbnail"));
        final ReadonlyEmbed.EmbedProvider provider = makeEmbedProvider(json.optJSONObject("provider"));
        final WebhookEmbed.EmbedFooter footer = makeEmbedFooter(json.optJSONObject("footer"));
        final WebhookEmbed.EmbedAuthor author = makeEmbedAuthor(json.optJSONObject("author"));
        final WebhookEmbed.EmbedTitle title = makeEmbedTitle(json);
        final Long timestamp;
        if (json.isNull("timestamp"))
            timestamp = null;
        else
            timestamp = OffsetDateTime.parse(json.getString("timestamp")).toInstant().toEpochMilli();
        final JSONArray fieldArray = json.optJSONArray("fields");
        final List<WebhookEmbed.EmbedField> fields = new ArrayList<>();
        if (fieldArray != null) {
            for (int i = 0; i < fieldArray.length(); i++) {
                JSONObject obj = fieldArray.getJSONObject(i);
                WebhookEmbed.EmbedField field = makeEmbedField(obj);
                if (field != null)
                    fields.add(field);
            }
        }
        return new ReadonlyEmbed(timestamp, color, description, thumbnail, image, footer, title, author, fields, provider);
    }

    public static ReadonlyMessage makeMessage(JSONObject json) {
        final long id = Long.parseUnsignedLong(json.getString("id"));
        final long channelId = Long.parseUnsignedLong(json.getString("channel_id"));
        final ReadonlyUser author = makeUser(json.getJSONObject("author"));
        final String content = json.getString("content");
        final String nonce = json.optString("nonce", null);
        final boolean tts = json.getBoolean("tts");
        final boolean mentionEveryone = json.getBoolean("mention_everyone");
        final JSONArray usersArray = json.getJSONArray("mentions");
        final JSONArray rolesArray = json.getJSONArray("mention_roles");
        final JSONArray embedArray = json.getJSONArray("embeds");
        final JSONArray attachmentArray = json.getJSONArray("attachments");
        final List<ReadonlyUser> mentionedUsers = convertToList(usersArray, EntityFactory::makeUser);
        final List<ReadonlyEmbed> embeds = convertToList(embedArray, EntityFactory::makeEmbed);
        final List<ReadonlyAttachment> attachments = convertToList(attachmentArray, EntityFactory::makeAttachment);
        final List<Long> mentionedRoles = new ArrayList<>();
        for (int i = 0; i < rolesArray.length(); i++) {
            mentionedRoles.add(Long.parseUnsignedLong(rolesArray.getString(i)));
        }
        return new ReadonlyMessage(
                id, channelId, mentionEveryone, tts,
                author, nonce, content,
                embeds, attachments,
                mentionedUsers, mentionedRoles);
    }

    private static <T> List<T> convertToList(JSONArray arr, Function<JSONObject, T> converter) {
        if (arr == null)
            return Collections.emptyList();
        final List<T> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject json = arr.getJSONObject(i);
            T out = converter.apply(json);
            if (out != null)
                list.add(out);
        }
        return Collections.unmodifiableList(list);
    }
}
