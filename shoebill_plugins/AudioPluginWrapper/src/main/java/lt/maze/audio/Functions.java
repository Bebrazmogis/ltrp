package lt.maze.audio;

import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.amx.AmxInstance;
import java.util.HashMap;
import net.gtaun.shoebill.amx.types.*;

public class Functions {

    private static HashMap<String, AmxCallable> functions = new HashMap<>();

    public static void registerFunctions(AmxInstance instance) {
        functions.put("Audio_CreateTCPServer", instance.getNative("Audio_CreateTCPServer", ReturnType.INTEGER));
        functions.put("Audio_DestroyTCPServer", instance.getNative("Audio_DestroyTCPServer", ReturnType.INTEGER));
        functions.put("Audio_SetPack", instance.getNative("Audio_SetPack", ReturnType.INTEGER));
        functions.put("Audio_IsClientConnected", instance.getNative("Audio_IsClientConnected", ReturnType.INTEGER));
        functions.put("Audio_SendMessage", instance.getNative("Audio_SendMessage", ReturnType.INTEGER));
        functions.put("Audio_TransferPack", instance.getNative("Audio_TransferPack", ReturnType.INTEGER));
        functions.put("Audio_CreateSequence", instance.getNative("Audio_CreateSequence", ReturnType.INTEGER));
        functions.put("Audio_DestroySequence", instance.getNative("Audio_DestroySequence", ReturnType.INTEGER));
        functions.put("Audio_AddToSequence", instance.getNative("Audio_AddToSequence", ReturnType.INTEGER));
        functions.put("Audio_RemoveFromSequence", instance.getNative("Audio_RemoveFromSequence", ReturnType.INTEGER));
        functions.put("Audio_Play", instance.getNative("Audio_Play", ReturnType.INTEGER));
        functions.put("Audio_PlayStreamed", instance.getNative("Audio_PlayStreamed", ReturnType.INTEGER));
        functions.put("Audio_PlaySequence", instance.getNative("Audio_PlaySequence", ReturnType.INTEGER));
        functions.put("Audio_Pause", instance.getNative("Audio_Pause", ReturnType.INTEGER));
        functions.put("Audio_Resume", instance.getNative("Audio_Resume", ReturnType.INTEGER));
        functions.put("Audio_Stop", instance.getNative("Audio_Stop", ReturnType.INTEGER));
        functions.put("Audio_Restart", instance.getNative("Audio_Restart", ReturnType.INTEGER));
        functions.put("Audio_GetPosition", instance.getNative("Audio_GetPosition", ReturnType.INTEGER));
        functions.put("Audio_SetPosition", instance.getNative("Audio_SetPosition", ReturnType.INTEGER));
        functions.put("Audio_SetVolume", instance.getNative("Audio_SetVolume", ReturnType.INTEGER));
        functions.put("Audio_SetFX", instance.getNative("Audio_SetFX", ReturnType.INTEGER));
        functions.put("Audio_RemoveFX", instance.getNative("Audio_RemoveFX", ReturnType.INTEGER));
        functions.put("Audio_Set3DPosition", instance.getNative("Audio_Set3DPosition", ReturnType.INTEGER));
        functions.put("Audio_Remove3DPosition", instance.getNative("Audio_Remove3DPosition", ReturnType.INTEGER));
        functions.put("Audio_SetRadioStation", instance.getNative("Audio_SetRadioStation", ReturnType.INTEGER));
        functions.put("Audio_StopRadio", instance.getNative("Audio_StopRadio", ReturnType.INTEGER));
        functions.put("Audio_AddPlayer", instance.getNative("Audio_AddPlayer", ReturnType.INTEGER));
        functions.put("Audio_RenamePlayer", instance.getNative("Audio_RenamePlayer", ReturnType.INTEGER));
        functions.put("Audio_RemovePlayer", instance.getNative("Audio_RemovePlayer", ReturnType.INTEGER));
    }

    public static int Audio_CreateTCPServer(int port) {
        return (int) functions.get("Audio_CreateTCPServer").call(port);
    }

    public static int Audio_DestroyTCPServer() {
        return (int) functions.get("Audio_DestroyTCPServer").call();
    }

    public static int Audio_SetPack(String name, boolean transferable, boolean automated) {
        return (int) functions.get("Audio_SetPack").call(name, transferable, automated);
    }

    public static int Audio_IsClientConnected(int playerid) {
        return (int) functions.get("Audio_IsClientConnected").call(playerid);
    }

    public static int Audio_SendMessage(int playerid, String message) {
        return (int) functions.get("Audio_SendMessage").call(playerid, message);
    }

    public static int Audio_TransferPack(int playerid) {
        return (int) functions.get("Audio_TransferPack").call(playerid);
    }

    public static int Audio_CreateSequence() {
        return (int) functions.get("Audio_CreateSequence").call();
    }

    public static int Audio_DestroySequence(int sequenceid) {
        return (int) functions.get("Audio_DestroySequence").call(sequenceid);
    }

    public static int Audio_AddToSequence(int sequenceid, int audioid) {
        return (int) functions.get("Audio_AddToSequence").call(sequenceid, audioid);
    }

    public static int Audio_RemoveFromSequence(int sequenceid, int audioid) {
        return (int) functions.get("Audio_RemoveFromSequence").call(sequenceid, audioid);
    }

    public static int Audio_Play(int playerid, int audioid, boolean pause, boolean loop, boolean downmix) {
        return (int) functions.get("Audio_Play").call(playerid, audioid, pause, loop, downmix);
    }

    public static int Audio_PlayStreamed(int playerid, String url, boolean pause, boolean loop, boolean downmix) {
        return (int) functions.get("Audio_PlayStreamed").call(playerid, url, pause, loop, downmix);
    }

    public static int Audio_PlaySequence(int playerid, int sequenceid, boolean pause, boolean loop, boolean downmix) {
        return (int) functions.get("Audio_PlaySequence").call(playerid, sequenceid, pause, loop, downmix);
    }

    public static int Audio_Pause(int playerid, int handleid) {
        return (int) functions.get("Audio_Pause").call(playerid, handleid);
    }

    public static int Audio_Resume(int playerid, int handleid) {
        return (int) functions.get("Audio_Resume").call(playerid, handleid);
    }

    public static int Audio_Stop(int playerid, int handleid) {
        return (int) functions.get("Audio_Stop").call(playerid, handleid);
    }

    public static int Audio_Restart(int playerid, int handleid) {
        return (int) functions.get("Audio_Restart").call(playerid, handleid);
    }

    public static int Audio_GetPosition(int playerid, int handleid, String callback) {
        return (int) functions.get("Audio_GetPosition").call(playerid, handleid, callback);
    }

    public static int Audio_SetPosition(int playerid, int handleid, int seconds) {
        return (int) functions.get("Audio_SetPosition").call(playerid, handleid, seconds);
    }

    public static int Audio_SetVolume(int playerid, int handleid, int volume) {
        return (int) functions.get("Audio_SetVolume").call(playerid, handleid, volume);
    }

    public static int Audio_SetFX(int playerid, int handleid, int type) {
        return (int) functions.get("Audio_SetFX").call(playerid, handleid, type);
    }

    public static int Audio_RemoveFX(int playerid, int handleid, int type) {
        return (int) functions.get("Audio_RemoveFX").call(playerid, handleid, type);
    }

    public static int Audio_Set3DPosition(int playerid, int handleid, float x, float y, float z, float distance) {
        return (int) functions.get("Audio_Set3DPosition").call(playerid, handleid, x, y, z, distance);
    }

    public static int Audio_Remove3DPosition(int playerid, int handleid) {
        return (int) functions.get("Audio_Remove3DPosition").call(playerid, handleid);
    }

    public static int Audio_SetRadioStation(int playerid, int station) {
        return (int) functions.get("Audio_SetRadioStation").call(playerid, station);
    }

    public static int Audio_StopRadio(int playerid) {
        return (int) functions.get("Audio_StopRadio").call(playerid);
    }

    public static int Audio_AddPlayer(int playerid, String ip, String name) {
        return (int) functions.get("Audio_AddPlayer").call(playerid, ip, name);
    }

    public static int Audio_RenamePlayer(int playerid, String name) {
        return (int) functions.get("Audio_RenamePlayer").call(playerid, name);
    }

    public static int Audio_RemovePlayer(int playerid) {
        return (int) functions.get("Audio_RemovePlayer").call(playerid);
    }

}