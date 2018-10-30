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

import okhttp3.OkHttpClient;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebhookClientBuilder {
    public static final OkHttpClient.Builder DEFAULT_HTTP_BUILDER = new OkHttpClient.Builder();
    private static final Pattern WEBHOOK_PATTERN = Pattern.compile("(?:https?://)?(?:\\w+\\.)?discordapp\\.com/api(?:/v\\d+)?/webhooks/(\\d+)/([\\w-]+)(?:/(?:\\w+)?)?");

    protected final long id;
    protected final String token;
    protected ScheduledExecutorService pool;
    protected OkHttpClient client;
    protected ThreadFactory threadFactory;
    protected boolean isDaemon;

    public WebhookClientBuilder(final long id, final String token) {
        Objects.requireNonNull(token, "Token");
        this.id = id;
        this.token = token;
    }

    public WebhookClientBuilder(String url) {
        Matcher matcher = WEBHOOK_PATTERN.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Failed to parse webhook URL");
        }

        this.id = Long.parseUnsignedLong(matcher.group(1));
        this.token = matcher.group(2);
    }

    public WebhookClientBuilder setExecutorService(ScheduledExecutorService executorService) {
        this.pool = executorService;
        return this;
    }

    public WebhookClientBuilder setHttpClient(OkHttpClient client) {
        this.client = client;
        return this;
    }

    public WebhookClientBuilder setThreadFactory(ThreadFactory factory) {
        this.threadFactory = factory;
        return this;
    }

    public WebhookClientBuilder setDaemon(boolean isDaemon) {
        this.isDaemon = isDaemon;
        return this;
    }

    public WebhookClient build() {
        OkHttpClient client = this.client == null
                              ? new OkHttpClient()
                              : this.client;
        ScheduledExecutorService pool = this.pool != null
                                        ? this.pool
                                        : Executors.newSingleThreadScheduledExecutor(
                threadFactory == null
                ? new DefaultWebhookThreadFactory()
                : threadFactory);
        return new WebhookClient(id, token, client, pool);
    }

    public final class DefaultWebhookThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            final Thread thread = new Thread(r, "Webhook-RateLimit Thread WebhookID: " + id);
            thread.setDaemon(isDaemon);
            return thread;
        }
    }
}
