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

import java.util.Collection;
import java.util.List;

/**
 * Container for multiple {@link ActionComponent ActionComponents}.
 * A LayoutComponent cannot contain another LayoutComponent
 *
 * @see club.minnced.discord.webhook.send.WebhookMessageBuilder#addComponents(ComponentLayout...)
 */
public interface ComponentLayout extends Component {
	/**
	 * The currently applied {@link ActionComponent ActionComponents}.
	 *
	 * @return The action components (buttons and select menus)
	 */
	@NotNull List<ComponentElement> getComponents();

	/**
	 * Creates an action row with the given list of components
	 *
	 * @param  components
	 *         The component to be added to the layout component
	 *
	 * @throws java.lang.NullPointerException
	 * 	       If null is provided
	 *
	 * @return This instance of action component for chaining
	 */
	@NotNull ComponentLayout addComponents(@NotNull Collection<? extends ComponentElement> components);

}