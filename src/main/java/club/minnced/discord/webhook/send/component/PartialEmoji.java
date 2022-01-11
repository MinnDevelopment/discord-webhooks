package club.minnced.discord.webhook.send.component;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONPropertyIgnore;
import org.json.JSONString;

public class PartialEmoji implements JSONString {

    private final String name;
    private final long id;
    private final boolean animated;

    public PartialEmoji(@NotNull String name, @NotNull String id, boolean animated) {
        this(name, Long.parseLong(id), animated);
    }

    public PartialEmoji(@NotNull String name, long id, boolean animated) {
        this.name = name;
        this.id  = id;
        this.animated = animated;
    }

    @JSONPropertyIgnore
    public String getName() {
        return name;
    }

    @JSONPropertyIgnore
    public long getId() {
        return id;
    }

    @JSONPropertyIgnore
    public boolean isAnimated() {
        return animated;
    }

    @Override
    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("name", this.name);
        json.put("animated", this.animated);
        return json.toString();
    }
}
