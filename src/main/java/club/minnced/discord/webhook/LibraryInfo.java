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

public class LibraryInfo {
    public static final int DISCORD_API_VERSION = 9;
    public static final String VERSION_MAJOR = "@MAJOR@";
    public static final String VERSION_MINOR = "@MINOR@";
    public static final String VERSION_PATCH = "@PATCH@";
    public static final String VERSION = "@VERSION@";
    public static final String COMMIT = "@COMMIT@";

    public static final String DEBUG_INFO = "DISCORD_API_VERSION: " + DISCORD_API_VERSION +
                                            "\nVERSION: " + VERSION +
                                            "\nCOMMIT: " + COMMIT;
}
