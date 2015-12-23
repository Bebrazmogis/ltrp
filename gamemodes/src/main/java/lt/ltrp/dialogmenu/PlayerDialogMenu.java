package lt.ltrp.dialogmenu;

import lt.ltrp.player.LtrpPlayer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.13.
 */
public abstract class PlayerDialogMenu extends DialogMenu {

    private LtrpPlayer player;
    private EventManager eventManager;

    public PlayerDialogMenu(LtrpPlayer player, EventManager manager) {
        this.player = player;
        this.eventManager = manager;
    }

    @Override
    public final void show(LtrpPlayer player) {
        this.player = player;
        show();
    }

    public abstract void show();

    public LtrpPlayer getPlayer() {
        return player;
    }

    public void setPlayer(LtrpPlayer player) {
        this.player = player;
    }

    protected EventManager getEventManager() {
        return eventManager;
    }
}
