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

package club.minnced.discord.webhook.send;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONPropertyIgnore;
import org.json.JSONPropertyName;
import org.json.JSONString;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Reduced version of an {@link club.minnced.discord.webhook.receive.ReadonlyEmbed}
 * used for sending. A webhook can send up to {@value WebhookMessage#MAX_EMBEDS} embeds
 * in a single message.
 *
 * @see club.minnced.discord.webhook.send.WebhookEmbedBuilder
 */
public class WebhookEmbed implements JSONString {
    /**
     * Max amount of fields an embed can hold (25)
     */
    public static final int MAX_FIELDS = 25;

    private final OffsetDateTime timestamp;
    private final Integer color;

    private final String description;
    private final String thumbnailUrl;
    private final String imageUrl;

    private final EmbedFooter footer;
    private final EmbedTitle title;
    private final EmbedAuthor author;
    private final List<EmbedField> fields;

    public WebhookEmbed(
            @Nullable OffsetDateTime timestamp, @Nullable Integer color,
            @Nullable String description, @Nullable String thumbnailUrl, @Nullable String imageUrl,
            @Nullable EmbedFooter footer, @Nullable EmbedTitle title, @Nullable EmbedAuthor author,
            @NotNull List<EmbedField> fields) {
        this.timestamp = timestamp;
        this.color = color;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrl = imageUrl;
        this.footer = footer;
        this.title = title;
        this.author = author;
        this.fields = Collections.unmodifiableList(fields);
    }

    /**
     * The thumbnail url
     *
     * @return Possibly-null url
     */
    @Nullable
    @JSONPropertyIgnore
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     * The image url
     *
     * @return Possibly-null url
     */
    @Nullable
    @JSONPropertyIgnore
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * The timestamp for the embed.
     * <br>The discord client displays this
     * in the correct timezone and locale of the viewing users.
     *
     * @return Possibly-null {@link java.time.OffsetDateTime} of the timestamp
     */
    @Nullable
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * The title of the embed, this is displayed
     * above the description and below the author.
     *
     * @return Possibly-null {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle}
     */
    @Nullable
    @JSONPropertyIgnore
    public EmbedTitle getTitle() {
        return title;
    }

    /**
     * The rgb color of this embed.
     * <br>This is the colored line on the left-hand side of the embed.
     *
     * @return Possibly-null boxed integer of the color
     */
    @Nullable
    public Integer getColor() {
        return color;
    }

    /**
     * The description of this embed
     *
     * @return Possibly-null description
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * The footer of the embed.
     * <br>This is displayed at the very bottom of the embed, left to the timestamp.
     *
     * @return Possibly-null {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedFooter}
     */
    @Nullable
    public EmbedFooter getFooter() {
        return footer;
    }

    /**
     * The embed author.
     * <br>This is displayed at the very top of the embed,
     * even above the {@link #getTitle()}.
     *
     * @return Possibly-null {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedAuthor}
     */
    @Nullable
    public EmbedAuthor getAuthor() {
        return author;
    }

    /**
     * List of fields for this embed.
     * <br>And embed can have up to {@value MAX_FIELDS}.
     *
     * @return List of fields for this embed
     */
    @NotNull
    public List<EmbedField> getFields() {
        return fields;
    }

    /**
     * Returns this embed instance, as its already reduced.
     *
     * @return The current instance
     */
    @NotNull
    public WebhookEmbed reduced() {
        return this;
    }

    /**
     * JSON representation of this embed
     *
     * @return The JSON representation
     */
    @Override
    public String toString() {
        return toJSONString();
    }

    @Override
    public String toJSONString() {
        JSONObject json = new JSONObject();
        if (description != null)
            json.put("description", description);
        if (timestamp != null)
            json.put("timestamp", timestamp);
        if (color != null)
            json.put("color", color & 0xFFFFFF);
        if (author != null)
            json.put("author", author);
        if (footer != null)
            json.put("footer", footer);
        if (thumbnailUrl != null)
            json.put("thumbnail",
                     new JSONObject()
                             .put("url", thumbnailUrl));
        if (imageUrl != null)
            json.put("image",
                     new JSONObject()
                             .put("url", imageUrl));
        if (!fields.isEmpty())
            json.put("fields", fields);
        if (title != null) {
            if (title.getUrl() != null)
                json.put("url", title.url);
            json.put("title", title.text);
        }
        return json.toString();
    }

    /**
     * POJO for an embed field.
     * <br>An embed can have up to {@value MAX_FIELDS} fields.
     * A row of fields can be up 3 wide, or 2 when a thumbnail is configured.
     * To be displayed in the same row as other fields, the field has to be set to {@link #isInline() inline}.
     */
    public static class EmbedField implements JSONString {
        private final boolean inline;
        private final String name, value;

        /**
         * Creates a new embed field
         *
         * @param inline
         *         Whether or not this should share a row with other fields
         * @param name
         *         The name of the field
         * @param value
         *         The value of the field
         *
         * @see club.minnced.discord.webhook.send.WebhookEmbedBuilder#addField(club.minnced.discord.webhook.send.WebhookEmbed.EmbedField)
         */
        public EmbedField(boolean inline, @NotNull String name, @NotNull String value) {
            this.inline = inline;
            this.name = Objects.requireNonNull(name);
            this.value = Objects.requireNonNull(value);
        }

        /**
         * Whether this field should share a row with other fields
         *
         * @return True, if this should be in the same row as other fields
         */
        public boolean isInline() {
            return inline;
        }

        /**
         * The name of this field.
         * <br>This is displayed above the value in a bold font.
         *
         * @return The name
         */
        @NotNull
        public String getName() {
            return name;
        }

        /**
         * The value of this field.
         * <br>This is displayed below the name in a regular font.
         *
         * @return The value
         */
        @NotNull
        public String getValue() {
            return value;
        }

        /**
         * JSON representation of this field
         *
         * @return The JSON representation
         */
        @Override
        public String toString() {
            return toJSONString();
        }

        @Override
        public String toJSONString() {
            return new JSONObject(this).toString();
        }
    }

    /**
     * POJO for an embed author.
     * <br>This can contain an icon (avatar), a name, and a url.
     * Often useful for posts from other platforms such as twitter/github.
     */
    public static class EmbedAuthor implements JSONString {
        private final String name, iconUrl, url;

        /**
         * Creates a new embed author
         *
         * @param name
         *         The name of the author
         * @param iconUrl
         *         The (nullable) icon url of the author
         * @param url
         *         The (nullable) hyperlink of the author
         *
         * @see club.minnced.discord.webhook.send.WebhookEmbedBuilder#setAuthor(club.minnced.discord.webhook.send.WebhookEmbed.EmbedAuthor)
         */
        public EmbedAuthor(@NotNull String name, @Nullable String iconUrl, @Nullable String url) {
            this.name = Objects.requireNonNull(name);
            this.iconUrl = iconUrl;
            this.url = url;
        }

        /**
         * The name of the author, this is the only visible text of this component.
         *
         * @return The name
         */
        @NotNull
        public String getName() {
            return name;
        }

        /**
         * The iconUrl of this author.
         * <br>This is displayed left to the name, similar to messages in discord.
         *
         * @return Possibly-null iconUrl url
         */
        @Nullable
        @JSONPropertyName("icon_url")
        public String getIconUrl() {
            return iconUrl;
        }

        /**
         * The url of this author.
         * <br>This can be used to highlight the name as a hyperlink
         * to the platform's profile service.
         *
         * @return Possibly-null url
         */
        @Nullable
        public String getUrl() {
            return url;
        }

        /**
         * JSON representation of this author
         *
         * @return The JSON representation
         */
        @Override
        public String toString() {
            return toJSONString();
        }

        @Override
        public String toJSONString() {
            return new JSONObject(this).toString();
        }
    }

    /**
     * POJO for an embed footer.
     * <br>Useful to display meta-data about context such as
     * for a github comment a repository name/icon.
     */
    public static class EmbedFooter implements JSONString {
        private final String text, icon;

        /**
         * Creates a new embed footer
         *
         * @param text
         *        The visible text of the footer
         * @param icon
         *        The (nullable) icon url of the footer
         *
         * @see   club.minnced.discord.webhook.send.WebhookEmbedBuilder#setFooter(club.minnced.discord.webhook.send.WebhookEmbed.EmbedFooter)
         */
        public EmbedFooter(@NotNull String text, @Nullable String icon) {
            this.text = Objects.requireNonNull(text);
            this.icon = icon;
        }

        /**
         * The visible text of the footer.
         *
         * @return The text
         */
        @NotNull
        public String getText() {
            return text;
        }

        /**
         * The url for the icon of this footer
         *
         * @return Possibly-null icon url
         */
        @Nullable
        @JSONPropertyName("icon_url")
        public String getIconUrl() {
            return icon;
        }

        /**
         * JSON representation of this footer
         *
         * @return The JSON representation
         */
        @Override
        public String toString() {
            return toJSONString();
        }

        @Override
        public String toJSONString() {
            return new JSONObject(this).toString();
        }
    }

    /**
     * POJO for an embed title.
     * <br>This is displayed above description and below the embed author.
     */
    public static class EmbedTitle {
        private final String text, url;

        /**
         * Creates a new embed title
         *
         * @param text
         *        The visible text
         * @param url
         *        The (nullable) hyperlink
         *
         * @see   club.minnced.discord.webhook.send.WebhookEmbedBuilder#setTitle(club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle)
         */
        public EmbedTitle(@NotNull String text, @Nullable String url) {
            this.text = Objects.requireNonNull(text);
            this.url = url;
        }

        /**
         * The visible text of this title
         *
         * @return The visible text
         */
        @NotNull
        public String getText() {
            return text;
        }

        /**
         * The hyperlink for this title.
         *
         * @return Possibly-null url
         */
        @Nullable
        public String getUrl() {
            return url;
        }

        /**
         * JSON representation of this title
         *
         * @return The JSON representation
         */
        @Override
        public String toString() {
            return new JSONObject(this).toString();
        }
    }
}
