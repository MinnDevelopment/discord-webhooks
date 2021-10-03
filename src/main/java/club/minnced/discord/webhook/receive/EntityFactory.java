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

package club.minnced.discord.webhook.receive;

import club.minnced.discord.webhook.send.WebhookEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Internal factory used to convert JSON representations
 * into java objects.
 */
public class EntityFactory {
    /**
     * Converts a user json into a {@link club.minnced.discord.webhook.receive.ReadonlyUser}
     *
     * @param json
     *         The JSON representation
     *
     * @return {@link club.minnced.discord.webhook.receive.ReadonlyUser}
     */
    @NotNull
    public static ReadonlyUser makeUser(@NotNull JSONObject json) {
        final long id = Long.parseUnsignedLong(json.getString("id"));
        final String name = json.getString("username");
        final String avatar = json.optString("avatar", null);
        final short discriminator = Short.parseShort(json.getString("discriminator"));
        final boolean bot = !json.isNull("bot") && json.getBoolean("bot");

        return new ReadonlyUser(id, discriminator, bot, name, avatar);
    }

    /**
     * Converts a attachment json into a {@link club.minnced.discord.webhook.receive.ReadonlyAttachment}
     *
     * @param json
     *         The JSON representation
     *
     * @return {@link club.minnced.discord.webhook.receive.ReadonlyAttachment}
     */
    @NotNull
    public static ReadonlyAttachment makeAttachment(@NotNull JSONObject json) {
        final String url = json.getString("url");
        final String proxy = json.getString("proxy_url");
        final String name = json.getString("filename");
        final int size = json.getInt("size");
        final int width = json.optInt("width", -1);
        final int height = json.optInt("height", -1);
        final long id = Long.parseUnsignedLong(json.getString("id"));
        return new ReadonlyAttachment(url, proxy, name, width, height, size, id);
    }

    /**
     * Converts a field json into a {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedField}
     *
     * @param json
     *         The JSON representation
     *
     * @return {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedField}
     */
    @Nullable
    public static WebhookEmbed.EmbedField makeEmbedField(@Nullable JSONObject json) {
        if (json == null)
            return null;
        final String name = json.getString("name");
        final String value = json.getString("value");
        final boolean inline = !json.isNull("inline") && json.getBoolean("inline");
        return new WebhookEmbed.EmbedField(inline, name, value);
    }

    /**
     * Converts an author json into a {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedAuthor}
     *
     * @param json
     *         The JSON representation
     *
     * @return {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedAuthor}
     */
    @Nullable
    public static WebhookEmbed.EmbedAuthor makeEmbedAuthor(@Nullable JSONObject json) {
        if (json == null)
            return null;
        final String name = json.getString("name");
        final String url = json.optString("url", null);
        final String icon = json.optString("icon_url", null);
        return new WebhookEmbed.EmbedAuthor(name, icon, url);
    }

    /**
     * Converts a footer json into a {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedFooter}
     *
     * @param json
     *         The JSON representation
     *
     * @return {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedFooter}
     */
    @Nullable
    public static WebhookEmbed.EmbedFooter makeEmbedFooter(@Nullable JSONObject json) {
        if (json == null)
            return null;
        final String text = json.getString("text");
        final String icon = json.optString("icon_url", null);
        return new WebhookEmbed.EmbedFooter(text, icon);
    }

    /**
     * Converts an embed json into a {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle}
     *
     * @param  json
     *         The JSON representation
     *
     * @return {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle}
     */
    @Nullable
    public static WebhookEmbed.EmbedTitle makeEmbedTitle(@NotNull JSONObject json) {
        final String text = json.optString("title", null);
        if (text == null)
            return null;
        final String url = json.optString("url", null);
        return new WebhookEmbed.EmbedTitle(text, url);
    }

    /**
     * Converts a image/thumbnail json into a {@link club.minnced.discord.webhook.receive.ReadonlyEmbed.EmbedImage}
     *
     * @param  json
     *         The JSON representation
     *
     * @return {@link club.minnced.discord.webhook.receive.ReadonlyEmbed.EmbedImage}
     */
    @Nullable
    public static ReadonlyEmbed.EmbedImage makeEmbedImage(@Nullable JSONObject json) {
        if (json == null)
            return null;
        final String url = json.getString("url");
        final String proxyUrl = json.getString("proxy_url");
        final int width = json.getInt("width");
        final int height = json.getInt("height");
        return new ReadonlyEmbed.EmbedImage(url, proxyUrl, width, height);
    }

    /**
     * Converts a provider json into a {@link club.minnced.discord.webhook.receive.ReadonlyEmbed.EmbedProvider}
     *
     * @param  json
     *         The JSON representation
     *
     * @return {@link club.minnced.discord.webhook.receive.ReadonlyEmbed.EmbedProvider}
     */
    @Nullable
    public static ReadonlyEmbed.EmbedProvider makeEmbedProvider(@Nullable JSONObject json) {
        if (json == null)
            return null;
        final String url = json.optString("url", null);
        final String name = json.optString("name", null);
        return new ReadonlyEmbed.EmbedProvider(name, url);
    }

    /**
     * Converts a video json into a {@link club.minnced.discord.webhook.receive.ReadonlyEmbed.EmbedVideo}
     *
     * @param  json
     *         The JSON representation
     *
     * @return {@link club.minnced.discord.webhook.receive.ReadonlyEmbed.EmbedVideo}
     */
    @Nullable
    public static ReadonlyEmbed.EmbedVideo makeEmbedVideo(@Nullable JSONObject json) {
        if (json == null)
            return null;
        final String url = json.getString("url");
        final int height = json.getInt("height");
        final int width = json.getInt("width");
        return new ReadonlyEmbed.EmbedVideo(url, width, height);
    }

    /**
     * Converts an embed json into a {@link club.minnced.discord.webhook.receive.ReadonlyEmbed}
     *
     * @param  json
     *         The JSON representation
     *
     * @return {@link club.minnced.discord.webhook.receive.ReadonlyEmbed}
     */
    @NotNull
    public static ReadonlyEmbed makeEmbed(@NotNull JSONObject json) {
        final String description = json.optString("description", null);
        final Integer color = json.isNull("color") ? null : json.getInt("color");
        final ReadonlyEmbed.EmbedImage image = makeEmbedImage(json.optJSONObject("image"));
        final ReadonlyEmbed.EmbedImage thumbnail = makeEmbedImage(json.optJSONObject("thumbnail"));
        final ReadonlyEmbed.EmbedProvider provider = makeEmbedProvider(json.optJSONObject("provider"));
        final ReadonlyEmbed.EmbedVideo video = makeEmbedVideo(json.optJSONObject("video"));
        final WebhookEmbed.EmbedFooter footer = makeEmbedFooter(json.optJSONObject("footer"));
        final WebhookEmbed.EmbedAuthor author = makeEmbedAuthor(json.optJSONObject("author"));
        final WebhookEmbed.EmbedTitle title = makeEmbedTitle(json);
        final OffsetDateTime timestamp;
        if (json.isNull("timestamp"))
            timestamp = null;
        else
            timestamp = OffsetDateTime.parse(json.getString("timestamp"));
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
        return new ReadonlyEmbed(timestamp, color, description, thumbnail, image, footer, title, author, fields, provider, video);
    }

    /**
     * Converts a message json into a {@link club.minnced.discord.webhook.receive.ReadonlyMessage}
     *
     * @param  json
     *         The JSON representation
     *
     * @return {@link club.minnced.discord.webhook.receive.ReadonlyMessage}
     */
    @NotNull
    public static ReadonlyMessage makeMessage(@NotNull JSONObject json) {
        final long id = Long.parseUnsignedLong(json.getString("id"));
        final long channelId = Long.parseUnsignedLong(json.getString("channel_id"));
        final ReadonlyUser author = makeUser(json.getJSONObject("author"));
        final String content = json.getString("content");
        final boolean tts = json.getBoolean("tts");
        final boolean mentionEveryone = json.getBoolean("mention_everyone");
        final int flags = json.optInt("flags", 0);
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
                flags, author, content,
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
