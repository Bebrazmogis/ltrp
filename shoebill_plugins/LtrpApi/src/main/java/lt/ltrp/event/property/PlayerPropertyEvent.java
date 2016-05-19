package lt.ltrp.event.property;


import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Property;

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
