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
