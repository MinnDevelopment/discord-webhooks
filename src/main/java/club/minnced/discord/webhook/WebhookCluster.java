/*
 *     Copyright 2015-2018 Austin Keener & Michael Ritter & Florian Spieß
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

package club.minnced.discord.webhook;

import club.minnced.discord.webhook.message.WebhookEmbed;
import club.minnced.discord.webhook.message.WebhookMessage;
import club.minnced.discord.webhook.message.WebhookMessageBuilder;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Predicate;

public class WebhookCluster implements AutoCloseable { //TODO: Message Receive
    protected final List<WebhookClient> webhooks;
    protected OkHttpClient defaultHttpClient;
    protected ScheduledExecutorService defaultPool;
    protected ThreadFactory threadFactory;
    protected boolean isDaemon;

    public WebhookCluster(@NotNull Collection<? extends WebhookClient> initialClients) {
        Objects.requireNonNull(initialClients, "List");
        webhooks = new ArrayList<>(initialClients.size());
        for (WebhookClient client : initialClients) {
            addWebhooks(client);
        }
    }

    public WebhookCluster(int initialCapacity) {
        webhooks = new ArrayList<>(initialCapacity);
    }

    public WebhookCluster() {
        webhooks = new ArrayList<>();
    }

    // Default builder values

    @NotNull
    public WebhookCluster setDefaultHttpClient(@Nullable OkHttpClient defaultHttpClient) {
        this.defaultHttpClient = defaultHttpClient;
        return this;
    }

    @NotNull
    public WebhookCluster setDefaultExecutorService(@Nullable ScheduledExecutorService executorService) {
        this.defaultPool = executorService;
        return this;
    }

    @NotNull
    public WebhookCluster setDefaultThreadFactory(@Nullable ThreadFactory factory) {
        this.threadFactory = factory;
        return this;
    }

    @NotNull
    public WebhookCluster setDefaultDaemon(boolean isDaemon) {
        this.isDaemon = isDaemon;
        return this;
    }

    // Webhook creation/add/remove

    @NotNull
    public WebhookCluster buildWebhook(long id, @NotNull String token) {
        this.webhooks.add(newBuilder(id, token).build());
        return this;
    }

    @NotNull
    public WebhookClientBuilder newBuilder(long id, @NotNull String token) {
        WebhookClientBuilder builder = new WebhookClientBuilder(id, token);
        builder.setExecutorService(defaultPool)
               .setHttpClient(defaultHttpClient)
               .setThreadFactory(threadFactory)
               .setDaemon(isDaemon);
        return builder;
    }

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

    @NotNull
    public WebhookCluster removeWebhooks(@NotNull WebhookClient... clients) {
        Objects.requireNonNull(clients, "Clients");
        webhooks.removeAll(Arrays.asList(clients));
        return this;
    }

    @NotNull
    public WebhookCluster removeWebhooks(@NotNull Collection<WebhookClient> clients) {
        Objects.requireNonNull(clients, "Clients");
        webhooks.removeAll(clients);
        return this;
    }

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

    @NotNull
    public List<WebhookClient> getWebhooks() {
        return Collections.unmodifiableList(new ArrayList<>(webhooks));
    }

    // Broadcasting / Multicasting

    @NotNull
    public List<CompletableFuture<?>> multicast(@NotNull Predicate<WebhookClient> filter, @NotNull WebhookMessage message) {
        Objects.requireNonNull(filter, "Filter");
        Objects.requireNonNull(message, "Message");
        final RequestBody body = message.getBody();
        final List<CompletableFuture<?>> callbacks = new ArrayList<>();
        for (WebhookClient client : webhooks) {
            if (filter.test(client))
                callbacks.add(client.execute(body));
        }
        return callbacks;
    }

    @NotNull
    public List<CompletableFuture<?>> broadcast(@NotNull WebhookMessage message) {
        Objects.requireNonNull(message, "Message");
        RequestBody body = message.getBody();
        final List<CompletableFuture<?>> callbacks = new ArrayList<>(webhooks.size());
        for (WebhookClient webhook : webhooks) {
            callbacks.add(webhook.execute(body));
            if (message.isFile()) // for files we have to make new data sets
                body = message.getBody();
        }
        return callbacks;
    }

    @NotNull
    public List<CompletableFuture<?>> broadcast(@NotNull WebhookEmbed[] embeds) {
        return broadcast(WebhookMessage.embeds(Arrays.asList(embeds)));
    }

    @NotNull
    public List<CompletableFuture<?>> broadcast(@NotNull WebhookEmbed first, @NotNull WebhookEmbed... embeds) {
        return broadcast(WebhookMessage.embeds(first, embeds));
    }

    @NotNull
    public List<CompletableFuture<?>> broadcast(@NotNull Collection<WebhookEmbed> embeds) {
        return broadcast(WebhookMessage.embeds(embeds));
    }

    @NotNull
    public List<CompletableFuture<?>> broadcast(@NotNull String content) {
        Objects.requireNonNull(content, "Content");
        if (content.length() > 2000)
            throw new IllegalArgumentException("Content may not exceed 2000 characters!");
        final RequestBody body = WebhookClient.newBody(new JSONObject().put("content", content).toString());
        final List<CompletableFuture<?>> callbacks = new ArrayList<>(webhooks.size());
        for (WebhookClient webhook : webhooks) {
            callbacks.add(webhook.execute(body));
        }
        return callbacks;
    }

    @NotNull
    public List<CompletableFuture<?>> broadcast(@NotNull File file) {
        Objects.requireNonNull(file, "File");
        return broadcast(file.getName(), file);
    }

    @NotNull
    public List<CompletableFuture<?>> broadcast(@NotNull String fileName, @NotNull File file) {
        Objects.requireNonNull(file, "File");
        if (file.length() > 10)
            throw new IllegalArgumentException("Provided File exceeds the maximum size of 8MB!");
        return broadcast(new WebhookMessageBuilder().addFile(fileName, file).build());
    }

    @NotNull
    public List<CompletableFuture<?>> broadcast(@NotNull String fileName, @NotNull InputStream data) {
        return broadcast(new WebhookMessageBuilder().addFile(fileName, data).build());
    }

    @NotNull
    public List<CompletableFuture<?>> broadcast(@NotNull String fileName, @NotNull byte[] data) {
        Objects.requireNonNull(data, "Data");
        if (data.length > 10)
            throw new IllegalArgumentException("Provided data exceeds the maximum size of 8MB!");
        return broadcast(new WebhookMessageBuilder().addFile(fileName, data).build());
    }

    @Override
    public void close() {
        webhooks.forEach(WebhookClient::close);
        webhooks.clear();
    }
}
