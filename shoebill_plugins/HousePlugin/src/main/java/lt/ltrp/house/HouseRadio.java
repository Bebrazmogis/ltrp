package lt.ltrp.house;


import lt.ltrp.house.event.PlayerEnterHouseEvent;
import lt.ltrp.house.event.PlayerExitHouseEvent;
import lt.ltrp.object.AbstractRadio;
import lt.ltrp.house.object.House;
import lt.ltrp.player.object.LtrpPlayer;
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
            LtrpPlayer.Companion.get()
                    .stream()
                    .filter(p -> house.equals(House.get(p)))
                    .forEach(p -> p.playAudioStream(radioStation.getUrl()));
        }
        super.play(radioStation);
    }

    @Override
    public void setVolume(int vol) {
        LtrpPlayer.Companion.get()
                .stream()
                .filter(p -> house.equals(House.get(p)))
                .forEach(p -> p.setVolume(vol));
        super.setVolume(vol);
    }

    @Override
    public void stop() {
        LtrpPlayer.Companion.get()
                .stream()
                .filter(p -> house.equals(House.get(p)))
                .forEach(LtrpPlayer::stopAudioStream);
        super.stop();
    }

}
