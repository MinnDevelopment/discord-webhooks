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

import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

public class WebhookEmbedBuilder { //TODO: Docs
    private final List<WebhookEmbed.EmbedField> fields;

    private OffsetDateTime timestamp;
    private Integer color;

    private String description;
    private String thumbnailUrl;
    private String imageUrl;

    private WebhookEmbed.EmbedFooter footer;
    private WebhookEmbed.EmbedTitle title;
    private WebhookEmbed.EmbedAuthor author;

    public WebhookEmbedBuilder() {
        fields = new ArrayList<>(10);
    }

    public WebhookEmbedBuilder(@Nullable WebhookEmbed embed) {
        this();
        if (embed != null) {
            timestamp = embed.getTimestamp();
            color = embed.getColor();
            description = embed.getDescription();
            thumbnailUrl = embed.getThumbnailUrl();
            imageUrl = embed.getImageUrl();
            footer = embed.getFooter();
            title = embed.getTitle();
            author = embed.getAuthor();
            fields.addAll(embed.getFields());
        }
    }

    @NotNull
    public WebhookEmbedBuilder setTimestamp(@Nullable TemporalAccessor timestamp) {
        this.timestamp = timestamp == null ? null : OffsetDateTime.from(timestamp);
        return this;
    }

    @NotNull
    public WebhookEmbedBuilder setColor(@Nullable Integer color) {
        this.color = color;
        return this;
    }

    @NotNull
    public WebhookEmbedBuilder setDescription(@Nullable String description) {
        this.description = description;
        return this;
    }

    @NotNull
    public WebhookEmbedBuilder setThumbnailUrl(@Nullable String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
        return this;
    }

    @NotNull
    public WebhookEmbedBuilder setImageUrl(@Nullable String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    @NotNull
    public WebhookEmbedBuilder setFooter(@Nullable WebhookEmbed.EmbedFooter footer) {
        this.footer = footer;
        return this;
    }

    @NotNull
    public WebhookEmbedBuilder setTitle(@Nullable WebhookEmbed.EmbedTitle title) {
        this.title = title;
        return this;
    }

    @NotNull
    public WebhookEmbedBuilder setAuthor(@Nullable WebhookEmbed.EmbedAuthor author) {
        this.author = author;
        return this;
    }

    public boolean isEmpty() {
        return isEmpty(description)
               && isEmpty(imageUrl)
               && isFieldsEmpty()
               && isAuthorEmpty()
               && isTitleEmpty()
               && isFooterEmpty();
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean isTitleEmpty() {
        return title == null || isEmpty(title.getText());
    }

    private boolean isFooterEmpty() {
        return footer == null || isEmpty(footer.getText());
    }

    private boolean isAuthorEmpty() {
        return author == null || isEmpty(author.getName());
    }

    private boolean isFieldsEmpty() {
        if (fields.isEmpty())
            return true;
        return fields.stream().allMatch(f -> isEmpty(f.getName()) && isEmpty(f.getValue()));
    }

    @NotNull
    public WebhookEmbed build() {
        if (isEmpty())
            throw new IllegalStateException("Cannot build an empty embed");
        return new WebhookEmbed(
                timestamp, color,
                description, thumbnailUrl, imageUrl,
                footer, title, author,
                new ArrayList<>(fields)
        );
    }
}
