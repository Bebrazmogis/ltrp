package lt.ltrp.object;


import lt.ltrp.player.PlayerController;
import lt.ltrp.constant.TalkStyle;
import lt.ltrp.constant.WalkStyle;
import lt.ltrp.data.*;
import lt.ltrp.player.job.data.PlayerJobData;
import lt.ltrp.player.licenses.data.PlayerLicenses;
import lt.ltrp.player.settings.data.PlayerSettings;
import lt.maze.audio.AudioHandle;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.constant.WeaponSlot;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;

import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;


/**
 * @author Bebras
 *         2016.04.07.
 */
public interface LtrpPlayer extends Player, PlayerData, InventoryEntity, Destroyable {
    public static final int INVALID_USER_ID = 0;
    public static final float DEFAULT_ACTION_MESSAGE_DISTANCE = 20f;
    public static final Color DEFAULT_PLAYER_COLOR = new Color(0xFFFFFF00);
    public static final int DEFAULT_INFOTEXT_DURATION = 60;
    public static final int MAX_LOGIN_TRIES = 3;


    static Collection<LtrpPlayer> get() {
        return PlayerController.instance.getPlayers();
    }

    static LtrpPlayer get(int uuid) {
        if(uuid == LtrpPlayer.INVALID_USER_ID)
            return null;
        Optional<LtrpPlayer> op = get()
                .stream()
                .filter(p -> p.getUUID() == uuid)
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }

    static LtrpPlayer get(Player player) {
        if(player == null)
            return null;
        if(player.isNpc())
            return null;
        Optional<LtrpPlayer> op = get()
                .stream()
                .filter(p -> p.equals(player))
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }

    static LtrpPlayer get(String name) {
        if(name == null)
            return null;
        Optional<LtrpPlayer> op = get()
                .stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }

    static LtrpPlayer getByPartName(String name) {
        if(name == null)
            return null;
        SortedMap<Integer, LtrpPlayer> unmatchedChars = new TreeMap<>();
        get()
                .stream()
                .filter(p -> p.getName().contains(name))
                .forEach(p -> {
                    unmatchedChars.put(p.getName().length() - name.length(), p);
                });
        return unmatchedChars.values().stream().findFirst().get();
    }

    static LtrpPlayer getClosest(LtrpPlayer player, float maxdistance) {
        LtrpPlayer closest = null;
        float closestDistance = maxdistance;
        for(LtrpPlayer p : get()) {
            float distance = p.getLocation().distance(player.getLocation());
            if(!p.equals(player) && distance <= closestDistance) {
                closest = p;
                closestDistance = distance;
            }
        }
        return closest;
    }

    static LtrpPlayer[] getClosestPlayers(LtrpPlayer player, float maxdistance) {
        List<Player> closest =new ArrayList<>();
        for(LtrpPlayer p : get()) {
            float distance = p.getLocation().distance(player.getLocation());
            if(!p.equals(player) && distance <= maxdistance) {
                closest.add(p);
            }
        }
        return (LtrpPlayer[])closest.toArray();
    }

    public static void sendAdminMessage(String s) {
        get().stream()
                .filter(p -> p.isAdmin() || p.getAdminLevel() > 0)
                .forEach(p -> {
                    p.sendMessage(Color.GREENYELLOW, s);
                });
    }

    /**
     * Sends an admin message to those admins that pass the condition
     * @param s message text
     * @param condition condition to pass(return true) for the message to be sent for that player
     */
    public static void sendAdminMessage(String s, Function<LtrpPlayer, Boolean> condition) {
        get().stream()
                .filter(p -> (p.isAdmin() || p.isModerator()) && condition.apply(p))
                .forEach(p -> {
                    p.sendMessage(Color.GREENYELLOW, s);
                });
    }

    public static void sendGlobalMessage(String s) {
        sendGlobalMessage(Color.GREENYELLOW, s);
    }

    public static void sendGlobalMessage(Color c, String s) {
        get().forEach(p -> p.sendMessage(c, s));
    }

    public static void sendModMessage(String s) {
        get().stream()
                .filter(p -> (p.isModerator() || p.isAdmin()) && !p.getSettings().isModChatDisabled())
                .forEach(p -> p.sendMessage(lt.ltrp.data.Color.MODERATOR, s));
    }

    public static void sendGlobalOocMessage(String s) {
        get().stream()
                .filter(p -> !p.getSettings().isOocDisabled())
                .forEach(p -> p.sendMessage(new Color(0xB1C8FBAA), s));
    }


    int getUUID();

    String getForumName();
    void setForumName(String name);

    PlayerSettings getSettings();
    void setSettings(PlayerSettings settings);

    default void sendInfoText(String msg)  {
        sendInfoText(msg, DEFAULT_INFOTEXT_DURATION);
    }
    void sendInfoText(String msg, int seconds);

    void setAdminLevel(int adminLevel);
    int getAdminLevel();
    int getModLevel();
    void setModLevel(int modLevel);
    boolean isModerator();

    void setLevel(int level);
    int getLevel();

    String getPassword();
    void setPassword(String password);

    String getSecretAnswer();
    void setSecretAnswer(String secretAnswer);

    String getSecretQuestion();
    void setSecretQuestion(String secretQuestion);

    boolean isInJail();

    LtrpWeaponData[] getWeapons();
    LtrpWeaponData getWeaponData(WeaponModel weaponModel);
    boolean ownsWeapon(WeaponModel model);
    boolean isWeaponSlotUsed(WeaponSlot slot);
    void removeWeapon(LtrpWeaponData weaponData);
    void removeWeapon(WeaponModel model);
    void removeJobWeapons();
    LtrpWeaponData getArmedWeaponData();
    void giveWeapon(LtrpWeaponData weaponData);

    LtrpVehicle getLastUsedVehicle();
    void setLastUsedVehicle(LtrpVehicle vehicle);

    PlayerCountdown getCountdown();
    void setCountdown(PlayerCountdown countdown);
    /**
     * Total unclaimed job money
     */
    int getTotalPaycheck();
    void setTotalPaycheck(int value);
    void addTotalPaycheck(int amount);
    /**
     * Current(current paydays) paycheck
     */
    void setCurrentPaycheck(int amount);
    int getCurrentPaycheck();
    void addCurrentPaycheck(int amount);
    /**
     * The user may have a contract binding him to a job, this returns the hours left on his contract
     */
    //int getJobContract();

    /**
     * Paydays a user spent online
     */
    int getOnlineHours();
    void setOnlineHours(int hours);

    /**
     * Basically this has one ue: to check if the user is allowed to get payday
     * If this is larger or equal to {@link PlayerController#MINUTES_FOR_PAYDAY} he will get payday
     */
    int getMinutesOnlineSincePayday();
    void setMinutesOnlineSincePayday(int minutes);

    void setBoxingStyle(int boxingStyle);
    int getBoxingStyle();

    PlayerInfoBox getInfoBox();

    PlayerLicenses getLicenses();
    void setLicenses(PlayerLicenses licenses);

    boolean isSeatbelt();
    void setSeatbelt(boolean seatbelt);

    boolean isMasked();
    void setMasked(boolean masked);
    String getMaskName();

    boolean isCuffed();
    void setCuffed(boolean cuffed);


    int getAge();
    void setAge(int age);

    int getRespect();
    void setRespect(int respect);

    int getDeaths();
    void setDeaths(int deaths);

    int getHunger();
    void setHunger(int hunger);

    AudioHandle getAudioHandle();
    void setVolume(int volume);

    PlayerDrugs getDrugs();
    void setDrugs(PlayerDrugs drugs);

    boolean isInComa();
    void setInComa(boolean isInComa);

    boolean isLoggedIn();
    boolean isDataLoaded();
    boolean isFactionManager();
    void setFactionManager(boolean set);

    Collection<PlayerOffer> getOffers();
    boolean containsOffer(Class type);
    <T extends PlayerOffer> Collection<T> getOffers(Class<T> type);
    <T extends PlayerOffer> T getOffer(Class<T> type);

    String getCharName();
    String getFirstName();
    String getLastName();

    void sendFadeMessage(Color color, String text, float distance);
    void sendErrorMessage(String message);
    void sendErrorMessage(int errorCode);
    void sendActionMessage(String message, float distance);
    void sendActionMessage(String s);
    void sendStateMessage(String s, float distance);
    void sendStateMessage(String s);
    default void sendMessage(String s, float distance) {
        sendMessage(Color.WHITE, s, distance);
    }
    void sendMessage(Color color, String s, float distance);
    void sendDebug(Color color, String message);
    void sendDebug(String message);
    void sendDebug(Object... objects);


    float getDistanceToPlayer(LtrpPlayer player);
    LtrpPlayer getClosestPlayer(float maxdistance);
    LtrpPlayer getClosestPlayer();
    LtrpPlayer[] getClosestPlayers(float maxdistance);
    void applyAnimation(Animation animation);
    default void applyLoopAnimation(String animLib, String animation, boolean lockX, boolean lockY, boolean stoppable) {
        applyLoopAnimation(animLib, animation, lockX, lockY, false, stoppable);
    }

    /**
     * Deprecated because of freeze parameter, it does not make sense to play a loop and then freeze.
     * @param animLib
     * @param animation
     * @param lockX
     * @param lockY
     * @param freeze
     * @param stoppable
     */
    @Deprecated
    default void applyLoopAnimation(String animLib, String animation, boolean lockX, boolean lockY, boolean freeze, boolean stoppable) {
        applyAnimation(animLib, animation, 4.1f, true, lockX, lockY, freeze, stoppable);
    }
    default void applyAnimation(String animLib, String anim, float speed, boolean loop, boolean lockX, boolean lockY, boolean freeze, int time, boolean forceSync) {
        applyAnimation(animLib, anim, speed, loop, lockX, lockY, freeze, time, forceSync, false);
    }
    void applyAnimation(String animLib, String anim, float speeed, boolean loop, boolean lockX, boolean lockY, boolean freeze, int time, boolean forceSync, boolean stopable);
    default void applyAnimation(String animLib, String anim, boolean lockX, boolean lockY, boolean freeze, int time, boolean stoppable) {
        applyAnimation(animLib, anim, 4.1f, false, lockX, lockY, freeze, time, true, stoppable);
    }
    void clearAnimations();
    Animation getAnimation();
    boolean isAnimationPlaying();

    /**
     *
     * @param animLib
     * @param animname
     * @param speed
     * @param loop
     * @param lockX
     * @param lockY
     * @param freeze
     */
    default void applyAnimation(String animLib, String animname, float speed, boolean loop, boolean lockX, boolean lockY, boolean freeze) {
        applyAnimation(animLib, animname, speed, loop, lockX, lockY, freeze, false);
    }
    void applyAnimation(String animLib, String animName, float speed, boolean loop, boolean lockX, boolean lockY, boolean freeze, boolean stoppable);
    boolean isAudioConnected();

    int getUcpId();
    void setUcpId(int ucpId);

    Timestamp getLastLogin();
    void setLastLogin(Timestamp timestamp);

    String getDescription();
    void setDescription(String description);

    String getNationality();
    void setNationality(String nationality);

    String getSex();
    void setSex(String sex);

    void freeze();
    void unfreeze();
    boolean isFrozen();

    void mute();
    void unMute();
    boolean isMuted();

    WalkStyle getWalkStyle();
    void setWalkStyle(WalkStyle walkStyle);

    TalkStyle getTalkStyle();
    void setTalkStyle(TalkStyle talkStyle);

    @Override
    void sendGameText(int time, int style, String text);
}