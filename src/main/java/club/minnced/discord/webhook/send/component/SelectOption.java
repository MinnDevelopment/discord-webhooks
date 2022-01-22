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
import org.json.JSONString;

public class SelectOption implements JSONString, SingleEmojiContainer<SelectOption> {

    private final String label;
    private final String value;
    private final String description;
    private PartialEmoji emoji;
    private final boolean isDeafault;

    public SelectOption(@NotNull String label, @NotNull String value, @Nullable String description, boolean isDeafault) {
        this.label = label;
        this.value = value;
        this.description = description;
        this.isDeafault = isDeafault;
    }

    /**
     * A SelectOption with the provided label and value
     * @param label User visible label for the option
     * @param value The dev-defined value for the option
     * @return A SelectOption with the provided label and value
     */
    @NotNull
    public static SelectOption of(@NotNull String label, @NotNull String value) {
        return new SelectOption(label, value, null, false);
    }

    /**
     * A SelectOption with the provided label, value and description
     * @param label User visible label for the option
     * @param value The dev-defined value for the option
     * @param description The description for the option
     * @return A SelectOption with the provided label and value and description
     */
    @NotNull
    public static SelectOption of(@NotNull String label, @NotNull String value, String description) {
        return new SelectOption(label, value, description, false);
    }

    /**
     * @return User visible name of the option
     */
    @NotNull
    public String getLabel() {
        return label;
    }

    /**
     * @return The dev-defined value for the option
     */
    @NotNull
    public String getValue() {
        return value;
    }

    /**
     * @return The description for the option
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * @return true if the option is marked as default option
     */
    public boolean isDefaultOption() {
        return isDeafault;
    }

    @Override
    @NotNull
    public SelectOption withEmoji(PartialEmoji emoji) {
        this.emoji = emoji;
        return this;
    }

    @Override
    @Nullable
    public PartialEmoji getEmoji() {
        return emoji;
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
        json.put("default", this.isDeafault);
        return json.toString();
    }
}
