package lt.maze;

import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.amx.types.ReturnType;

import java.util.HashMap;

/**
 * @author Bebras
 *         2016.02.15.
 */
public class Functions {

    private static HashMap<String, AmxCallable> functions = new HashMap<>();

    public static void registerFunctions(AmxInstance amxInstance) {
        functions.put("Audio_CreateTCPServer", amxInstance.getNative("Audio_CreateTCPServer", ReturnType.INTEGER));
        functions.put("Audio_DestroyTCPServer", amxInstance.getNative("Audio_DestroyTCPServer", ReturnType.INTEGER));
        functions.put("Audio_SetPack", amxInstance.getNative("Audio_SetPack", ReturnType.INTEGER));
        functions.put("Audio_IsClientConnected", amxInstance.getNative("Audio_IsClientConnected", ReturnType.INTEGER));
        functions.put("Audio_SendMessage", amxInstance.getNative("Audio_SendMessage", ReturnType.INTEGER));
        functions.put("Audio_TransferPack", amxInstance.getNative("Audio_TransferPack", ReturnType.INTEGER));
        functions.put("Audio_CreateSequence", amxInstance.getNative("Audio_CreateSequence", ReturnType.INTEGER));
        functions.put("Audio_DestroySequence", amxInstance.getNative("Audio_DestroySequence", ReturnType.INTEGER));
        functions.put("Audio_AddToSequence", amxInstance.getNative("Audio_AddToSequence", ReturnType.INTEGER));
        functions.put("Audio_RemoveFromSequence", amxInstance.getNative("Audio_RemoveFromSequence", ReturnType.INTEGER));
        functions.put("Audio_Play", amxInstance.getNative("Audio_Play", ReturnType.INTEGER));
        functions.put("Audio_PlayStreamed", amxInstance.getNative("Audio_PlayStreamed", ReturnType.INTEGER));
        functions.put("Audio_PlaySequence", amxInstance.getNative("Audio_PlaySequence", ReturnType.INTEGER));
        functions.put("Audio_Pause", amxInstance.getNative("Audio_Pause", ReturnType.INTEGER));
        functions.put("Audio_Resume", amxInstance.getNative("Audio_Resume", ReturnType.INTEGER));
        functions.put("Audio_Stop", amxInstance.getNative("Audio_Stop", ReturnType.INTEGER));
        functions.put("Audio_Restart", amxInstance.getNative("Audio_Restart", ReturnType.INTEGER));
        functions.put("Audio_GetPosition", amxInstance.getNative("Audio_GetPosition", ReturnType.INTEGER));
        functions.put("Audio_SetPosition", amxInstance.getNative("Audio_SetPosition", ReturnType.INTEGER));
        functions.put("Audio_SetVolume", amxInstance.getNative("Audio_SetVolume", ReturnType.INTEGER));
        functions.put("Audio_SetFX", amxInstance.getNative("Audio_SetFX", ReturnType.INTEGER));
        functions.put("Audio_RemoveFX", amxInstance.getNative("Audio_RemoveFX", ReturnType.INTEGER));
        functions.put("Audio_Set3DPosition", amxInstance.getNative("Audio_Set3DPosition", ReturnType.INTEGER));
        functions.put("Audio_Remove3DPosition", amxInstance.getNative("Audio_Remove3DPosition", ReturnType.INTEGER));
        functions.put("Audio_SetRadioStation ", amxInstance.getNative("Audio_SetRadioStation ", ReturnType.INTEGER));
        functions.put("Audio_StopRadio", amxInstance.getNative("Audio_StopRadio", ReturnType.INTEGER));
        functions.put("Audio_AddPlayer", amxInstance.getNative("Audio_AddPlayer ", ReturnType.INTEGER));
        functions.put("Audio_RenamePlayer", amxInstance.getNative("Audio_RenamePlayer", ReturnType.INTEGER));
        functions.put("Audio_RemovePlayer", amxInstance.getNative("Audio_RemovePlayer", ReturnType.INTEGER));
    }


    static int Audio_CreateTCPServer(int port) {
        return (Integer)functions.get("Audio_CreateTCPServer").call(port);
    }
    static int Audio_DestroyTCPServer() {
        return (Integer)functions.get("Audio_DestroyTCPServer").call();
    }

    static int Audio_SetPack(String name, boolean transferable, boolean automated) {
        return (Integer)functions.get("Audio_SetPack").call(name, transferable, automated);
    }

    static int Audio_IsClientConnected(int playerid) {
        return (Integer)functions.get("Audio_IsClientConnected").call(playerid);
    }

    static int Audio_SendMessage(int playerid, String message) {
        return (Integer)functions.get("Audio_SendMessage").call(playerid, message);
    }

    static int Audio_TransferPack(int playerid) {
        return (Integer)functions.get("Audio_TransferPack").call(playerid);
    }

    static int Audio_CreateSequence() {
        return (Integer)functions.get("Audio_CreateSequence").call();
    }

    static int Audio_DestroySequence(int sequenceid) {
        return (Integer)functions.get("Audio_DestroySequence").call(sequenceid);
    }

    static int Audio_AddToSequence(int sequenceid, int audioid) {
        return (Integer)functions.get("Audio_AddToSequence").call(sequenceid, audioid);
    }

    static int Audio_RemoveFromSequence(int sequenceid, int audioid) {
        return (Integer)functions.get("Audio_RemoveFromSequence").call(sequenceid, audioid);
    }

    static int Audio_Play(int playerid, int audioid, boolean pause, boolean loop, boolean downmix) {
        return (Integer)functions.get("Audio_Play").call(playerid, audioid, pause, loop, downmix);
    }

    static int Audio_PlayStreamed(int playerid, final String url, boolean pause, boolean loop, boolean downmix) {
        return (Integer)functions.get("Audio_PlayStreamed").call(playerid, url, pause, loop, downmix);
    }

    static int Audio_PlaySequence(int playerid, int sequenceid, boolean pause, boolean loop, boolean downmix) {
        return (Integer)functions.get("Audio_PlaySequence").call(playerid, sequenceid, pause, loop, downmix);
    }

    static int Audio_Pause(int playerid, int handleid) {
        return (Integer)functions.get("Audio_Pause").call(playerid, handleid);
    }

    static int Audio_Resume(int playerid, int handleid) {
        return (Integer)functions.get("Audio_Resume").call(playerid, handleid);
    }

    static int Audio_Stop(int playerid, int handleid) {
        return (Integer)functions.get("Audio_Stop").call(playerid, handleid);
    }

    static int Audio_Restart(int playerid, int handleid) {
        return (Integer)functions.get("Audio_Restart").call(playerid, handleid);
    }

    static int Audio_GetPosition(int playerid, int handleid, final String callback) {
        return (Integer)functions.get("Audio_GetPosition").call(playerid, handleid, callback);
    }

    static int Audio_SetPosition(int playerid, int handleid, int seconds) {
        return (Integer)functions.get("Audio_SetPosition").call(playerid, handleid, seconds);
    }

    static int Audio_SetVolume(int playerid, int handleid, int volume) {
        return (Integer)functions.get("Audio_SetVolume").call(playerid, handleid, volume);
    }

    static int Audio_SetFX(int playerid, int handleid, int type) {
        return (Integer)functions.get("Audio_SetFX").call(playerid, handleid, type);
    }

    static int Audio_RemoveFX(int playerid, int handleid, int type) {
        return (Integer)functions.get("Audio_RemoveFX").call(playerid, handleid, type);
    }

    static int Audio_Set3DPosition(int playerid, int handleid, float x, float y, float z, float distance) {
        return (Integer)functions.get("Audio_Set3DPosition").call(playerid, handleid, x, y, z, distance);
    }

    static int Audio_Remove3DPosition(int playerid, int handleid) {
        return (Integer)functions.get("Audio_Remove3DPosition").call(playerid, handleid);
    }

    static int Audio_SetRadioStation(int playerid, int station) {
        return (Integer)functions.get("Audio_SetRadioStation").call(playerid, station);
    }

    static int Audio_StopRadio(int playerid) {
        return (Integer)functions.get("Audio_StopRadio").call(playerid);
    }

    static int Audio_AddPlayer(int playerid, final String ip, final String name) {
        return (Integer)functions.get("Audio_AddPlayer").call(playerid, ip, name);
    }

    static int Audio_RenamePlayer(int playerid, final String name) {
        return (Integer)functions.get("Audio_RenamePlayer").call(playerid, name);
    }

    static int Audio_RemovePlayer(int playerid) {
        return (Integer)functions.get("Audio_RemovePlayer").call(playerid);
    }


}
