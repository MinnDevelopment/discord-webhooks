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

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;

/**
 * Utility for various I/O operations used within library internals
 */
public class IOUtil { //TODO: test json
    /**
     * application/json
     */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    /** application/octet-stream */
    public static final MediaType OCTET = MediaType.parse("application/octet-stream; charset=utf-8");
    /** Empty byte-array, used for {@link #readAllBytes(java.io.InputStream)} */
    public static final byte[] EMPTY_BYTES = new byte[0];

    private static final CompletableFuture[] EMPTY_FUTURES = new CompletableFuture[0];

    /**
     * Reads all bytes from an {@link java.io.InputStream}
     *
     * @param  stream
     *         The InputStream
     *
     * @throws IOException
     *         If some I/O error occurs
     *
     * @return {@code byte[]} containing all bytes of the stream
     */
    @NotNull
    public static byte[] readAllBytes(@NotNull InputStream stream) throws IOException {
        int count = 0, pos = 0;
        byte[] output = EMPTY_BYTES;
        byte[] buf = new byte[1024];
        while ((count = stream.read(buf)) > 0) {
            if (pos + count >= output.length) {
                byte[] tmp = output;
                output = new byte[pos + count];
                System.arraycopy(tmp, 0, output, 0, tmp.length);
            }

            for (int i = 0; i < count; i++) {
                output[pos++] = buf[i];
            }
        }
        return output;
    }

    /**
     * Helper method which handles gzip encoded response bodies
     *
     * @param  req
     *         {@link okhttp3.Response} instance
     *
     * @throws IOException
     *         If some I/O error occurs
     *
     * @return {@link java.io.InputStream} representing the response body
     */
    @Nullable
    public static InputStream getBody(@NotNull okhttp3.Response req) throws IOException {
        List<String> encoding = req.headers("content-encoding");
        ResponseBody body = req.body();
        if (!encoding.isEmpty() && body != null) {
            return new GZIPInputStream(body.byteStream());
        }
        return body != null ? body.byteStream() : null;
    }

    /**
     * Converts an {@link java.io.InputStream} to a {@link org.json.JSONObject}
     *
     * @param  input
     *         The {@link java.io.InputStream}
     *
     * @throws org.json.JSONException
     *         If parsing fails
     *
     * @return {@link org.json.JSONObject} for the provided input
     */
    @NotNull
    public static JSONObject toJSON(@NotNull InputStream input) {
        return new JSONObject(new JSONTokener(input));
    }

    /**
     * Converts a list of futures in a future of a list.
     *
     * @param list
     *         The list of futures to flatten
     * @param <T>
     *         Component type of the list
     *
     * @return A future that will be completed with the resulting list
     */
    @NotNull
    public static <T> CompletableFuture<List<T>> flipFuture(@NotNull List<CompletableFuture<T>> list) {
        List<T> result = new ArrayList<>(list.size());
        List<CompletableFuture<Void>> updatedStages = new ArrayList<>(list.size());

        list.stream()
            .map(it -> it.thenAccept(result::add))
            .forEach(updatedStages::add);

        CompletableFuture<Void> tracker = CompletableFuture.allOf(updatedStages.toArray(EMPTY_FUTURES));
        CompletableFuture<List<T>> future = new CompletableFuture<>();

        tracker.thenRun(() -> future.complete(result)).exceptionally((e) -> {
            future.completeExceptionally(e);
            return null;
        });

        return future;
    }

    /**
     * Supplier that can throw checked-exceptions
     *
     * @param <T>
     *        The component type
     */
    public interface SilentSupplier<T> {
        @Nullable
        T get() throws Exception;
    }

    /**
     * Lazy evaluation for logging complex objects
     *
     * <h1>Example</h1>
     * {@code LOG.debug("Suspicious json found", new Lazy(() -> json.toString()));}
     */
    public static class Lazy {
        private final SilentSupplier<?> supply;

        public Lazy(SilentSupplier<?> supply) {
            this.supply = supply;
        }

        @NotNull
        @Override
        public String toString() {
            try {
                return String.valueOf(supply.get());
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * Wrapper for an {@link #OCTET} request body
     */
    public static class OctetBody extends RequestBody {
        private final byte[] data;

        public OctetBody(@NotNull byte[] data) {
            this.data = data;
        }

        @Override
        public MediaType contentType() {
            return OCTET;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            sink.write(data);
        }
    }
}
