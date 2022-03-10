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

import club.minnced.discord.webhook.WebhookClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

/**
 * Used to dynamically handle errors for webhook requests in {@link WebhookClient}
 * <br>If not explicitly configured, this uses {@link #DEFAULT}.
 *
 * @see WebhookClient#setDefaultErrorHandler(WebhookErrorHandler)
 * @see WebhookClient#setErrorHandler(WebhookErrorHandler)
 */
@FunctionalInterface
public interface WebhookErrorHandler {
    /**
     * The default error handling which simply logs the exception using SLF4J
     */
    WebhookErrorHandler DEFAULT = (client, message, throwable) -> LoggerFactory.getLogger(WebhookClient.class).error(message, throwable);

    /**
     * Implements error handling, must not throw anything!
     *
     * @param client
     *        The {@link WebhookClient} instance which encountered the exception
     * @param message
     *        The context message used for logging
     * @param throwable
     *        The encountered exception, or null if the error is only a context message
     */
    void handle(@NotNull WebhookClient client, @NotNull String message, @Nullable Throwable throwable);
}
