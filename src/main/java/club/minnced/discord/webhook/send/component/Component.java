package club.minnced.discord.webhook.send.component;

import org.json.JSONString;

public interface Component extends JSONString {

    /**
     * The type of the component
     * <p>(1 for action row)
     * <p>(2 for button)
     * <p>(3 for select menu)
     * @return The type of the component
     */
    int getType();

}
