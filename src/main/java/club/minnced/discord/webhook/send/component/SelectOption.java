package club.minnced.discord.webhook.send.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONPropertyIgnore;
import org.json.JSONString;

public class SelectOption implements JSONString {

    private final String label;
    private final String value;
    private final String description;
    private PartialEmoji emoji;
    private final boolean def;

    public SelectOption(@NotNull String label, @NotNull String value, @Nullable String description, boolean def) {
        this.label = label;
        this.value = value;
        this.description = description;
        this.def = def;
    }

    public static SelectOption of(@NotNull String label, @NotNull String value) {
        return new SelectOption(label, value, null, false);
    }

    public SelectOption withEmoji(PartialEmoji emoji) {
        this.emoji = emoji;
        return this;
    }

    @NotNull
    @JSONPropertyIgnore
    public String getLabel() {
        return label;
    }

    @NotNull
    @JSONPropertyIgnore
    public String getValue() {
        return value;
    }

    @Nullable
    @JSONPropertyIgnore
    public String getDescription() {
        return description;
    }

    @Nullable
    @JSONPropertyIgnore
    public PartialEmoji getEmoji() {
        return emoji;
    }

    @JSONPropertyIgnore
    public boolean isDef() {
        return def;
    }

    @Override
    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("label", this.label);
        json.put("value", this.value);
        if (this.description != null)
            json.put("description", this.description);
        if (this.emoji != null)
            json.put("emoji", this.emoji);
        json.put("default", this.def);
        return json.toString();
    }
}
