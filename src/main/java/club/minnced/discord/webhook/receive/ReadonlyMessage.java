package club.minnced.discord.webhook.receive;

import club.minnced.discord.webhook.send.WebhookEmbed;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReadonlyMessage { //TODO: Create ReadonlyEmbed with more info
    private final long id;
    private final long channelId;
    private final long guildId;
    private final boolean mentionsEveryone;

    private final ReadonlyUser author;

    private final String nonce;
    private final String content;
    private final List<WebhookEmbed> embeds;
    private final List<ReadonlyAttachment> attachments;

    private final List<Long> mentionedUsers;
    private final List<Long> mentionedRoles;

    public ReadonlyMessage(
            long id, long channelId, long guildId, boolean mentionsEveryone,
            @NotNull ReadonlyUser author, @NotNull String nonce, @NotNull String content,
            @NotNull List<WebhookEmbed> embeds, @NotNull List<ReadonlyAttachment> attachments,
            @NotNull List<Long> mentionedUsers, @NotNull List<Long> mentionedRoles) {
        this.id = id;
        this.channelId = channelId;
        this.guildId = guildId;
        this.mentionsEveryone = mentionsEveryone;
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

    public long getGuildId() {
        return guildId;
    }

    public boolean isMentionsEveryone() {
        return mentionsEveryone;
    }

    public ReadonlyUser getAuthor() {
        return author;
    }

    public String getNonce() {
        return nonce;
    }

    public String getContent() {
        return content;
    }

    public List<WebhookEmbed> getEmbeds() {
        return embeds;
    }

    public List<ReadonlyAttachment> getAttachments() {
        return attachments;
    }

    public List<Long> getMentionedUsers() {
        return mentionedUsers;
    }

    public List<Long> getMentionedRoles() {
        return mentionedRoles;
    }
}
