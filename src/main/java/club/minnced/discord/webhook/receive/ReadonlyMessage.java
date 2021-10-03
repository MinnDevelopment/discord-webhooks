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

import club.minnced.discord.webhook.send.WebhookMessage;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONString;

import java.util.List;

/**
 * Readonly message representation used for responses
 * of {@link club.minnced.discord.webhook.WebhookClient} send methods.
 *
 * @see #toWebhookMessage()
 */
public class ReadonlyMessage implements JSONString {
    private final long id;
    private final long channelId;
    private final boolean mentionsEveryone;
    private final boolean tts;
    private final int flags;

    private final ReadonlyUser author;

    private final String content;
    private final List<ReadonlyEmbed> embeds;
    private final List<ReadonlyAttachment> attachments;

    private final List<ReadonlyUser> mentionedUsers;
    private final List<Long> mentionedRoles;

    public ReadonlyMessage(
            long id, long channelId, boolean mentionsEveryone, boolean tts, int flags,
            @NotNull ReadonlyUser author, @NotNull String content,
            @NotNull List<ReadonlyEmbed> embeds, @NotNull List<ReadonlyAttachment> attachments,
            @NotNull List<ReadonlyUser> mentionedUsers, @NotNull List<Long> mentionedRoles) {
        this.id = id;
        this.channelId = channelId;
        this.mentionsEveryone = mentionsEveryone;
        this.tts = tts;
        this.flags = flags;
        this.author = author;
        this.content = content;
        this.embeds = embeds;
        this.attachments = attachments;
        this.mentionedUsers = mentionedUsers;
        this.mentionedRoles = mentionedRoles;
    }

    /**
     * The id of this message
     *
     * @return The id
     */
    public long getId() {
        return id;
    }

    /**
     * The channel id for the channel this message was sent in
     *
     * @return The channel id
     */
    public long getChannelId() {
        return channelId;
    }

    /**
     * Whether this message mentioned everyone/here
     *
     * @return True, if this message mentioned everyone/here
     */
    public boolean isMentionsEveryone() {
        return mentionsEveryone;
    }

    /**
     * Whether this message used Text-to-Speech (TTS)
     *
     * @return True, if this message used TTS
     */
    public boolean isTTS() {
        return tts;
    }

    /**
     * The flags for this message.
     * <br>You can use {@link club.minnced.discord.webhook.MessageFlags} to determine which flags are set.
     *
     * @return The flags
     */
    public int getFlags() {
        return flags;
    }

    /**
     * The author of this message, represented by a {@link club.minnced.discord.webhook.receive.ReadonlyUser} instance.
     *
     * @return The author
     */
    @NotNull
    public ReadonlyUser getAuthor() {
        return author;
    }

    /**
     * The content of this message, this is displayed above embeds and attachments.
     *
     * @return The content
     */
    @NotNull
    public String getContent() {
        return content;
    }

    /**
     * The embeds in this message, a webhook can send up to 10 embeds
     * in one message. Additionally this contains embeds generated from links.
     *
     * @return List of embeds for this message
     */
    @NotNull
    public List<ReadonlyEmbed> getEmbeds() {
        return embeds;
    }

    /**
     * The attachments of this message. This contains files
     * added through methods such as {@link club.minnced.discord.webhook.send.WebhookMessageBuilder#addFile(java.io.File)}.
     * <br>The attachments only contain meta-data and not the actual files.
     *
     * @return List of attachments
     */
    @NotNull
    public List<ReadonlyAttachment> getAttachments() {
        return attachments;
    }

    /**
     * Users mentioned by this message.
     * <br>This will not contain all users when using an everyone/here mention,
     * it only contains directly mentioned users.
     *
     * @return List of mentioned users.
     */
    @NotNull
    public List<ReadonlyUser> getMentionedUsers() {
        return mentionedUsers;
    }

    /**
     * List of mentioned role ids
     *
     * @return List of ids for directly mentioned roles
     */
    @NotNull
    public List<Long> getMentionedRoles() {
        return mentionedRoles;
    }

    /**
     * Converts this message to a reduced webhook message.
     * <br>This can be used for sending.
     *
     * @return {@link club.minnced.discord.webhook.send.WebhookMessage}
     */
    @NotNull
    public WebhookMessage toWebhookMessage() {
        return WebhookMessage.from(this);
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
        JSONObject json = new JSONObject();
        json.put("content", content)
            .put("embeds", embeds)
            .put("mentions", mentionedUsers)
            .put("mention_roles", mentionedRoles)
            .put("attachments", attachments)
            .put("author", author)
            .put("tts", tts)
            .put("id", Long.toUnsignedString(id))
            .put("channel_id", Long.toUnsignedString(channelId))
            .put("mention_everyone", mentionsEveryone);
        return json.toString();
    }
}
