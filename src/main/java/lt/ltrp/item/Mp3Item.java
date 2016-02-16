package lt.ltrp.item;

import lt.ltrp.RadioStation;
import lt.ltrp.data.Color;
import lt.ltrp.dialogmenu.radio.RadioStationListDialog;
import lt.ltrp.player.LtrpPlayer;
import lt.maze.AudioHandle;
import net.gtaun.shoebill.common.dialog.InputDialog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2016.02.15.
 */
public class Mp3Item extends BasicItem {

    private boolean isPlaying;
    private int volume;
    private RadioStation currentStation;
    private AudioHandle handle;

    public Mp3Item(String name) {
        super(name, ItemType.Mp3Player, false);
        this.volume = 50;
    }

    public Mp3Item() {
        this("MP3 grotuvas");
    }

    @ItemUsageOption(name = "Pasirinkti stot�")
    public boolean play(LtrpPlayer player) {
        RadioStationListDialog dialog = new RadioStationListDialog(player, ItemController.getEventManager(), RadioStation.get());
        dialog.setClickOkHandler((d, station) -> {
            this.currentStation = station;
            this.isPlaying = true;
            handle = player.playStream(station.getUrl());
            handle.setVolume(volume);
            player.sendMessage(Color.CHOCOLATE, "Grojama stotis " + station.getName());
            player.sendActionMessage("i�sitraukia MP3 grotuv�, nustato radijo stot� ir v�l j� �siki�a");

        });
        dialog.show();
        return true;
    }

    @ItemUsageOption(name = "Keisti gars�")
    public boolean volume(LtrpPlayer player) {
        if(player.isAudioConnected()) {
            InputDialog.create(player, ItemController.getEventManager())
                    .caption("MP3 grotuvo garsas")
                    .message("�veskite norim� gars� nuo 0 iki 100." +
                        "0 - visi�ka tyla" +
                        "100 - maksimalus garsas")
                    .buttonOk("Nustatyti")
                    .buttonCancel("At�aukti")
                    .onClickOk((d, s) -> {
                        int vol = -1;
                        try {
                            vol = Integer.parseInt(s);
                        } catch(NumberFormatException ignored) {}
                        if(vol < 0 || vol > 100) {
                            d.show();
                        } else {
                            this.volume = vol;
                            if(handle != null)
                                handle.setVolume(vol);
                        }
                    })
                    .build()
                    .show();
            return true;

        } else {
            player.sendErrorMessage("�i funkcija galima tik naudojant Audio plugin. Daugiau apie j� galite su�inoti para�� /audioplugin");
        }
        return false;
    }

    @ItemUsageOption(name = "I�jungti")
    public boolean stop(LtrpPlayer player) {
        if(isPlaying) {
            this.isPlaying = false;
            player.stopAudioStream();
            handle.stop();
        } else {
            player.sendMessage(Color.CHOCOLATE, "J�s� grotuvas negroja!");
        }
        return false;
    }

    protected static Mp3Item getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_basic WHERE id = ?";
        Mp3Item item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new Mp3Item(result.getString("name"));
                item.setItemId(itemid);
            }
        }
        return item;
    }
}
