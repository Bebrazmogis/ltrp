package lt.ltrp.item;

import lt.ltrp.dialog.IntegerInputDialog;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.plugin.mapandreas.MapAndreas;
import lt.ltrp.radio.RadioStation;
import lt.ltrp.radio.dialog.RadioStationListDialog;
import lt.maze.streamer.event.PlayerEnterDynamicAreaEvent;
import lt.maze.streamer.event.PlayerLeaveDynamicAreaEvent;
import lt.maze.streamer.object.DynamicObject;
import lt.maze.streamer.object.DynamicSphere;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


/**
 * @author Bebras
 *         2016.02.16.
 */
public class BoomBoxItem extends BasicItem {

    private static final List<BoomBoxItem> BOOM_BOX_ITEMS = new ArrayList<>();
    private static final String OPTION_PLACE = "Padëti";
    private static final String OPTION_PICKUP = "Paimti";
    private static final String OPTION_STOP = "Iðjungti";
    private static final String OPTION_SELECT_STATION = "Pasirinkti stotá";
    private static final String OPTION_VOLUME = "Keisti garsà";

    private static final float MUSIC_RANGE  = 25f;
    private static final int MODEL = 2103;

    public static List<BoomBoxItem> get() {
        return BOOM_BOX_ITEMS;
    }

    private DynamicObject audioObject;
    private DynamicSphere dynamicArea;
    private HandlerEntry enterHandleEntry;
    private HandlerEntry exitHandleEntry;
    private RadioStation radioStation;


    public BoomBoxItem(int id, String name, EventManager eventManager) {
        super(id, name, eventManager, ItemType.BoomBox, false);
        BOOM_BOX_ITEMS.add(this);
    }

    public BoomBoxItem(EventManager eventManager) {
        this(0, "Radija", eventManager);
    }


    @ItemUsageOption(name = OPTION_PLACE)
    public boolean place(LtrpPlayer player) {
        if(audioObject == null) {
            Optional<BoomBoxItem> tooClose = get().stream().filter(b ->
                    b != null && !b.equals(this) && b.audioObject.getPosition().distance(this.audioObject.getPosition()) < MUSIC_RANGE).findFirst();
            if(!tooClose.isPresent()) {
                Location location = player.getLocation();
                location.z = MapAndreas.FindZ(location.x, location.y);
                audioObject = DynamicObject.create(MODEL, location, new Vector3D());
                dynamicArea = DynamicSphere.create(location, MUSIC_RANGE);
                this.enterHandleEntry = getEventManager().registerHandler(PlayerEnterDynamicAreaEvent.class, e -> {
                     if(e.getArea().equals(dynamicArea) && radioStation != null) {
                         e.getPlayer().playAudioStream(radioStation.getUrl());
                     }
                });

                this.exitHandleEntry = getEventManager().registerHandler(PlayerLeaveDynamicAreaEvent.class, e -> {
                    if(e.getArea().equals(dynamicArea) && radioStation != null) {
                        player.stopAudioStream();
                    }
                });
                return true;
            } else {
                player.sendErrorMessage("Netoliese jau yra psatatyta magnetola!");
            }
        }
        return false;
    }

    @ItemUsageOption(name = OPTION_PICKUP)
    public boolean pickup(LtrpPlayer player) {
        LtrpPlayer.get().stream()
                .filter(p -> dynamicArea.isInArea(p) && p.getAudioHandle() != null)
                .forEach(p -> p.getAudioHandle().stop());
        audioObject.destroy();
        dynamicArea.destroy();
        exitHandleEntry.cancel();
        enterHandleEntry.cancel();
        player.sendActionMessage("paima nuo þemes magnetolà ir jà iðjungia");
        audioObject = null;
        radioStation = null;
        dynamicArea = null;
        return true;

    }

    @ItemUsageOption(name = OPTION_SELECT_STATION)
    public boolean play(LtrpPlayer player) {
        if(audioObject != null) {
            if(audioObject.getPosition().distance(player.getLocation()) <= 5f) {
                RadioStationListDialog d = new RadioStationListDialog(player, getEventManager(), RadioStation.get());
                d.setClickOkHandler((dd, s) -> {
                    radioStation = s;
                    LtrpPlayer.get().stream()
                            .filter(dynamicArea::isInArea)
                            .forEach(p -> p.playAudioStream(s.getUrl()));
                });
            } else {
                player.sendErrorMessage("Jûs per toli nuo magnetolos kad pakeistumëte stotá.");
            }
        }
        return false;
    }

    @ItemUsageOption(name = OPTION_VOLUME)
    public boolean setVolume(LtrpPlayer player) {
        IntegerInputDialog.create(player, getEventManager())
                .caption("Garso nustatymai")
                .message("Galimas garso dydis nuo 1 iki 100")
                .buttonOk("Nustatyti")
                .buttonCancel("Iðeiti")
                .onClickOk((d, i) -> {
                    LtrpPlayer.get()
                            .stream()
                            .filter(dynamicArea::isInArea)
                            .forEach(p -> p.setVolume(i));
                })
                .build()
                .show();
        return false;
    }

    @ItemUsageOption(name = OPTION_STOP)
    public boolean stop(LtrpPlayer player) {
        if(audioObject != null && dynamicArea != null) {
            LtrpPlayer.get().stream().filter(dynamicArea::isInArea).forEach(p -> p.getAudioHandle().stop());
            radioStation = null;
            return true;
        }
        return false;
    }

    @ItemUsageEnabler
    public Supplier<Boolean> isEnabled(String itemName) {
        switch(itemName) {
            case OPTION_STOP:
                return () -> radioStation != null;
            case OPTION_SELECT_STATION:
                return () -> audioObject != null;
            case OPTION_PLACE:
                return () -> audioObject == null;
            case OPTION_PICKUP:
                return () -> audioObject != null;
            case OPTION_VOLUME:
                return () -> audioObject != null;

        }
        return null;
    }

    @Override
    public void destroy() {
        super.destroy();
        BOOM_BOX_ITEMS.remove(this);
    }


}
