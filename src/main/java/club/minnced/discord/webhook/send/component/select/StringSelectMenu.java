/*
 * Copyright 2018-2020 Florian Spieß
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

package club.minnced.discord.webhook.send.component.select;

import club.minnced.discord.webhook.send.component.ComponentLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Customizable String Select Menu (dropdowns) components that can be placed inside a {@link ComponentLayout}
 *
 * @see ComponentLayout#addComponents(java.util.Collection)
 */
public class StringSelectMenu extends SelectMenu {
	public static final int MAX_OPTIONS = 25;
	public static final int MAX_PLACEHOLDER_LENGTH = 150;

	private final List<SelectOption> options;
	private String placeholder;
	private int minValues;
	private int maxValues;

	/**
	 * Creates a new String Select Menu instance for the provided custom ID.
	 * <br>You must still add options to make this menu valid.
	 *
	 * @param customId
	 *        The custom ID used for interactions
	 */
	public StringSelectMenu(@NotNull String customId) {
		super(customId);
		this.options = new ArrayList<>(MAX_OPTIONS);
	}

	/**
	 * The placeholder text to show when no options are selected.
	 *
	 * @return The placeholder text
	 */
	@NotNull
	public String getPlaceholder() {
		return placeholder;
	}

	/**
	 * The minimum number of options that must be selected.
	 *
	 * @return The minimum number of options
	 */
	public int getMinValues() {
		return minValues;
	}

	/**
	 * The maximum number of options that can be selected.
	 *
	 * @return The maximum number of options
	 */
	public int getMaxValues() {
		return maxValues;
	}

	@NotNull
	@Override
	public Type getType() {
		return Type.STRING_SELECT;
	}

	/**
	 * Set the placeholder, which is shown when no options are selected.
	 *
	 * @param  placeholder
	 * 	       The placeholder text, must be at most {@value #MAX_PLACEHOLDER_LENGTH} codepoints
	 *
	 * @return The updated StringSelectMenu instance
	 */
	@NotNull
	public StringSelectMenu setPlaceholder(@Nullable String placeholder) {
		this.placeholder = placeholder;
		return this;
	}

	/**
	 * Set the minimum number of options that must be selected.
	 *
	 * @param  minValues
	 * 	       The minimum number of options, must be at least 0 and at most {@value #MAX_OPTIONS}
	 *
	 * @return The updated StringSelectMenu instance
	 */
	@NotNull
	public StringSelectMenu setMinValues(int minValues) {
		this.minValues = minValues;
		return this;
	}

	/**
	 * Set the maximum number of options that can be selected.
	 *
	 * @param  maxValues
	 * 	       The maximum number of options, must be at least 1 and at most {@value #MAX_OPTIONS}
	 *
	 * @return The updated StringSelectMenu instance
	 */
	@NotNull
	public StringSelectMenu setMaxValues(int maxValues) {
		this.maxValues = maxValues;
		return this;
	}

	@NotNull
	@Override
	public StringSelectMenu setDisabled(boolean disabled) {
		return (StringSelectMenu) super.setDisabled(disabled);
	}

	@Override
	public boolean isValid() {
		if (getCustomId().isEmpty() || getCustomId().length() > 100)
			return false;
		if (options.isEmpty() || options.size() > MAX_OPTIONS)
			return false;
		if (placeholder != null && placeholder.length() > MAX_PLACEHOLDER_LENGTH)
			return false;
		return options.stream().allMatch(SelectOption::isValid);
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		json.put("type", this.getType().getId());
		json.put("custom_id", this.getCustomId());
		json.put("options", this.options);
		if (placeholder != null)
			json.put("placeholder", this.placeholder);
		json.put("min_values", Math.min(this.minValues, this.options.size()));
		json.put("max_values", Math.min(this.maxValues, this.options.size()));
		json.put("disabled", this.isDisabled());
		return json.toString();
	}
}