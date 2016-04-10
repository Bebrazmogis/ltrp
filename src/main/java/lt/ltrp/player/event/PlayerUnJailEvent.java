package lt.ltrp.player.event;

import lt.ltrp.player.JailData;
import lt.ltrp.player.LtrpPlayer;

/**
 * Created by Bebras on 2016.03.27.
 */
public class PlayerUnJailEvent extends PlayerEvent {

    private JailData jailData;

    public PlayerUnJailEvent(LtrpPlayer p, JailData jd) {
        super(p);
        this.jailData = jd;
    }

    public JailData getJailData() {
        return jailData;
    }

    @Override
    public String toString() {
        return super.toString() + "jailData=" + jailData;
    }
}
