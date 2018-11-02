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

import club.minnced.discord.webhook.send.WebhookMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONString;

import java.util.List;

public class ReadonlyMessage implements JSONString { //TODO: Docs
    private final long id;
    private final long channelId;
    private final boolean mentionsEveryone;
    private final boolean tts;

    private final ReadonlyUser author;

    private final String nonce;
    private final String content;
    private final List<ReadonlyEmbed> embeds;
    private final List<ReadonlyAttachment> attachments;

    private final List<ReadonlyUser> mentionedUsers;
    private final List<Long> mentionedRoles;

    public ReadonlyMessage(
            long id, long channelId, boolean mentionsEveryone, boolean tts,
            @NotNull ReadonlyUser author, @Nullable String nonce, @NotNull String content,
            @NotNull List<ReadonlyEmbed> embeds, @NotNull List<ReadonlyAttachment> attachments,
            @NotNull List<ReadonlyUser> mentionedUsers, @NotNull List<Long> mentionedRoles) {
        this.id = id;
        this.channelId = channelId;
        this.mentionsEveryone = mentionsEveryone;
        this.tts = tts;
        this.author = author;
        this.nonce = nonce;
        this.content = content;
        this.embeds = embeds;
        this.attachments = attachments;
        this.mentionedUsers = mentionedUsers;
        this.mentionedRoles = mentionedRoles;
    }

    public long getId() {
        return id;
    }

    public long getChannelId() {
        return channelId;
    }

    public boolean isMentionsEveryone() {
        return mentionsEveryone;
    }

    public boolean isTTS() {
        return tts;
    }

    @Nullable
    public String getNonce() {
        return nonce;
    }

    @NotNull
    public ReadonlyUser getAuthor() {
        return author;
    }

    @NotNull
    public String getContent() {
        return content;
    }

    @NotNull
    public List<ReadonlyEmbed> getEmbeds() {
        return embeds;
    }

    @NotNull
    public List<ReadonlyAttachment> getAttachments() {
        return attachments;
    }

    @NotNull
    public List<ReadonlyUser> getMentionedUsers() {
        return mentionedUsers;
    }

    @NotNull
    public List<Long> getMentionedRoles() {
        return mentionedRoles;
    }

    @NotNull
    public WebhookMessage toWebhookMessage() {
        return WebhookMessage.from(this);
    }

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
            .put("nonce", nonce)
            .put("author", author)
            .put("tts", tts)
            .put("id", Long.toUnsignedString(id))
            .put("channel_id", Long.toUnsignedString(channelId))
            .put("mention_everyone", mentionsEveryone);
        return json.toString();
    }
}
