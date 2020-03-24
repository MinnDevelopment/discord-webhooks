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

package club.minnced.discord.webhook.exception;

import okhttp3.Headers;
import org.jetbrains.annotations.NotNull;


/**
 * Exception thrown in case of unexpected non-2xx HTTP response.
 */
public class HttpException extends RuntimeException {

    private final int code;
    private final String body;
    private final Headers headers;

    public HttpException(int code, @NotNull String body, @NotNull Headers headers) {
        super("Request returned failure " + code + ": " + body);
        this.body = body;
        this.code = code;
        this.headers = headers;
    }

    /**
     * The HTTP status code
     *
     * @return The status code
     */
    public int getCode() {
        return code;
    }

    /**
     * The body of HTTP response
     *
     * @return The body
     */
    @NotNull
    public String getBody() {
        return body;
    }

    /**
     * The HTTP headers. Useful to check content-type or rate limit buckets.
     *
     * @return {@link okhttp3.Headers}
     */
    @NotNull
    public Headers getHeaders() {
        return headers;
    }
}
