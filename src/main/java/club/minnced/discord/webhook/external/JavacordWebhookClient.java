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
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import club.minnced.discord.webhook.util.ThreadPools;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;

public class JavacordWebhookClient extends WebhookClient {
    public JavacordWebhookClient(long id, String token, boolean parseMessage, OkHttpClient client, ScheduledExecutorService pool, AllowedMentions mentions) {
        this(id, token, parseMessage, client, pool, mentions, 0L);
    }

    public JavacordWebhookClient(long id, String token, boolean parseMessage, OkHttpClient client, ScheduledExecutorService pool, AllowedMentions mentions, long threadId) {
        super(id, token, parseMessage, client, pool, mentions, threadId);
    }

    protected JavacordWebhookClient(JavacordWebhookClient parent, long threadId) {
        super(parent, threadId);
    }

    /**
     * Creates a WebhookClient for the provided webhook.
     *
     * <p>You can use {@link #onThread(long)} to target specific threads on the channel.
     *
     * @param  webhook
     *         The webhook
     *
     * @throws NullPointerException
     *         If the webhook is null or does not provide a token
     *
     * @return The JavacordWebhookClient
     */
    @NotNull
    public static JavacordWebhookClient from(@NotNull org.javacord.api.entity.webhook.Webhook webhook) {
        return WebhookClientBuilder.fromJavacord(webhook).buildJavacord();
    }

    /**
     * Factory method to create a basic JavacordWebhookClient with the provided id and token.
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
     * @return The JavacordWebhookClient for the provided id and token
     */
    @NotNull
    public static JavacordWebhookClient withId(long id, @NotNull String token) {
        Objects.requireNonNull(token, "Token");
        ScheduledExecutorService pool = ThreadPools.getDefaultPool(id, null, false);
        return new JavacordWebhookClient(id, token, true, new OkHttpClient(), pool, AllowedMentions.all());
    }

    /**
     * Factory method to create a basic JavacordWebhookClient with the provided id and token.
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
     * @return The JavacordWebhookClient for the provided url
     */
    @NotNull
    public static JavacordWebhookClient withUrl(@NotNull String url) {
        Objects.requireNonNull(url, "URL");
        Matcher matcher = WebhookClientBuilder.WEBHOOK_PATTERN.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Failed to parse webhook URL");
        }
        return withId(Long.parseUnsignedLong(matcher.group(1)), matcher.group(2));
    }

    @NotNull
    @Override
    public JavacordWebhookClient onThread(long threadId) {
        return new JavacordWebhookClient(this, threadId);
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
        return sendEmbeds(WebhookEmbedBuilder.fromJavacord(embed).build());
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
    public CompletableFuture<ReadonlyMessage> edit(@NotNull String messageId, @NotNull org.javacord.api.entity.message.Message message) {
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
    public CompletableFuture<ReadonlyMessage> edit(@NotNull String messageId, @NotNull org.javacord.api.entity.message.embed.Embed embed) {
        return edit(messageId, WebhookEmbedBuilder.fromJavacord(embed).build());
    }
}
