/*
 * Copyright 2018-2020 Florian Spie�
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

public interface SingleEmojiContainer <T>{

	/**
	 * Adds an emoji to the container. Replaces it if it already exists
	 * @param emoji
	 *        the emoji to add
	 * @return this instance of container for chaining
	 */
	@NotNull T withEmoji(@NotNull PartialEmoji emoji);

	/**
	 * @return The emoji inside the container, null if there is no emoji
	 */
	@Nullable PartialEmoji getEmoji();

}