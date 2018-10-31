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

package club.minnced.discord.webhook.receive;

import org.jetbrains.annotations.NotNull;

public class ReadonlyAttachment {
    private final String url;
    private final String proxyUrl;
    private final String fileName;
    private final int width, height;
    private final int size;
    private final long id;

    public ReadonlyAttachment(
            @NotNull String url, @NotNull String proxyUrl, @NotNull String fileName,
            int width, int height, int size, long id) {
        this.url = url;
        this.proxyUrl = proxyUrl;
        this.fileName = fileName;
        this.width = width;
        this.height = height;
        this.size = size;
        this.id = id;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    @NotNull
    public String getProxyUrl() {
        return proxyUrl;
    }

    @NotNull
    public String getFileName() {
        return fileName;
    }

    public int getSize() {
        return size;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getId() {
        return id;
    }
}
