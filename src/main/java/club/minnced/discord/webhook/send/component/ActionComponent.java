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

import org.jetbrains.annotations.Nullable;

/**
 * Interactive components that can be inserted inside a {@link LayoutComponent}
 *
 * @see LayoutComponent#addComponent(ActionComponent)
 */
public interface ActionComponent extends Component {

    /**
     * The custom id of the component.
     * <br>This can be used for handling interactions with this component.
     *
     * @return Custom id of the component, or null for link style buttons
     */
    @Nullable String getCustomID();

    /**
     * Changes the disabled status of button
     * @param disabled
     *        use true to disable button
     */
    void withDisabled(boolean disabled);

    /**
     * @return true if the button is disabled
     */
    boolean isDisabled();

}
