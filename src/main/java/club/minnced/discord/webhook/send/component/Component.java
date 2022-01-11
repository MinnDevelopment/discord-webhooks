package club.minnced.discord.webhook.send.component;

import org.json.JSONString;

public interface Component extends JSONString {

    int getType();

}
