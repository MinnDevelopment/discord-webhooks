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

import club.minnced.discord.webhook.IOUtil;
import club.minnced.discord.webhook.MessageFlags;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Send-only message for a {@link club.minnced.discord.webhook.WebhookClient}
 * <br>A {@link club.minnced.discord.webhook.receive.ReadonlyMessage} can be sent
 * by first converting it to a WebhookMessage with {@link #from(club.minnced.discord.webhook.receive.ReadonlyMessage)}.
 */
public class WebhookMessage {
    /**
     * Maximum amount of files a single message can hold (10)
     */
    public static final int MAX_FILES = 10;
    /** Maximum amount of embeds a single message can hold (10) */
    public static final int MAX_EMBEDS = 10;

    protected final String username, avatarUrl, content;
    protected final List<WebhookEmbed> embeds;
    protected final boolean isTTS;
    protected final MessageAttachment[] attachments;
    protected final AllowedMentions allowedMentions;
    protected final int flags;

    protected WebhookMessage(final String username, final String avatarUrl, final String content,
                             final List<WebhookEmbed> embeds, final boolean isTTS,
                             final MessageAttachment[] files, final AllowedMentions allowedMentions,
                             final int flags) {
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.embeds = embeds;
        this.isTTS = isTTS;
        this.attachments = files;
        this.allowedMentions = allowedMentions;
        this.flags = flags;
    }

    /**
     * The username for this message
     *
     * @return Possibly-null username
     */
    @Nullable
    public String getUsername() {
        return username;
    }

    /**
     * The avatar url for this message
     *
     * @return Possibly-null avatar url
     */
    @Nullable
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * The content for this message
     *
     * @return Possibly-null content
     */
    @Nullable
    public String getContent() {
        return content;
    }

    /**
     * The embeds for this message
     *
     * @return The embeds
     */
    @NotNull
    public List<WebhookEmbed> getEmbeds() {
        return embeds == null ? Collections.emptyList() : embeds;
    }

    /**
     * The attachments for this message
     *
     * @return The attachments
     */
    @Nullable
    public MessageAttachment[] getAttachments() {
        return attachments;
    }

    /**
     * Whether this message should use Text-to-Speech (TTS)
     *
     * @return True, if this message will use tts
     */
    public boolean isTTS() {
        return isTTS;
    }

    /**
     * The message flags used for this message.
     *
     * @return The flags
     */
    public int getFlags() {
        return flags;
    }

    /**
     * Returns a new WebhookMessage instance with the ephemeral flag turned on/off (true/false).
     * <br>This instance remains unchanged and a new instance is returned.
     *
     * @param  ephemeral
     *         Whether to make this message ephemeral
     *
     * @return New WebhookMessage instance
     */
    @NotNull
    public WebhookMessage asEphemeral(boolean ephemeral) {
        int flags = this.flags;
        if (ephemeral)
            flags |= MessageFlags.EPHEMERAL;
        else
            flags &= ~MessageFlags.EPHEMERAL;
        return new WebhookMessage(username, avatarUrl, content, embeds, isTTS, attachments, allowedMentions, flags);
    }

    /**
     * Converts a {@link club.minnced.discord.webhook.receive.ReadonlyMessage} to a
     * WebhookMessage.
     * <br>This does not convert attachments.
     *
     * @param  message
     *         The message to convert
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     *
     * @return A WebhookMessage copy
     */
    @NotNull
    public static WebhookMessage from(@NotNull ReadonlyMessage message) {
        Objects.requireNonNull(message, "Message");
        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setAvatarUrl(message.getAuthor().getAvatarId());
        builder.setUsername(message.getAuthor().getName());
        builder.setContent(message.getContent());
        builder.setTTS(message.isTTS());
        builder.setEphemeral((message.getFlags() & MessageFlags.EPHEMERAL) != 0);
        builder.addEmbeds(message.getEmbeds());
        return builder.build();
    }

    /**
     * Creates a WebhookMessage from
     * the provided embeds. A message can hold up to {@value #MAX_EMBEDS} embeds.
     *
     * @param first
     *         The first embed
     * @param embeds
     *         Optional additional embeds for the message
     *
     * @return A WebhookMessage for the embeds
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     * @throws java.lang.IllegalArgumentException
     *         If more than {@value WebhookMessage#MAX_EMBEDS} are provided
     */
    @NotNull // forcing first embed as we expect at least one entry (Effective Java 3rd. Edition - Item 53)
    public static WebhookMessage embeds(@NotNull WebhookEmbed first, @NotNull WebhookEmbed... embeds) {
        Objects.requireNonNull(embeds, "Embeds");
        if (embeds.length >= WebhookMessage.MAX_EMBEDS)
            throw new IllegalArgumentException("Cannot add more than 10 embeds to a message");
        for (WebhookEmbed e : embeds) {
            Objects.requireNonNull(e);
        }
        List<WebhookEmbed> list = new ArrayList<>(1 + embeds.length);
        list.add(first);
        Collections.addAll(list, embeds);
        return new WebhookMessage(null, null, null, list, false, null, AllowedMentions.all(), 0);
    }

    /**
     * Creates a WebhookMessage from
     * the provided embeds. A message can hold up to {@value #MAX_EMBEDS} embeds.
     *
     * @param  embeds
     *         Embeds for the message
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     * @throws java.lang.IllegalArgumentException
     *         If more than {@value WebhookMessage#MAX_EMBEDS} are provided
     *
     * @return A WebhookMessage for the embeds
     */
    @NotNull
    public static WebhookMessage embeds(@NotNull Collection<WebhookEmbed> embeds) {
        Objects.requireNonNull(embeds, "Embeds");
        if (embeds.size() > WebhookMessage.MAX_EMBEDS)
            throw new IllegalArgumentException("Cannot add more than 10 embeds to a message");
        if (embeds.isEmpty())
            throw new IllegalArgumentException("Cannot build an empty message");
        embeds.forEach(Objects::requireNonNull);
        return new WebhookMessage(null, null, null, new ArrayList<>(embeds), false, null, AllowedMentions.all(), 0);
    }

    /**
     * Creates a WebhookMessage from the provided attachments.
     * <br>A message can hold up to {@value #MAX_FILES} attachments
     * and a total of 8MiB of data.
     *
     * @param  attachments
     *         The attachments to add, keys are the alternative names
     *         for each attachment
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     * @throws java.lang.IllegalArgumentException
     *         If no attachments are provided or more than {@value #MAX_FILES}
     *
     * @return A WebhookMessage for the attachments
     */
    @NotNull
    public static WebhookMessage files(@NotNull Map<String, ?> attachments) {
        Objects.requireNonNull(attachments, "Attachments");

        int fileAmount = attachments.size();
        if (fileAmount == 0)
            throw new IllegalArgumentException("Cannot build an empty message");
        if (fileAmount > WebhookMessage.MAX_FILES)
            throw new IllegalArgumentException("Cannot add more than " + WebhookMessage.MAX_FILES + " files to a message");
        Set<? extends Map.Entry<String, ?>> entries = attachments.entrySet();
        MessageAttachment[] files = new MessageAttachment[fileAmount];
        int i = 0;
        for (Map.Entry<String, ?> attachment : entries) {
            String name = attachment.getKey();
            Objects.requireNonNull(name, "Name");
            Object data = attachment.getValue();
            files[i++] = convertAttachment(name, data);
        }
        return new WebhookMessage(null, null, null, null, false, files, AllowedMentions.all(), 0);
    }

    /**
     * Creates a WebhookMessage from the provided attachments.
     * <br>A message can hold up to {@value #MAX_FILES} attachments
     * and a total of 8MiB of data.
     *
     * <p>The files are provided in pairs of {@literal Name->Data} similar
     * to the first 2 arguments.
     * <br>The allowed data types are {@code byte[] | InputStream | File}
     *
     * @param name1
     *         The alternative name of the first attachment
     * @param data1
     *         The first attachment, must be of type {@code byte[] | InputStream | File}
     * @param attachments
     *         Optional additional attachments to add, pairs of {@literal String->Data}
     *
     * @return A WebhookMessage for the attachments
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     * @throws java.lang.IllegalArgumentException
     *         If no attachments are provided or more than {@value #MAX_FILES}
     *         or the additional arguments are not an even count or an invalid format
     */
    @NotNull // forcing first pair as we expect at least one entry (Effective Java 3rd. Edition - Item 53)
    public static WebhookMessage files(@NotNull String name1, @NotNull Object data1, @NotNull Object... attachments) {
        Objects.requireNonNull(name1, "Name");
        Objects.requireNonNull(data1, "Data");
        Objects.requireNonNull(attachments, "Attachments");
        if (attachments.length % 2 != 0)
            throw new IllegalArgumentException("Must provide even number of varargs arguments");
        int fileAmount = 1 + attachments.length / 2;
        if (fileAmount > WebhookMessage.MAX_FILES)
            throw new IllegalArgumentException("Cannot add more than " + WebhookMessage.MAX_FILES + " files to a message");
        MessageAttachment[] files = new MessageAttachment[fileAmount];
        files[0] = convertAttachment(name1, data1);
        for (int i = 0, j = 1; i < attachments.length; j++, i += 2) {
            Object name = attachments[i];
            Object data = attachments[i + 1];
            if (!(name instanceof String))
                throw new IllegalArgumentException("Provided arguments must be pairs for (String, Data). Expected String and found " + (name == null ? null : name.getClass().getName()));
            files[j] = convertAttachment((String) name, data);
        }
        return new WebhookMessage(null, null, null, null, false, files, AllowedMentions.all(), 0);
    }

    /**
     * Whether this message contains files
     *
     * @return True, if this message contains files
     */
    public boolean isFile() {
        return attachments != null;
    }

    /**
     * Provides a {@link okhttp3.RequestBody} of this message.
     * <br>This is used internally for executing webhooks through HTTP requests.
     *
     * @return The request body
     */
    @NotNull
    public RequestBody getBody() {
        final JSONObject payload = new JSONObject();
        payload.put("content", content);
        if (embeds != null && !embeds.isEmpty()) {
            final JSONArray array = new JSONArray();
            for (WebhookEmbed embed : embeds) {
                array.put(embed.reduced());
            }
            payload.put("embeds", array);
        } else {
            payload.put("embeds", new JSONArray());
        }
        if (avatarUrl != null)
            payload.put("avatar_url", avatarUrl);
        if (username != null)
            payload.put("username", username);
        payload.put("tts", isTTS);
        payload.put("allowed_mentions", allowedMentions);
        payload.put("flags", flags);
        String json = payload.toString();
        if (isFile()) {
            final MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            for (int i = 0; i < attachments.length; i++) {
                final MessageAttachment attachment = attachments[i];
                if (attachment == null)
                    break;
                builder.addFormDataPart("file" + i, attachment.getName(), new IOUtil.OctetBody(attachment.getData()));
            }
            return builder.addFormDataPart("payload_json", json).build();
        }
        return RequestBody.create(IOUtil.JSON, json);
    }

    @NotNull
    private static MessageAttachment convertAttachment(@NotNull String name, @NotNull Object data) {
        Objects.requireNonNull(name, "Name");
        Objects.requireNonNull(data, "Data");
        try {
            MessageAttachment a;
            if (data instanceof File)
                a = new MessageAttachment(name, (File) data);
            else if (data instanceof InputStream)
                a = new MessageAttachment(name, (InputStream) data);
            else if (data instanceof byte[])
                a = new MessageAttachment(name, (byte[]) data);
            else
                throw new IllegalArgumentException("Provided arguments must be pairs for (String, Data). Unexpected data type " + data.getClass().getName());
            return a;
        }
        catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
