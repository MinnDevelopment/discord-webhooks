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

package club.minnced.discord.webhook.receive;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONPropertyName;
import org.json.JSONString;

/**
 * Readonly POJO of a discord user
 */
public class ReadonlyUser implements JSONString {
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

    /**
     * The id of this user
     *
     * @return The id
     */
    public long getId() {
        return id;
    }

    /**
     * The 4 digit discriminator of this user
     * <br>This is show in the client after the {@code #} when viewing profiles.
     *
     * @return The discriminator
     */
    public String getDiscriminator() {
        return String.format("%04d", discriminator);
    }

    /**
     * Whether this is a bot or not, webhook authors are always bots.
     *
     * @return True, if this is a bot
     */
    public boolean isBot() {
        return bot;
    }

    /**
     * The name of this user, this is the username and not the guild-specific nickname.
     *
     * @return The name of this user
     */
    @NotNull
    @JSONPropertyName("username")
    public String getName() {
        return name;
    }

    /**
     * The avatar id of this user, or {@code null} if no avatar is set.
     *
     * @return The avatar id
     */
    @Nullable
    @JSONPropertyName("avatar_id")
    public String getAvatarId() {
        return avatar;
    }

    /**
     * JSON representation of this user
     *
     * @return THe JSON representation of this user
     */
    @Override
    public String toString() {
        return toJSONString();
    }

    @Override
    public String toJSONString() {
        return new JSONObject(this).toString();
    }
}
