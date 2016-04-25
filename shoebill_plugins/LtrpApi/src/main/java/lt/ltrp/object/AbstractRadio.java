package lt.ltrp.object;

import lt.ltrp.data.RadioStation;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

/**
 * Created by Bebras on 2016.03.26.
 */
public abstract class AbstractRadio implements Radio {

    private RadioStation station;
    private int volume;
    private boolean isDestroyed, playing;
    protected EventManagerNode eventManager;

    public AbstractRadio(EventManager eventManager) {
        this.eventManager = eventManager.createChildNode();
        this.volume = 50;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    public void play(RadioStation radioStation) {
        this.station = radioStation;
        this.playing = true;
    }

    @Override
    public void stop() {
        station = null;
        this.playing = false;
    }

    @Override
    public RadioStation getStation() {
        return station;
    }

    @Override
    public void destroy() {
        isDestroyed = true;
        eventManager.cancelAll();
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }
}
