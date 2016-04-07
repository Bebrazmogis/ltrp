package lt.ltrp.player.event;

import lt.ltrp.player.JailData;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.event.player.PlayerEvent;

/**
 * Created by Bebras on 2016.03.27.
 */
public class PlayerUnJailEvent extends PlayerEvent {

    private JailData jailData;

    public PlayerUnJailEvent(LtrpPlayer p, JailData jd) {
        super(p);
        this.jailData = jd;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    public JailData getJailData() {
        return jailData;
    }

}
