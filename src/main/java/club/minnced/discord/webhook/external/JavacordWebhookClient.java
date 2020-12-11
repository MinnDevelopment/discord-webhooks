package club.minnced.discord.webhook.external;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

public class JavacordWebhookClient extends WebhookClient {
    public JavacordWebhookClient(long id, String token, boolean parseMessage, OkHttpClient client, ScheduledExecutorService pool, AllowedMentions mentions) {
        super(id, token, parseMessage, client, pool, mentions);
    }

    /**
     * Creates a WebhookClient for the provided webhook.
     *
     * @param  webhook
     *         The webhook
     *
     * @throws NullPointerException
     *         If the webhook is null or does not provide a token
     *
     * @return The WebhookClient
     */
    @NotNull
    public static WebhookClient from(@NotNull org.javacord.api.entity.webhook.Webhook webhook) {
        return WebhookClientBuilder.fromJavacord(webhook).build();
    }

    /**
     * Sends the provided {@link org.javacord.api.entity.message.Message Message} to the webhook.
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
     * @see    WebhookMessageBuilder#fromJavacord(org.javacord.api.entity.message.Message)
     */
    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull org.javacord.api.entity.message.Message message) {
        return send(WebhookMessageBuilder.fromJavacord(message).build());
    }

    /**
     * Sends the provided {@link org.javacord.api.entity.message.embed.Embed Embed} to the webhook.
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
     * @see    WebhookEmbedBuilder#fromJavacord(org.javacord.api.entity.message.embed.Embed)
     */
    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull org.javacord.api.entity.message.embed.Embed embed) {
        return send(WebhookEmbedBuilder.fromJavacord(embed).build());
    }

    /**
     * Edits the target message with the provided {@link org.javacord.api.entity.message.Message Message} to the webhook.
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
     * @see    WebhookMessageBuilder#fromJavacord(org.javacord.api.entity.message.Message)
     */
    @NotNull
    public CompletableFuture<ReadonlyMessage> edit(long messageId, @NotNull org.javacord.api.entity.message.Message message) {
        return edit(messageId, WebhookMessageBuilder.fromJavacord(message).build());
    }

    /**
     * Edits the target message with the provided {@link org.javacord.api.entity.message.embed.Embed Embed} to the webhook.
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
     * @see    WebhookEmbedBuilder#fromJavacord(org.javacord.api.entity.message.embed.Embed)
     */
    @NotNull
    public CompletableFuture<ReadonlyMessage> edit(long messageId, @NotNull org.javacord.api.entity.message.embed.Embed embed) {
        return edit(messageId, WebhookEmbedBuilder.fromJavacord(embed).build());
    }
}
