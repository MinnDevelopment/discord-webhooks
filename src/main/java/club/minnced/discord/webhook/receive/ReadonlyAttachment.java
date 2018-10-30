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
