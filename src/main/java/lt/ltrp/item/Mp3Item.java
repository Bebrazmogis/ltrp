package lt.ltrp.item;

import lt.ltrp.radio.RadioStation;
import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.radio.dialog.RadioStationListDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.15.
 */
public class Mp3Item extends BasicItem {

    private boolean isPlaying;
    private int volume;
    private RadioStation currentStation;

    public Mp3Item(int id, String name, EventManager eventManager) {
        super(id, name, eventManager, ItemType.Mp3Player, false);
        this.volume = 50;
    }

    public Mp3Item(EventManager eventManager) {
        this(0, "MP3 grotuvas", eventManager);
    }

    @ItemUsageOption(name = "Pasirinkti stotá")
    public boolean play(LtrpPlayer player) {
        RadioStationListDialog dialog = new RadioStationListDialog(player, getEventManager(), RadioStation.get());
        dialog.setClickOkHandler((d, station) -> {
            setCurrentRadioStation(station);
            this.isPlaying = true;
            player.playAudioStream(station.getUrl());
            player.setVolume(volume);
            player.sendMessage(Color.CHOCOLATE, "Grojama stotis " + station.getName());
            player.sendActionMessage("iðsitraukia MP3 grotuvà, nustato radijo stotá ir vël já ásikiða");
        });
        dialog.show();
        return true;
    }

    @ItemUsageOption(name = "Keisti garsà")
    public boolean volume(LtrpPlayer player) {
        if(player.isAudioConnected()) {
            InputDialog.create(player, getEventManager())
                    .caption("MP3 grotuvo garsas")
                    .message("Áveskite norimà garsà nuo 0 iki 100." +
                        "0 - visiðka tyla" +
                        "100 - maksimalus garsas")
                    .buttonOk("Nustatyti")
                    .buttonCancel("Atðaukti")
                    .onClickOk((d, s) -> {
                        int vol = -1;
                        try {
                            vol = Integer.parseInt(s);
                        } catch(NumberFormatException ignored) {}
                        if(vol < 0 || vol > 100) {
                            d.show();
                        } else {
                            this.volume = vol;
                            player.setVolume(vol);
                        }
                    })
                    .build()
                    .show();
            return true;

        } else {
            player.sendErrorMessage("Ði funkcija galima tik naudojant Audio plugin. Daugiau apie já galite suþinoti paraðæ /audioplugin");
        }
        return false;
    }

    @ItemUsageOption(name = "Iðjungti")
    public boolean stop(LtrpPlayer player) {
        if(isPlaying) {
            this.isPlaying = false;
            player.stopAudioStream();
            player.sendActionMessage("iðjungia savo MP3 grotuvà.");
        } else {
            player.sendMessage(Color.CHOCOLATE, "Jûsø grotuvas negroja!");
        }
        return false;
    }

    public void setCurrentRadioStation(RadioStation station) {
        this.currentStation = station;
    }

    public RadioStation getCurrentStation() {
        return currentStation;
    }


    public int getVolume() {
        return volume;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

}
