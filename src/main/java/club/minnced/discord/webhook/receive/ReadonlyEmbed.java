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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONPropertyName;
import org.json.JSONString;

import java.time.OffsetDateTime;
import java.util.List;

public class ReadonlyEmbed extends WebhookEmbed {
    private final EmbedProvider provider;
    private final EmbedImage thumbnail, image;
    private final EmbedVideo video;

    public ReadonlyEmbed(
            @Nullable OffsetDateTime timestamp, @Nullable Integer color, @Nullable String description,
            @Nullable EmbedImage thumbnail, @Nullable EmbedImage image, @Nullable EmbedFooter footer,
            @Nullable EmbedTitle title, @Nullable EmbedAuthor author, @NotNull List<EmbedField> fields,
            @Nullable EmbedProvider provider, @Nullable EmbedVideo video) {
        super(timestamp, color, description,
              thumbnail == null ? null : thumbnail.getUrl(),
              image == null ? null : image.getUrl(),
              footer, title, author, fields);
        this.thumbnail = thumbnail;
        this.image = image;
        this.provider = provider;
        this.video = video;
    }

    @Nullable
    public EmbedProvider getProvider() {
        return provider;
    }

    @Nullable
    public EmbedImage getThumbnail() {
        return thumbnail;
    }

    @Nullable
    public EmbedImage getImage() {
        return image;
    }

    @Nullable
    public EmbedVideo getVideo() {
        return video;
    }

    @Override
    @NotNull
    public WebhookEmbed reduced() {
        return new WebhookEmbed(
                getTimestamp(), getColor(), getDescription(),
                thumbnail == null ? null : thumbnail.getUrl(),
                image == null ? null : image.getUrl(),
                getFooter(), getTitle(), getAuthor(), getFields());
    }

    @Override
    public String toJSONString() {
        JSONObject base = new JSONObject(super.toJSONString());
        base.put("provider", provider)
            .put("thumbnail", thumbnail)
            .put("video", video)
            .put("image", image);
        if (getTitle() != null) {
            base.put("title", getTitle().getText());
            base.put("url", getTitle().getUrl());
        }
        return base.toString();
    }

    @Override
    public String toString() {
        return toJSONString();
    }

    public static class EmbedProvider implements JSONString {
        private final String name, url;

        public EmbedProvider(@NotNull String name, @NotNull String url) {
            this.name = name;
            this.url = url;
        }

        @NotNull
        public String getName() {
            return name;
        }

        @NotNull
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

    public static class EmbedVideo implements JSONString {
        private final String url;
        private final int width, height;

        public EmbedVideo(@NotNull String url, int width, int height) {
            this.url = url;
            this.width = width;
            this.height = height;
        }

        @NotNull
        public String getUrl() {
            return url;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
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

    public static class EmbedImage implements JSONString {
        private final String url, proxyUrl;
        private final int width, height;

        public EmbedImage(
                @NotNull String url, @NotNull String proxyUrl,
                int width, int height) {
            this.url = url;
            this.proxyUrl = proxyUrl;
            this.width = width;
            this.height = height;
        }

        @NotNull
        public String getUrl() {
            return url;
        }

        @NotNull
        @JSONPropertyName("proxy_url")
        public String getProxyUrl() {
            return proxyUrl;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
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
}
