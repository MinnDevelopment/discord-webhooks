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

import club.minnced.discord.webhook.MessageFlags;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.discordjson.json.AllowedMentionsData;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.MessageCreateRequest;
import discord4j.discordjson.json.MessageEditRequest;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.MultipartRequest;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.entities.DataMessage;
import net.dv8tion.jda.internal.entities.ReceivedMessage;
import org.javacord.api.entity.DiscordEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Constructs a {@link club.minnced.discord.webhook.send.WebhookMessage}
 */
public class WebhookMessageBuilder {
    protected final StringBuilder content = new StringBuilder();
    protected final List<WebhookEmbed> embeds = new LinkedList<>();
    protected final MessageAttachment[] files = new MessageAttachment[WebhookMessage.MAX_FILES];
    protected AllowedMentions allowedMentions = AllowedMentions.all();
    protected String username, avatarUrl;
    protected boolean isTTS;
    protected int flags;
    private int fileIndex = 0;

    /**
     * Whether this builder is currently empty
     *
     * @return True, if this builder is empty
     */
    public boolean isEmpty() {
        return content.length() == 0 && embeds.isEmpty() && getFileAmount() == 0;
    }

    /**
     * The amount of files currently added
     *
     * @return The amount of currently added files
     */
    public int getFileAmount() {
        return fileIndex;
    }

    /**
     * Clears this builder to its default state
     *
     * @return This builder for chaining convenience
     */
    @NotNull
    public WebhookMessageBuilder reset() {
        content.setLength(0);
        resetEmbeds();
        resetFiles();
        username = null;
        avatarUrl = null;
        isTTS = false;
        return this;
    }

    /**
     * Clears all files currently added to this builder
     *
     * @return This builder for chaining convenience
     */
    @NotNull
    public WebhookMessageBuilder resetFiles() {
        for (int i = 0; i < WebhookMessage.MAX_FILES; i++) {
            files[i] = null;
        }
        fileIndex = 0;
        return this;
    }

    /**
     * Clears all embeds currently added this builder
     *
     * @return This builder for chaining convenience
     */
    @NotNull
    public WebhookMessageBuilder resetEmbeds() {
        this.embeds.clear();
        return this;
    }

    /**
     * The mention whitelist.
     * <br>See {@link AllowedMentions} for more details.
     *
     * @param  mentions
     *         The mention whitelist
     *
     * @throws NullPointerException
     *         If provided null
     *
     * @return This builder for chaining convenience
     */
    @NotNull
    public WebhookMessageBuilder setAllowedMentions(@NotNull AllowedMentions mentions)
    {
        this.allowedMentions = Objects.requireNonNull(mentions);
        return this;
    }

    /**
     * Adds the provided embeds to the builder
     *
     * @param embeds
     *         The embeds to add
     *
     * @return This builder for chaining convenience
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     * @throws java.lang.IllegalStateException
     *         If more than {@value WebhookMessage#MAX_EMBEDS} are added
     */
    @NotNull
    public WebhookMessageBuilder addEmbeds(@NotNull WebhookEmbed... embeds) {
        Objects.requireNonNull(embeds, "Embeds");
        if (this.embeds.size() + embeds.length > WebhookMessage.MAX_EMBEDS)
            throw new IllegalStateException("Cannot add more than 10 embeds to a message");
        for (WebhookEmbed embed : embeds) {
            Objects.requireNonNull(embed, "Embed");
            this.embeds.add(embed);
        }
        return this;
    }

    /**
     * Adds the provided embeds to the builder
     *
     * @param  embeds
     *         The embeds to add
     *
     * @return This builder for chaining convenience
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     * @throws java.lang.IllegalStateException
     *         If more than {@value WebhookMessage#MAX_EMBEDS} are added
     */
    @NotNull
    public WebhookMessageBuilder addEmbeds(@NotNull Collection<? extends WebhookEmbed> embeds) {
        Objects.requireNonNull(embeds, "Embeds");
        if (this.embeds.size() + embeds.size() > WebhookMessage.MAX_EMBEDS)
            throw new IllegalStateException("Cannot add more than 10 embeds to a message");
        for (WebhookEmbed embed : embeds) {
            Objects.requireNonNull(embed, "Embed");
            this.embeds.add(embed);
        }
        return this;
    }

    /**
     * Configures the content for this builder
     *
     * @param  content
     *         The (nullable) content to use
     *
     * @return This builder for chaining convenience
     *
     * @throws java.lang.IllegalArgumentException
     *         If the content is larger than 2000 characters
     */
    @NotNull
    public WebhookMessageBuilder setContent(@Nullable String content) {
        if (content != null && content.length() > 2000)
            throw new IllegalArgumentException("Content may not exceed 2000 characters!");
        this.content.setLength(0);
        if (content != null && !content.isEmpty())
            this.content.append(content);
        return this;
    }

    /**
     * Appends the provided content to the already
     * present content in this message.
     *
     * @param  content
     *         The content to append
     *
     * @return This builder for chaining convenience
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     * @throws java.lang.IllegalArgumentException
     *         If the content exceeds 2000 characters
     */
    @NotNull
    public WebhookMessageBuilder append(@NotNull String content) {
        Objects.requireNonNull(content, "Content");
        if (this.content.length() + content.length() > 2000)
            throw new IllegalArgumentException("Content may not exceed 2000 characters!");
        this.content.append(content);
        return this;
    }

    /**
     * The username to use for this message.
     * <br>Each message by a webhook can have a different user appearance.
     * If this is not set it will default the user appearance in the settings of
     * the webhook.
     *
     * @param  username
     *         The (nullable) username to use
     *
     * @return This builder for chaining convenience
     */
    @NotNull
    public WebhookMessageBuilder setUsername(@Nullable String username) {
        this.username = username == null || username.trim().isEmpty() ? null : username.trim();
        return this;
    }

    /**
     * The avatar url to use for this message.
     * <br>Each message by a webhook can have a different user appearance.
     * If this is not set it will default the user appearance in the settings of
     * the webhook.
     *
     * @param  avatarUrl
     *         The (nullable) avatar url to use
     *
     * @return This builder for chaining convenience
     */
    @NotNull
    public WebhookMessageBuilder setAvatarUrl(@Nullable String avatarUrl) {
        this.avatarUrl = avatarUrl == null || avatarUrl.trim().isEmpty() ? null : avatarUrl.trim();
        return this;
    }

    /**
     * Whether this message should use Text-to-Speech (TTS)
     *
     * @param  tts
     *         True, if this message should use tts
     *
     * @return This builder for chaining convenience
     */
    @NotNull
    public WebhookMessageBuilder setTTS(boolean tts) {
        isTTS = tts;
        return this;
    }

    /**
     * Whether the message should be ephemeral (only works for interaction webhooks).
     *
     * @param  ephemeral
     *         True if the message should be ephemeral, false otherwise
     *
     * @return This builder for chaining convenience
     */
    @NotNull
    public WebhookMessageBuilder setEphemeral(boolean ephemeral) {
        if (ephemeral)
            flags |= MessageFlags.EPHEMERAL;
        else
            flags &= ~MessageFlags.EPHEMERAL;
        return this;
    }

    /**
     * Adds the provided file as an attachment to this message.
     * <br>A single message can have up to {@value WebhookMessage#MAX_FILES} attachments.
     *
     * @param file
     *         The file to attach
     *
     * @return This builder for chaining convenience
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     */
    @NotNull
    public WebhookMessageBuilder addFile(@NotNull File file) {
        Objects.requireNonNull(file, "File");
        return addFile(file.getName(), file);
    }

    /**
     * Adds the provided file as an attachment to this message.
     * <br>A single message can have up to {@value WebhookMessage#MAX_FILES} attachments.
     *
     * @param  name
     *         The alternative name that should be used instead
     * @param  file
     *         The file to attach
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     *
     * @return This builder for chaining convenience
     */
    @NotNull
    public WebhookMessageBuilder addFile(@NotNull String name, @NotNull File file) {
        Objects.requireNonNull(file, "File");
        Objects.requireNonNull(name, "Name");
        if (!file.exists() || !file.canRead()) throw new IllegalArgumentException("File must exist and be readable");
        if (fileIndex >= WebhookMessage.MAX_FILES)
            throw new IllegalStateException("Cannot add more than " + WebhookMessage.MAX_FILES + " attachments to a message");

        try {
            MessageAttachment attachment = new MessageAttachment(name, file);
            files[fileIndex++] = attachment;
            return this;
        }
        catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * Adds the provided data as a file attachment to this message.
     * <br>A single message can have up to {@value WebhookMessage#MAX_FILES} attachments.
     *
     * @param  name
     *         The alternative name that should be used
     * @param  data
     *         The data to attach as a file
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     *
     * @return This builder for chaining convenience
     */
    @NotNull
    public WebhookMessageBuilder addFile(@NotNull String name, @NotNull byte[] data) {
        Objects.requireNonNull(data, "Data");
        Objects.requireNonNull(name, "Name");
        if (fileIndex >= WebhookMessage.MAX_FILES)
            throw new IllegalStateException("Cannot add more than " + WebhookMessage.MAX_FILES + " attachments to a message");

        MessageAttachment attachment = new MessageAttachment(name, data);
        files[fileIndex++] = attachment;
        return this;
    }

    /**
     * Adds the provided data as a file attachment to this message.
     * <br>A single message can have up to {@value WebhookMessage#MAX_FILES} attachments.
     *
     * @param  name
     *         The alternative name that should be used
     * @param  data
     *         The data to attach as a file
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     *
     * @return This builder for chaining convenience
     */
    @NotNull
    public WebhookMessageBuilder addFile(@NotNull String name, @NotNull InputStream data) {
        Objects.requireNonNull(data, "InputStream");
        Objects.requireNonNull(name, "Name");
        if (fileIndex >= WebhookMessage.MAX_FILES)
            throw new IllegalStateException("Cannot add more than " + WebhookMessage.MAX_FILES + " attachments to a message");

        try {
            MessageAttachment attachment = new MessageAttachment(name, data);
            files[fileIndex++] = attachment;
            return this;
        }
        catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * Constructs the {@link club.minnced.discord.webhook.send.WebhookMessage}
     * from the current configurations.
     *
     * @return The resulting {@link club.minnced.discord.webhook.send.WebhookMessage}
     */
    @NotNull
    public WebhookMessage build() {
        if (isEmpty())
            throw new IllegalStateException("Cannot build an empty message!");
        return new WebhookMessage(username, avatarUrl, content.toString(), embeds, isTTS,
                fileIndex == 0 ? null : Arrays.copyOf(files, fileIndex), allowedMentions, flags);
    }


    /////////////////////////////////
    /// Third-party compatibility ///
    /////////////////////////////////

    /**
     * Converts a JDA {@link Message} into a compatible WebhookMessageBuilder.
     *
     * @param  message
     *         The message
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return WebhookMessageBuilder with the converted data
     */
    @NotNull
    public static WebhookMessageBuilder fromJDA(@NotNull net.dv8tion.jda.api.entities.Message message) {
        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setTTS(message.isTTS());
        builder.setContent(message.getContentRaw());
        builder.setEphemeral(message.isEphemeral());
        message.getEmbeds().forEach(embed -> builder.addEmbeds(WebhookEmbedBuilder.fromJDA(embed).build()));

        if (message instanceof DataMessage) {
            DataMessage data = (DataMessage) message;
            AllowedMentions allowedMentions = AllowedMentions.none();
            EnumSet<Message.MentionType> parse = data.getAllowedMentions();
            allowedMentions.withUsers(data.getMentionedUsersWhitelist());
            allowedMentions.withRoles(data.getMentionedRolesWhitelist());
            if (parse != null) {
                allowedMentions.withParseUsers(parse.contains(Message.MentionType.USER));
                allowedMentions.withParseRoles(parse.contains(Message.MentionType.ROLE));
                allowedMentions.withParseEveryone(parse.contains(Message.MentionType.EVERYONE) || parse.contains(Message.MentionType.HERE));
            }
            builder.setAllowedMentions(allowedMentions);
        } else if (message instanceof ReceivedMessage) {
            AllowedMentions allowedMentions = AllowedMentions.none();
            allowedMentions.withRoles(
                message.getMentionedRoles().stream()
                    .map(Role::getId)
                    .collect(Collectors.toList()));
            allowedMentions.withUsers(
                message.getMentionedUsers().stream()
                    .map(User::getId)
                    .collect(Collectors.toList()));
            allowedMentions.withParseEveryone(message.mentionsEveryone());
            builder.setAllowedMentions(allowedMentions);
        }
        return builder;
    }

    /**
     * Converts a Javacord {@link org.javacord.api.entity.message.Message Message} into a compatible WebhookMessageBuilder.
     *
     * @param  message
     *         The message
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return WebhookMessageBuilder with the converted data
     */
    @NotNull
    public static WebhookMessageBuilder fromJavacord(@NotNull org.javacord.api.entity.message.Message message) {
        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setTTS(message.isTts());
        builder.setContent(message.getContent());
        message.getEmbeds().forEach(embed -> builder.addEmbeds(WebhookEmbedBuilder.fromJavacord(embed).build()));

        AllowedMentions allowedMentions = AllowedMentions.none();
        allowedMentions.withUsers(
            message.getMentionedUsers().stream()
                .map(DiscordEntity::getIdAsString)
                .collect(Collectors.toList()));
        allowedMentions.withRoles(
            message.getMentionedRoles().stream()
                .map(DiscordEntity::getIdAsString)
                .collect(Collectors.toList()));
        allowedMentions.withParseEveryone(message.mentionsEveryone());
        builder.setAllowedMentions(allowedMentions);
        return builder;
    }

    /**
     * Converts a Discord4J {@link MessageCreateSpec} into a compatible WebhookMessageBuilder.
     *
     * @param  callback
     *         The callback used to specify the desired message settings
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return WebhookMessageBuilder with the converted data
     *
     * @deprecated Replace with {@link #fromD4J(MessageCreateSpec)}
     */
    @NotNull
    @Deprecated
    public static WebhookMessageBuilder fromD4J(@NotNull Consumer<? super MessageCreateSpec> callback) {
        throw new UnsupportedOperationException("Cannot build messages via consumers in Discord4J 3.2.0! Please change to fromD4J(spec)");
    }

    /**
     * Converts a Discord4J {@link MessageCreateSpec} into a compatible WebhookMessageBuilder.
     *
     * @param  spec
     *         The message create spec used to specify the desired message settings
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return WebhookMessageBuilder with the converted data
     */
    @NotNull
    public static WebhookMessageBuilder fromD4J(@NotNull MessageCreateSpec spec) {
        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        MultipartRequest<MessageCreateRequest> data = spec.asRequest();
        data.getFiles().forEach(tuple -> builder.addFile(tuple.getT1(), tuple.getT2()));
        MessageCreateRequest jsonPayload = data.getJsonPayload();
        if (jsonPayload == null)
            return builder;

        Possible<String> content = jsonPayload.content();
        Possible<List<EmbedData>> embeds = jsonPayload.embeds();
        Possible<Boolean> tts = jsonPayload.tts();
        Possible<AllowedMentionsData> allowedMentions = jsonPayload.allowedMentions();

        if (!content.isAbsent())
            builder.setContent(content.get());
        if (!tts.isAbsent())
            builder.setTTS(tts.get());
        if (!embeds.isAbsent()) {
            builder.addEmbeds(
                embeds.get().stream()
                    .map(WebhookEmbedBuilder::fromD4J)
                    .map(WebhookEmbedBuilder::build)
                    .collect(Collectors.toList())
            );
        }

        if (!allowedMentions.isAbsent()) {
            AllowedMentionsData mentions = allowedMentions.get();
            AllowedMentions whitelist = AllowedMentions.none();
            if (!mentions.users().isAbsent())
                whitelist.withUsers(mentions.users().get());
            if (!mentions.roles().isAbsent())
                whitelist.withRoles(mentions.roles().get());
            if (!mentions.parse().isAbsent()) {
                List<String> parse = mentions.parse().get();
                whitelist.withParseRoles(parse.contains("roles"));
                whitelist.withParseEveryone(parse.contains("everyone"));
                whitelist.withParseUsers(parse.contains("users"));
            }
            builder.setAllowedMentions(whitelist);
        }

        return builder;
    }

    /**
     * Converts a Discord4J {@link MessageCreateSpec} into a compatible WebhookMessageBuilder.
     *
     * @param  spec
     *         The message create spec used to specify the desired message settings
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return WebhookMessageBuilder with the converted data
     */
    @NotNull
    public static WebhookMessageBuilder fromD4J(@NotNull MessageEditSpec spec) {
        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        MultipartRequest<MessageEditRequest> data = spec.asRequest();
        data.getFiles().forEach(tuple -> builder.addFile(tuple.getT1(), tuple.getT2()));
        MessageEditRequest jsonPayload = data.getJsonPayload();
        if (jsonPayload == null)
            return builder;

        Possible<Optional<String>> content = jsonPayload.content();
        Possible<Optional<List<EmbedData>>> embeds = jsonPayload.embeds();
        Possible<Optional<AllowedMentionsData>> allowedMentions = jsonPayload.allowedMentions();

        if (!content.isAbsent() && content.get().isPresent())
            builder.setContent(content.get().get());
        if (!embeds.isAbsent() && embeds.get().isPresent()) {
            builder.addEmbeds(
                    embeds.get().get().stream()
                            .map(WebhookEmbedBuilder::fromD4J)
                            .map(WebhookEmbedBuilder::build)
                            .collect(Collectors.toList())
            );
        }

        if (!allowedMentions.isAbsent() && allowedMentions.get().isPresent()) {
            AllowedMentionsData mentions = allowedMentions.get().get();
            AllowedMentions whitelist = AllowedMentions.none();
            if (!mentions.users().isAbsent())
                whitelist.withUsers(mentions.users().get());
            if (!mentions.roles().isAbsent())
                whitelist.withRoles(mentions.roles().get());
            if (!mentions.parse().isAbsent()) {
                List<String> parse = mentions.parse().get();
                whitelist.withParseRoles(parse.contains("roles"));
                whitelist.withParseEveryone(parse.contains("everyone"));
                whitelist.withParseUsers(parse.contains("users"));
            }
            builder.setAllowedMentions(whitelist);
        }

        return builder;
    }
}
