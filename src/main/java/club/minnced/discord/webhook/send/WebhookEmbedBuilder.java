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

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.*;
import discord4j.discordjson.possible.Possible;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.javacord.api.entity.message.embed.EmbedImage;
import org.javacord.api.entity.message.embed.EmbedThumbnail;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder for a {@link club.minnced.discord.webhook.send.WebhookEmbed} instance.
 */
public class WebhookEmbedBuilder {
    private final List<WebhookEmbed.EmbedField> fields;

    private OffsetDateTime timestamp;
    private Integer color;

    private String description;
    private String thumbnailUrl;
    private String imageUrl;

    private WebhookEmbed.EmbedFooter footer;
    private WebhookEmbed.EmbedTitle title;
    private WebhookEmbed.EmbedAuthor author;

    /**
     * Creates an empty builder
     */
    public WebhookEmbedBuilder() {
        fields = new ArrayList<>(10);
    }

    /**
     * Creates a builder with predefined settings from
     * the provided {@link club.minnced.discord.webhook.send.WebhookEmbed} instance
     *
     * @param embed
     *         The (nullable) embed to copy
     */
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

    /**
     * Resets the builder to its default state
     */
    public void reset() {
        fields.clear();
        timestamp = null;
        color = null;
        description = null;
        thumbnailUrl = null;
        imageUrl = null;
        footer = null;
        title = null;
        author = null;
    }

    /**
     * The timestamp for the resulting embed.
     * <br>Usually used in combination with {@link java.time.OffsetDateTime}.
     *
     * @param  timestamp
     *         The timestamp
     *
     * @throws java.time.DateTimeException
     *         If unable to convert to an {@link java.time.OffsetDateTime}
     *
     * @return The current builder for chaining convenience
     */
    @NotNull
    public WebhookEmbedBuilder setTimestamp(@Nullable TemporalAccessor timestamp) {
        if (timestamp instanceof Instant) {
            this.timestamp = OffsetDateTime.ofInstant((Instant) timestamp, ZoneId.of("UTC"));
        }
        else {
            this.timestamp = timestamp == null ? null : OffsetDateTime.from(timestamp);
        }
        return this;
    }

    /**
     * The rgb color to use for the line left to the resulting embed
     *
     * @param  color
     *         The (nullable) color to use
     *
     * @return The current builder for chaining convenience
     */
    @NotNull
    public WebhookEmbedBuilder setColor(@Nullable Integer color) {
        this.color = color;
        return this;
    }

    /**
     * The description of the embed, this is the default
     * text used in most embeds. It is displayed
     * below author and title and above fields and image.
     *
     * @param  description
     *         The (nullable) description to use
     *
     * @return The current builder for chaining convenience
     */
    @NotNull
    public WebhookEmbedBuilder setDescription(@Nullable String description) {
        this.description = description;
        return this;
    }

    /**
     * The thumbnail url for this embed.
     * <br>This is displayed as small image to the right side of the description.
     *
     * @param  thumbnailUrl
     *         The (nullable) thumbnail url
     *
     * @return The current builder for chaining convenience
     */
    @NotNull
    public WebhookEmbedBuilder setThumbnailUrl(@Nullable String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
        return this;
    }

    /**
     * The image url for this embed.
     * <br>This is displayed below the description.
     *
     * @param  imageUrl
     *         The (nullable) image url
     *
     * @return The current builder for chaining convenience
     */
    @NotNull
    public WebhookEmbedBuilder setImageUrl(@Nullable String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    /**
     * The footer for this embed.
     * <br>This is displayed at the very bottom of the embed next to the timestamp.
     *
     * @param  footer
     *         The (nullable) {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedFooter}
     *
     * @return The current builder for chaining convenience
     */
    @NotNull
    public WebhookEmbedBuilder setFooter(@Nullable WebhookEmbed.EmbedFooter footer) {
        this.footer = footer;
        return this;
    }

    /**
     * The title for this embed.
     * <br>This is displayed below the author and above everything else.
     *
     * @param  title
     *         The (nullable) {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle}
     *
     * @return The current builder for chaining convenience
     */
    @NotNull
    public WebhookEmbedBuilder setTitle(@Nullable WebhookEmbed.EmbedTitle title) {
        this.title = title;
        return this;
    }

    /**
     * The author for this embed.
     * <br>This is displayed above everything else in the embed.
     *
     * @param  author
     *         The (nullable) {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedAuthor}
     *
     * @return The current builder for chaining convenience
     */
    @NotNull
    public WebhookEmbedBuilder setAuthor(@Nullable WebhookEmbed.EmbedAuthor author) {
        this.author = author;
        return this;
    }

    /**
     * Adds an {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedField} to this embed.
     * <br>And embed can hold up to 25 total fields. Each row can contain 2-3 fields
     * depending on whether a thumbnail is configured.
     *
     * @param  field
     *         The {@link club.minnced.discord.webhook.send.WebhookEmbed.EmbedField} to add
     *
     * @throws java.lang.IllegalStateException
     *         If the maximum amount of fields has already been reached
     *
     * @return The current builder for chaining convenience
     */
    @NotNull
    public WebhookEmbedBuilder addField(@NotNull WebhookEmbed.EmbedField field) {
        if (fields.size() == WebhookEmbed.MAX_FIELDS)
            throw new IllegalStateException("Cannot add more than 25 fields");
        fields.add(Objects.requireNonNull(field));
        return this;
    }

    /**
     * Whether this embed is currently empty.
     *
     * @return True, if this embed is empty
     */
    public boolean isEmpty() {
        return isEmpty(description)
               && isEmpty(imageUrl)
               && isEmpty(thumbnailUrl)
               && isFieldsEmpty()
               && isAuthorEmpty()
               && isTitleEmpty()
               && isFooterEmpty()
               && timestamp == null;
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

    /**
     * Builds a new {@link club.minnced.discord.webhook.send.WebhookEmbed} instance
     * from the current settings.
     *
     * @throws java.lang.IllegalStateException
     *         If this embed is currently empty
     *
     * @return The {@link club.minnced.discord.webhook.send.WebhookEmbed}
     */
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


    /////////////////////////////////
    /// Third-party compatibility ///
    /////////////////////////////////

    /**
     * Converts a JDA {@link MessageEmbed} into a compatible WebhookEmbedBuilder.
     *
     * @param  embed
     *         The embed
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return WebhookEmbedBuilder with the converted data
     */
    @NotNull
    @SuppressWarnings("ConstantConditions")
    public static WebhookEmbedBuilder fromJDA(@NotNull net.dv8tion.jda.api.entities.MessageEmbed embed) {
        WebhookEmbedBuilder builder = new WebhookEmbedBuilder();
        String url = embed.getUrl();
        String title = embed.getTitle();
        String description = embed.getDescription();
        MessageEmbed.Thumbnail thumbnail = embed.getThumbnail();
        MessageEmbed.AuthorInfo author = embed.getAuthor();
        MessageEmbed.Footer footer = embed.getFooter();
        MessageEmbed.ImageInfo image = embed.getImage();
        List<MessageEmbed.Field> fields = embed.getFields();
        int color = embed.getColorRaw();
        OffsetDateTime timestamp = embed.getTimestamp();

        if (title != null)
            builder.setTitle(new WebhookEmbed.EmbedTitle(title, url));
        if (description != null)
            builder.setDescription(description);
        if (thumbnail != null)
            builder.setThumbnailUrl(thumbnail.getUrl());
        if (author != null)
            builder.setAuthor(new WebhookEmbed.EmbedAuthor(author.getName(), author.getIconUrl(), author.getUrl()));
        if (footer != null)
            builder.setFooter(new WebhookEmbed.EmbedFooter(footer.getText(), footer.getIconUrl()));
        if (image != null)
            builder.setImageUrl(image.getUrl());
        if (!fields.isEmpty())
            fields.forEach(field -> builder.addField(new WebhookEmbed.EmbedField(field.isInline(), field.getName(), field.getValue())));
        if (color != Role.DEFAULT_COLOR_RAW)
            builder.setColor(color);
        if (timestamp != null)
            builder.setTimestamp(timestamp);

        return builder;
    }

    /**
     * Converts a Javacord {@link org.javacord.api.entity.message.embed.Embed Embed} into a compatible WebhookEmbedBuilder.
     *
     * @param  embed
     *         The embed
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return WebhookEmbedBuilder with the converted data
     */
    @NotNull
    public static WebhookEmbedBuilder fromJavacord(@NotNull org.javacord.api.entity.message.embed.Embed embed) {
        WebhookEmbedBuilder builder = new WebhookEmbedBuilder();

        embed.getTitle().ifPresent(title ->
                builder.setTitle(new WebhookEmbed.EmbedTitle(title, embed.getUrl().map(URL::toString).orElse(null))));
        embed.getDescription().ifPresent(builder::setDescription);
        embed.getTimestamp().ifPresent(builder::setTimestamp);
        embed.getColor().map(java.awt.Color::getRGB).ifPresent(builder::setColor);
        embed.getFooter().map(footer -> new WebhookEmbed.EmbedFooter(footer.getText().orElseThrow(NullPointerException::new), footer.getIconUrl().map(URL::toString).orElse(null))).ifPresent(builder::setFooter);
        embed.getImage().map(EmbedImage::getUrl).map(URL::toString).ifPresent(builder::setImageUrl);
        embed.getThumbnail().map(EmbedThumbnail::getUrl).map(URL::toString).ifPresent(builder::setThumbnailUrl);
        embed.getFields().stream()
                .map(field -> new WebhookEmbed.EmbedField(field.isInline(), field.getName(), field.getValue()))
                .forEach(builder::addField);
        return builder;
    }

    /**
     * Converts a Discord4J {@link EmbedCreateSpec} into a compatible WebhookEmbedBuilder.
     *
     * @param  callback
     *         The callback which applies the desired settings to the {@link EmbedCreateSpec}
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return WebhookEmbedBuilder with the converted data
     *
     * @deprecated Replace with {@link #fromD4J(EmbedCreateSpec)}
     */
    @NotNull
    @Deprecated
    public static WebhookEmbedBuilder fromD4J(@NotNull Consumer<? super EmbedCreateSpec> callback) {
        throw new UnsupportedOperationException("Cannot build embeds via consumers in Discord4J 3.2.0! Please change to fromD4J(spec)");
    }

    /**
     * Converts a Discord4J {@link EmbedCreateSpec} into a compatible WebhookEmbedBuilder.
     *
     * @param  spec
     *         The embed create spec which applies the desired settings
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return WebhookEmbedBuilder with the converted data
     */
    @NotNull
    public static WebhookEmbedBuilder fromD4J(@NotNull EmbedCreateSpec spec) {
        return fromD4J(spec.asRequest());
    }

    /**
     * Converts a Discord4J {@link EmbedData} into a compatible WebhookEmbedBuilder.
     *
     * @param  data
     *         The embed data
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return WebhookEmbedBuilder with the converted data
     */
    @NotNull
    public static WebhookEmbedBuilder fromD4J(@NotNull EmbedData data) {
        WebhookEmbedBuilder builder = new WebhookEmbedBuilder();

        // there aren't any docs for this, so I'm completely going off of assumptions here
        Possible<String> title = data.title();
        Possible<String> description = data.description();
        Possible<String> url = data.url();
        Possible<String> timestamp = data.timestamp();
        Possible<Integer> color = data.color();
        Possible<EmbedFooterData> footer = data.footer();
        Possible<EmbedImageData> image = data.image();
        Possible<EmbedThumbnailData> thumbnail = data.thumbnail();
        Possible<EmbedAuthorData> author = data.author();
        Possible<List<EmbedFieldData>> fields = data.fields();

        if (!title.isAbsent())
            builder.setTitle(new WebhookEmbed.EmbedTitle(title.get(), url.toOptional().orElse(null)));
        if (!description.isAbsent())
            builder.setDescription(description.get());
        if (!timestamp.isAbsent())
            builder.setTimestamp(OffsetDateTime.parse(timestamp.get()));
        if (!color.isAbsent())
            builder.setColor(color.get());
        if (!footer.isAbsent())
            builder.setFooter(new WebhookEmbed.EmbedFooter(footer.get().text(), footer.get().iconUrl().toOptional().orElse(null)));
        if (!image.isAbsent())
            builder.setImageUrl(image.get().url().get());
        if (!thumbnail.isAbsent())
            builder.setThumbnailUrl(thumbnail.get().url().get());
        if (!author.isAbsent()) {
            EmbedAuthorData authorData = author.get();
            builder.setAuthor(new WebhookEmbed.EmbedAuthor(
                    authorData.name().get(),
                    authorData.iconUrl().toOptional().orElse(null),
                    authorData.url().toOptional().orElse(null)));
        }
        if (!fields.isAbsent()) {
            fields.get()
                    .stream()
                    .map(field -> new WebhookEmbed.EmbedField(field.inline().toOptional().orElse(false), field.name(), field.value()))
                    .forEach(builder::addField);
        }

        return builder;
    }

}
