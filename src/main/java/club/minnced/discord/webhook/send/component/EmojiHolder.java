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

package club.minnced.discord.webhook.send.component;

import club.minnced.discord.webhook.DiscordEmoji;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Components which can make use of emojis for display purposes.
 *
 * @param <T>
 *        The component subtype
 */
public interface EmojiHolder<T> {
	/**
	 * Use the provided emoji.
	 * <br>Note: This does not reflect updating a message in discord.
	 * You must still use {@link WebhookClient#edit(long, WebhookMessage)}.
	 *
	 * @param  emoji
	 *         the emoji to add, or null to remove emoji
	 *
	 * @return Update object with the provided emoji
	 */
	@NotNull
	T setEmoji(@Nullable DiscordEmoji emoji);

	/**
	 * The emoji used by this object.
	 *
	 * @return The emoji, or null if there is no emoji
	 */
	@Nullable DiscordEmoji getEmoji();

}