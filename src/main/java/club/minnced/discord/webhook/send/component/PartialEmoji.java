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
import org.json.JSONObject;
import org.json.JSONString;

public class PartialEmoji implements JSONString {

    private final String name;
    private final long ID;
    private final boolean animated;

    private PartialEmoji(@NotNull String name, @NotNull String ID, boolean animated) {
        this(name, Long.parseLong(ID), animated);
    }


    private PartialEmoji(@NotNull String name, long ID, boolean animated) {
        this.name = name;
        this.ID = ID;
        this.animated = animated;
    }

    /**
     * Emoji with only necessary features required by webhooks
     * @param name
     *        the name of the emoji
     * @param id
     *        the id of the emoji
     * @param animated
     *        true if the emoji is animated
     * @return An instance of Partial emoji with the provided info
     */
    @NotNull
    public static PartialEmoji of(@NotNull String name, long id, boolean animated) {
        return new PartialEmoji(name, id, animated);
    }

    /**
     * Emoji with only necessary features required by webhooks
     * @param name
     *        the name of the emoji
     * @param id
     *        the id of the emoji
     * @param animated
     *        true if the emoji is animated
     * @return An instance of Partial emoji with the provided info
     */
    @NotNull
    public static PartialEmoji of(@NotNull String name, String id, boolean animated) {
        return new PartialEmoji(name, id, animated);
    }

    /**
     * @return The name of the emoji
     */
    public String getName() {
        return name;
    }

    /**
     * @return The snowflake id of the emoji
     */
    public long getID() {
        return ID;
    }

    /**
     * @return true if the emoji is animated
     */
    public boolean isAnimated() {
        return animated;
    }

    @Override
    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("id", this.ID);
        json.put("name", this.name);
        json.put("animated", this.animated);
        return json.toString();
    }
}
