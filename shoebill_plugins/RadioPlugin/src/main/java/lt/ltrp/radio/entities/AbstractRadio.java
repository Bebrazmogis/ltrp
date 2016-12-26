package lt.ltrp.radio.entities;

import lt.ltrp.radio.data.RadioStation;
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

    public void play(RadioStation radioStation) {
        this.station = radioStation;
        this.playing = true;
    }

    public void stop() {
        station = null;
        this.playing = false;
    }

    public RadioStation getStation() {
        return station;
    }

    public void destroy() {
        isDestroyed = true;
        eventManager.cancelAll();
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public boolean isPlaying() {
        return playing;
    }
}
