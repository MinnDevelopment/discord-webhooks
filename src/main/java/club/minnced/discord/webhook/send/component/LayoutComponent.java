package club.minnced.discord.webhook.send.component;

import java.util.List;

public interface LayoutComponent extends Component {

    int MAX_COMPONENTS = 5;

    List<ActionComponent> getComponents();

}
