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

package club.minnced.discord.webhook.send.component.select;

import club.minnced.discord.webhook.send.component.ActionComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class SelectMenu implements ActionComponent {
    private final String customId;
    private boolean disabled;

    protected SelectMenu(@NotNull String customId) {
        this.customId = Objects.requireNonNull(customId);
    }

    @NotNull
    @Override
    public String getCustomId() {
        return customId;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @NotNull
    @Override
    public SelectMenu setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }
}
