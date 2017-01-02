package lt.ltrp.vehicle.radio.data;



import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent;
import net.gtaun.util.event.EventManager;

/**
 * Created by Bebras on 2016.03.26.
 */
public class VehicleRadio extends AbstractRadio {

    private LtrpVehicle vehicle;

    public VehicleRadio(LtrpVehicle vehicle, EventManager eventManager) {
        super(eventManager);
        this.vehicle = vehicle;
        this.eventManager.registerHandler(PlayerStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.Companion.get(e.getPlayer());
            PlayerState newState = player.getState();
            if((newState.equals(PlayerState.DRIVER) || newState.equals(PlayerState.PASSENGER)) && getStation() != null) {
                player.playAudioStream(getStation().getUrl());
                player.setVolume(getVolume());
            } else if(newState.equals(PlayerState.ONFOOT)) {
                player.stopAudioStream();
            }
        });
    }

    @Override
    public void play(RadioStation s) {
        if(s != null) {
            LtrpPlayer.get()
                    .stream()
                    .filter(p -> vehicle.equals(p.getVehicle()))
                    .forEach(p -> p.playAudioStream(s.getUrl()));
        }
        super.play(s);
    }

    @Override
    public void setVolume(int vol) {
        LtrpPlayer.get()
                .stream()
                .filter(p -> vehicle.equals(p.getVehicle()))
                .forEach(p -> p.setVolume(vol));
        super.setVolume(vol);
    }

    @Override
    public void stop() {
        if(isPlaying()) {
            LtrpPlayer.get()
                    .stream()
                    .filter(p -> vehicle.equals(p.getVehicle()))
                    .forEach(LtrpPlayer::stopAudioStream);
        }
        super.stop();
    }
}
