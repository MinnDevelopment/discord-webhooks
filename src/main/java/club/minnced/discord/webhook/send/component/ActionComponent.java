package club.minnced.discord.webhook.send.component;

import org.jetbrains.annotations.Nullable;

/**
 * Components that can be inserted inside layout components (buttons & select menus)
 */
public interface ActionComponent extends Component {

    /**
     * The dev-defined id of the component
     * @return dev-defined id of the component. Nullable for link style buttons
     */
    @Nullable String getCustomID();

}
