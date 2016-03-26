package lt.ltrp.property;

import lt.ltrp.event.property.PlayerEnterHouseEvent;
import lt.ltrp.event.property.PlayerExitHouseEvent;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.radio.AbstractRadio;
import lt.ltrp.radio.RadioStation;
import net.gtaun.util.event.EventManager;

/**
 * Created by Bebras on 2016.03.26.
 */
public class HouseRadio extends AbstractRadio {

    private House house;

    public HouseRadio(House house, EventManager eventManager1) {
        super(eventManager1);
        this.house = house;
        this.eventManager.registerHandler(PlayerEnterHouseEvent.class, e -> {
            if(isPlaying()) {
                e.getPlayer().playAudioStream(getStation().getUrl());
            }
        });
        this.eventManager.registerHandler(PlayerExitHouseEvent.class, e -> {
            if(isPlaying()) {
                e.getPlayer().stopAudioStream();
            }
        });
    }

    @Override
    public void play(RadioStation radioStation) {
        if(radioStation != null) {
            LtrpPlayer.get()
                    .stream()
                    .filter(p -> p.getProperty().equals(house))
                    .forEach(p -> p.playAudioStream(radioStation.getUrl()));
        }
        super.play(radioStation);
    }

    @Override
    public void stop() {
        LtrpPlayer.get()
                .stream()
                .filter(p -> p.getProperty().equals(house))
                .forEach(LtrpPlayer::stopAudioStream);
        super.stop();
    }

}
