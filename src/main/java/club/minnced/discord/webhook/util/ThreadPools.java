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

package club.minnced.discord.webhook.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public class ThreadPools { // internal utils
    public static ScheduledExecutorService getDefaultPool(long id, ThreadFactory factory, boolean isDaemon) {
        return Executors.newSingleThreadScheduledExecutor(factory == null ? new DefaultWebhookThreadFactory(id, isDaemon) : factory);
    }

    public static final class DefaultWebhookThreadFactory implements ThreadFactory {
        private final long id;
        private final boolean isDaemon;

        public DefaultWebhookThreadFactory(long id, boolean isDaemon) {
            this.id = id;
            this.isDaemon = isDaemon;
        }

        @Override
        public Thread newThread(Runnable r) {
            final Thread thread = new Thread(r, "Webhook-RateLimit Thread WebhookID: " + id);
            thread.setDaemon(isDaemon);
            return thread;
        }
    }
}
