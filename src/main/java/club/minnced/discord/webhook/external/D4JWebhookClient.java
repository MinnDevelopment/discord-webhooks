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

package club.minnced.discord.webhook.external;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import club.minnced.discord.webhook.util.ThreadPools;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
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
        this(id, token, parseMessage, client, pool, mentions, 0L);
    }

    public D4JWebhookClient(long id, String token, boolean parseMessage, OkHttpClient client, ScheduledExecutorService pool, AllowedMentions mentions, long threadId) {
        super(id, token, parseMessage, client, pool, mentions, threadId);
    }

    protected D4JWebhookClient(D4JWebhookClient parent, long threadId) {
        super(parent, threadId);
    }

    /**
     * Creates a D4JWebhookClient for the provided webhook.
     *
     * <p>You can use {@link #onThread(long)} to target specific threads on the channel.
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
     * <p>You can use {@link #onThread(long)} to target specific threads on the channel.
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
        return new D4JWebhookClient(id, token, true, new OkHttpClient(), pool, AllowedMentions.all(), 0L);
    }

    /**
     * Factory method to create a basic D4JWebhookClient with the provided id and token.
     *
     * <p>You can use {@link #onThread(long)} to target specific threads on the channel.
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

    @NotNull
    @Override
    public D4JWebhookClient onThread(final long threadId) {
        return new D4JWebhookClient(this, threadId);
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
     * @deprecated Replace wth {@link #send(MessageCreateSpec)}
     *
     * @see    #isWait()
     * @see    WebhookMessageBuilder#fromD4J(Consumer)
     */
    @NotNull
    @Deprecated
    @CheckReturnValue
    public Mono<ReadonlyMessage> send(@NotNull Consumer<? super MessageCreateSpec> callback) {
        throw new UnsupportedOperationException("Cannot build messages via consumers in Discord4J 3.2.0! Please change to fromD4J(spec)");
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
     * @deprecated Replace with {@link #edit(long, MessageEditSpec)}
     *
     * @see    #isWait()
     * @see    WebhookMessageBuilder#fromD4J(Consumer)
     */
    @NotNull
    @Deprecated
    @CheckReturnValue
    public Mono<ReadonlyMessage> edit(long messageId, @NotNull Consumer<? super MessageCreateSpec> callback) {
        throw new UnsupportedOperationException("Cannot build messages via consumers in Discord4J 3.2.0! Please change to fromD4J(spec)");
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
     * @deprecated Replace with {@link #edit(long, MessageEditSpec)}
     *
     * @see    #isWait()
     * @see    WebhookMessageBuilder#fromD4J(Consumer)
     */
    @NotNull
    @Deprecated
    @CheckReturnValue
    public Mono<ReadonlyMessage> edit(@NotNull String messageId, @NotNull Consumer<? super MessageCreateSpec> callback) {
        throw new UnsupportedOperationException("Cannot build messages via consumers in Discord4J 3.2.0! Please change to fromD4J(spec)");
    }

    /**
     * Sends the provided {@link MessageCreateSpec} to the webhook.
     *
     * @param  spec
     *         The message create spec used to specify the desired message settings
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return {@link Mono}
     *
     * @see    #isWait()
     * @see    WebhookMessageBuilder#fromD4J(MessageCreateSpec)
     */
    @NotNull
    @CheckReturnValue
    public Mono<ReadonlyMessage> send(@NotNull MessageCreateSpec spec) {
        WebhookMessage message = WebhookMessageBuilder.fromD4J(spec).build();
        return Mono.fromFuture(() -> send(message));
    }

    /**
     * Edits the target message with the provided {@link MessageCreateSpec} to the webhook.
     *
     * @param  messageId
     *         The target message id
     * @param  spec
     *         The message edit spec used to specify the desired message settings
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return {@link Mono}
     *
     * @see    #isWait()
     * @see    WebhookMessageBuilder#fromD4J(MessageEditSpec)
     */
    @NotNull
    @CheckReturnValue
    public Mono<ReadonlyMessage> edit(long messageId, @NotNull MessageEditSpec spec) {
        WebhookMessage message = WebhookMessageBuilder.fromD4J(spec).build();
        return Mono.fromFuture(() -> edit(messageId, message));
    }

    /**
     * Edits the target message with the provided {@link MessageCreateSpec} to the webhook.
     *
     * @param  messageId
     *         The target message id
     * @param  spec
     *         The message edit spec used to specify the desired message settings
     *
     * @throws NullPointerException
     *         If null is provided
     *
     * @return {@link Mono}
     *
     * @see    #isWait()
     * @see    WebhookMessageBuilder#fromD4J(MessageEditSpec)
     */
    @NotNull
    @CheckReturnValue
    public Mono<ReadonlyMessage> edit(@NotNull String messageId, @NotNull MessageEditSpec spec) {
        WebhookMessage message = WebhookMessageBuilder.fromD4J(spec).build();
        return Mono.fromFuture(() -> edit(messageId, message));
    }
}
