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

package club.minnced.discord.webhook.send;

import club.minnced.discord.webhook.IOUtil;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class WebhookMessage { //TODO: Docs

    protected final String username, avatarUrl, content, nonce;
    protected final List<WebhookEmbed> embeds;
    protected final boolean isTTS;
    protected final MessageAttachment[] attachments;

    protected WebhookMessage(final String username, final String avatarUrl, final String content, final String nonce,
                             final List<WebhookEmbed> embeds, final boolean isTTS,
                             final MessageAttachment[] files) {
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.nonce = nonce;
        this.embeds = embeds;
        this.isTTS = isTTS;
        this.attachments = files;
    }

    @NotNull
    public static WebhookMessage from(@NotNull ReadonlyMessage message) {
        Objects.requireNonNull(message, "Message");
        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setAvatarUrl(message.getAuthor().getAvatar());
        builder.setUsername(message.getAuthor().getName());
        builder.setContent(message.getContent());
        builder.setTTS(message.isTTS());
        builder.addEmbeds(message.getEmbeds());
        return builder.build();
    }

    // forcing first embed as we expect at least one entry (Effective Java 3rd. Edition - Item 53)
    @NotNull
    public static WebhookMessage embeds(@NotNull WebhookEmbed first, @NotNull WebhookEmbed... embeds) {
        Objects.requireNonNull(embeds, "Embeds");
        for (WebhookEmbed e : embeds) {
            Objects.requireNonNull(e);
        }
        List<WebhookEmbed> list = new ArrayList<>(1 + embeds.length);
        list.add(first);
        Collections.addAll(list, embeds);
        return new WebhookMessage(null, null, null, null, list, false, null);
    }

    @NotNull
    public static WebhookMessage embeds(@NotNull Collection<WebhookEmbed> embeds) {
        Objects.requireNonNull(embeds, "Embeds");
        embeds.forEach(Objects::requireNonNull);
        return new WebhookMessage(null, null, null, null, new ArrayList<>(embeds), false, null);
    }

    @NotNull
    public static WebhookMessage files(@NotNull Map<String, ?> attachments) {
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
        return new WebhookMessage(null, null, null, null, null, false, files);
    }

    // forcing first pair as we expect at least one entry (Effective Java 3rd. Edition - Item 53)
    @NotNull
    public static WebhookMessage files(@NotNull String name1, @NotNull Object data1, @NotNull Object... attachments) {
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
        return new WebhookMessage(null, null, null, null, null, false, files);
    }

    public boolean isFile() {
        return attachments != null;
    }

    @NotNull
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
                builder.addFormDataPart("file" + i, attachment.getName(), new IOUtil.OctetBody(attachment.getData()));
            }
            return builder.addFormDataPart("payload_json", payload.toString()).build();
        }
        return RequestBody.create(IOUtil.JSON, payload.toString());
    }

    @NotNull
    private static MessageAttachment convertAttachment(@NotNull String name, @NotNull Object data) {
        Objects.requireNonNull(name, "Name");
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
}
