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

package club.minnced.discord.webhook.message;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;

public class WebhookMessage { //TODO: Tests
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType OCTET = MediaType.parse("application/octet-stream");

    protected final String username, avatarUrl, content;
    protected final List<WebhookEmbed> embeds;
    protected final boolean isTTS;
    protected final MessageAttachment[] attachments;

    protected WebhookMessage(final String username, final String avatarUrl, final String content,
                             final List<WebhookEmbed> embeds, final boolean isTTS,
                             final MessageAttachment[] files) {
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.embeds = embeds;
        this.isTTS = isTTS;
        this.attachments = files;
    }

    // forcing first embed as we expect at least one entry (Effective Java 3rd. Edition - Item 53)
    public static WebhookMessage embeds(WebhookEmbed first, WebhookEmbed... embeds) {
        Objects.requireNonNull(embeds, "Embeds");
        for (WebhookEmbed e : embeds) {
            Objects.requireNonNull(e);
        }
        List<WebhookEmbed> list = new ArrayList<>(1 + embeds.length);
        list.add(first);
        Collections.addAll(list, embeds);
        return new WebhookMessage(null, null, null, list, false, null);
    }

    public static WebhookMessage embeds(Collection<WebhookEmbed> embeds) {
        Objects.requireNonNull(embeds, "Embeds");
        embeds.forEach(Objects::requireNonNull);
        return new WebhookMessage(null, null, null, new ArrayList<>(embeds), false, null);
    }

    public static WebhookMessage files(Map<String, ?> attachments) {
        Objects.requireNonNull(attachments, "Attachments");

        int fileAmount = attachments.size();
        if (fileAmount == 0)
            throw new IllegalArgumentException("Cannot build an empty message");
        if (fileAmount > WebhookMessageBuilder.MAX_FILES)
            throw new IllegalArgumentException("Cannot add more than " + WebhookMessageBuilder.MAX_FILES + " files to a message");
        Set<? extends Map.Entry<String, ?>> entries = attachments.entrySet();
        MessageAttachment[] files = new MessageAttachment[fileAmount];
        int i = 0;
        for (Map.Entry<String, ?> attachment : entries) {
            String name = attachment.getKey();
            Objects.requireNonNull(name, "Name");
            Object data = attachment.getValue();
            files[i++] = convertAttachment(name, data);
        }
        return new WebhookMessage(null, null, null, null, false, files);
    }

    // forcing first pair as we expect at least one entry (Effective Java 3rd. Edition - Item 53)
    public static WebhookMessage files(String name1, Object data1, Object... attachments) {
        Objects.requireNonNull(name1, "Name");
        Objects.requireNonNull(data1, "Data");
        Objects.requireNonNull(attachments, "Attachments");
        if (attachments.length % 2 != 0)
            throw new IllegalArgumentException("Must provide even number of varargs arguments");
        int fileAmount = 1 + attachments.length / 2;
        if (fileAmount > WebhookMessageBuilder.MAX_FILES)
            throw new IllegalArgumentException("Cannot add more than " + WebhookMessageBuilder.MAX_FILES + " files to a message");
        MessageAttachment[] files = new MessageAttachment[fileAmount];
        files[0] = convertAttachment(name1, data1);
        for (int i = 0, j = 1; i < attachments.length; j++, i += 2) {
            Object name = attachments[i];
            Object data = attachments[i + 1];
            if (!(name instanceof String))
                throw new IllegalArgumentException("Provided arguments must be pairs for (String, Data). Expected String and found " + (name == null ? null : name.getClass().getName()));
            files[j] = convertAttachment((String) name, data);
        }
        return new WebhookMessage(null, null, null, null, false, files);
    }

    public boolean isFile() {
        return attachments != null;
    }

    public RequestBody getBody() {
        final JSONObject payload = new JSONObject();
        if (content != null)
            payload.put("content", content);
        if (embeds != null && !embeds.isEmpty()) {
            final JSONArray array = new JSONArray();
            for (WebhookEmbed embed : embeds) {
                array.put(embed);
            }
            payload.put("embeds", array);
        }
        if (avatarUrl != null)
            payload.put("avatar_url", avatarUrl);
        if (username != null)
            payload.put("username", username);
        payload.put("tts", isTTS);
        if (isFile()) {
            final MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            for (int i = 0; i < attachments.length; i++) {
                final MessageAttachment attachment = attachments[i];
                if (attachment == null)
                    break;
                builder.addFormDataPart("file" + i, attachment.getName(), new OctetBody(attachment.getData()));
            }
            return builder.addFormDataPart("payload_json", payload.toString()).build();
        }
        return RequestBody.create(JSON, payload.toString());
    }

    private static MessageAttachment convertAttachment(String name, Object data) {
        Objects.requireNonNull(data, "Data");
        try {
            MessageAttachment a;
            if (data instanceof File)
                a = new MessageAttachment(name, (File) data);
            else if (data instanceof InputStream)
                a = new MessageAttachment(name, (InputStream) data);
            else if (data instanceof byte[])
                a = new MessageAttachment(name, (byte[]) data);
            else
                throw new IllegalArgumentException("Provided arguments must be pairs for (String, Data). Unexpected data type " + data.getClass().getName());
            return a;
        }
        catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private static class OctetBody extends RequestBody {
        private final InputStream in;

        private OctetBody(InputStream in) {
            this.in = in;
        }

        @Override
        public MediaType contentType() {
            return OCTET;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            ByteBuffer buf = ByteBuffer.allocate(1024);
            while (in.available() > 0) {
                buf.position(in.read(buf.array()));
                sink.write(buf);
            }
        }
    }
}
