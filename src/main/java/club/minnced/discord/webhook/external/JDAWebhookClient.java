package club.minnced.discord.webhook.external;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

public class JDAWebhookClient extends WebhookClient {
    public JDAWebhookClient(long id, String token, boolean parseMessage, OkHttpClient client, ScheduledExecutorService pool, AllowedMentions mentions) {
        super(id, token, parseMessage, client, pool, mentions);
    }

    /**
     * Creates a WebhookClient for the provided webhook.
     *
     * @param webhook The webhook
     * @return The WebhookClient
     * @throws NullPointerException If the webhook is null or does not provide a token
     */
    @NotNull
    public static WebhookClient fromJDA(@NotNull net.dv8tion.jda.api.entities.Webhook webhook) {
        return WebhookClientBuilder.fromJDA(webhook).build();
    }

    /**
     * Sends the provided {@link net.dv8tion.jda.api.entities.Message Message} to the webhook.
     *
     * @param  message
     *         The message to send
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return {@link CompletableFuture}
     *
     * @see    #isWait()
     * @see    WebhookMessageBuilder#fromJDA(Message)
     */
    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull net.dv8tion.jda.api.entities.Message message) {
        return send(WebhookMessageBuilder.fromJDA(message).build());
    }

    /**
     * Sends the provided {@link net.dv8tion.jda.api.entities.MessageEmbed MessageEmbed} to the webhook.
     *
     * @param  embed
     *         The embed to send
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return {@link CompletableFuture}
     *
     * @see    #isWait()
     * @see    WebhookEmbedBuilder#fromJDA(net.dv8tion.jda.api.entities.MessageEmbed)
     */
    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull net.dv8tion.jda.api.entities.MessageEmbed embed) {
        return send(WebhookEmbedBuilder.fromJDA(embed).build());
    }

    /**
     * Edits the target message with the provided {@link net.dv8tion.jda.api.entities.Message Message} to the webhook.
     *
     * @param  messageId
     *         The target message id
     * @param  message
     *         The message to send
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return {@link CompletableFuture}
     *
     * @see    #isWait()
     * @see    WebhookMessageBuilder#fromJDA(Message)
     */
    @NotNull
    public CompletableFuture<ReadonlyMessage> edit(long messageId, @NotNull net.dv8tion.jda.api.entities.Message message) {
        return edit(messageId, WebhookMessageBuilder.fromJDA(message).build());
    }

    /**
     * Edits the target message with the provided {@link net.dv8tion.jda.api.entities.MessageEmbed MessageEmbed} to the webhook.
     *
     * @param  messageId
     *         The target message id
     * @param  embed
     *         The embed to send
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return {@link CompletableFuture}
     *
     * @see    #isWait()
     * @see    WebhookEmbedBuilder#fromJDA(net.dv8tion.jda.api.entities.MessageEmbed)
     */
    @NotNull
    public CompletableFuture<ReadonlyMessage> edit(long messageId, @NotNull net.dv8tion.jda.api.entities.MessageEmbed embed) {
        return edit(messageId, WebhookEmbedBuilder.fromJDA(embed).build());
    }

    /**
     * Edits the target message with the provided {@link net.dv8tion.jda.api.entities.Message Message} to the webhook.
     *
     * @param  messageId
     *         The target message id
     * @param  message
     *         The message to send
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return {@link CompletableFuture}
     *
     * @see    #isWait()
     * @see    WebhookMessageBuilder#fromJDA(Message)
     */
    @NotNull
    public CompletableFuture<ReadonlyMessage> edit(@NotNull String messageId, @NotNull net.dv8tion.jda.api.entities.Message message) {
        return edit(messageId, WebhookMessageBuilder.fromJDA(message).build());
    }

    /**
     * Edits the target message with the provided {@link net.dv8tion.jda.api.entities.MessageEmbed MessageEmbed} to the webhook.
     *
     * @param  messageId
     *         The target message id
     * @param  embed
     *         The embed to send
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return {@link CompletableFuture}
     *
     * @see    #isWait()
     * @see    WebhookEmbedBuilder#fromJDA(net.dv8tion.jda.api.entities.MessageEmbed)
     */
    @NotNull
    public CompletableFuture<ReadonlyMessage> edit(@NotNull String messageId, @NotNull net.dv8tion.jda.api.entities.MessageEmbed embed) {
        return edit(messageId, WebhookEmbedBuilder.fromJDA(embed).build());
    }
}
