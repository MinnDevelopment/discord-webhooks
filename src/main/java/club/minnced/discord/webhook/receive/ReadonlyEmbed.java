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
import org.json.JSONObject;
import org.json.JSONPropertyName;
import org.json.JSONString;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Extension of {@link club.minnced.discord.webhook.send.WebhookEmbed}
 * with additional meta-data on receivable embeds.
 */
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

    /**
     * The {@link club.minnced.discord.webhook.receive.ReadonlyEmbed.EmbedProvider}
     * <br>Used for services that are automatically embedded by discord when posting a link,
     * this includes services like youtube or twitter.
     *
     * @return Possibly-null embed provider
     */
    @Nullable
    public EmbedProvider getProvider() {
        return provider;
    }

    /**
     * The thumbnail of this embed.
     *
     * @return Possibly-null {@link club.minnced.discord.webhook.receive.ReadonlyEmbed.EmbedImage} for the thumbnail
     */
    @Nullable
    public EmbedImage getThumbnail() {
        return thumbnail;
    }

    /**
     * The image of this embed.
     *
     * @return Possibly-null {@link club.minnced.discord.webhook.receive.ReadonlyEmbed.EmbedImage} for the image
     */
    @Nullable
    public EmbedImage getImage() {
        return image;
    }

    /**
     * The video of this embed.
     * <br>This is a whitelisted feature only available for services like youtube
     * and is only populated for link embeds.
     *
     * @return Possibly-null {@link club.minnced.discord.webhook.receive.ReadonlyEmbed.EmbedVideo}
     */
    @Nullable
    public EmbedVideo getVideo() {
        return video;
    }

    /**
     * Reduces this embed to a simpler {@link club.minnced.discord.webhook.send.WebhookEmbed}
     * instance that can be used for sending, this is done implicitly
     * when trying to send an instance of a readonly-embed.
     *
     * @return The reduced embed instance
     */
    @Override
    @NotNull
    public WebhookEmbed reduced() {
        return new WebhookEmbed(
                getTimestamp(), getColor(), getDescription(),
                thumbnail == null ? null : thumbnail.getUrl(),
                image == null ? null : image.getUrl(),
                getFooter(), getTitle(), getAuthor(), getFields());
    }

    /**
     * JSON representation of this embed.
     * <br>Note that received embeds look different compared to sent ones.
     *
     * @return The JSON representation
     */
    @Override
    public String toString() {
        return toJSONString();
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

    /**
     * POJO containing meta-data for an embed provider
     *
     * @see #getProvider()
     */
    public static class EmbedProvider implements JSONString {
        private final String name, url;

        public EmbedProvider(@Nullable String name, @Nullable String url) {
            this.name = name;
            this.url = url;
        }

        /**
         * The name of the provider, or {@code null} if none is set
         *
         * @return The name
         */
        @Nullable
        public String getName() {
            return name;
        }

        /**
         * The url of the provider, or {@code null} if none is set
         *
         * @return The url
         */
        @Nullable
        public String getUrl() {
            return url;
        }

        /**
         * JSON representation of this provider
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
     * POJO containing meta-data about an embed video
     *
     * @see #getVideo()
     */
    public static class EmbedVideo implements JSONString {
        private final String url;
        private final int width, height;

        public EmbedVideo(@NotNull String url, int width, int height) {
            this.url = url;
            this.width = width;
            this.height = height;
        }

        /**
         * The URL fot this video
         *
         * @return The URL
         */
        @NotNull
        public String getUrl() {
            return url;
        }

        /**
         * The width of this video
         *
         * @return The width
         */
        public int getWidth() {
            return width;
        }

        /**
         * The height of this video
         *
         * @return The height
         */
        public int getHeight() {
            return height;
        }

        /**
         * JSON representation of this video
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
     * POJO containing meta-data about an embed image component
     *
     * @see #getThumbnail()
     * @see #getImage()
     */
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

        /**
         * The URL fot this image
         *
         * @return The URL
         */
        @NotNull
        public String getUrl() {
            return url;
        }

        /**
         * The proxy url for this image, this is used
         * to render previews in the discord client.
         *
         * @return The proxy url
         */
        @NotNull
        @JSONPropertyName("proxy_url")
        public String getProxyUrl() {
            return proxyUrl;
        }

        /**
         * The width of this image
         *
         * @return The width
         */
        public int getWidth() {
            return width;
        }

        /**
         * The height of this image
         *
         * @return The height
         */
        public int getHeight() {
            return height;
        }

        /**
         * JSON representation of this provider
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
}
