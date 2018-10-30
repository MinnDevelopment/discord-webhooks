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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class WebhookMessageBuilder {
    public static final int MAX_FILES = 10;

    protected final StringBuilder content = new StringBuilder();
    protected final List<WebhookEmbed> embeds = new LinkedList<>();
    protected final MessageAttachment[] files = new MessageAttachment[MAX_FILES];
    protected String username, avatarUrl;
    protected boolean isTTS;
    private int fileIndex = 0;

    public boolean isEmpty() {
        return content.length() == 0 && embeds.isEmpty() && fileIndex == 0;
    }

    public int getFileAmount() {
        return fileIndex;
    }

    public WebhookMessageBuilder reset() {
        content.setLength(0);
        embeds.clear();
        resetFiles();
        username = null;
        avatarUrl = null;
        isTTS = false;
        return this;
    }

    public WebhookMessageBuilder resetFiles() {
        for (int i = 0; i < MAX_FILES; i++) {
            files[i] = null;
        }
        fileIndex = 0;
        return this;
    }

    public WebhookMessageBuilder resetEmbeds() {
        this.embeds.clear();
        return this;
    }

    public WebhookMessageBuilder addEmbeds(WebhookEmbed... embeds) {
        Objects.requireNonNull(embeds, "Embeds");
        for (WebhookEmbed embed : embeds) {
            Objects.requireNonNull(embed, "Embed");
            this.embeds.add(embed);
        }
        return this;
    }

    public WebhookMessageBuilder addEmbeds(Collection<WebhookEmbed> embeds) {
        Objects.requireNonNull(embeds, "Embeds");
        for (WebhookEmbed embed : embeds) {
            Objects.requireNonNull(embed, "Embed");
            this.embeds.add(embed);
        }
        return this;
    }

    public WebhookMessageBuilder setContent(String content) {
        if (content != null && content.length() > 2000)
            throw new IllegalArgumentException("Content may not exceed 2000 characters!");
        if (content != null)
            this.content.replace(0, content.length(), content);
        else
            this.content.setLength(0);
        return this;
    }

    public WebhookMessageBuilder append(String content) {
        Objects.requireNonNull(content, "Content");
        if (this.content.length() + content.length() > 2000)
            throw new IllegalArgumentException("Content may not exceed 2000 characters!");
        this.content.append(content);
        return this;
    }

    public WebhookMessageBuilder setUsername(String username) {
        this.username = username == null || username.trim().isEmpty() ? null : username.trim();
        return this;
    }

    public WebhookMessageBuilder setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl == null || avatarUrl.trim().isEmpty() ? null : avatarUrl.trim();
        return this;
    }

    public WebhookMessageBuilder addFile(File file) {
        Objects.requireNonNull(file, "File");
        return addFile(file.getName(), file);
    }

    public WebhookMessageBuilder addFile(String name, File file) {
        Objects.requireNonNull(file, "File");
        Objects.requireNonNull(name, "Name");
        if (!file.exists() || !file.canRead()) throw new IllegalArgumentException("File must exist and be readable");
        if (fileIndex >= MAX_FILES)
            throw new IllegalStateException("Cannot add more than " + MAX_FILES + " attachments to a message");

        try {
            MessageAttachment attachment = new MessageAttachment(name, file);
            files[fileIndex++] = attachment;
            return this;
        }
        catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public WebhookMessageBuilder addFile(String name, byte[] data) {
        Objects.requireNonNull(data, "Data");
        Objects.requireNonNull(name, "Name");
        if (fileIndex >= MAX_FILES)
            throw new IllegalStateException("Cannot add more than " + MAX_FILES + " attachments to a message");

        MessageAttachment attachment = new MessageAttachment(name, data);
        files[fileIndex++] = attachment;
        return this;
    }

    public WebhookMessageBuilder addFile(String name, InputStream data) {
        Objects.requireNonNull(data, "InputStream");
        Objects.requireNonNull(name, "Name");
        if (fileIndex >= MAX_FILES)
            throw new IllegalStateException("Cannot add more than " + MAX_FILES + " attachments to a message");

        try {
            MessageAttachment attachment = new MessageAttachment(name, data);
            files[fileIndex++] = attachment;
            return this;
        }
        catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public WebhookMessageBuilder setTTS(boolean tts) {
        isTTS = tts;
        return this;
    }

    public WebhookMessage build() {
        if (isEmpty())
            throw new IllegalStateException("Cannot build an empty message!");
        return new WebhookMessage(username, avatarUrl, content.toString(), embeds, isTTS, fileIndex == 0 ? null : Arrays.copyOf(files, fileIndex));
    }
}
