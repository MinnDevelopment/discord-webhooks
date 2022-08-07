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
import org.json.JSONString;

/**
 * Components are a framework for adding interactive elements to the messages your app sends.
 */

public interface Component extends JSONString {

	/**
	 * @return The type of the component
	 */
	@NotNull Type getType();

	enum Type {
		ACTION_ROW(1),
		BUTTON(2),
		SELECT_MENU(3);

		private final int id;

		Type(int id) {
			this.id = id;
		}

		/**
		 * @return Integer used by discord to determine the type of component
		 */
		public int getId() {
			return this.id;
		}

	}

}