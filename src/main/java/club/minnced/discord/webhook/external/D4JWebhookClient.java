package club.minnced.discord.webhook.external;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import club.minnced.discord.webhook.util.ThreadPools;
import discord4j.core.spec.MessageCreateSpec;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import javax.annotation.CheckReturnValue;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.regex.Matcher;

public class D4JWebhookClient extends WebhookClient {
    public D4JWebhookClient(long id, String token, boolean parseMessage, OkHttpClient client, ScheduledExecutorService pool, AllowedMentions mentions) {
        super(id, token, parseMessage, client, pool, mentions);
    }

    /**
     * Creates a D4JWebhookClient for the provided webhook.
     *
     * @param  webhook
     *         The webhook
     *
     * @throws NullPointerException
     *         If the webhook is null or does not provide a token
     *
     * @return The D4JWebhookClient
     */
    @NotNull
    public static D4JWebhookClient from(@NotNull discord4j.core.object.entity.Webhook webhook) {
        return WebhookClientBuilder.fromD4J(webhook).buildD4J();
    }

    /**
     * Factory method to create a basic D4JWebhookClient with the provided id and token.
     *
     * @param  id
     *         The webhook id
     * @param  token
     *         The webhook token
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     *
     * @return The D4JWebhookClient for the provided id and token
     */
    @NotNull
    public static D4JWebhookClient withId(long id, @NotNull String token) {
        Objects.requireNonNull(token, "Token");
        ScheduledExecutorService pool = ThreadPools.getDefaultPool(id, null, false);
        return new D4JWebhookClient(id, token, true, new OkHttpClient(), pool, AllowedMentions.all());
    }

    /**
     * Factory method to create a basic D4JWebhookClient with the provided id and token.
     *
     * @param  url
     *         The url for the webhook
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     * @throws java.lang.NumberFormatException
     *         If no valid id is part o the url
     *
     * @return The D4JWebhookClient for the provided url
     */
    @NotNull
    public static D4JWebhookClient withUrl(@NotNull String url) {
        Objects.requireNonNull(url, "URL");
        Matcher matcher = WebhookClientBuilder.WEBHOOK_PATTERN.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Failed to parse webhook URL");
        }
        return withId(Long.parseUnsignedLong(matcher.group(1)), matcher.group(2));
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
}
