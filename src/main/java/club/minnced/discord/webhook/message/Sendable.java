package club.minnced.discord.webhook.message;

import org.json.JSONString;

public interface Sendable extends JSONString {
    Type getType();

    enum Type {
        EMBED, MESSAGE
    }
}
