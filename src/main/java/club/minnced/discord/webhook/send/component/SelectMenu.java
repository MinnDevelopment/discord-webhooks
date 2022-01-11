package club.minnced.discord.webhook.send.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SelectMenu implements ActionComponent{

    private final String customID;
    private final List<SelectOption> options;
    private final String placeholder;
    private final int minValues;
    private final int maxValues;
    private final boolean disabled;

    public SelectMenu(@NotNull String customID, @NotNull Collection<SelectOption> options, @Nullable String placeholder, int minValues, int maxValues, boolean disabled) {
        this.customID = customID;
        this.options = new ArrayList<>(options);
        this.placeholder = placeholder;
        this.minValues = minValues;
        this.maxValues = maxValues;
        this.disabled = disabled;
    }

    public static SelectMenu of(@NotNull String customID, @NotNull SelectOption... options) {
        return new SelectMenu(customID, Arrays.asList(options), null, 1, 1, false);
    }

    public static SelectMenu of(@NotNull String customID, @Nullable String placeholder, @NotNull SelectOption... options) {
        return new SelectMenu(customID, Arrays.asList(options), placeholder, 1, 1, false);

    }

    @Nullable
    @Override
    public String getCustomID() {
        return customID;
    }

    @Override
    public int getType() {
        return 3;
    }

    @Override
    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("type", this.getType());
        json.put("custom_id", this.customID);
        json.put("options", this.options);
        if (placeholder != null)
            json.put("placeholder", this.placeholder);
        json.put("min_values", this.minValues);
        json.put("max_values", this.maxValues);
        json.put("disabled", this.disabled);
        return json.toString();
    }

}
