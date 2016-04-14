package lt.ltrp.player.event;



import lt.ltrp.player.data.JailData;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.event.player.PlayerEvent;


/**
 * Created by Bebras on 2016.03.27.
 */
public class PlayerJailEvent extends PlayerEvent {

    private JailData jailData;

    public PlayerJailEvent(LtrpPlayer p, JailData jd) {
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
