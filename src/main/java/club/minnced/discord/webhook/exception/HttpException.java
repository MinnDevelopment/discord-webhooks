package club.minnced.discord.webhook.exception;

import org.jetbrains.annotations.NotNull;

public class HttpException extends RuntimeException {
    public HttpException(@NotNull String message) {
        super(message);
    }
}
