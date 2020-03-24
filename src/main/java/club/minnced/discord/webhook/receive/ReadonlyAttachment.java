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

package club.minnced.discord.webhook.receive;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONPropertyName;
import org.json.JSONString;

/**
 * Readonly message attachment meta-data.
 * <br>This does not actually contain the file but only meta-data
 * useful to retrieve the actual attachment.
 */
public class ReadonlyAttachment implements JSONString {
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

    /**
     * The URL for this attachment
     *
     * @return The url
     */
    @NotNull
    public String getUrl() {
        return url;
    }

    /**
     * The proxy url for this attachment, this is used by the client
     * to generate previews of images.
     *
     * @return The proxy url
     */
    @NotNull
    @JSONPropertyName("proxy_url")
    public String getProxyUrl() {
        return proxyUrl;
    }

    /**
     * The name of this attachment
     *
     * @return The file name
     */
    @NotNull
    @JSONPropertyName("filename")
    public String getFileName() {
        return fileName;
    }

    /**
     * The approximated size of this embed in bytes
     *
     * @return The approximated size in bytes
     */
    public int getSize() {
        return size;
    }

    /**
     * Width of the attachment, this is only relevant to images and videos
     *
     * @return Width of this image, or -1 if not an image or video
     */
    public int getWidth() {
        return width;
    }

    /**
     * Height of the attachment, this is only relevant to images and videos
     *
     * @return Height of this image, or -1 if not an image or video
     */
    public int getHeight() {
        return height;
    }

    /**
     * The id of this attachment
     *
     * @return The idi
     */
    public long getId() {
        return id;
    }

    /**
     * JSON representation of this attachment
     *
     * @return The JSON representation
     */
    @Override
    public String toString() {
        return toJSONString();
    }

    @Override
    public String toJSONString() {
        return new JSONObject(this).toString();
    }
}
