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

package club.minnced.discord.webhook;

/**
 * Constants for the message flags described by the <a href="https://discord.com/developers/docs/resources/channel#message-object-message-flags" target="_blank">Discord Documentation</a>.
 */
@SuppressWarnings("PointlessBitwiseExpression")
public class MessageFlags {
    public static final int CROSSPOSTED            = 1 << 0;
    public static final int IS_CROSSPOSTED         = 1 << 1;
    public static final int SUPPRESS_EMBEDS        = 1 << 2;
    public static final int SOURCE_MESSAGE_DELETED = 1 << 3;
    public static final int URGENT                 = 1 << 4;
    public static final int HAS_THREAD             = 1 << 5;
    public static final int EPHEMERAL              = 1 << 6;
    public static final int LOADING                = 1 << 7;
}
