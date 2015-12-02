package lt.ltrp.event.property;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.Property;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerPropertyEvent extends PlayerEvent {

    private Property property;

    public PlayerPropertyEvent(LtrpPlayer player, Property property) {
        super(player);
        this.property = property;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    public Property getProperty() {
        return property;
    }
}
