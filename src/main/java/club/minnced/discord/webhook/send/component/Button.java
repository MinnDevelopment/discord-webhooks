package club.minnced.discord.webhook.send.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONPropertyIgnore;

import java.net.URL;

public class Button implements ActionComponent {

    private final ButtonStyle style;
    private final String label, customID, url;
    private final boolean disabled;
    private PartialEmoji emoji;

    public Button(@NotNull ButtonStyle style, @NotNull String label, @Nullable String customID, @Nullable String url, boolean disabled) {
        this.style = style;
        this.label = label;
        this.customID = customID;
        this.url = url;
        this.disabled = disabled;
    }

    public static Button primary(@NotNull String customID, @NotNull String label) {
        return new Button(ButtonStyle.PRIMARY, label, customID, null, false);
    }

    public static Button success(@NotNull String customID, @NotNull String label) {
        return new Button(ButtonStyle.SUCCESS, label, customID, null, false);
    }

    public static Button secondary(@NotNull String customID, @NotNull String label) {
        return new Button(ButtonStyle.SECONDARY, label, customID, null, false);
    }

    public static Button destructive(@NotNull String customID, @NotNull String label) {
        return new Button(ButtonStyle.DANGER, label, customID, null, false);
    }

    public static Button link(@NotNull URL url, @NotNull String label) {
        return new Button(ButtonStyle.LINK, label, null, url.toString(), false);
    }

    public Button withEmoji(@NotNull PartialEmoji emoji) {
        this.emoji = emoji;
        return this;
    }

    @NotNull
    @JSONPropertyIgnore
    public ButtonStyle getStyle() {
        return style;
    }

    @NotNull
    @JSONPropertyIgnore
    public String getLabel() {
        return label;
    }

    @Nullable
    @JSONPropertyIgnore
    @Override
    public String getCustomID() {
        return customID;
    }

    @Nullable
    @JSONPropertyIgnore
    public String getUrl() {
        return url;
    }

    @JSONPropertyIgnore
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("type", this.getType());
        json.put("style", this.style.VALUE);
        json.put("label", this.label);
        if (this.customID != null)
            json.put("custom_id", this.customID);
        if (this.url != null)
            json.put("url", this.url);
        json.put("disabled", this.disabled);
        json.put("emoji", this.emoji);
        return json.toString();
    }

    @Override
    public int getType() {
        return 2;
    }

    public enum ButtonStyle {
        PRIMARY(1),
        SECONDARY(2),
        SUCCESS(3),
        DANGER(4),
        LINK(5);

        ButtonStyle(int value) {
            this.VALUE = value;
        }

        public final int VALUE;
    }
}
