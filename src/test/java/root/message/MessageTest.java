package root.message;

import club.minnced.discord.webhook.IOUtil;
import club.minnced.discord.webhook.message.WebhookEmbed;
import club.minnced.discord.webhook.message.WebhookEmbedBuilder;
import club.minnced.discord.webhook.message.WebhookMessage;
import club.minnced.discord.webhook.message.WebhookMessageBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MessageTest {
    private WebhookMessageBuilder builder;

    @Before
    public void setupBuilder() {
        builder = new WebhookMessageBuilder();
    }

    @Test
    public void buildMessage() {
        builder.setContent("Hello World");
        builder.setUsername("Minn");
        builder.build().getBody();
    }

    @Test
    public void buildMessageWithEmbed() {
        builder.addEmbeds(
                new WebhookEmbedBuilder()
                        .setDescription("Hello World")
                        .build()
        ).build().getBody();
    }

    @Test
    public void buildMessageWithFiles() throws IOException {
        File tmp = File.createTempFile("message-test", "cat.png");
        builder.addFile(tmp);
        builder.addFile("dog.png", new FileInputStream(tmp));
        builder.addFile("bird.png", IOUtil.readAllBytes(new FileInputStream(tmp)));
        builder.build().getBody();
        tmp.delete();
    }

    @Test
    public void factoryEmbeds() {
        WebhookEmbed embed1 = new WebhookEmbedBuilder()
                .setDescription("Hello").build();
        WebhookEmbed embed2 = new WebhookEmbedBuilder()
                .setDescription("World").build();
        WebhookMessage.embeds(embed1, embed2).getBody();
        WebhookMessage.embeds(Arrays.asList(embed1, embed2)).getBody();
    }

    @Test
    public void factoryFiles() throws IOException {
        File tmp = File.createTempFile("message-test", "cat.png");
        WebhookMessage.files(
                "cat.png", tmp,
                "dog.png", new FileInputStream(tmp),
                "bird.png", IOUtil.readAllBytes(new FileInputStream(tmp))).getBody();
        Map<String, Object> files = new HashMap<>();
        files.put("cat.png", tmp);
        files.put("dog.png", new FileInputStream(tmp));
        files.put("bird.png", IOUtil.readAllBytes(new FileInputStream(tmp)));
        WebhookMessage.files(files).getBody();
        tmp.delete();
    }
}
