package root.message;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class EmbedTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private WebhookEmbedBuilder builder;

    @Before
    public void setup() {
        builder = new WebhookEmbedBuilder();
    }

    @Test
    public void buildEmbed() {
        builder.setAuthor(new WebhookEmbed.EmbedAuthor("Minn", null, null));
        builder.setColor(0xff00ff);
        builder.setDescription("Hello World!");
        builder.setFooter(new WebhookEmbed.EmbedFooter("Footer text", null));
        builder.setImageUrl(null);
        builder.setThumbnailUrl(null);
        builder.setTitle(new WebhookEmbed.EmbedTitle("Title", null));
        builder.setTimestamp(System.currentTimeMillis());
        builder.build();
    }

    @Test
    public void buildEmptyEmbed() {
        expectedException.expect(IllegalStateException.class);
        builder.build();
    }
}

