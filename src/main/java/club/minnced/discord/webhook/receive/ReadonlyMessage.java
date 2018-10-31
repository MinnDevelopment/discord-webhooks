package club.minnced.discord.webhook.receive;

import club.minnced.discord.webhook.send.WebhookEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReadonlyMessage { //TODO: Create ReadonlyEmbed with more info
    private final long id;
    private final long channelId;
    private final boolean mentionsEveryone;
    private final boolean tts;

    private final ReadonlyUser author;

    private final String nonce;
    private final String content;
    private final List<WebhookEmbed> embeds;
    private final List<ReadonlyAttachment> attachments;

    private final List<ReadonlyUser> mentionedUsers;
    private final List<Long> mentionedRoles;

    public ReadonlyMessage(
            long id, long channelId, boolean mentionsEveryone, boolean tts,
            @NotNull ReadonlyUser author, @Nullable String nonce, @NotNull String content,
            @NotNull List<WebhookEmbed> embeds, @NotNull List<ReadonlyAttachment> attachments,
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
    public List<WebhookEmbed> getEmbeds() {
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
}
