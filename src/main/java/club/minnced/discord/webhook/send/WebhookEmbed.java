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

public class WebhookEmbed implements JSONString { //TODO: Docs
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

    @Nullable
    @JSONPropertyIgnore
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Nullable
    @JSONPropertyIgnore
    public String getImageUrl() {
        return imageUrl;
    }

    @Nullable
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    @Nullable
    @JSONPropertyIgnore
    public EmbedTitle getTitle() {
        return title;
    }

    @Nullable
    public Integer getColor() {
        return color;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public EmbedFooter getFooter() {
        return footer;
    }

    @Nullable
    public EmbedAuthor getAuthor() {
        return author;
    }

    @NotNull
    public List<EmbedField> getFields() {
        return fields;
    }

    @NotNull
    public WebhookEmbed reduced() {
        return this;
    }

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

    public static class EmbedField implements JSONString {
        private final boolean inline;
        private final String name, value;

        public EmbedField(boolean inline, String name, String value) {
            this.inline = inline;
            this.name = name;
            this.value = value;
        }

        public boolean isInline() {
            return inline;
        }

        @NotNull
        public String getName() {
            return name;
        }

        @NotNull
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return toJSONString();
        }

        @Override
        public String toJSONString() {
            return new JSONObject(this).toString();
        }
    }

    public static class EmbedAuthor implements JSONString {
        private final String name, icon, url;

        public EmbedAuthor(@NotNull String name, @Nullable String icon, @Nullable String url) {
            this.name = name;
            this.icon = icon;
            this.url = url;
        }

        @NotNull
        public String getName() {
            return name;
        }

        @Nullable
        @JSONPropertyName("icon_url")
        public String getIcon() {
            return icon;
        }

        @Nullable
        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return toJSONString();
        }

        @Override
        public String toJSONString() {
            return new JSONObject(this).toString();
        }
    }

    public static class EmbedFooter implements JSONString {
        private final String text, icon;

        public EmbedFooter(@NotNull String text, @Nullable String icon) {
            this.text = text;
            this.icon = icon;
        }

        @NotNull
        public String getText() {
            return text;
        }

        @Nullable
        @JSONPropertyName("icon_url")
        public String getIconUrl() {
            return icon;
        }

        @Override
        public String toString() {
            return toJSONString();
        }

        @Override
        public String toJSONString() {
            return new JSONObject(this).toString();
        }
    }

    public static class EmbedTitle {
        private final String text, url;

        public EmbedTitle(@NotNull String text, @Nullable String url) {
            this.text = text;
            this.url = url;
        }

        @NotNull
        public String getText() {
            return text;
        }

        @Nullable
        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return new JSONObject(this).toString();
        }
    }
}
