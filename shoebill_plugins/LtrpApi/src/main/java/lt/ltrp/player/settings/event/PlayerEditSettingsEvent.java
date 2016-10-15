package lt.ltrp.player.settings.event;


import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.player.settings.data.PlayerSettings;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.10.
 */
public class PlayerEditSettingsEvent extends PlayerEvent {

    private PlayerSettings settings;

    public PlayerEditSettingsEvent(LtrpPlayer player, PlayerSettings settings) {
        super(player);
        this.settings = settings;
    }

    public PlayerSettings getSettings() {
        return settings;
    }

    @Override
    public String toString() {
        return super.toString() + "settings="+settings;
    }
}
