package lt.maze;

import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Server;
import net.gtaun.shoebill.resource.Plugin;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Bebras, Incognito
 *         2016.02.15.
 *
 *  This plugin is Java wrapper for SAMP audio plugin
 *      http://forum.sa-mp.com/showthread.php?t=82162
 *      https://github.com/samp-incognito/samp-audio-client-plugin
 *
 *  Once this is used it should NOT be used as this plugin will also create the TCP server aswell as other functions that are done in audio.inc via callback hooking.
 *  I merely created this wrapper, the plugins author is Incognito
 */
public class AudioPlugin extends Plugin {

    private static AudioPlugin instance;
    private static Logger logger;

    public static AudioPlugin getInstance() {
        return instance;
    }

    private Map<Player, List<AudioHandle>> handles;
    private List<AudioSequence> sequences;

    @Override
    public void onEnable() throws Throwable {
        instance = this;
        logger = getLogger();
        this.handles = new HashMap<>();
        this.sequences = new ArrayList<>();

        Callbacks.registerCallbacks(getShoebill().getAmxInstanceManager());
        Functions.registerFunctions(AmxInstance.getDefault());

        Functions.Audio_CreateTCPServer(Server.get().getIntVar("port"));
        logger.info("lt.maze.AudioPlugin wrapper was loaded successfully");
    }

    @Override
    public void onDisable() throws Throwable {
        sequences.stream().filter(AudioSequence::isDestroyed).collect(Collectors.toList()).forEach(AudioSequence::destroy);
        sequences.clear();
        sequences = null;

        handles.values().forEach(l -> l.forEach(AudioHandle::destroy));
        handles.clear();
        handles = null;

        Callbacks.unregisterCallbacks(getShoebill().getAmxInstanceManager());

        Functions.Audio_DestroyTCPServer();
        logger.info("lt.maze.AudioPlugin wrapper was unloaded successfully");
    }

    protected void addHandle(Player p, AudioHandle handle) {
        List<AudioHandle> playerHandles = handles.get(p);
        if(playerHandles == null) {
            playerHandles = new ArrayList<>();
            handles.put(p, playerHandles);
        }
        playerHandles.add(handle);
    }

    protected AudioHandle getHandle(Player p, int id) {
        Optional<AudioHandle> optional = handles.get(p).stream().filter(h -> h.getId() == id).findFirst();
        if(optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }


    protected void addSequence(AudioSequence sequence) {
        sequences.add(sequence);
    }

    protected AudioSequence getSequence(int id) {
        Optional<AudioSequence> optional = sequences.stream().filter(h -> h.getId() == id).findFirst();
        if(optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }


    public static void renamePlayer(Player p, String name) {
        Functions.Audio_RenamePlayer(p.getId(), name);
    }

    public static void setRadioStation(Player p, RadioStation station) {
        Functions.Audio_SetRadioStation(p.getId(), station.getId());
    }

    public static void stopRadio(Player p) {
        Functions.Audio_StopRadio(p.getId());
    }

    public static boolean isConnected(Player p) {
        return Functions.Audio_IsClientConnected(p.getId()) == 1;
    }

    public static void sendMessage(Player p, String message) {
        Functions.Audio_SendMessage(p.getId(), message);
    }

    public static void transferPack(Player p) {
        Functions.Audio_TransferPack(p.getId());
    }

    public static void setPack(String name, boolean transferable, boolean automated) {
        Functions.Audio_SetPack(name, transferable, automated);
    }

    public static void setPack(String name) {
        setPack(name, true, true);
    }



}
