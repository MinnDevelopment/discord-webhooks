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

package club.minnced.discord.webhook.send;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Constructs a whitelist of allowed mentions for a message.
 * If any argument in this class is {@code null}, a {@link NullPointerException} will be thrown.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * AllowedMentions mentions = new AllowedMentions()
 *   .withUsers("86699011792191488", "107562988810027008")
 *   .withParseEveryone(false)
 *   .withParseRoles(false);
 *
 * // This will only mention the user with the id 86699011792191488 (Minn#6688)
 * // The @everyone will be ignored since the allowed mentions disabled it.
 * client.send(
 *   new WebhookMessageBuilder()
 *     .setAllowedMentions(mentions)
 *     .setContent("Hello <@86699011792191488>! And hello @everyone else!")
 *     .build()
 * );
 * }</pre>
 *
 * @see WebhookMessageBuilder#setAllowedMentions(AllowedMentions)
 * @see club.minnced.discord.webhook.WebhookClientBuilder#setAllowedMentions(AllowedMentions) WebhookClientBuilder#setAllowedMentions(AllowedMentions)
 *
 * @see #all()
 * @see #none()
 */
public class AllowedMentions implements JSONString {
    /**
     * Parse all mentions.
     *
     * <p>Equivalent:
     * <pre>{@code
     * return new AllowedMentions()
     *     .withParseEveryone(true)
     *     .withParseRoles(true)
     *     .withParseUsers(true);
     * }</pre>
     *
     * @return Every mention type will be parsed.
     */
    public static AllowedMentions all() {
        return new AllowedMentions()
            .withParseEveryone(true)
            .withParseRoles(true)
            .withParseUsers(true);
    }

    /**
     * Disable all mentions.
     *
     * <p>Equivalent:
     * <pre>{@code
     * return new AllowedMentions()
     *     .withParseEveryone(false)
     *     .withParseRoles(false)
     *     .withParseUsers(false);
     * }</pre>
     *
     * @return No mentions will be parsed.
     */
    public static AllowedMentions none() {
        return new AllowedMentions()
            .withParseEveryone(false)
            .withParseRoles(false)
            .withParseUsers(false);
    }

    private boolean parseRoles, parseUsers, parseEveryone;
    private final Set<String> users = new HashSet<>();
    private final Set<String> roles = new HashSet<>();

    /**
     * Whitelist specified users for mention.
     * <br>This will set {@link #withParseUsers(boolean)} to false.
     *
     * @param  userId
     *         The whitelist of users to mention
     *
     * @return AllowedMentions instance with applied whitelist
     */
    @NotNull
    public AllowedMentions withUsers(@NotNull String... userId)
    {
        Collections.addAll(users, userId);
        parseUsers = false;
        return this;
    }

    /**
     * Whitelist specified roles for mention.
     * <br>This will set {@link #withParseRoles(boolean)} to false.
     *
     * @param  roleId
     *         The whitelist of roles to mention
     *
     * @return AllowedMentions instance with applied whitelist
     */
    @NotNull
    public AllowedMentions withRoles(@NotNull String... roleId)
    {
        Collections.addAll(roles, roleId);
        parseRoles = false;
        return this;
    }

    /**
     * Whitelist specified users for mention.
     * <br>This will set {@link #withParseUsers(boolean)} to false.
     *
     * @param  userId
     *         The whitelist of users to mention
     *
     * @return AllowedMentions instance with applied whitelist
     */
    @NotNull
    public AllowedMentions withUsers(@NotNull Collection<String> userId)
    {
        users.addAll(userId);
        parseUsers = false;
        return this;
    }

    /**
     * Whitelist specified roles for mention.
     * <br>This will set {@link #withParseRoles(boolean)} to false.
     *
     * @param  roleId
     *         The whitelist of roles to mention
     *
     * @return AllowedMentions instance with applied whitelist
     */
    @NotNull
    public AllowedMentions withRoles(@NotNull Collection<String> roleId)
    {
        roles.addAll(roleId);
        parseRoles = false;
        return this;
    }

    /**
     * Whether to parse {@code @everyone} or {@code @here} mentions.
     *
     * @param  allowEveryoneMention
     *         True, if {@code @everyone} should be parsed
     *
     * @return AllowedMentions instance with applied parsing rule
     */
    @NotNull
    public AllowedMentions withParseEveryone(boolean allowEveryoneMention)
    {
        parseEveryone = allowEveryoneMention;
        return this;
    }

    /**
     * Whether to parse user mentions.
     * <br>Setting this to {@code true} will clear the whitelist provided by {@link #withUsers(String...)}.
     *
     * @param  allowParseUsers
     *         True, if all user mentions should be parsed
     *
     * @return AllowedMentions instance with applied parsing rule
     */
    @NotNull
    public AllowedMentions withParseUsers(boolean allowParseUsers)
    {
        parseUsers = allowParseUsers;
        if (parseUsers)
            users.clear();
        return this;
    }

    /**
     * Whether to parse role mentions.
     * <br>Setting this to {@code true} will clear the whitelist provided by {@link #withRoles(String...)}.
     *
     * @param  allowParseRoles
     *         True, if all role mentions should be parsed
     *
     * @return AllowedMentions instance with applied parsing rule
     */
    @NotNull
    public AllowedMentions withParseRoles(boolean allowParseRoles)
    {
        parseRoles = allowParseRoles;
        if (parseRoles)
            roles.clear();
        return this;
    }

    @Override
    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("parse", new JSONArray());

        if (!users.isEmpty())
            json.put("users", users);
        else if (parseUsers)
            json.accumulate("parse", "users");

        if (!roles.isEmpty())
            json.put("roles", roles);
        else if (parseRoles)
            json.accumulate("parse", "roles");

        if (parseEveryone)
            json.accumulate("parse", "everyone");
        return json.toString();
    }
}
