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

package root;

import club.minnced.discord.webhook.IOUtil;
import org.junit.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ThreadLocalRandom;

public class IOTest {
    public static String CONTENT;
    private File tempFile;

    @BeforeClass
    public static void randomContent() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int size = random.nextInt(4098);
        StringBuilder builder = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            builder.append(random.nextInt());
        }
        CONTENT = builder.toString();
    }

    @Before
    public void setup() throws IOException {
        tempFile = File.createTempFile("test", "Data");
        Files.write(tempFile.toPath(), CONTENT.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @After
    public void cleanup() {
        tempFile.delete();
    }

    @Test
    public void readAll() throws IOException {
        String content = new String(IOUtil.readAllBytes(new FileInputStream(tempFile)));
        Assert.assertEquals(CONTENT, content);
    }
}
