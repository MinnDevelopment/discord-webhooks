package club.minnced.discord.webhook.exception;

public class HttpException extends RuntimeException {
    public HttpException(String message) {
        super(message);
    }
}
