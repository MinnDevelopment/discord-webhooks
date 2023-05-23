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

package club.minnced.discord.webhook.send.component.button;

import club.minnced.discord.webhook.DiscordEmoji;
import club.minnced.discord.webhook.send.component.ActionComponent;
import club.minnced.discord.webhook.send.component.ComponentLayout;
import club.minnced.discord.webhook.send.component.EmojiHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

/**
 * Button components that can be placed inside a {@link ComponentLayout}
 *
 * @see ComponentLayout#addComponents(java.util.Collection)
 */
public class Button implements ActionComponent, EmojiHolder<Button> {

	public static final int MAX_BUTTONS = 5;
	public static final int MAX_LABEL_LENGTH = 80;

	private final Style style;
	private String label, customId, url;
	private boolean disabled;
	private DiscordEmoji emoji;

	/**
	 * Creates a new button instance for the provided style.
	 * <br>You must still add a label or emoji to make this button valid.
	 *
	 * @param style
	 *        The {@link Style} for this button
	 * @param customIdOrUrl
	 *        Either a custom ID used for interactions, or a link for a link button
	 */
	public Button(@NotNull Style style, @NotNull String customIdOrUrl) {
		this.style = style;
		this.label = "";

		if (style == Style.LINK)
			this.url = customIdOrUrl;
		else
			this.customId = customIdOrUrl;
	}

	/**
	 * The {@link Style} used by this button
	 *
	 * @return The style of the button
	 */
	@NotNull
	public Style getStyle() {
		return style;
	}

	/**
	 * The label shown to the user
	 *
	 * @return The label/button text of the button
	 */
	@NotNull
	public String getLabel() {
		return label;
	}

	/**
	 * The custom id used for handling of interactions
	 *
	 * @return The custom id of the button, or null if this is a link button
	 */
	@Nullable
	@Override
	public String getCustomId() {
		return customId;
	}

	/**
	 * The external URL used for this button, only applicable for link buttons.
	 *
	 * @return The url the button links to, or null if this is not a link button
	 */
	@Nullable
	public String getUrl() {
		return url;
	}

	@NotNull
	@Override
	public Type getType() {
		return Type.BUTTON;
	}

	@Override
	public boolean isDisabled() {
		return disabled;
	}

	@NotNull
	@Override
	public Button setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	@NotNull
	@Override
	public Button setEmoji(@Nullable DiscordEmoji emoji) {
		this.emoji = emoji;
		return this;
	}

	/**
	 * The display label used to show the button to the user.
	 *
	 * @param label
	 *        The label to use, should be at most {@value #MAX_LABEL_LENGTH} codepoints long
	 *
	 * @return The current button instance
	 */
	@NotNull
	public Button setLabel(@NotNull String label) {
		this.label = label;
		return this;
	}

	@Override
	@Nullable
	public DiscordEmoji getEmoji() {
		return this.emoji;
	}

	@Override
	public boolean isValid() {
		if (style != Style.LINK && (customId.isEmpty() || customId.length() > MAX_CUSTOM_ID_LENGTH))
			return false;
		if (label.isEmpty())
			return emoji != null;
		return label.codePoints().count() <= MAX_LABEL_LENGTH;
	}

	@NotNull
	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		json.put("type", this.getType().getId());
		json.put("style", this.style.value);
		json.put("label", this.label);
		if (this.customId != null)
			json.put("custom_id", this.customId);
		if (this.url != null)
			json.put("url", this.url);
		json.put("disabled", this.disabled);
		json.put("emoji", this.emoji);
		return json.toString();
	}

	/**
	 * The currently available button styles
	 */
	public enum Style {
		PRIMARY(1),
		SECONDARY(2),
		SUCCESS(3),
		DANGER(4),
		LINK(5),
		;

		private final int value;

		Style(int value) {
			this.value = value;
		}

		/**
		 * Raw int used by the API to represent the style
		 *
		 * @return Integer used by discord to determine the button style
		 */
		public int getValue() {
			return value;
		}
	}
}