package lt.ltrp.business.event;

import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessNameChangeEvent extends BusinessEvent {

    private String newName;

    public BusinessNameChangeEvent(Business property, LtrpPlayer player, String newName) {
        super(property, player);
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }
}
