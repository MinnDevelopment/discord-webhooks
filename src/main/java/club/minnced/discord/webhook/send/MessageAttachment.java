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

package club.minnced.discord.webhook.send;

import club.minnced.discord.webhook.IOUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Internal representation of attachments for outgoing messages
 */
public class MessageAttachment {
    private static final byte[] empty = new byte[0];
    private final long id;
    private final String name;
    private final byte[] data;

    protected MessageAttachment(long id, @NotNull String name) {
        this.id = id;
        this.name = name;
        this.data = empty;
    }

    MessageAttachment(@NotNull String name, @NotNull byte[] data) {
        this.id = 0;
        this.name = name;
        this.data = data;
    }

    MessageAttachment(@NotNull String name, @NotNull InputStream stream) throws IOException {
        this.id = 0;
        this.name = name;
        try (InputStream data = stream) {
            this.data = IOUtil.readAllBytes(data);
        }
    }

    MessageAttachment(@NotNull String name, @NotNull File file) throws IOException {
        this(name, new FileInputStream(file));
    }

    /**
     * Create an instance of this class with the provided ID and name.
     *
     * <p>This can be used in {@link club.minnced.discord.webhook.send.WebhookMessageBuilder#addFile(MessageAttachment)}
     * to retain existing attachments on a message for an edit request.
     * The name parameter can also be used to rename the attachment.
     *
     * @param  id
     *         The snowflake ID for this attachment (must exist on the message you edit9
     * @param  name
     *         The new name for the attachment, or null to keep existing name
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided ID is not a valid snowflake
     *
     * @return The attachment instance
     */
    @NotNull
    public static MessageAttachment fromId(long id, @Nullable String name) {
        if (id > WebhookMessage.MAX_FILES)
            throw new IllegalArgumentException("MessageAttachment ID must be higher than " + WebhookMessage.MAX_FILES + ".");
        return new MessageAttachment(id, name == null ? "" : name);
    }


    /**
     * Create an instance of this class with the provided ID and name.
     *
     * <p>This can be used in {@link club.minnced.discord.webhook.send.WebhookMessageBuilder#addFile(MessageAttachment)}
     * to retain existing attachments on a message for an edit request.
     *
     * @param  id
     *         The snowflake ID for this attachment (must exist on the message you edit9
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided ID is not a valid snowflake
     *
     * @return The attachment instance
     */
    @NotNull
    public static MessageAttachment fromId(long id) {
        return fromId(id, null);
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public byte[] getData() {
        return data;
    }

    public long getId() {
        return id;
    }

    @NotNull
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        if (!name.isEmpty())
            json.put("name", name);
        if (id != 0)
            json.put("id", id);
        return json;
    }
}
