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

import club.minnced.discord.webhook.util.Validate;
import org.jetbrains.annotations.NotNull;
import org.json.JSONString;

/**
 * Components are a framework for adding interactive elements to the messages your webhooks send.
 */
public interface Component extends JSONString, Validate {
	/**
	 * Type enum representing the individual types of components.
	 * <br>This includes both {@link ComponentLayout ComponentLayouts} and {@link ComponentElement ComponentElements}.
	 * 
	 * @return The type of the component
	 */
	@NotNull
	Type getType();

	/**
	 * All currently available component types.
	 */
	enum Type {
		ACTION_ROW(1),
		BUTTON(2, 5),
		STRING_SELECT(3),
// Not currently usable in messages.
//		TEXT_INPUT(4),
// TODO: Add this later
//		USER_SELECT(5),
//		ROLE_SELECT(6),
//		MENTIONABLE_SELECT(7),
//		CHANNEL_SELECT(8),
		;

		private final int id;
		private final int maxPerLayout;

		Type(int id) {
			this.id = id;
			this.maxPerLayout = 1;
		}

		Type(int id, int maxPerLayout) {
			this.id = id;
			this.maxPerLayout = maxPerLayout;
		}

		/**
		 * The raw type number used by the API.
		 *
		 * @return Integer used by discord to determine the type of component
		 */
		public int getId() {
			return this.id;
		}

		/**
		 * The maximum number of components of this type that can be added to a single layout.
		 *
		 * @return The maximum number of components of this type that can be added to a single layout
		 */
		public int getMaxPerLayout() {
			return this.maxPerLayout;
		}

	}
}