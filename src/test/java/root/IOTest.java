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
