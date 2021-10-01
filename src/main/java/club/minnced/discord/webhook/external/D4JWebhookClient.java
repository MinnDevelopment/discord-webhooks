package club.minnced.discord.webhook.external;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import discord4j.core.spec.MessageCreateSpec;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import javax.annotation.CheckReturnValue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public class D4JWebhookClient extends WebhookClient {
    public D4JWebhookClient(long id, String token, boolean parseMessage, OkHttpClient client, ScheduledExecutorService pool, AllowedMentions mentions) {
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
    public static WebhookClient from(@NotNull discord4j.core.object.entity.Webhook webhook) {
        return WebhookClientBuilder.fromD4J(webhook).build();
    }

    /**
     * Sends the provided {@link MessageCreateSpec} to the webhook.
     *
     * @param  callback
     *         The callback used to specify the desired message settings
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return {@link Mono}
     *
     * @see    #isWait()
     * @see    WebhookMessageBuilder#fromD4J(Consumer)
     */
    @NotNull
    @CheckReturnValue
    public Mono<ReadonlyMessage> send(@NotNull Consumer<? super MessageCreateSpec> callback) {
        WebhookMessage message = WebhookMessageBuilder.fromD4J(callback).build();
        return Mono.fromFuture(() -> send(message));
    }

    /**
     * Edits the target message with the provided {@link MessageCreateSpec} to the webhook.
     *
     * @param  messageId
     *         The target message id
     * @param  callback
     *         The callback used to specify the desired message settings
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return {@link Mono}
     *
     * @see    #isWait()
     * @see    WebhookMessageBuilder#fromD4J(Consumer)
     */
    @NotNull
    @CheckReturnValue
    public Mono<ReadonlyMessage> edit(long messageId, @NotNull Consumer<? super MessageCreateSpec> callback) {
        WebhookMessage message = WebhookMessageBuilder.fromD4J(callback).build();
        return Mono.fromFuture(() -> edit(messageId, message));
    }

    /**
     * Edits the target message with the provided {@link MessageCreateSpec} to the webhook.
     *
     * @param  messageId
     *         The target message id
     * @param  callback
     *         The callback used to specify the desired message settings
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return {@link Mono}
     *
     * @see    #isWait()
     * @see    WebhookMessageBuilder#fromD4J(Consumer)
     */
    @NotNull
    @CheckReturnValue
    public Mono<ReadonlyMessage> edit(@NotNull String messageId, @NotNull Consumer<? super MessageCreateSpec> callback) {
        WebhookMessage message = WebhookMessageBuilder.fromD4J(callback).build();
        return Mono.fromFuture(() -> edit(messageId, message));
    }
}
