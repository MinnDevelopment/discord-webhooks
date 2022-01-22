/*
 * Copyright 2018-2020 Florian Spieﬂ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package club.minnced.discord.webhook.send.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Menu components that can be placed inside a {@link LayoutComponent}
 *
 * @see LayoutComponent#addComponent(ActionComponent)
 */

public class SelectMenu implements ActionComponent{

    private final String customID;
    private final List<SelectOption> options;
    private final String placeholder;
    private final int minValues;
    private final int maxValues;
    private boolean disabled;

    public SelectMenu(@NotNull String customID, @NotNull Collection<SelectOption> options, @Nullable String placeholder, int minValues, int maxValues, boolean disabled) {
        this.customID = customID;
        this.options = new ArrayList<>(options);
        this.placeholder = placeholder;
        this.minValues = minValues;
        this.maxValues = maxValues;
        this.disabled = disabled;
    }

    /**
     * A select menu with the defined id and options
     * @param customID the dev-defined id
     * @param options the options in the dropdown menu
     * @return A select menu with the defined id and options
     */
    public static SelectMenu of(@NotNull String customID, @NotNull SelectOption... options) {
        return new SelectMenu(customID, Arrays.asList(options), null, 1, 1, false);
    }

    /**
     * A select menu with the defined id, placeholder and options
     * @param customID the dev-defined id
     * @param placeholder the placeholder text if nothing is selected
     * @param options the options in the dropdown menu
     * @return A select menu with the defined id, placeholder and options
     */
    public static SelectMenu of(@NotNull String customID, @Nullable String placeholder, @NotNull SelectOption... options) {
        return new SelectMenu(customID, Arrays.asList(options), placeholder, 1, 1, false);
    }

    @Nullable
    @Override
    public String getCustomID() {
        return customID;
    }

    @NotNull
    @Override
    public Type getType() {
        return Type.SELECT_MENU;
    }

    @Override
    public void withDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean isDisabled() {
        return this.disabled;
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
