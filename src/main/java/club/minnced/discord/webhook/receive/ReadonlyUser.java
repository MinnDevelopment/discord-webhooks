/*
 * Copyright 2018-2019 Florian Spieß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package club.minnced.discord.webhook.receive;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReadonlyUser {
    private final long id;
    private final short discriminator;
    private final boolean bot;
    private final String name;
    private final String avatar;

    public ReadonlyUser(long id, short discriminator, boolean bot, @NotNull String name, @Nullable String avatar) {
        this.id = id;
        this.discriminator = discriminator;
        this.bot = bot;
        this.name = name;
        this.avatar = avatar;
    }

    public long getId() {
        return id;
    }

    public String getDiscriminator() {
        return String.format("%04d", discriminator);
    }

    public boolean isBot() {
        return bot;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getAvatar() {
        return avatar;
    }
}
