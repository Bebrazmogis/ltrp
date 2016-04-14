package lt.ltrp.property.event;


import lt.ltrp.player.event.PlayerEvent;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.property.object.Property;

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


    public Property getProperty() {
        return property;
    }
}
