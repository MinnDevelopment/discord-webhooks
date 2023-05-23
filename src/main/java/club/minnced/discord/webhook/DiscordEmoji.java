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

package club.minnced.discord.webhook;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONString;

import java.util.Objects;

/**
 * Emoji class used for components.
 *
 * <p>The {@link #toString()} method returns the message content serialized format, e.g. {@code <:name:id>} for custom emoji.
 */
public class DiscordEmoji implements JSONString {
	private final String name;
	private final long id;
	private final boolean animated;

	private DiscordEmoji(@NotNull String name, long id, boolean animated) {
		this.name = name;
		this.id = id;
		this.animated = animated;
	}

	/**
	 * Emoji instance for custom emoji, with an id and optional animations.
	 * <br>Webhooks are limited to emoji from the same guild.
	 *
	 * @param  name
	 *         the name of the emoji
	 * @param  id
	 *         the id of the emoji
	 * @param  animated
	 *         true if the emoji is animated
	 *
	 * @throws java.lang.NullPointerException
	 *         If null is provided
	 *
	 * @return An instance of emoji, usable in components
	 */
	@NotNull
	public static DiscordEmoji custom(@NotNull String name, long id, boolean animated) {
		return new DiscordEmoji(Objects.requireNonNull(name), id, animated);
	}

	/**
	 * Emoji instance for unicode codepoints.
	 * <br>This does not support aliases such as {@code :smiley:}.
	 * You must use the correct unicode characters.
	 *
	 * @param  codepoints
	 *         The unicode codepoints
	 *
	 * @return An instance of emoji, usable in components
	 */
	@NotNull
	public static DiscordEmoji unicode(@NotNull String codepoints) {
		return new DiscordEmoji(Objects.requireNonNull(codepoints), 0, false);
	}

	/**
	 * The emoji name, or unicode codepoints
	 *
	 * @return The name of the emoji
	 */
	public String getName() {
		return name;
	}

	/**
	 * The snowflake id of the custom emoji, or {@code 0} for unicode
	 *
	 * @return The snowflake id of the emoji
	 */
	public long getId() {
		return id;
	}

	/**
	 * Whether this is an animated custom emoji.
	 *
	 * @return true if the emoji is animated
	 */
	public boolean isAnimated() {
		return animated;
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		json.put("name", this.name);
		if (id != 0) {
			json.put("id", this.id);
			json.put("animated", this.animated);
		}
		return json.toString();
	}

	@Override
	public String toString() {
		if (id == 0)
			return name;
		else
			return (animated ? "<a:" : "<:") + name + ":" + id + ">";
	}
}