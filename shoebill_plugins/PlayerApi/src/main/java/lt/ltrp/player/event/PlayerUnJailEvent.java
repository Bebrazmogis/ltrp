package lt.ltrp.player.event;


import lt.ltrp.player.data.JailData;
import lt.ltrp.player.object.LtrpPlayer;

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
