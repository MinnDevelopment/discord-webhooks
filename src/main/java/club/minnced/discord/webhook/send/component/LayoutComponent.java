package club.minnced.discord.webhook.send.component;

import java.util.List;

public interface LayoutComponent extends Component {

    /**
     * Maximum allowed layout components (action rows) in a message
     */
    int MAX_COMPONENTS = 5;

    /**
     * @return The action components (buttons and select menus)
     */
    List<ActionComponent> getComponents();

}
