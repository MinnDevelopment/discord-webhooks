package club.minnced.discord.webhook.send.component;

import org.jetbrains.annotations.Nullable;

public interface ActionComponent extends Component {

    @Nullable String getCustomID();

}
