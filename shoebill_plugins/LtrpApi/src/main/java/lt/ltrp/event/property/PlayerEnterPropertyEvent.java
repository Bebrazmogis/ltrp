package lt.ltrp.event.property;


import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Property;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerEnterPropertyEvent extends PropertyEvent {

    private LtrpPlayer player;

    public PlayerEnterPropertyEvent(Property property, LtrpPlayer player) {
        super(property);
        this.player = player;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }
}
