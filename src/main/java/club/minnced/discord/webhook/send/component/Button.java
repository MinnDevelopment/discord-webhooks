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

    /**
     * A primary style button with the provided id and label
     * @param customID dev-defined id
     * @param label text on button
     * @return A primary style button with the provided id and label
     */
    public static Button primary(@NotNull String customID, @NotNull String label) {
        return new Button(ButtonStyle.PRIMARY, label, customID, null, false);
    }

    /**
     * A success style button with the provided id and label
     * @param customID dev-defined id
     * @param label text on button
     * @return A success style button with the provided id and label
     */
    public static Button success(@NotNull String customID, @NotNull String label) {
        return new Button(ButtonStyle.SUCCESS, label, customID, null, false);
    }

    /**
     * A secondary style button with the provided id and label
     * @param customID dev-defined id
     * @param label text on button
     * @return A secondary style button with the provided id and label
     */
    public static Button secondary(@NotNull String customID, @NotNull String label) {
        return new Button(ButtonStyle.SECONDARY, label, customID, null, false);
    }

    /**
     * A destructive style button with the provided id and label
     * @param customID dev-defined id
     * @param label text on button
     * @return A destructive style button with the provided id and label
     */
    public static Button destructive(@NotNull String customID, @NotNull String label) {
        return new Button(ButtonStyle.DANGER, label, customID, null, false);
    }

    /**
     * A link style button with the provided id and label
     * @param url The URL to link
     * @param label text on button
     * @return A link style button with the provided link and label
     */
    public static Button link(@NotNull URL url, @NotNull String label) {
        return new Button(ButtonStyle.LINK, label, null, url.toString(), false);
    }

    /**
     * Adds an emoji to the button. Replaces it if it already exists
     * @param emoji the emoji to add
     * @return this instance of button
     */
    public Button withEmoji(@NotNull PartialEmoji emoji) {
        this.emoji = emoji;
        return this;
    }

    /**
     * @return The style of the button
     */
    @NotNull
    @JSONPropertyIgnore
    public ButtonStyle getStyle() {
        return style;
    }

    /**
     * @return The label/button text of the button
     */
    @NotNull
    @JSONPropertyIgnore
    public String getLabel() {
        return label;
    }

    /**
     * @return The dev-defined id of the button
     */
    @Nullable
    @JSONPropertyIgnore
    @Override
    public String getCustomID() {
        return customID;
    }

    /**
     * @return The url the button links to
     */
    @Nullable
    @JSONPropertyIgnore
    public String getUrl() {
        return url;
    }

    /**
     * @return If the button is disabled
     */
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

        /**
         * Discord defined value for the button styles
         */
        public final int VALUE;
    }
}
