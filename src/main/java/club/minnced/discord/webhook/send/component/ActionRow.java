package club.minnced.discord.webhook.send.component;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionRow implements LayoutComponent {

    private final List<ActionComponent> components;

    public ActionRow(@NotNull List<ActionComponent> components) {
        int buttonCount = 0;
        boolean hasSelectMenu = false;
        for (ActionComponent component : components) {
            if (component instanceof Button) buttonCount++;
            if (component instanceof SelectMenu) hasSelectMenu = true;
        }
        if (hasSelectMenu && components.size() > 1)
            throw new IllegalStateException("An action row containing a select menu cant have have more than 1 component");
        if (buttonCount > 5)
            throw new IllegalStateException("An action row cant contain more than 5 buttons");
        this.components = components;
    }

    public static ActionRow of(ActionComponent... components) {
        if (components == null || components.length == 0)
            return new ActionRow(new ArrayList<>());
        return new ActionRow(Arrays.asList(components));
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public List<ActionComponent> getComponents() {
        return this.components;
    }

    @Override
    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("type", this.getType());
        json.put("components", this.components);
        return json.toString();
    }
}
