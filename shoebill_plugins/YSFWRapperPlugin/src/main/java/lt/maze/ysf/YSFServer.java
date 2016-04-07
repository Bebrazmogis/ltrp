package lt.maze.ysf;

import lt.maze.ysf.constant.ServerRuleFlag;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.amx.types.ReferenceInt;
import net.gtaun.shoebill.amx.types.ReferenceString;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class YSFServer {
    private static YSFServer ourInstance = new YSFServer();

    public static YSFServer get() {
        return ourInstance;
    }

    private ReferenceInt showPlayerMarkers;
    private ReferenceInt showNameTags;
    private ReferenceInt stuntBonuses;
    private ReferenceInt usePlayerPedAnims;
    private ReferenceInt limitChatRadius;
    private ReferenceInt disableInteriorExits;
    private ReferenceInt nameTagLOS;
    private ReferenceInt manualVehicleEngine;
    private ReferenceInt limitPlayerMarkers;
    private ReferenceInt vehicleFriendlyFire;
    private ReferenceInt defaultCameraCollision;
    private ReferenceFloat globalChatRadius;
    private ReferenceFloat nameTagDrawDistance;
    private ReferenceFloat playerMarkerLimit;


    private YSFServer() {
        showPlayerMarkers = new ReferenceInt(0);
        showNameTags = new ReferenceInt(0);
        stuntBonuses = new ReferenceInt(0);
        usePlayerPedAnims = new ReferenceInt(0);
        limitChatRadius = new ReferenceInt(0);
        disableInteriorExits = new ReferenceInt(0);
        nameTagLOS = new ReferenceInt(0);
        manualVehicleEngine = new ReferenceInt(0);
        limitPlayerMarkers = new ReferenceInt(0);
        vehicleFriendlyFire = new ReferenceInt(0);
        defaultCameraCollision = new ReferenceInt(0);
        globalChatRadius = new ReferenceFloat(0f);
        nameTagDrawDistance = new ReferenceFloat(0f);
        playerMarkerLimit = new ReferenceFloat(0f);
    }

    public boolean limitPlayerMarkers() {
        getSettings();
        return limitPlayerMarkers.getValue() != 0;
    }

    public boolean vehicleFriendlyFire() {
        getSettings();
        return vehicleFriendlyFire.getValue() != 0;
    }

    public boolean defaultCameraCols() {
        getSettings();
        return defaultCameraCollision.getValue() != 0;
    }

    public float getGlobalChatRadius() {
        getSettings();
        return globalChatRadius.getValue();
    }

    public float getNameTagDrawDistance() {
        getSettings();
        return nameTagDrawDistance.getValue();
    }

    public float playerMarkerLimit() {
        getSettings();
        return playerMarkerLimit.getValue();
    }

    public boolean nameTagLOS() {
        getSettings();
        return nameTagLOS.getValue() != 0;
    }

    public boolean manualVehicleEngine() {
        getSettings();
        return manualVehicleEngine.getValue() != 0;
    }

    public boolean disableInteriorEntersExits() {
        getSettings();
        return disableInteriorExits.getValue() != 0;
    }

    public boolean showPlayerMarkers() {
        getSettings();
        return showPlayerMarkers.getValue() != 0;
    }

    public boolean showNameTags() {
        getSettings();
        return showNameTags.getValue() != 0;
    }

    public boolean stuntBonusesEnabled() {
        getSettings();
        return stuntBonuses.getValue() != 0;
    }

    public boolean usePlayerPedAnims() {
        getSettings();
        return usePlayerPedAnims.getValue() != 0;
    }

    public boolean limitChatRadius() {
        getSettings();
        return limitChatRadius.getValue() != 0;
    }


    public void setModeRestartTime(float time) {
        Functions.SetModeRestartTime(time);
    }

    public float getModeRestartTime() {
        return Functions.GetModeRestartTime();
    }

    public void setMaxPlayers(int maxPlayers) {
        Functions.SetMaxPlayers(maxPlayers);
    }

    public int setMaxNPCs(int npcs) {
        return Functions.SetMaxNPCs(npcs);
    }

    public boolean loadFilterScript(String name) {
        return Functions.LoadFilterScript(name) != 0;
    }

    public boolean unLoadFilterScript(String name) {
        return Functions.UnLoadFilterScript(name) != 0;
    }

    public int getFilterScriptCount() {
        return Functions.GetFilterScriptCount();
    }

    public String getFilterScriptName(int id) {
        ReferenceString string = new ReferenceString("", 128);
        Functions.GetFilterScriptName(id, string, string.getLength());
        return string.getValue();
    }


    public void addRule(String rule, String value, ServerRuleFlag flag) {
        Functions.AddServerRule(rule, value, flag.getValue());
    }

    public void addRule(String rule, String value) {
        addRule(rule, value, ServerRuleFlag.RULE);
    }

    public void setRule(String rule, String value) {
        Functions.SetServerRule(rule, value);
    }

    public boolean isValidRule(String rule) {
        return Functions.IsValidServerRule(rule) != 0;
    }

    public void setRuleFlags(String rule, ServerRuleFlag... flags) {
        for(int i = 0; i < flags.length; i++)
            Functions.SetServerRuleFlags(rule, flags[i].getValue());
    }

    public int getRuleFlags() {
        throw new NotImplementedException();
    }

    public void allowCharacterInName(char c, boolean allow) {
        Functions.AllowNickNameCharacter(c, allow);
    }

    public boolean isNameCharEnabled(char c) {
        return Functions.IsNickNameCharacterAllowed(c) != 0;
    }

    public int getRunningTimers() {
        return Functions.GetRunningTimers();
    }

    public static boolean isValidNickname(String s) {
        return Functions.IsValidNickName(s) != 0;
    }

    private void getSettings() {
        Functions.GetServerSettings(showPlayerMarkers, showNameTags, stuntBonuses, usePlayerPedAnims, limitChatRadius,
                disableInteriorExits, nameTagLOS, manualVehicleEngine, limitPlayerMarkers, vehicleFriendlyFire,
                defaultCameraCollision, globalChatRadius, nameTagDrawDistance, playerMarkerLimit);
    }



}
