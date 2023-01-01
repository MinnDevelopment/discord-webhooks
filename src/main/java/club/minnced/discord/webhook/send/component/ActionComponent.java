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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interactive components that can be inserted inside a {@link ComponentLayout}
 *
 * @see ComponentLayout#addComponents(java.util.Collection)
 */
public interface ActionComponent extends ComponentElement {
	int MAX_CUSTOM_ID_LENGTH = 100;

	/**
	 * The custom id of the component.
	 * <br>This can be used for handling interactions with this component.
	 *
	 * @return Custom id of the component, or null if not applicable
	 */
	@Nullable
	String getCustomId();

	/**
	 * Changes the disabled status of button.
	 * <br>Note: This does not reflect updating a message in discord.
	 * You must still use {@link club.minnced.discord.webhook.WebhookClient#edit(long, club.minnced.discord.webhook.send.WebhookMessage)}.
	 *
	 * @param  disabled
	 *         use true to disable button
	 *
	 * @return Updated component instance
	 */
	@NotNull
	ActionComponent setDisabled(boolean disabled);

	/**
	 * Whether this component is disabled (not usable).
	 *
	 * @return true if the component is disabled
	 */
	boolean isDisabled();

}