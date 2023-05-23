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

import club.minnced.discord.webhook.DiscordEmoji;
import club.minnced.discord.webhook.send.component.EmojiHolder;
import club.minnced.discord.webhook.util.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONString;

import java.util.Objects;

/**
 * Options used for {@link StringSelectMenu StringSelectMenus}.
 */
public class SelectOption implements JSONString, EmojiHolder<SelectOption>, Validate {
	public static final int MAX_LABEL_LENGTH = 100;
	public static final int MAX_VALUE_LENGTH = 100;
	public static final int MAX_DESCRIPTION_LENGTH = 100;

	private final String value;
	private String label;
	private String description;
	private DiscordEmoji emoji;
	private boolean isDefault;

	/**
	 * Creates a new select option with the provided value.
	 * <br>The value must be unique for each option in a select menu.
	 *
	 * @param label
	 * 	      The label for this option, shown to the user
	 * @param value
	 *        The value for this option, used by interaction handlers
	 */
	public SelectOption(@NotNull String label, @NotNull String value) {
		this.value = Objects.requireNonNull(value, "Value");
		this.label = Objects.requireNonNull(label, "Label");
	}

	/**
	 * The display label for this option.
	 *
	 * @return User visible name of the option
	 */
	@NotNull
	public String getLabel() {
		return label;
	}

	/**
	 * The value used for interactions.
	 *
	 * @return The dev-defined value for the option
	 */
	@NotNull
	public String getValue() {
		return value;
	}

	/**
	 * The description shown below the label.
	 *
	 * @return The description for the option, or null if unset
	 */
	@Nullable
	public String getDescription() {
		return description;
	}

	/**
	 * Whether this option is selected by default.
	 *
	 * @return true if the option is marked as default option
	 */
	public boolean isDefaultOption() {
		return isDefault;
	}

	@Override
	@Nullable
	public DiscordEmoji getEmoji() {
		return emoji;
	}

	/**
	 * Sets the label for this option.
	 *
	 * @param  label
	 * 	       The new label, should be at most {@value #MAX_LABEL_LENGTH} codepoints
	 *
	 * @throws java.lang.NullPointerException
	 * 	       If the provided label is null
	 *
	 * @return The updated option instance
	 */
	@NotNull
	public SelectOption setLabel(@NotNull String label) {
		this.label = Objects.requireNonNull(label, "Label");
		return this;
	}

	/**
	 * Sets the description for this option.
	 *
	 * @param  description
	 * 	       The new description, should be at most {@value #MAX_DESCRIPTION_LENGTH} codepoints
	 *
	 * @return The updated option instance
	 */
	@NotNull
	public SelectOption setDescription(@Nullable String description) {
		this.description = description;
		return this;
	}

	/**
	 * Whether this option is selected by default.
	 *
	 * @param  isDefault
	 *         True to show this option as selected by default
	 *
	 * @return The updated option instance
	 */
	@NotNull
	public SelectOption setDefaultOption(boolean isDefault) {
		this.isDefault = isDefault;
		return this;
	}

	/**
	 * Sets the emoji for this option.
	 *
	 * @param  emoji
	 * 	       The new emoji
	 *
	 * @return The updated option instance
	 */
	@Override
	@NotNull
	public SelectOption setEmoji(@Nullable DiscordEmoji emoji) {
		this.emoji = emoji;
		return this;
	}

	@Override
	public boolean isValid() {
		if (value.isEmpty())
			return false;
		if (label.isEmpty() && emoji == null)
			return false;
		if (label.codePoints().count() > MAX_LABEL_LENGTH)
			return false;
		if (value.length() > MAX_VALUE_LENGTH)
			return false;
		return description == null || description.codePoints().count() <= MAX_DESCRIPTION_LENGTH;
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
		json.put("default", this.isDefault);
		return json.toString();
	}
}