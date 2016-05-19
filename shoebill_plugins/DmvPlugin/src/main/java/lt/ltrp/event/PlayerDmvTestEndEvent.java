package lt.ltrp.event;

import lt.ltrp.object.Dmv;
import lt.ltrp.object.DmvTest;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * @author Bebras
 *         2015.12.25.
 */
public class PlayerDmvTestEndEvent extends PlayerEvent {

    private DmvTest test;
    private Dmv dmv;

    public PlayerDmvTestEndEvent(LtrpPlayer player, Dmv dmv, DmvTest test) {
        super(player);
        this.test = test;
        this.dmv = dmv;
    }

    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    public DmvTest getTest() {
        return test;
    }


    public Dmv getDmv() {
        return dmv;
    }
}
