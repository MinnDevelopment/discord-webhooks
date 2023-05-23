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

package club.minnced.discord.webhook;

import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessage;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Collection of webhooks, useful for subscriber pattern.
 * <br>Register several webhooks and broadcast to all of them with a single call.
 *
 * <p>Webhook created by the cluster through {@link #buildWebhook(long, String)}
 * are initialized with defaults specified by
 * <ul>
 * <li>{@link #setDefaultHttpClient(okhttp3.OkHttpClient)}</li>
 * <li>{@link #setDefaultExecutorService(java.util.concurrent.ScheduledExecutorService)}</li>
 * <li>{@link #setDefaultThreadFactory(java.util.concurrent.ThreadFactory)}</li>
 * <li>{@link #setDefaultDaemon(boolean)}</li>
 * </ul>
 */
public class WebhookCluster implements AutoCloseable { //TODO: tests
    protected final List<WebhookClient> webhooks;
    protected OkHttpClient defaultHttpClient;
    protected ScheduledExecutorService defaultPool;
    protected ThreadFactory threadFactory;
    protected AllowedMentions allowedMentions = AllowedMentions.all();
    protected boolean isDaemon;

    /**
     * Creates a new WebhookCluster with the provided clients
     *
     * @param  initialClients
     *         Clients to add to the cluster
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     */
    public WebhookCluster(@NotNull Collection<? extends WebhookClient> initialClients) {
        Objects.requireNonNull(initialClients, "List");
        webhooks = new ArrayList<>(initialClients.size());
        for (WebhookClient client : initialClients) {
            addWebhooks(client);
        }
    }

    /**
     * Creates a webhook cluster with the specified capacity.
     * <br>Note that this capacity can be expanded dynamically by
     * building/adding more clients.
     *
     * @param  initialCapacity
     *         The initial capacity
     *
     * @throws java.lang.IllegalArgumentException
     *         If the capacity is illegal
     *
     * @see    java.util.ArrayList#ArrayList(int)
     */
    public WebhookCluster(int initialCapacity) {
        webhooks = new ArrayList<>(initialCapacity);
    }

    /**
     * Default initializes a new WebhookCluster.
     * <br>This cluster will be empty.
     */
    public WebhookCluster() {
        webhooks = new ArrayList<>();
    }

    // Default builder values

    /**
     * Configures the default http client that will be used to build
     * {@link club.minnced.discord.webhook.WebhookClient} instances.
     *
     * @param  defaultHttpClient
     *         The default http client
     *
     * @return WebhookCluster instance for chaining convenience
     *
     * @see    club.minnced.discord.webhook.WebhookClientBuilder#setHttpClient(okhttp3.OkHttpClient)
     */
    @NotNull
    public WebhookCluster setDefaultHttpClient(@Nullable OkHttpClient defaultHttpClient) {
        this.defaultHttpClient = defaultHttpClient;
        return this;
    }

    /**
     * Configures the default executor service that will be used to build
     * {@link club.minnced.discord.webhook.WebhookClient} instances.
     *
     * @param  executorService
     *         The default executor service
     *
     * @return WebhookCluster instance for chaining convenience
     *
     * @see    club.minnced.discord.webhook.WebhookClientBuilder#setExecutorService(java.util.concurrent.ScheduledExecutorService)
     */
    @NotNull
    public WebhookCluster setDefaultExecutorService(@Nullable ScheduledExecutorService executorService) {
        this.defaultPool = executorService;
        return this;
    }

    /**
     * Configures the default thread factory that will be used to build
     * {@link club.minnced.discord.webhook.WebhookClient} instances.
     *
     * @param  factory
     *         The default thread factory
     *
     * @return WebhookCluster instance for chaining convenience
     *
     * @see    club.minnced.discord.webhook.WebhookClientBuilder#setThreadFactory(java.util.concurrent.ThreadFactory)
     */
    @NotNull
    public WebhookCluster setDefaultThreadFactory(@Nullable ThreadFactory factory) {
        this.threadFactory = factory;
        return this;
    }

    /**
     * The mention whitelist to use by default for every webhook client.
     * <br>See {@link AllowedMentions} for more details.
     *
     * @param  allowedMentions
     *         The default mention whitelist
     *
     * @return WebhookCluster instance for chaining convenience
     */
    @NotNull
    public WebhookCluster setAllowedMentions(@Nullable AllowedMentions allowedMentions) {
        this.allowedMentions = allowedMentions == null ? AllowedMentions.all() : allowedMentions;
        return this;
    }

    /**
     * Configures whether {@link club.minnced.discord.webhook.WebhookClient} instances should be daemon by default.
     *
     * @param  isDaemon
     *         True, if clients should be daemon
     *
     * @return WebhookCluster instance for chaining convenience
     *
     * @see    club.minnced.discord.webhook.WebhookClientBuilder#setDaemon(boolean)
     */
    @NotNull
    public WebhookCluster setDefaultDaemon(boolean isDaemon) {
        this.isDaemon = isDaemon;
        return this;
    }

    // Webhook creation/add/remove

    /**
     * Builds a {@link club.minnced.discord.webhook.WebhookClient} instance with the provided
     * components and specified default configurations.
     *
     * @param  id
     *         The id of the webhook
     * @param  token
     *         The token of the webhook
     *
     * @throws java.lang.NullPointerException
     *         If the token is null
     *
     * @return WebhookCluster instance for chaining convenience
     */
    @NotNull
    public WebhookCluster buildWebhook(long id, @NotNull String token) {
        this.webhooks.add(newBuilder(id, token).build());
        return this;
    }

    /**
     * Creates a {@link club.minnced.discord.webhook.WebhookClientBuilder} instance with the provided
     * components and specified default configurations.
     * <br>The webhook client must be explicitly added to the cluster after building.
     *
     * @param  id
     *         The id of the webhook
     * @param  token
     *         The token of the webhook
     *
     * @throws java.lang.NullPointerException
     *         If the token is null
     *
     * @return WebhookClientBuilder instance
     */
    @NotNull
    public WebhookClientBuilder newBuilder(long id, @NotNull String token) {
        WebhookClientBuilder builder = new WebhookClientBuilder(id, token);
        builder.setExecutorService(defaultPool)
               .setHttpClient(defaultHttpClient)
               .setThreadFactory(threadFactory)
               .setAllowedMentions(allowedMentions)
               .setDaemon(isDaemon);
        return builder;
    }

    /**
     * Adds the provided webhooks to the cluster.
     *
     * @param  clients
     *         The clients to add
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     * @throws java.lang.IllegalArgumentException
     *         If at least one of the clients is already shutdown
     *
     * @return WebhookCluster instance for chaining convenience
     */
    @NotNull
    public WebhookCluster addWebhooks(@NotNull WebhookClient... clients) {
        Objects.requireNonNull(clients, "Clients");
        for (WebhookClient client : clients) {
            Objects.requireNonNull(client, "Client");
            if (client.isShutdown)
                throw new IllegalArgumentException("One of the provided WebhookClients has been closed already!");
            webhooks.add(client);
        }
        return this;
    }

    /**
     * Adds the provided webhooks to the cluster.
     *
     * @param  clients
     *         The clients to add
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     * @throws java.lang.IllegalArgumentException
     *         If at least one of the clients is already shutdown
     *
     * @return WebhookCluster instance for chaining convenience
     */
    @NotNull
    public WebhookCluster addWebhooks(@NotNull Collection<WebhookClient> clients) {
        Objects.requireNonNull(clients, "Clients");
        for (WebhookClient client : clients) {
            Objects.requireNonNull(client, "Client");
            if (client.isShutdown)
                throw new IllegalArgumentException("One of the provided WebhookClients has been closed already!");
            webhooks.add(client);
        }
        return this;
    }

    /**
     * Removes the provided webhooks from the cluster.
     *
     * @param  clients
     *         The clients to remove
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     *
     * @return WebhookCluster instance for chaining convenience
     */
    @NotNull
    public WebhookCluster removeWebhooks(@NotNull WebhookClient... clients) {
        Objects.requireNonNull(clients, "Clients");
        webhooks.removeAll(Arrays.asList(clients));
        return this;
    }

    /**
     * Removes the provided webhooks from the cluster.
     *
     * @param  clients
     *         The clients to remove
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     *
     * @return WebhookCluster instance for chaining convenience
     */
    @NotNull
    public WebhookCluster removeWebhooks(@NotNull Collection<WebhookClient> clients) {
        Objects.requireNonNull(clients, "Clients");
        webhooks.removeAll(clients);
        return this;
    }

    /**
     * Removes webhooks from the cluster based on the specified filter.
     *
     * @param  predicate
     *         The filter to decide whether to remove a client or not
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     *
     * @return {@link java.util.List} of the removed webhooks
     */
    @NotNull
    public List<WebhookClient> removeIf(@NotNull Predicate<WebhookClient> predicate) {
        Objects.requireNonNull(predicate, "Predicate");
        List<WebhookClient> clients = new ArrayList<>();
        for (WebhookClient client : webhooks) {
            if (predicate.test(client))
                clients.add(client);
        }
        removeWebhooks(clients);
        return clients;
    }

    /**
     * Closes and removes webhook clients based on the specified filter.
     *
     * @param  predicate
     *         The filter to decide whether to close and remove the client
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     *
     * @return {@link java.util.List} of the removed webhooks
     */
    @NotNull
    public List<WebhookClient> closeIf(@NotNull Predicate<WebhookClient> predicate) {
        Objects.requireNonNull(predicate, "Filter");
        List<WebhookClient> clients = new ArrayList<>();
        for (WebhookClient client : webhooks) {
            if (predicate.test(client))
                clients.add(client);
        }
        removeWebhooks(clients);
        clients.forEach(WebhookClient::close);
        return clients;
    }

    /**
     * Unmodifiable list of currently registered clients
     *
     * @return List of clients
     */
    @NotNull
    public List<WebhookClient> getWebhooks() {
        return Collections.unmodifiableList(new ArrayList<>(webhooks));
    }

    // Broadcasting / Multicasting

    /**
     * Sends a message to a filtered set of clients.
     *
     * <p><b>This will override the default {@link AllowedMentions} of the client!</b>
     *
     * @param  filter
     *         The filter to decide whether a client should be targeted
     * @param  message
     *         The message to send
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     *
     * @return List of futures for each client execution
     */
    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> multicast(@NotNull Predicate<WebhookClient> filter, @NotNull WebhookMessage message) {
        Objects.requireNonNull(filter, "Filter");
        Objects.requireNonNull(message, "Message");
        final RequestBody body = message.getBody();
        final List<CompletableFuture<ReadonlyMessage>> callbacks = new ArrayList<>();
        for (WebhookClient client : webhooks) {
            if (filter.test(client))
                callbacks.add(client.execute(body));
        }
        return callbacks;
    }

    /**
     * Sends a message to all registered clients.
     *
     * <p><b>This will override the default {@link AllowedMentions} of the client!</b>
     *
     * @param  message
     *         The message to send
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     *
     * @return List of futures for each client execution
     */
    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull WebhookMessage message) {
        Objects.requireNonNull(message, "Message");
        RequestBody body = message.getBody();
        final List<CompletableFuture<ReadonlyMessage>> callbacks = new ArrayList<>(webhooks.size());
        for (WebhookClient webhook : webhooks) {
            callbacks.add(webhook.execute(body));
            if (message.isFile()) // for files we have to make new data sets
                body = message.getBody();
        }
        return callbacks;
    }

    /**
     * Sends a message to all registered clients.
     *
     * @param  first
     *         The first embed to send
     * @param  embeds
     *         Optional additional embeds to send, up to 10
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     *
     * @return List of futures for each client execution
     */
    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull WebhookEmbed first, @NotNull WebhookEmbed... embeds) {
        List<WebhookEmbed> list = new ArrayList<>(embeds.length + 2);
        list.add(first);
        Collections.addAll(list, embeds);
        return broadcast(list);
    }

    /**
     * Sends a message to all registered clients.
     *
     * @param  embeds
     *         The embeds to send, up to 10
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     *
     * @return List of futures for each client execution
     */
    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull Collection<WebhookEmbed> embeds) {
        return webhooks.stream()
                .map(w -> w.sendEmbeds(embeds))
                .collect(Collectors.toList());
    }

    /**
     * Sends a message to all registered clients.
     *
     * @param  content
     *         The message to send
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     *
     * @return List of futures for each client execution
     */
    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull String content) {
        Objects.requireNonNull(content, "Content");
        if (content.length() > 2000)
            throw new IllegalArgumentException("Content may not exceed 2000 characters!");
        return webhooks.stream()
                .map(w -> w.send(content))
                .collect(Collectors.toList());
    }

    /**
     * Sends a message to all registered clients.
     *
     * @param  file
     *         The file to send
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     * @throws UncheckedIOException
     *         If an I/O error occurs
     *
     * @return List of futures for each client execution
     */
    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull File file) {
        Objects.requireNonNull(file, "File");
        return broadcast(file.getName(), file);
    }

    /**
     * Sends a message to all registered clients.
     *
     * @param  fileName
     *         The alternative file name to use
     * @param  file
     *         The file to send
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     * @throws UncheckedIOException
     *         If an I/O error occurs
     *
     * @return List of futures for each client execution
     */
    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull String fileName, @NotNull File file) {
        Objects.requireNonNull(file, "File");
        if (file.length() > 10)
            throw new IllegalArgumentException("Provided File exceeds the maximum size of 8MB!");
        try {
            return broadcast(fileName, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Sends a message to all registered clients.
     *
     * @param  fileName
     *         The alternative file name to use
     * @param  data
     *         The data to send
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     * @throws UncheckedIOException
     *         If an I/O error occurs
     *
     * @return List of futures for each client execution
     */
    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull String fileName, @NotNull InputStream data) {
        try {
            return broadcast(fileName, IOUtil.readAllBytes(data));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Sends a message to all registered clients.
     *
     * @param  fileName
     *         The alternative file name to use
     * @param  data
     *         The data to send
     *
     * @throws java.lang.NullPointerException
     *         If provided with null
     *
     * @return List of futures for each client execution
     */
    @NotNull
    public List<CompletableFuture<ReadonlyMessage>> broadcast(@NotNull String fileName, @NotNull byte[] data) {
        Objects.requireNonNull(data, "Data");
        if (data.length > 10)
            throw new IllegalArgumentException("Provided data exceeds the maximum size of 8MB!");
        return webhooks.stream()
                .map(w -> w.send(data, fileName))
                .collect(Collectors.toList());
    }

    /**
     * Performs cascade closing on current webhook clients,
     * all clients will be closed and removed after this returns.
     * <br>The cluster may still be used after calls to this method occurred.
     */
    @Override
    public void close() {
        webhooks.forEach(WebhookClient::close);
        webhooks.clear();
    }
}
