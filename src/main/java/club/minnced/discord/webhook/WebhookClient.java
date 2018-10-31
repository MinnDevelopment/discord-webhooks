/*
 * Copyright 2018-2019 Florian Spieß
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

import club.minnced.discord.webhook.exception.HttpException;
import club.minnced.discord.webhook.receive.EntityFactory;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.*;

public class WebhookClient implements AutoCloseable { //TODO: Flag to disable message receive
    public static final String WEBHOOK_URL = "https://discordapp.com/api/v7/webhooks/%s/%s?wait=%s";
    public static final String USER_AGENT = "Webhook(https://github.com/MinnDevelopment/discord-webhooks | 0.1.0)";
    public static final Logger LOG = LoggerFactory.getLogger(WebhookClient.class);

    protected final String url;
    protected final long id;
    protected final OkHttpClient client;
    protected final ScheduledExecutorService pool;
    protected final Bucket bucket;
    protected final BlockingQueue<Request> queue;
    protected final boolean parseMessage;
    protected volatile boolean isQueued;
    protected boolean isShutdown;

    protected WebhookClient(
            final long id, final String token, final boolean parseMessage,
            final OkHttpClient client, final ScheduledExecutorService pool) {
        this.client = client;
        this.id = id;
        this.parseMessage = parseMessage;
        this.url = String.format(WEBHOOK_URL, Long.toUnsignedString(id), token, parseMessage);
        this.pool = pool;
        this.bucket = new Bucket();
        this.queue = new LinkedBlockingQueue<>();
        this.isQueued = false;
    }

    public long getId() {
        return id;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull WebhookMessage message) {
        Objects.requireNonNull(message, "WebhookMessage");
        return execute(message.getBody());
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull File file) {
        Objects.requireNonNull(file, "File");
        return send(file, file.getName());
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull File file, @NotNull String fileName) {
        return send(new WebhookMessageBuilder().addFile(fileName, file).build());
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull byte[] data, @NotNull String fileName) {
        return send(new WebhookMessageBuilder().addFile(fileName, data).build());
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull InputStream data, @NotNull String fileName) {
        return send(new WebhookMessageBuilder().addFile(fileName, data).build());
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull WebhookEmbed[] embeds) {
        return send(WebhookMessage.embeds(Arrays.asList(embeds)));
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull WebhookEmbed first, @NotNull WebhookEmbed... embeds) {
        return send(WebhookMessage.embeds(first, embeds));
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull Collection<WebhookEmbed> embeds) {
        return send(WebhookMessage.embeds(embeds));
    }

    @NotNull
    public CompletableFuture<ReadonlyMessage> send(@NotNull String content) {
        Objects.requireNonNull(content, "Content");
        content = content.trim();
        if (content.isEmpty())
            throw new IllegalArgumentException("Cannot send an empty message");
        if (content.length() > 2000)
            throw new IllegalArgumentException("Content may not exceed 2000 characters");
        return execute(newBody(new JSONObject().put("content", content).toString()));
    }

    @Override
    public void close() {
        isShutdown = true;
        pool.shutdown();
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void finalize() {
        if (!isShutdown)
            LOG.warn("Detected unclosed WebhookClient! Did you forget to close it?");
    }

    protected void checkShutdown() {
        if (isShutdown)
            throw new RejectedExecutionException("Cannot send to closed client!");
    }

    @NotNull
    protected static RequestBody newBody(String object) {
        return RequestBody.create(IOUtil.JSON, object);
    }

    @NotNull
    protected CompletableFuture<ReadonlyMessage> execute(RequestBody body) {
        checkShutdown();
        return queueRequest(body);
    }

    @NotNull
    protected static HttpException failure(Response response) throws IOException {
        final InputStream stream = IOUtil.getBody(response);
        final String responseBody = stream == null ? "" : new String(IOUtil.readAllBytes(stream));

        return new HttpException("Request returned failure " + response.code() + ": " + responseBody);
    }

    @NotNull
    protected CompletableFuture<ReadonlyMessage> queueRequest(RequestBody body) {
        final boolean wasQueued = isQueued;
        isQueued = true;
        CompletableFuture<ReadonlyMessage> callback = new CompletableFuture<>();
        Request req = new Request(callback, body);
        enqueuePair(req);
        if (!wasQueued)
            backoffQueue();
        return callback;
    }

    @NotNull
    protected okhttp3.Request newRequest(RequestBody body) {
        return new okhttp3.Request.Builder()
                .url(url)
                .method("POST", body)
                .header("accept-encoding", "gzip")
                .header("user-agent", USER_AGENT)
                .build();
    }

    protected void backoffQueue() {
        pool.schedule(this::drainQueue, bucket.retryAfter(), TimeUnit.MILLISECONDS);
    }

    protected void drainQueue() {
        while (!queue.isEmpty()) {
            final Request pair = queue.peek();
            executePair(pair);
        }
        isQueued = false;
    }

    private boolean enqueuePair(@Async.Schedule Request pair) {
        return queue.add(pair);
    }

    private void executePair(@Async.Execute Request req) {
        if (req.future.isCancelled()) {
            queue.poll();
            return;
        }

        final okhttp3.Request request = newRequest(req.body);
        try (Response response = client.newCall(request).execute()) {
            bucket.update(response);
            if (response.code() == Bucket.RATE_LIMIT_CODE) {
                backoffQueue();
                return;
            }
            else if (!response.isSuccessful()) {
                final HttpException exception = failure(response);
                LOG.error("Sending a webhook message failed with non-OK http response", exception);
                queue.poll().future.completeExceptionally(exception);
                return;
            }
            ReadonlyMessage message = null;
            if (parseMessage) {
                InputStream body = IOUtil.getBody(response);
                JSONObject json = IOUtil.toJSON(body);
                message = EntityFactory.makeMessage(json);
            }
            queue.poll().future.complete(message);
            if (bucket.isRateLimit()) {
                backoffQueue();
                return;
            }
        }
        catch (JSONException | IOException e) {
            LOG.error("There was some error while sending a webhook message", e);
            queue.poll().future.completeExceptionally(e);
        }
    }

    protected static final class Bucket {
        public static final int RATE_LIMIT_CODE = 429;
        public long resetTime;
        public int remainingUses;
        public int limit = Integer.MAX_VALUE;

        public synchronized boolean isRateLimit() {
            if (retryAfter() <= 0)
                remainingUses = limit;
            return remainingUses <= 0;
        }

        public synchronized long retryAfter() {
            return resetTime - System.currentTimeMillis();
        }

        private synchronized void handleRatelimit(Response response, long current) throws IOException {
            final String retryAfter = response.header("Retry-After");
            long delay;
            if (retryAfter == null) {
                InputStream stream = IOUtil.getBody(response);
                final JSONObject body = IOUtil.toJSON(stream);
                delay = body.getLong("retry_after");
            }
            else {
                delay = Long.parseLong(retryAfter);
            }
            resetTime = current + delay;
        }

        private synchronized void update0(Response response) throws IOException {
            final long current = System.currentTimeMillis();
            final boolean is429 = response.code() == RATE_LIMIT_CODE;
            if (is429) {
                handleRatelimit(response, current);
            }
            else if (!response.isSuccessful()) {
                LOG.debug("Failed to update buckets due to unsuccessful response with code: {} and body: \n{}",
                          response.code(), new IOUtil.Lazy(() -> new String(IOUtil.readAllBytes(IOUtil.getBody(response)))));
                return;
            }
            remainingUses = Integer.parseInt(response.header("X-RateLimit-Remaining"));
            limit = Integer.parseInt(response.header("X-RateLimit-Limit"));
            final String date = response.header("Date");

            if (date != null && !is429) {
                final long reset = Long.parseLong(response.header("X-RateLimit-Reset")); //epoch seconds
                OffsetDateTime tDate = OffsetDateTime.parse(date, DateTimeFormatter.RFC_1123_DATE_TIME);
                final long delay = tDate.toInstant().until(Instant.ofEpochSecond(reset), ChronoUnit.MILLIS);
                resetTime = current + delay;
            }
        }

        public void update(Response response) {
            try {
                update0(response);
            }
            catch (Exception ex) {
                LOG.error("Could not read http response", ex);
            }
        }
    }

    private static final class Request {
        private final CompletableFuture<ReadonlyMessage> future;
        private final RequestBody body;

        public Request(CompletableFuture<ReadonlyMessage> future, RequestBody body) {
            this.future = future;
            this.body = body;
        }
    }
}
