package lt.ltrp.player.object;


import lt.ltrp.InventoryEntity;
import lt.ltrp.job.Job;
import lt.ltrp.job.Rank;
import lt.ltrp.player.PlayerController;
import lt.ltrp.player.dao.PlayerDao;
import lt.ltrp.player.data.*;
import lt.ltrp.property.Property;
import lt.ltrp.vehicle.LtrpVehicle;
import lt.maze.audio.AudioHandle;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.WeaponData;
import net.gtaun.shoebill.object.Player;

import java.util.*;


/**
 * @author Bebras
 *         2016.04.07.
 */
public interface LtrpPlayer extends Player, InventoryEntity {
    public static final int INVALID_USER_ID = 0;


    static PlayerDao getPlayerDao() {
        return PlayerController.get().getPlayerDao();
    }


    static Collection<LtrpPlayer> get() {
        return PlayerController.get().getPlayers();
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

    public static void sendGlobalMessage(String s) {
        sendGlobalMessage(Color.GREENYELLOW, s);
    }

    public static void sendGlobalMessage(Color c, String s) {
        get().forEach(p -> p.sendMessage(c, s));
    }



    void sendInfoText(String msg);

    void setAdminLevel(int adminLevel);
    int getAdminLevel();

    void setLevel(int level);
    int getLevel();

    String getPassword();
    void setPassword(String password);

    String getSecretAnswer();
    void setSecretAnswer(String secretAnswer);

    String getSecretQuestion();
    void setSecretQuestion(String secretQuestion);

    JailData getJailData();
    void setJailData(JailData jailData);
    void jail(JailData jailData);
    void unjail();

    Property getProperty();
    void setProperty(Property property);

    LtrpWeaponData[] getWeapons();
    boolean ownsWeapon(WeaponModel model);
    void removeWeapon(LtrpWeaponData weaponData);
    void removeJobWeapons();
    LtrpWeaponData getArmedWeaponData();
    void giveWeapon(LtrpWeaponData weaponData);

    LtrpVehicle getLastUsedVehicle();
    void setLastUsedVehicle(LtrpVehicle vehicle);
    @Override
    LtrpVehicle getVehicle();

    PlayerCountdown getCountdown();
    void setCountdown(PlayerCountdown countdown);

    void setJob(Job job);
    void setJobRank(Rank rank);
    void setJobExperience(int experience);
    void setJobHours(int hours);
    void setJobContract(int contract);
    Job getJob();
    Rank getJobRank();
    int getJobExperience();
    int getJobHours();
    void addJobExperience(int amount);
    /**
     * Total unclaimed job money
     */
    int getTotalPaycheck();
    void setTotalPaycheck(int value);
    void addTotalPaycheck(int amount);
    /**
     * The user may have a contract binding him to a job, this returns the hours left on his contract
     */
    int getJobContract();

    /**
     * Paydays a user spent online
     */
    int getOnlineHours();
    void setOnlineHours(int hours);

    /**
     * Basically this has one ue: to check if the user is allowed to get payday
     * If this is larger or equal to {@link lt.ltrp.player.PlayerController#MINUTES_FOR_PAYDAY} he will get payday
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

    void setSpawnData(SpawnData spawnData);
    SpawnData getSpawnData();

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
    <T extends PlayerOffer> T getOffer(Class<T> type);

    String getCharName();

    void sendErrorMessage(String message);
    void sendActionMessage(String message, float distance);
    void sendActionMessage(String s);
    void sendStateMessage(String s, float distance);
    void sendStateMessage(String s);
    default void sendMessage(String s, float distance) {
        sendMessage(Color.WHITE, s, distance);
    }
    void sendMessage(Color color, String s, float distance);


    float getDistanceToPlayer(LtrpPlayer player);
    LtrpPlayer getClosestPlayer(float maxdistance);
    LtrpPlayer getClosestPlayer();
    LtrpPlayer[] getClosestPlayers(float maxdistance);
    void applyAnimation(Animation animation);
    void applyAnimation(String animlib, String anim, float speed, boolean loop, boolean lockX, boolean lockY, boolean freeze, int time, boolean forsesync);
    void applyAnimation(String animLib, String animname, float speed, boolean loop, boolean lockX, boolean lockY, boolean freeze);
    boolean isAudioConnected();



}