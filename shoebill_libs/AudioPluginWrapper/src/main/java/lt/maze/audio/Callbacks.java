package lt.maze.audio;

import lt.maze.audio.event.*;
import net.gtaun.shoebill.amx.AmxInstanceManager;
import net.gtaun.shoebill.entities.Player;
import net.gtaun.shoebill.event.amx.AmxCallEvent;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerEntry;

import java.util.function.Consumer;

/**
 * @author Bebras
 *         2016.02.15.
 */
class Callbacks {

    static HandlerEntry playerConnectEntry, playerDisconnectEntry;

    static void registerCallbacks(AmxInstanceManager manager) {
        AudioPlugin plugin = AudioPlugin.getInstance();
        EventManager eventManager = plugin.getEventManager();

        manager.hookCallback("Audio_OnClientConnect", new Consumer<AmxCallEvent>() {
            @Override
            public void accept(AmxCallEvent e) {
                Player p = Player.get((int)e.getParameters()[0]);
                eventManager.dispatchEvent(new AudioClientConnectEvent(p), p);
            }
        }, "i");

        manager.hookCallback("Audio_OnClientDisconnect", new Consumer<AmxCallEvent>() {
            @Override
            public void accept(AmxCallEvent e) {
                Player p = Player.get((int)e.getParameters()[0]);
                eventManager.dispatchEvent(new AudioClientDisconnectEvent(p), p);
            }
        }, "i");

        manager.hookCallback("Audio_OnTransferFile", new Consumer<AmxCallEvent>() {
            @Override
            public void accept(AmxCallEvent e) {
                int playerid = (int)e.getParameters()[0];
                String filename = (String)e.getParameters()[1];
                int current = (int)e.getParameters()[2];
                int total = (int)e.getParameters()[3];
                int result = (int)e.getParameters()[4];
                Player p = Player.get(playerid);
                TransferResult r = TransferResult.getById(result);
                eventManager.dispatchEvent(new AudioTransferFileEvent(p, filename, current, total, r), p, r);
            }
        }, "isiii");

        manager.hookCallback("Audio_OnPlay", new Consumer<AmxCallEvent>() {
            @Override
            public void accept(AmxCallEvent e) {
                int playerid = (int)e.getParameters()[0];
                int handleid = (int)e.getParameters()[1];
                Player p = Player.get(playerid);
                AudioHandle handle = AudioHandle.get(p, handleid);
                eventManager.dispatchEvent(new AudioOnPlayEvent(p, handle), p, handle);
            }
        }, "ii");

        manager.hookCallback("Audio_OnStop", new Consumer<AmxCallEvent>() {
            @Override
            public void accept(AmxCallEvent e) {
                int playerid = (int)e.getParameters()[0];
                int handleid = (int)e.getParameters()[1];
                Player p = Player.get(playerid);
                AudioHandle handle = AudioHandle.get(p, handleid);
                eventManager.dispatchEvent(new AudioOnStopEvent(p, handle), p, handle);
            }
        }, "ii");

        manager.hookCallback("Audio_OnTrackChange", new Consumer<AmxCallEvent>() {
            @Override
            public void accept(AmxCallEvent e) {
                int playerid = (int)e.getParameters()[0];
                int handleid = (int)e.getParameters()[1];
                String track = (String)e.getParameters()[2];
                Player p = Player.get(playerid);
                AudioHandle handle = AudioHandle.get(p, handleid);
                eventManager.dispatchEvent(new AudioTrackChangeEvent(p, handle, track), p, handle, track);
            }
        }, "iis");

        manager.hookCallback("Audio_OnRadioStationChange", new Consumer<AmxCallEvent>() {
            @Override
            public void accept(AmxCallEvent e) {
                int playerid = (int)e.getParameters()[0];
                int stationid = (int)e.getParameters()[1];
                Player p = Player.get(playerid);
                RadioStation s = RadioStation.get(stationid);
                eventManager.dispatchEvent(new AudioStationChangeEvent(p, s), p, s);
            }
        }, "ii");

        manager.hookCallback("Audio_OnGetPosition", new Consumer<AmxCallEvent>() {
            @Override
            public void accept(AmxCallEvent e) {
                int playerid = (int)e.getParameters()[0];
                int handleid = (int)e.getParameters()[1];
                int seconds = (int)e.getParameters()[2];
                Player p = Player.get(playerid);
                AudioHandle h = AudioHandle.get(p, handleid);
                if(h.getHandler() != null) {
                    h.getHandler().onPositionGet(h, seconds);
                }
                eventManager.dispatchEvent(new AudioPositionGetEvent(p, h, seconds), p, h);
            }
        }, "iii");


        playerConnectEntry = eventManager.registerHandler(PlayerConnectEvent.class, e -> {
            Functions.Audio_AddPlayer(e.getPlayer().getId(), e.getPlayer().getIp(), e.getPlayer().getName());
        });

        playerDisconnectEntry = eventManager.registerHandler(PlayerDisconnectEvent.class, e -> {
            Functions.Audio_RemovePlayer(e.getPlayer().getId());
        });

    }

    static void unregisterCallbacks(AmxInstanceManager manager) {
        manager.unhookCallback("Audio_OnClientConnect");
        manager.unhookCallback("Audio_OnClientDisconnect");
        manager.unhookCallback("Audio_OnTransferFile");
        manager.unhookCallback("Audio_OnPlay");
        manager.unhookCallback("Audio_OnStop");
        manager.unhookCallback("Audio_OnTrackChange");
        manager.unhookCallback("Audio_OnRadioStationChange");
        manager.unhookCallback("Audio_OnGetPosition");


        playerDisconnectEntry.cancel();
        playerDisconnectEntry.cancel();
    }

}
