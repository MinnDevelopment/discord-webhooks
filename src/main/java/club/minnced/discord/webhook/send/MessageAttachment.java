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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Internal representation of attachments for outgoing messages
 */
public class MessageAttachment {
    private final String name;
    private final byte[] data;

    MessageAttachment(@NotNull String name, @NotNull byte[] data) {
        this.name = name;
        this.data = data;
    }

    MessageAttachment(@NotNull String name, @NotNull InputStream stream) throws IOException {
        this.name = name;
        try (InputStream data = stream) {
            this.data = IOUtil.readAllBytes(data);
        }
    }

    MessageAttachment(@NotNull String name, @NotNull File file) throws IOException {
        this(name, new FileInputStream(file));
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public byte[] getData() {
        return data;
    }
}
