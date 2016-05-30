package lt.ltrp.data;

import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class SprayPermission{

    private LtrpPlayer player;
    private LtrpPlayer allowedBy;
    private boolean isValid;

    public SprayPermission(LtrpPlayer player, LtrpPlayer allowedBy) {
        this.player = player;
        this.allowedBy = allowedBy;
        this.isValid = true;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public LtrpPlayer getAllowedBy() {
        return allowedBy;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }
}
