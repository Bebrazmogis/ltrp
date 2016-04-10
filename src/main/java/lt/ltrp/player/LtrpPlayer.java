package lt.ltrp.player;

import javafx.util.Pair;
import lt.ltrp.InventoryEntity;
import lt.ltrp.LtrpGamemode;
import lt.ltrp.Util.PawnFunc;
import lt.ltrp.data.*;
import lt.ltrp.data.Animation;
import lt.ltrp.item.Inventory;
import lt.ltrp.job.ContractJob;
import lt.ltrp.job.ContractJobRank;
import lt.ltrp.job.Job;
import lt.ltrp.job.Rank;
import lt.ltrp.player.event.*;
import lt.ltrp.property.Property;
import lt.ltrp.vehicle.LtrpVehicle;
import lt.ltrp.vehicle.PlayerVehicle;
import lt.ltrp.vehicle.PlayerVehiclePermission;
import lt.maze.audio.AudioHandle;
import lt.maze.audio.AudioPlugin;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.constant.*;
import net.gtaun.shoebill.data.*;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.shoebill.exception.IllegalLengthException;
import net.gtaun.shoebill.object.*;
import net.gtaun.util.event.EventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class LtrpPlayer extends InventoryEntity implements Player {

    public static final int INVALID_USER_ID = 0;

    private static List<LtrpPlayer> players = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(LtrpPlayer.class);


    public static List<LtrpPlayer> get() {
        return players;
    }

    public static LtrpPlayer get(String name) {
        for(LtrpPlayer p : players) {
            if(p.getName().equals(name))
                return p;
        }
        return null;
    }

    public static LtrpPlayer getByPartName(String part) {
        SortedMap<Integer, LtrpPlayer> unmatchedChars = new TreeMap<>();
        for(LtrpPlayer p : players) {
            if (p.getName().contains(part)) {
                unmatchedChars.put(p.getName().length() - part.length(), p);
            }
        }
        return unmatchedChars.values().stream().findFirst().get();
    }

    public static LtrpPlayer get(int id) {
        for(LtrpPlayer p : players) {
            if(p.getId() == id)
                return p;
        }
        return null;
    }

    public static LtrpPlayer getByUserId(int uid) {
        for(LtrpPlayer p : players) {
            if(p.getUserId() == uid)
                return p;
        }
        return null;
    }

    public static LtrpPlayer get(Player player) {
        if(player == null)
            return null;
        if(player.isNpc())
            return null;
        for(LtrpPlayer p : players) {
            if(p.equals(player)) {
                return p;
            }
        }
        return null;
    }

    public static void remove(Player p) {
        logger.debug("remove. Removing player " + p.getId() + " from global list");
        players.remove(p);
    }

    public static LtrpPlayer getClosest(LtrpPlayer player, float maxdistance) {
        LtrpPlayer closest = null;
        float closestDistance = maxdistance;
        for(LtrpPlayer p : players) {
            float distance = p.getLocation().distance(player.getLocation());
            if(!p.equals(player) && distance <= closestDistance) {
                closest = p;
                closestDistance = distance;
            }
        }
        return closest;
    }

    public static LtrpPlayer[] getClosestPlayers(LtrpPlayer player, float maxdistance) {
        List<Player> closest =new ArrayList<>();
        float closestDistance = maxdistance;
        for(LtrpPlayer p : players) {
            float distance = p.getLocation().distance(player.getLocation());
            if(!p.equals(player) && distance <= closestDistance) {
                closest.add(p);
            }
        }
        return (LtrpPlayer[])closest.toArray();
    }

    public static void sendAdminMessage(String s) {
        for(LtrpPlayer p : players) {
            if(p.isAdmin() || p.getAdminLevel() > 0) {
                p.sendMessage(Color.GREENYELLOW, s);
            }
        }
    }

    public static void sendGlobalMessage(String s) {
        sendGlobalMessage(Color.GREENYELLOW, s);
    }

    public static void sendGlobalMessage(Color c, String s) {
        get().forEach(p -> p.sendMessage(c, s));
    }


    private Player player;

    private int adminLevel, level;

    private String password, secretAnswer, secretQuestion;
    private JailData jailData;
    private Property property;
    private LtrpWeaponData[] weapons;
    private LtrpVehicle lastUsedVehicle;
    private PlayerCountdown countdown;
    private Job job;
    private Rank jobRank;
    private PlayerInfoBox infoBox;
    private PlayerLicenses licenses;
    private boolean seatbelt, masked, cuffed;
    private int jobExperience, jobHours, boxStyle, age, respect, deaths, hunger;
    private PlayerSettings settings;
    private EventManager eventManager;
    /**
     * Paydays a user spent online
     */
    private int onlineHours;

    /**
     * Basically this has one ue: to check if the user is allowed to get payday
     * If this is larger or equal to {@link lt.ltrp.player.PlayerController#MINUTES_FOR_PAYDAY} he will get payday
     */
    private int minutesOnlineSincePayday;

    /**
     * Total unclaimed job money
     */
    private int totalPaycheck;
    /**
     * The user may have a contract binding him to a job, this hold the hours left on his contract
     */
    private int jobContract;
    private SpawnData spawnData;
    private AudioHandle audioHandle;
    /**
     * Not necessarily all existing player vehicles, just the ones that are currently loaded( or not spawned)
     * Basically lazy loading is used, if the vehicle was loaded once it will be stored
     */
    private Map<PlayerVehicle, List<PlayerVehiclePermission>> loadedVehicles;
    private Map<Integer, Pair<Integer, List<PlayerVehiclePermission>>> vehicleMetadata;
    private Collection<PlayerOffer> offers;

    private PlayerDrugs drugs;

    private boolean isInComa;
    private boolean loggedIn, dataLoaded, isFactionManager;


    public LtrpPlayer(Player player, int userid, EventManager manager) {
        super(userid, player.getName(), null);
        this.player = player;
        this.weapons = new LtrpWeaponData[13];
        this.infoBox = new PlayerInfoBox(this);
        this.offers = new ArrayList<>();
        this.eventManager = manager;
        players.add(this);
        logger.debug("Creating instance of LtrpPlayer. Player object id " +player.getId());
    }

    public int getUserId() {
        return super.getUUID();
    }

    public PlayerSettings getSettings() {
        return settings;
    }

    public void setSettings(PlayerSettings settings) {
        this.settings = settings;
    }

    public PlayerDrugs getDrugs() {
        if(drugs == null) {
            drugs = new PlayerDrugs(this);
        }
        return drugs;
    }

    public void setDrugs(PlayerDrugs drugs) {
        this.drugs = drugs;
    }

    public Collection<PlayerOffer> getOffers() {
        return offers;
    }

    public boolean containsOffer(Class type) {
        return offers.stream().filter(o -> o.getType() == type).findFirst().isPresent();
    }

    public <T extends PlayerOffer> T getOffer(Class<T> type) {
        Optional<T> optional = (Optional<T>)offers.stream().filter(o -> o.getType() == type).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

    public PlayerCountdown getCountdown() {
        return countdown;
    }

    public void setCountdown(PlayerCountdown countdown) {
        this.countdown = countdown;
    }

    public String getCharName() {
        if(isMasked()) {
            return getMaskName();
        } else {
            return getName().replace("_", " ");
        }
    }

    public void addJobExperience(int amount) {
        setJobExperience(getJobExperience() + amount);
    }

    public int getJobExperience() {
        return jobExperience;
    }

    public void setJobExperience(int jobExperience) {
        this.jobExperience = jobExperience;
        if(getJob() instanceof ContractJob) {
            ContractJobRank rank = (ContractJobRank)getJobRank();
            if(rank.getXpNeeded() <= getJobExperience()) {
                setJobRank(rank);
            }
        }
    }

    public int getMinutesOnlineSincePayday() {
        return minutesOnlineSincePayday;
    }

    public void setMinutesOnlineSincePayday(int minutesOnlineSincePayday) {
        this.minutesOnlineSincePayday = minutesOnlineSincePayday;
    }

    public int getJobContract() {
        return jobContract;
    }

    public void setJobContract(int jobContract) {
        this.jobContract = jobContract;
    }

    public int getOnlineHours() {
        return onlineHours;
    }

    public void setOnlineHours(int onlineHours) {
        this.onlineHours = onlineHours;
    }

    public void addTotalPaycheck(int amount) {
        setTotalPaycheck(getTotalPaycheck() + amount);
    }

    public int getTotalPaycheck() {
        return totalPaycheck;
    }

    public void setTotalPaycheck(int totalPaycheck) {
        this.totalPaycheck = totalPaycheck;
    }

    public int getJobHours() {
        return jobHours;
    }

    public void setJobHours(int jobHours) {
        this.jobHours = jobHours;
    }


    public boolean isSeatbelt() {
        return seatbelt;
    }

    public String getMaskName() {
        return "((Kaukëtasis " + (getId() + 400) + "))";
    }

    public boolean isMasked() {
        return masked;
    }

    public void setMasked(boolean masked) {
        this.masked = masked;
        LtrpPlayer.get().forEach(p -> this.showNameTagForPlayer(p, masked));
    }

    public JailData getJailData() {
        return jailData;
    }

    public void setJailData(JailData data) {
        this.jailData = data;
    }

    public void unjail() {
        eventManager.dispatchEvent(new PlayerUnJailEvent(this, jailData));
        this.jailData = null;
    }

    public void jail(JailData data) {
        this.jailData = data;
        eventManager.dispatchEvent(new PlayerJailEvent(this, jailData));
    }


    public void jail(JailData.JailType type, int time, LtrpPlayer jailer) {
        this.jail(new JailData(0, this, type, time, jailer.getName()));
    }

    public Map<PlayerVehicle, List<PlayerVehiclePermission>> getLoadedVehicles() {
        return loadedVehicles;
    }

    public void setLoadedVehicles(Map<PlayerVehicle, List<PlayerVehiclePermission>> loadedVehicles) {
        this.loadedVehicles = loadedVehicles;
    }

    public Map<Integer, Pair<Integer, List<PlayerVehiclePermission>>> getVehicleMetadata() {
        return vehicleMetadata;
    }

    public void setVehicleMetadata(Map<Integer, Pair<Integer, List<PlayerVehiclePermission>>> vehicleMetadata) {
        this.vehicleMetadata = vehicleMetadata;
    }

    public LtrpVehicle getLastUsedVehicle() {
        return lastUsedVehicle;
    }

    public void setLastUsedVehicle(LtrpVehicle lastUsedVehicle) {
        this.lastUsedVehicle = lastUsedVehicle;
    }

    public PlayerInfoBox getInfoBox() {
        return infoBox;
    }

    public PlayerLicenses getLicenses() {
        return licenses;
    }

    public void setLicenses(PlayerLicenses licenses) {
        this.licenses = licenses;
    }

    public void setInfoBox(PlayerInfoBox infoBox) {
        this.infoBox = infoBox;
    }

    public void sendInfoText(String s) {
        AmxCallable ShowInfoText = PawnFunc.getPublicMethod("ShowInfoText");
        if(ShowInfoText != null) {
            ShowInfoText.call(this.getId(), s, 2500);
        } else {
            player.sendMessage(Color.PALEGOLDENROD, "[INFOTEXT]" + s);
        }
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        eventManager.dispatchEvent(new PlayerChangeJobEvent(this, this.job, job));
        this.job = job;
    }

    public Rank getJobRank() {
        return jobRank;
    }

    public void setJobRank(Rank jobRank) {
        this.jobRank = jobRank;
    }

    public boolean getSeatbelt() {
        return seatbelt;
    }

    public void setSeatbelt(boolean seatbelt) {
        this.seatbelt = seatbelt;
    }

    // Internal weapon management
    private void addWeapon(WeaponModel model, int ammo) {
        addWeapon(new LtrpWeaponData(model, ammo, false));
    }

    private void addWeapon(LtrpWeaponData weaponData) {
        for(int i = 0; i < weapons.length; i++) {
            if(weapons[i] == null) {
                weapons[i] = weaponData;
                break;
            }
        }
    }

    private void addWeapon(WeaponData weaponData) {
        addWeapon(new LtrpWeaponData(weaponData, false));
    }

    private void clearWeapons() {
        for(int i = 0; i < weapons.length; i++) {
            weapons[i] = null;
        }
    }

    public LtrpWeaponData[] getWeapons() {
        return weapons;
    }

    public boolean ownsWeapon(WeaponModel model) {
        for(int i = 0; i < weapons.length; i++) {
            if(weapons[i] != null && weapons[i].getModel().equals(model)) {
                return true;
            }
        }
        return false;
    }

    // public method for removing weapons
    public void removeWeapon(LtrpWeaponData weaponData) {
        LtrpWeaponData[] newWeapons = new LtrpWeaponData[13];
        int newWeaponCount = 0;
        for(int i = 0; i < weapons.length; i++) {
            if(weapons[i] != weaponData) {
                newWeapons[newWeaponCount++] = weapons[i];
            }
        }
        resetWeapons();
        this.weapons = newWeapons;
    }

    public void removeJobWeapons() {
        LtrpWeaponData[] newWeapons = new LtrpWeaponData[13];
        int newWeaponCount = 0;
        for(int i = 0; i < weapons.length; i++) {
            if(!weapons[i].isJob()) {
                newWeapons[newWeaponCount++] = weapons[i];
            }
        }
        resetWeapons();
        this.weapons = newWeapons;
    }



    public boolean isCuffed() {
        return cuffed;
    }

    public void setCuffed(boolean cuffed) {
        this.cuffed = cuffed;
        if(cuffed) {
            this.setSpecialAction(SpecialAction.CUFFED);
            getAttach().getSlot(0).set(PlayerAttachBone.HAND_RIGHT, 19418, new Vector3D(-0.011f, 0.028f, -0.022f), new Vector3D(-15.600012f, -33.699977f, -81.700035f), new Vector3D(0.891999f, 1.0f, 1.168f), 0, 0);
        } else {
            this.setSpecialAction(SpecialAction.NONE);
            getAttach().getSlot(0).remove();
        }
    }

    public SpawnData getSpawnData() {
        return spawnData;
    }

    public void setSpawnData(SpawnData spawnData) {
        this.spawnData = spawnData;
    }

    public String getSecretAnswer() {
        return secretAnswer;
    }

    public void setSecretAnswer(String secretAnswer) {
        this.secretAnswer = secretAnswer;
    }

    public String getSecretQuestion() {
        return secretQuestion;
    }

    public void setSecretQuestion(String secretQuestion) {
        this.secretQuestion = secretQuestion;
    }

    public int getBoxStyle() {
        return boxStyle;
    }

    public void setBoxStyle(int boxStyle) {
        this.boxStyle = boxStyle;
        setFightStyle(FightStyle.get(boxStyle));
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getRespect() {
        return respect;
    }

    public void setRespect(int respect) {
        this.respect = respect;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public boolean isInComa() {
        return isInComa;
    }

    public void setInComa(boolean isInComa) {
        this.isInComa = isInComa;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public int getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(int adminLevel) {
        this.adminLevel = adminLevel;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isDataLoaded() {
        return dataLoaded;
    }

    public void setDataLoaded(boolean dataLoaded) {
        this.dataLoaded = dataLoaded;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isFactionManager() {
        return isFactionManager;
    }

    public void setFactionManager(boolean isFactionManager) {
        this.isFactionManager = isFactionManager;
    }

    public void sendErrorMessage(String s) {
        this.sendMessage(Color.RED, s);
    }

    public void sendActionMessage(String s, float distance) {
        this.sendMessage(lt.ltrp.data.Color.ACTION, "* "+ getName() + " " + s, distance);
        this.eventManager.dispatchEvent(new PlayerActionMessageEvent(this, s));
    }

    public void sendActionMessage(String s) {
        this.sendActionMessage(s, 20.0f);
    }

    public void sendStateMessage(String s, float distance) {
        this.sendMessage(lt.ltrp.data.Color.ACTION, "* " + s + " ((" + getName() + "))", distance);
        this.eventManager.dispatchEvent(new PlayerStateMessageEvent(this, s));
    }

    public void sendStateMessage(String s) {
        this.sendStateMessage(s, 20.0f);
    }

    public void sendMessage(Color color, String s, float distance) {
        for(LtrpPlayer p : players) {
            if(getDistanceToPlayer(p) <= distance) {
                p.sendMessage(color, s);
            }
        }
    }

    public float getDistanceToPlayer(LtrpPlayer player) {
        return player.getLocation().distance(this.getLocation());
    }


    public LtrpPlayer getClosestPlayer(float maxdistance) {
        return getClosest(this, maxdistance);
    }

    public LtrpPlayer getClosestPlayer() {
        return this.getClosestPlayer(Float.MAX_VALUE);
    }

    public LtrpPlayer[] getClosestPlayers(float maxdistance) {
        return getClosestPlayers(this, maxdistance);
    }

    public void applyAnimation(Animation animation) {
        applyAnimation(animation.getAnimLib(), animation.getAnimName(), animation.getSpeed(),
                animation.isLoop() ? 1 : 0,
                animation.isLockX() ? 1 : 0,
                animation.isLockY() ? 1 : 0,
                animation.isFreeze() ? 1 : 0,
                animation.getTime(),
                animation.isForseSync() ? 1 : 0);
    }


    public boolean isAudioConnected() {
        return AudioPlugin.isConnected(this);
    }


    // Overrides

    // Object overrides
    @Override
    public boolean equals(Object o) {
        if(o instanceof Player) {
            if(o instanceof LtrpPlayer) {
                return this.getUserId() == ((LtrpPlayer) o).getUserId();
            } else
                return this.player == o;
        }
        return false;
    }

    @Override
    public String toString() {
        return "uuid=" + getUUID() + player.toString();
    }

    // Player overrides

    @Override
    public boolean isOnline() {
        return player.isOnline();
    }

    @Override
    public int getId() {
        return player.getId();
    }

    @Override
    public PlayerKeyState getKeyState() {
        return player.getKeyState();
    }

    @Override
    public PlayerAttach getAttach() {
        return player.getAttach();
    }

    @Override
    public PlayerWeaponSkill getWeaponSkill() {
        return player.getWeaponSkill();
    }

    @Override
    public int getPing() {
        return player.getPing();
    }

    @Override
    public int getTeam() {
        return player.getTeam();
    }

    @Override
    public int getSkin() {
        return player.getSkin();
    }

    @Override
    public int getWantedLevel() {
        return player.getWantedLevel();
    }

    @Override
    public int getCodepage() {
        return player.getCodepage();
    }

    @Override
    public String getIp() {
        return player.getIp();
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public Color getColor() {
        return player.getColor();
    }

    @Override
    public long getUpdateCount() {
        return player.getUpdateCount();
    }


    @Override
    public float getHealth() {
        return player.getHealth();
    }

    @Override
    public float getArmour() {
        return player.getArmour();
    }

    @Override
    public WeaponModel getArmedWeapon() {
        return player.getArmedWeapon();
    }

    public LtrpWeaponData getArmedWeaponData() {
        WeaponModel armedWeapon = this.player.getArmedWeapon();
        for(LtrpWeaponData wd : weapons) {
            if(wd.getModel() == armedWeapon) {
                return wd;
            }
        }
        return null;
    }


    @Override
    public void setArmedWeapon(WeaponModel weaponModel) {
        player.setArmedWeapon(weaponModel);
    }

    @Override
    public int getArmedWeaponAmmo() {
        return player.getArmedWeaponAmmo();
    }

    @Override
    public int getMoney() {
        return player.getMoney();
    }

    @Override
    public int getScore() {
        return player.getScore();
    }

    @Override
    public int getWeather() {
        return player.getWeather();
    }

    @Override
    public int getCameraMode() {
        return player.getCameraMode();
    }

    @Override
    public float getCameraAspectRatio() {
        return player.getCameraAspectRatio();
    }

    @Override
    public float getCameraZoom() {
        return player.getCameraZoom();
    }

    @Override
    public FightStyle getFightStyle() {
        return player.getFightStyle();
    }

    @Override
    public LtrpVehicle getVehicle() {
        Vehicle vehicle = player.getVehicle();
        if(vehicle != null) {
            if(vehicle instanceof LtrpVehicle) {
                return (LtrpVehicle) vehicle;
            } else {
                logger.error("An instance of Vehicle found. IG ID:" + vehicle.getId() + " Location:" + vehicle.getLocation());
                return LtrpVehicle.getByVehicle(vehicle);
            }
        }
        return null;
    }

    @Override
    public int getVehicleSeat() {
        return player.getVehicleSeat();
    }

    @Override
    public SpecialAction getSpecialAction() {
        return player.getSpecialAction();
    }

    @Override
    public Player getSpectatingPlayer() {
        return player.getSpectatingPlayer();
    }

    @Override
    public Vehicle getSpectatingVehicle() {
        return player.getSpectatingVehicle();
    }

    @Override
    public float getAngle() {
        return player.getAngle();
    }

    @Override
    public AngledLocation getLocation() {
        return player.getLocation();
    }

    @Override
    public Area getWorldBound() {
        return player.getWorldBound();
    }

    @Override
    public Velocity getVelocity() {
        return player.getVelocity();
    }

    @Override
    public PlayerState getState() {
        return player.getState();
    }

    @Override
    public Checkpoint getCheckpoint() {
        return player.getCheckpoint();
    }

    @Override
    public RaceCheckpoint getRaceCheckpoint() {
        return player.getRaceCheckpoint();
    }

    @Override
    public DialogId getDialog() {
        return player.getDialog();
    }

    @Override
    public boolean isStuntBonusEnabled() {
        return player.isStuntBonusEnabled();
    }

    @Override
    public boolean isSpectating() {
        return player.isSpectating();
    }

    @Override
    public boolean isRecording() {
        return player.isRecording();
    }

    @Override
    public boolean isControllable() {
        return player.isControllable();
    }

    @Override
    public void setCodepage(int i) {
        player.setCodepage(i);
    }

    @Override
    public void setName(String s) {
        super.setName(s);
        try {
            player.setName(s);
        } catch (IllegalLengthException e) {
            e.printStackTrace();
        } catch (AlreadyExistException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSpawnInfo(float v, float v1, float v2, int i, int i1, float v3, int i2, int i3, WeaponModel weaponModel, int i4, WeaponModel weaponModel1, int i5, WeaponModel weaponModel2, int i6) {
        player.setSpawnInfo(v, v1, v2, i, i1, v3, i2, i3, weaponModel, i4, weaponModel1, i5, weaponModel2, i6);
        addWeapon(weaponModel, i4);
        addWeapon(weaponModel1, i5);
        addWeapon(weaponModel2, i6);
    }

    @Override
    public void setSpawnInfo(Vector3D vector3D, int i, int i1, float v, int i2, int i3, WeaponData weaponData, WeaponData weaponData1, WeaponData weaponData2) {
        player.setSpawnInfo(vector3D, i, i1, v, i2, i3, weaponData, weaponData1, weaponData2);
        addWeapon(weaponData);
        addWeapon(weaponData1);
        addWeapon(weaponData2);
    }

    @Override
    public void setSpawnInfo(Location location, float v, int i, int i1, WeaponData weaponData, WeaponData weaponData1, WeaponData weaponData2) {
        player.setSpawnInfo(location, v, i, i1, weaponData, weaponData1, weaponData2);
        addWeapon(weaponData);
        addWeapon(weaponData1);
        addWeapon(weaponData2);
    }

    @Override
    public void setSpawnInfo(AngledLocation angledLocation, int i, int i1, WeaponData weaponData, WeaponData weaponData1, WeaponData weaponData2) {
        player.setSpawnInfo(angledLocation, i, i1, weaponData, weaponData1, weaponData2);
        addWeapon(weaponData);
        addWeapon(weaponData1);
        addWeapon(weaponData2);
    }

    @Override
    public void setSpawnInfo(SpawnInfo spawnInfo) {
        player.setSpawnInfo(spawnInfo);
    }

    @Override
    public void setColor(Color color) {
        player.setColor(color);
    }

    @Override
    public void setHealth(float v) {
        player.setHealth(v);
    }

    @Override
    public void setArmour(float v) {
        player.setArmour(v);
    }

    @Override
    public void setWeaponAmmo(WeaponModel weaponModel, int i) {
        player.setWeaponAmmo(weaponModel, i);
        for(WeaponData wd : weapons) {
            if(wd.getModel() == weaponModel) {
                wd.setAmmo(i);
            }
        }
    }

    @Override
    public void setMoney(int i) {
        player.setMoney(i);
    }

    @Override
    public void giveMoney(int i) {
        player.giveMoney(i);
    }

    @Override
    public void setScore(int i) {
        player.setScore(i);
    }

    @Override
    public void setWeather(int i) {
        player.setWeather(i);
    }

    @Override
    public void setFightStyle(FightStyle fightStyle) {
        player.setFightStyle(fightStyle);
    }

    @Override
    public void setVehicle(Vehicle vehicle, int i) {
        player.setVehicle(vehicle, i);
    }

    public void setVehicle(LtrpVehicle vehicle, int i) {
        player.setVehicle(vehicle, i);
    }

    @Override
    public void setVehicle(Vehicle vehicle) {
        player.setVehicle(vehicle);
    }

    @Override
    public void setLocation(float v, float v1, float v2) {
        player.setLocation(v, v1, v2);
    }

    @Override
    public void setLocation(Vector3D vector3D) {
        player.setLocation(vector3D);
    }

    @Override
    public void setLocation(Location location) {
        player.setLocation(location);
    }

    @Override
    public void setLocation(AngledLocation angledLocation) {
        player.setLocation(angledLocation);
    }

    @Override
    public void setLocationFindZ(float v, float v1, float v2) {
        player.setLocationFindZ(v, v1, v2);
    }

    @Override
    public void setLocationFindZ(Vector3D vector3D) {
        player.setLocationFindZ(vector3D);
    }

    @Override
    public void setLocationFindZ(Location location) {
        player.setLocationFindZ(location);
    }

    @Override
    public void setLocationFindZ(AngledLocation angledLocation) {
        player.setLocationFindZ(angledLocation);
    }

    @Override
    public void setAngle(float v) {
        player.setAngle(v);
    }

    @Override
    public void setInterior(int i) {
        player.setInterior(i);
    }

    @Override
    public void setWorld(int i) {
        player.setWorld(i);
    }

    @Override
    public void setWorldBound(Area area) {
        player.setWorldBound(area);
    }

    @Override
    public void setVelocity(Vector3D vector3D) {
        player.setVelocity(vector3D);
    }

    @Override
    public void sendMessage(String s) {
        player.sendMessage(s);
    }

    @Override
    public void sendMessage(Color color, String s) {
        player.sendMessage(color, s);
    }

    @Override
    public void sendMessage(Color color, String s, Object... objects) {
        player.sendMessage(color, s, objects);
    }

    @Override
    public void sendChat(Player player, String s) {
        player.sendChat(player, s);
    }

    @Override
    public void sendChatToAll(String s) {
        player.sendChatToAll(s);
    }

    @Override
    public void sendDeathMessage(Player player, Player player1, WeaponModel weaponModel) {
        player.sendDeathMessage(player, player1, weaponModel);
    }

    @Override
    public void sendGameText(int i, int i1, String s) {
        player.sendGameText(i, i1, s);
    }

    @Override
    public void sendGameText(int i, int i1, String s, Object... objects) {
        player.sendGameText(i, i1, s, objects);
    }

    @Override
    public void spawn() {
        player.spawn();
    }

    @Override
    public void setDrunkLevel(int i) {
        player.setDrunkLevel(i);
    }

    @Override
    public int getDrunkLevel() {
        return player.getDrunkLevel();
    }

    @Override
    @Deprecated
    public void applyAnimation(String s, String s1, float v, int i, int i1, int i2, int i3, int i4, int i5) {
            throw new NotImplementedException();
    }

    public void applyAnimation(String animlib, String anim, float speed, boolean loop, boolean lockX, boolean lockY, boolean freeze, int time, boolean forsesync) {
        player.applyAnimation(animlib, anim, speed, loop ? 1 : 0, lockX ? 1 : 0, lockY ? 1 : 0, freeze ? 1 : 0, time, forsesync ? 1 : 0);
    }

    public void applyAnimation(String animLib, String animname, float speed, boolean loop, boolean lockX, boolean lockY, boolean freeze) {
        this.applyAnimation(animLib, animname, speed, loop, lockX, lockY, freeze, 0, false);
    }

    public void clearAnimations() {
        clearAnimations(1);
    }

    @Override
    public void clearAnimations(int i) {
        player.clearAnimations(i);
    }

    @Override
    public int getAnimationIndex() {
        return player.getAnimationIndex();
    }

    @Override
    public void playSound(int i, float v, float v1, float v2) {
        player.playSound(i, v, v1, v2);
    }

    @Override
    public void playSound(int i, Vector3D vector3D) {
        player.playSound(i, vector3D);
    }

    @Override
    public void playSound(int i) {
        player.playSound(i);
    }

    @Override
    public void markerForPlayer(Player player, Color color) {
        player.markerForPlayer(player, color);
    }

    @Override
    public void showNameTagForPlayer(Player player, boolean b) {
        player.showNameTagForPlayer(player, b);
    }

    @Override
    public void kick() {
        player.kick();
    }

    @Override
    public void ban() {
        player.ban();
    }

    @Override
    public void ban(String s) {
        player.ban(s);
    }

    @Override
    public Menu getCurrentMenu() {
        return player.getCurrentMenu();
    }

    @Override
    public void setCameraPosition(float v, float v1, float v2) {
        player.setCameraPosition(v, v1, v2);
    }

    @Override
    public void setCameraPosition(Vector3D vector3D) {
        player.setCameraPosition(vector3D);
    }

    @Override
    public void setCameraLookAt(float v, float v1, float v2, CameraCutStyle cameraCutStyle) {
        player.setCameraLookAt(v, v1, v2, cameraCutStyle);
    }

    @Override
    public void setCameraLookAt(Vector3D vector3D, CameraCutStyle cameraCutStyle) {
        player.setCameraLookAt(vector3D, cameraCutStyle);
    }

    @Override
    public void setCameraLookAt(float v, float v1, float v2) {
        player.setCameraLookAt(v, v1, v2);
    }

    @Override
    public void setCameraLookAt(Vector3D vector3D) {
        player.setCameraLookAt(vector3D);
    }

    @Override
    public void setCameraBehind() {
        player.setCameraBehind();
    }

    @Override
    public Vector3D getCameraPosition() {
        return player.getCameraPosition();
    }

    @Override
    public Vector3D getCameraFrontVector() {
        return player.getCameraFrontVector();
    }

    @Override
    public boolean isInAnyVehicle() {
        return player.isInAnyVehicle();
    }

    @Override
    public boolean isInVehicle(Vehicle vehicle) {
        return player.isInVehicle(vehicle);
    }

    @Override
    public boolean isAdmin() {
        return player.isAdmin() || getAdminLevel() > 0;
    }

    @Override
    public boolean isStreamedIn(Player player) {
        return player.isStreamedIn(player);
    }

    @Override
    public boolean isNpc() {
        return player.isNpc();
    }

    @Override
    public void setCheckpoint(Checkpoint checkpoint) {
        player.setCheckpoint(checkpoint);
    }

    @Override
    public void disableCheckpoint() {
        player.disableCheckpoint();
    }

    @Override
    public void setRaceCheckpoint(RaceCheckpoint raceCheckpoint) {
        player.setRaceCheckpoint(raceCheckpoint);
    }

    @Override
    public void disableRaceCheckpoint() {
        player.disableRaceCheckpoint();
    }

    @Override
    public void setTeam(int i) {
        player.setTeam(i);
    }

    @Override
    public void setSkin(int i) {
        player.setSkin(i);
    }

    @Override
    public WeaponState getWeaponState() {
        return player.getWeaponState();
    }

    @Override
    public LtrpWeaponData getWeaponData(int i) {
        return (LtrpWeaponData)player.getWeaponData(i);
    }

    @Override
    public void giveWeapon(WeaponModel weaponModel, int i) {
        player.giveWeapon(weaponModel, i);
        addWeapon(weaponModel, i);
    }

    public void giveWeapon(LtrpWeaponData weaponData) {
        logger.debug("giveWeapon LtrpWeaponData called. Model:"+ weaponData.getModel() + " ammo:" + weaponData.getAmmo());
        player.giveWeapon(weaponData);
        addWeapon(weaponData);
    }

    @Override
    public void giveWeapon(WeaponData weaponData) {
        player.giveWeapon(weaponData);
    }

    @Override
    public void resetWeapons() {
        player.resetWeapons();
        clearWeapons();
    }

    @Override
    public Time getTime() {
        return player.getTime();
    }

    @Override
    public void setTime(int i, int i1) {
        player.setTime(i, i1);
    }

    @Override
    public void setTime(Time time) {
        player.setTime(time);
    }

    @Override
    public void toggleClock(boolean b) {
        player.toggleClock(b);
    }

    @Override
    public void forceClassSelection() {
        player.forceClassSelection();
    }

    @Override
    public void setWantedLevel(int i) {
        player.setWantedLevel(i);
    }

    @Override
    public void playCrimeReport(int i, int i1) {
        player.playCrimeReport(i, i1);
    }

    @Override
    public void setShopName(ShopName shopName) {
        player.setShopName(shopName);
    }

    @Override
    public Vehicle getSurfingVehicle() {
        return player.getSurfingVehicle();
    }

    @Override
    public void removeFromVehicle() {
        player.removeFromVehicle();
    }

    @Override
    public void toggleControllable(boolean b) {
        player.toggleControllable(b);
    }

    @Override
    public void setSpecialAction(SpecialAction specialAction) {
        player.setSpecialAction(specialAction);
    }

    @Override
    public PlayerMapIcon getMapIcon() {
        return player.getMapIcon();
    }

    @Override
    public void enableStuntBonus(boolean b) {
        player.enableStuntBonus(b);
    }

    @Override
    public void toggleSpectating(boolean b) {
        player.toggleSpectating(b);
    }

    @Override
    public void spectate(Player player, SpectateMode spectateMode) {
        player.spectate(player, spectateMode);
    }

    @Override
    public void spectate(Vehicle vehicle, SpectateMode spectateMode) {
        player.spectate(vehicle, spectateMode);
    }

    @Override
    public void startRecord(RecordType recordType, String s) {
        player.startRecord(recordType, s);
    }

    @Override
    public void stopRecord() {
        player.stopRecord();
    }

    @Override
    public SampObject getSurfingObject() {
        return player.getSurfingObject();
    }

    @Override
    public String getNetworkStats() {
        return player.getNetworkStats();
    }

    @Override
    public Player getAimedTarget() {
        return player.getAimedTarget();
    }

    public void setVolume(int volume) {
        if(audioHandle != null && volume >= 0 && volume <= 100) {
            audioHandle.setVolume(volume);
        }
    }


    public AudioHandle getCurrentAudioHandle() {
        return audioHandle;
    }

    @Override
    public void stopAudioStream() {
        if(isAudioConnected() && getCurrentAudioHandle() != null) {
            getCurrentAudioHandle().stop();
        } else {
            player.stopAudioStream();
        }
    }

    @Override
    public void playAudioStream(String s) {
        if(isAudioConnected()) {
            if(audioHandle != null)
                audioHandle.stop();
            this.audioHandle = AudioHandle.playStreamed(this, s, false, false, false);
        } else {
            player.playAudioStream(s);
        }
    }

    @Override
    public void playAudioStream(String s, float v, float v1, float v2, float v3) {
        if(isAudioConnected()) {
            if(audioHandle == null) {
               audioHandle = AudioHandle.playStreamed(this, s, false, false, false);
            }
            audioHandle.set3DPosition(v, v1, v2, v3);
        } else {
            player.playAudioStream(s, v, v1, v2, v3);
        }
    }

    @Override
    public void playAudioStream(String s, Vector3D vector3D, float v) {
        playAudioStream(s, vector3D.x, vector3D.y, vector3D.z, v);
    }

    @Override
    public void playAudioStream(String s, Radius radius) {
        playAudioStream(s, radius.x, radius.y, radius.z, radius.radius);
    }


    @Override
    public void removeBuilding(int i, float v, float v1, float v2, float v3) {
        player.removeBuilding(i, v, v1, v2, v3);
    }

    @Override
    public Vector3D getLastShotOrigin() {
        return player.getLastShotOrigin();
    }

    @Override
    public Vector3D getLastShotHitPosition() {
        return player.getLastShotHitPosition();
    }

    @Override
    public void removeBuilding(int i, Vector3D vector3D, float v) {
        player.removeBuilding(i, vector3D, v);
    }

    @Override
    public void removeBuilding(int i, Radius radius) {
        player.removeBuilding(i, radius);
    }

    @Override
    public void showDialog(DialogId dialogId, DialogStyle dialogStyle, String s, String s1, String s2, String s3) {
        player.showDialog(dialogId, dialogStyle, s, s1, s2, s3);
    }

    @Override
    public void cancelDialog() {
        player.cancelDialog();
    }

    @Override
    public boolean editObject(SampObject sampObject) {
        return player.editObject(sampObject);
    }

    @Override
    public boolean editPlayerObject(PlayerObject playerObject) {
        return player.editPlayerObject(playerObject);
    }

    @Override
    public void selectObject() {
        player.selectObject();
    }

    @Override
    public void cancelEdit() {
        player.cancelEdit();
    }

    @Override
    public void attachCameraTo(SampObject sampObject) {
        player.attachCameraTo(sampObject);
    }

    @Override
    public void attachCameraTo(PlayerObject playerObject) {
        player.attachCameraTo(playerObject);
    }

    @Override
    public void interpolateCameraPosition(float v, float v1, float v2, float v3, float v4, float v5, int i, CameraCutStyle cameraCutStyle) {
        player.interpolateCameraPosition(v, v1, v2, v3, v4, v5, i, cameraCutStyle);
    }

    @Override
    public void interpolateCameraPosition(Vector3D vector3D, Vector3D vector3D1, int i, CameraCutStyle cameraCutStyle) {
        player.interpolateCameraPosition(vector3D, vector3D1, i, cameraCutStyle);
    }

    @Override
    public void interpolateCameraLookAt(float v, float v1, float v2, float v3, float v4, float v5, int i, CameraCutStyle cameraCutStyle) {
        player.interpolateCameraPosition(v, v1, v2, v3, v4, v5, i, cameraCutStyle);
    }

    @Override
    public void interpolateCameraLookAt(Vector3D vector3D, Vector3D vector3D1, int i, CameraCutStyle cameraCutStyle) {
        player.interpolateCameraPosition(vector3D, vector3D1, i, cameraCutStyle);
    }

    @Override
    public void selectTextDraw(Color color) {
        player.selectTextDraw(color);
    }

    @Override
    public void cancelSelectTextDraw() {
        player.cancelEdit();
    }

    @Override
    public void createExplosion(float v, float v1, float v2, int i, float v3) {
        player.createExplosion(v, v1, v2, i, v3);
    }

    @Override
    public String getVersion() {
        return player.getVersion();
    }

    @Override
    public LocationZone getMainZoneName() {
        return player.getMainZoneName();
    }

    @Override
    public LocationZone getZoneName() {
        return player.getZoneName();
    }

    /**
     *
     * @return returns the time a player is connected in milliseconds
     */
    @Override
    public int getConnectedTime() {
        return player.getConnectedTime();
    }

    @Override
    public int getMessagesReceived() {
        return player.getMessagesReceived();
    }

    @Override
    public int getBytesReceived() {
        return player.getBytesReceived();
    }

    @Override
    public int getMessagesSent() {
        return player.getMessagesSent();
    }

    @Override
    public int getBytesSent() {
        return player.getBytesSent();
    }

    @Override
    public int getMessagesRecvPerSecond() {
        return player.getMessagesRecvPerSecond();
    }

    @Override
    public float getPacketLossPercent() {
        return player.getPacketLossPercent();
    }

    @Override
    public int getConnectionStatus() {
        return player.getConnectionStatus();
    }

    @Override
    public String getIpPort() {
        return player.getIpPort();
    }

    @Override
    public void setChatBubble(String s, Color color, float v, int i) {
        player.setChatBubble(s, color, v, i);
    }

    @Override
    public void setVarInt(String s, int i) {
        player.setVarInt(s, i);
    }

    @Override
    public int getVarInt(String s) {
        return player.getVarInt(s);
    }

    @Override
    public void setVarString(String s, String s1) {
        player.setVarString(s, s1);
    }

    @Override
    public String getVarString(String s) {
        return player.getVarString(s);
    }

    @Override
    public void setVarFloat(String s, float v) {
        player.setVarFloat(s, v);
    }

    @Override
    public float getVarFloat(String s) {
        return player.getVarFloat(s);
    }

    @Override
    public boolean deleteVar(String s) {
        return player.deleteVar(s);
    }

    @Override
    public List<String> getVarNames() {
        return player.getVarNames();
    }

    @Override
    public PlayerVarType getVarType(String s) {
        return player.getVarType(s);
    }

    @Override
    public void disableRemoteVehicleCollisions(boolean b) {
        player.disableRemoteVehicleCollisions(b);
    }

    @Override
    public void enablePlayerCameraTarget(boolean b) {
        player.enablePlayerCameraTarget(b);
    }

    @Override
    public Actor getCameraTargetActor() {
        return player.getCameraTargetActor();
    }

    @Override
    public SampObject getCameraTargetObject() {
        return player.getCameraTargetObject();
    }

    @Override
    public Player getCameraTargetPlayer() {
        return player.getCameraTargetPlayer();
    }

    @Override
    public Vehicle getCameraTargetVehicle() {
        return player.getCameraTargetVehicle();
    }

    @Override
    public Actor getTargetActor() {
        return player.getTargetActor();
    }
}
