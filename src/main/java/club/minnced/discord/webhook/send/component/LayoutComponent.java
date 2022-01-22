/*
 * Copyright 2018-2020 Florian Spieﬂ
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

import java.util.List;

/**
 * A container for multiple {@link ActionComponent}
 * A LayoutComponent cannot contain another LayoutComponent
 * 
 * @see club.minnced.discord.webhook.send.WebhookMessageBuilder#addComponents(LayoutComponent...)
 */

public interface LayoutComponent extends Component {

    /**
     * Maximum allowed layout components (action rows) in a message
     */
    int MAX_COMPONENTS = 5;

    /**
     * @return The action components (buttons and select menus)
     */
    @NotNull List<ActionComponent> getComponents();

    /**
     * Creates an action row with the given list of components
     *
     * @param  component
     *         The component to be added to the layout component
     * @throws IllegalStateException
     *         If a select menu is added with any other component in total
     * @throws IllegalStateException
     *         If more than {@value LayoutComponent#MAX_COMPONENTS} buttons are added in the same action row in total
     * @return This instance of action component for chaining
     */
    @NotNull LayoutComponent addComponent(ActionComponent component);

}
