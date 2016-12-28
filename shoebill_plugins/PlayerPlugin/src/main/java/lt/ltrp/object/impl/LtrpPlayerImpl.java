package lt.ltrp.object.impl;

import lt.ltrp.*;
import lt.ltrp.data.*;
import lt.ltrp.data.Animation;
import lt.ltrp.data.Color;

import lt.ltrp.event.player.PlayerActionMessageEvent;
import lt.ltrp.event.player.PlayerStateMessageEvent;
import lt.ltrp.object.*;
import lt.ltrp.player.event.PlayerMuteEvent;
import lt.ltrp.player.event.PlayerUnMuteEvent;
import lt.ltrp.player.licenses.data.PlayerLicenses;
import lt.ltrp.player.settings.data.PlayerSettings;
import lt.maze.audio.AudioHandle;
import lt.maze.audio.AudioPlugin;
import lt.maze.streamer.object.DynamicLabel;
import net.gtaun.shoebill.constant.*;
import net.gtaun.shoebill.data.*;
import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.shoebill.exception.IllegalLengthException;
import net.gtaun.shoebill.object.*;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.resource.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class LtrpPlayerImpl extends PlayerDataImpl implements LtrpPlayer {

    private static final Logger logger = LoggerFactory.getLogger(LtrpPlayer.class);

    /**
     * A scheduled exectuor service to run animation clean up
     */
    private static final ScheduledExecutorService ANIMATION_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();


    /**
     * A task that clears {@link this#playingAnimation}
     * Note that this task is run on a background thread
     */
    private final Runnable ANIMATION_CLEAR_TASK = () -> {
        playingAnimation = null;
    };

    private Player player;

    private String forumName;
    private Property property;
    private LtrpWeaponData[] weapons;
    private LtrpVehicle lastUsedVehicle;
    private PlayerCountdown countdown;
    //private Job job;
    //private JobRank jobRank;
    private PlayerInfoBox infoBox;
    private PlayerLicenses licenses;
    private boolean seatbelt, masked, cuffed, isDestroyed, muted, frozen;
    private int boxStyle;
    private PlayerSettings settings;
    //private EventManager eventManager;

    /**
     * Label which is shown for administrators and moderators when this player is muted
     */
    private DynamicLabel muteLabel;

    /**
     * Textdraw used to display messages
     */
    private PlayerTextdraw infoTextTextdraw;
    /**
     * Timer used to hide {@link lt.ltrp.object.impl.LtrpPlayerImpl#infoTextTextdraw} messages
     */
    private Timer infoTextTimer;

    /**
     * Paydays a user spent online
     */
    private int onlineHours;

    /**
     * This paydays paycheck
     */
    private int currentPaycheck;
    /**
     * The user may have a contract binding him to a job, this hold the hours left on his contract
     */
    //private int jobContract;
    private AudioHandle audioHandle;
    /**
     * Not necessarily all existing player vehicles, just the ones that are currently loaded( or not spawned)
     * Basically lazy loading is used, if the vehicle was loaded once it will be stored
     */
  /*  private Map<PlayerVehicle, List<PlayerVehiclePermission>> loadedVehicles;
    private Map<Integer, Pair<Integer, List<PlayerVehiclePermission>>> vehicleMetadata;
    */
    private Collection<PlayerOffer> offers;

    /**
     * Player IC description
     */
    private String description;

    /**
     * Players nationality, aka country of origin
     */
    private String nationality;

    /**
     * User panel user ID
     */
    private int ucpId;

    /**
     * Timestamp of the last player login
     */
    private Timestamp lastLogin;

    private PlayerDrugs drugs;

    private boolean isInComa;
    private boolean dataLoaded, isFactionManager;

    /**
     * Animation the player is currently playing, null if none
     */
    private Animation playingAnimation;

    /**
     * A field to store the ScheduledFuture returned by {@link this#ANIMATION_EXECUTOR_SERVICE}
     */
    private ScheduledFuture<?> animationTaskFuture;


    public LtrpPlayerImpl(PlayerData playerData, Player player) {
        super(playerData);
        this.player = player;
        this.weapons = new LtrpWeaponData[13];
        this.infoBox = new PlayerInfoBoxImpl(this);
        this.offers = new ArrayList<>();
        PlayerContainer.INSTANCE.getPlayerList().add(this);
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

    @Override
    public void sendInfoText(String msg) {
        sendInfoText(msg, 30000);
    }

    public String getForumName() {
        return forumName;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName;
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

    @SuppressWarnings("unchecked")
    public <T extends PlayerOffer> Collection<T> getOffers(Class<T> type) {
        Collection<T> offers = new ArrayList<>();
        getOffers().stream().filter(o -> o.getType().equals(type)).forEach(o -> offers.add((T)o));
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

    @Override
    public String getFirstName() {
        int index = getName().indexOf("_");
        return getName().substring(0, index);
    }

    @Override
    public String getLastName() {
        int index = getName().indexOf("_");
        return getName().substring(index);
    }


    @Override
    public void sendFadeMessage(net.gtaun.shoebill.data.Color color, String s, float v) {
        float[] hsb = java.awt.Color.RGBtoHSB(color.getR(), color.getG(), color.getB(), null);
        LtrpPlayer.get().stream().forEach(p -> {
            float distanceToMessage = p.getLocation().distance(getLocation());
            if(distanceToMessage <= v) {
                hsb[2] -= v - distanceToMessage;
                Color c = new Color(java.awt.Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
                p.sendMessage(c, s);
            }
        });
    }

    @Override
    public void sendFadeMessage(Color color, String text, Location location) {
        float[] hsb = java.awt.Color.RGBtoHSB(color.getR(), color.getG(), color.getB(), null);
        float distanceToMessage = getLocation().distance(location);
        hsb[2] -= distanceToMessage;
        Color c = new Color(java.awt.Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
        sendMessage(c, text);
    }

    /*public void addJobExperience(int amount) {
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
    }*/
/*
    public int getJobContract() {
        return jobContract;
    }

    public void setJobContract(int jobContract) {
        this.jobContract = jobContract;
    }
*/
    public int getOnlineHours() {
        return onlineHours;
    }

    public void setOnlineHours(int onlineHours) {
        this.onlineHours = onlineHours;
    }

    public int getCurrentPaycheck() {
        return currentPaycheck;
    }

    public void setCurrentPaycheck(int currentPaycheck) {
        this.currentPaycheck = currentPaycheck;
    }

    public void addCurrentPaycheck(int amount) {
        setCurrentPaycheck(getCurrentPaycheck() + amount);
    }

    public void addTotalPaycheck(int amount) {
        setTotalPaycheck(getTotalPaycheck() + amount);
    }

/*
    public int getJobHours() {
        return jobHours;
    }

    public void setJobHours(int jobHours) {
        this.jobHours = jobHours;
    }*/


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

/*
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
    }*/

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

    public void sendInfoText(String s, int seconds) {
        // "lazy" loading
        if(infoTextTextdraw == null) {
            infoTextTextdraw = PlayerTextdraw.create(this, 13, 150, "_");
            infoTextTextdraw.setUseBox(true);
            infoTextTextdraw.setBoxColor(new lt.ltrp.data.Color(0x00000066));
            infoTextTextdraw.setTextSize(158f, 91f);
            infoTextTextdraw.setBackgroundColor(new Color(0x000000ff));
            infoTextTextdraw.setFont(TextDrawFont.FONT2);
            infoTextTextdraw.setLetterSize(0.36f, 1.5f);
            infoTextTextdraw.setColor(new Color(0xffffffff));
            infoTextTextdraw.setProportional(true);
            infoTextTextdraw.setShadowSize(0);
        }
        // Jei praeitas tekstas dar rodomas, nuÞudom timerá
        if(infoTextTimer != null && infoTextTimer.isRunning())
            infoTextTimer.stop();

        infoTextTextdraw.setText(s);
        infoTextTextdraw.show();
        infoTextTimer = Timer.create(seconds * 1000, 1, (i) -> {
            infoTextTextdraw.hide();
            infoTextTimer.destroy();
            infoTextTimer = null;
        });
        infoTextTimer.start();
    }

    @Override
    public boolean isModerator() {
        return super.getModLevel() > 0;
    }
/*
    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        Job oldJob = this.job;
        this.job = job;
        eventManager.dispatchEvent(new PlayerChangeJobEvent(this, oldJob, job));
    }

    public JobRank getJobRank() {
        return jobRank;
    }

    public void setJobRank(JobRank jobRank) {
        this.jobRank = jobRank;
    }*/

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
        // Alright, first we try to add ammo to an existing weapon
        for(int i = 0; i < weapons.length; i++) {
            // If the weapon models match we add the ammo and the actual weapon
            if(weapons[i] != null && weapons[i].getModel() == weaponData.getModel()) {
                weapons[i].ammo += weaponData.ammo;
                player.giveWeapon(weaponData);
                // If they are both non-job weapons, we update the storage record
                if(!weapons[i].isJob() && !weaponData.isJob()) {
                    new Thread(() -> {
                        PlayerPlugin.get(PlayerPlugin.class).getWeaponDao().update(weaponData);
                    }).start();
                    // If only the new weapon is non-job, we insert a new record
                } else if(!weaponData.isJob()) {
                    new Thread(() -> {
                        PlayerPlugin.get(PlayerPlugin.class).getWeaponDao().insert(this, weaponData);
                    }).start();
                }
                return;
            }
        }
        // If we couldn't add the weapon to an existent slot, we look for a new one
        for(int i = 0; i < weapons.length; i++) {
            if(weapons[i] == null || weapons[i].getAmmo() == 0) {
                weapons[i] = weaponData;
                player.giveWeapon(weaponData);
                new Thread(() -> {
                    PlayerPlugin.get(PlayerPlugin.class).getWeaponDao().insert(this, weaponData);
                }).start();
                return;
            }
        }
        logger.error("Could not add player weapon.");
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

    @Override
    public boolean isWeaponSlotUsed(WeaponSlot weaponSlot) {
        return player.getWeaponData(weaponSlot.getSlotId()) != null;
    }

    // public method for removing weapons
    public void removeWeapon(LtrpWeaponData weaponData) {
        new Thread(() -> {
            PlayerPlugin.get(PlayerPlugin.class).getWeaponDao().remove(weaponData);
        }).start();
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

    @Override
    public void removeWeapon(WeaponModel weaponModel) {

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

    @Override
    public boolean isInJail() {
        PenaltyPlugin plugin = ResourceManager.get().getPlugin(PenaltyPlugin.class);
        if(plugin != null) {
            return plugin.getJailData(this) != null;
        } else logger.error("Missing PEnaltyPlugin");
        return false;
    }

    @Override
    public int getBoxingStyle() {
        return boxStyle;
    }

    @Override
    public void setBoxingStyle(int boxStyle) {
        this.boxStyle = boxStyle;
        setFightStyle(FightStyle.get(boxStyle));
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

    public boolean isDataLoaded() {
        return dataLoaded;
    }

    public void setDataLoaded(boolean dataLoaded) {
        this.dataLoaded = dataLoaded;
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

    @Override
    public void sendErrorMessage(int errorCode) {
        this.sendMessage(Color.RED, "Atsipraðome bet ðiuo metu negalime uþbaigti jûsø veiksmo. Klaidos kodas " + errorCode + ".");
        logger.error("Error ID " + errorCode);
    }

    public void sendActionMessage(String s, float distance) {
        this.sendMessage(lt.ltrp.data.Color.ACTION, "* "+ getName() + " " + s, distance);
        this.setChatBubble(s, lt.ltrp.data.Color.ACTION, distance, s.length() * 50);
        getEventManager().dispatchEvent(new PlayerActionMessageEvent(this, s));
    }

    public void sendActionMessage(String s) {
        this.sendActionMessage(s, DEFAULT_ACTION_MESSAGE_DISTANCE);
    }

    public void sendStateMessage(String s, float distance) {
        this.sendMessage(lt.ltrp.data.Color.ACTION, "* " + s + " ((" + getName() + "))", distance);
        this.setChatBubble(s, lt.ltrp.data.Color.ACTION, distance, s.length() * 50);
        getEventManager().dispatchEvent(new PlayerStateMessageEvent(this, s));
    }

    public void sendStateMessage(String s) {
        this.sendStateMessage(s, 20.0f);
    }

    @Override
    public void sendMessage(String s, float v) {
        sendMessage(Color.WHITE, s, v);
    }

    @Override
    public void sendMessage(net.gtaun.shoebill.data.Color color, String s, float distance) {
        for(LtrpPlayer p : LtrpPlayer.get()) {
            if(getDistanceToPlayer(p) <= distance) {
                p.sendMessage(color, s);
            }
        }
    }

    @Override
    public void sendDebug(net.gtaun.shoebill.data.Color color, String s) {
        player.sendMessage(color, "[DEBUG]" + s);
    }

    @Override
    public void sendDebug(String s) {
        sendDebug(Color.LIGHTGREEN, s);
    }

    @Override
    public void sendDebug(Object... objects) {
        for(int i = 0; i < objects.length; i++)
            sendDebug(objects[i] == null ? "null" : objects[i].getClass().getSimpleName() + ":" +objects[i].toString());
    }

    public float getDistanceToPlayer(LtrpPlayer player) {
        return player.getLocation().distance(this.getLocation());
    }


    public LtrpPlayer getClosestPlayer(float maxdistance) {
        return LtrpPlayer.getClosest(this, maxdistance);
    }

    public LtrpPlayer getClosestPlayer() {
        return this.getClosestPlayer(Float.MAX_VALUE);
    }

    public LtrpPlayer[] getClosestPlayers(float maxdistance) {
        return LtrpPlayer.getClosestPlayers(this, maxdistance);
    }


    public boolean isAudioConnected() {
        return AudioPlugin.isConnected(this);
    }

    @Override
    public int getUcpId() {
        return ucpId;
    }

    @Override
    public void setUcpId(int i) {
        this.ucpId = i;
    }

    @Override
    public Timestamp getLastLogin() {
        return lastLogin;
    }

    @Override
    public void setLastLogin(Timestamp timestamp) {
        this.lastLogin = timestamp;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String s) {
        this.description = s;
    }

    @Override
    public String getNationality() {
        return nationality;
    }

    @Override
    public void setNationality(String s) {
        this.nationality = s;
    }

    @Override
    public void freeze() {
        frozen = true;
        toggleControllable(false);
    }

    @Override
    public void unfreeze() {
        frozen = false;
        toggleControllable(true);
    }

    @Override
    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public void mute() {
        muted = true;
        updateMuteLabel();
        getEventManager().dispatchEvent(new PlayerMuteEvent(this));
    }

    public void updateMuteLabel() {
        List<Integer> admins = LtrpPlayer.get().stream().filter(p -> p.isAdmin() || p.isModerator()).map(LtrpPlayer::getId).collect(Collectors.toList());
        muteLabel = DynamicLabel.create("Uþtildytas", Color.RED, getLocation(), 10f, this, admins.toArray(new Integer[0]));
    }

    @Override
    public void unMute() {
        muted = false;
        if(muteLabel != null) muteLabel.destroy();
        getEventManager().dispatchEvent(new PlayerUnMuteEvent(this));
    }

    @Override
    public boolean isMuted() {
        return muted;
    }


    // Overrides

    // Object overrides
    @Override
    public boolean equals(Object o) {
        if(o instanceof Player) {
            if(o instanceof LtrpPlayer) {
                return this.getUserId() == ((LtrpPlayer) o).getUUID();
            } else
                return this.player.equals(o);
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
        return (lt.ltrp.data.Color)player.getColor();
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
                LtrpVehicle v = LtrpVehicle.getByVehicle(vehicle);
                if(v == null)
                    logger.error("An instance of Vehicle found without a LtrpVehicle wrapper. IG ID:" + vehicle.getId() + " Location:" + vehicle.getLocation());
                return v;
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
    public void setColor(net.gtaun.shoebill.data.Color color) {
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
        super.setMoney(i);
        player.setMoney(i);
    }

    @Override
    public void giveMoney(int i) {
        super.setMoney(super.getMoney() + i);
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
        LtrpVehicle v = LtrpVehicle.getByVehicle(vehicle);
        if(v != null)
            setVehicle(v, i);
        else
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
    public void sendMessage(net.gtaun.shoebill.data.Color color, String s) {
        player.sendMessage(color, s);
    }

    @Override
    public void sendMessage(net.gtaun.shoebill.data.Color color, String s, Object... objects) {
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
    public void sendGameText(int time, int style, String s) {
        if(style > 6)
            GameTextStyleManager.sendGameText(this, s, style, time);
        else
            player.sendGameText(time, style, s);
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

    @Override
    public void applyAnimation(Animation animation) {
        // If it is a timed animation, we need to reset {@link this#playingAnimation} after it stops playing
        // If it freezes the player no reset is needed until {@link this#clearAnimations} is called
        if(!animation.isLoop() && !animation.isFreeze()) {
            // If we are playing an animation we need to stop it before starting a new one
            // The SAMP animation will be just fine, but we must reset OUR variables
            if(isAnimationPlaying() && animationTaskFuture != null) {
                animationTaskFuture.cancel(true);
            }
            animationTaskFuture = ANIMATION_EXECUTOR_SERVICE.schedule(ANIMATION_CLEAR_TASK, animation.getTime(), TimeUnit.MILLISECONDS);
        }
        this.playingAnimation = animation;
        this.player.applyAnimation(animation.getAnimLib(),
                animation.getAnimLib(),
                animation.getSpeed(),
                animation.isLoop() ? 1 : 0,
                animation.isLockX() ? 1 : 0,
                animation.isLockY() ? 1 : 0,
                animation.isFreeze() ? 1 : 0,
                animation.getTime(),
                animation.isForceSync() ? 1 : 0);
    }


    @Override
    public void applyAnimation(String animLib, String anim, float speeed, boolean loop, boolean lockX, boolean lockY, boolean freeze, int time, boolean forceSync, boolean stopable) {
        applyAnimation(new Animation(animLib, anim, speeed, loop, lockX, lockY, freeze, forceSync, time, stopable));
    }

    @Override
    public void applyAnimation(String animLib, String animName, float speed, boolean loop, boolean lockX, boolean lockY, boolean freeze, boolean stoppable) {
        applyAnimation(new Animation(animLib, animName, speed, loop, lockX, lockY, freeze, true, 0, stoppable));
    }

    public void clearAnimations() {
        clearAnimations(1);
    }

    @Override
    public Animation getAnimation() {
        return playingAnimation;
    }

    @Override
    public boolean isAnimationPlaying() {
        return playingAnimation != null;
    }

    @Override
    public void clearAnimations(int i) {
        this.playingAnimation = null;
        if(animationTaskFuture != null && !animationTaskFuture.isDone())
            animationTaskFuture.cancel(true);
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
    public void markerForPlayer(Player player, net.gtaun.shoebill.data.Color color) {
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

    public LtrpWeaponData getWeaponData(WeaponModel model) {
        for(int i = 0; i < weapons.length; i++) {
            if(weapons[i] != null && weapons[i].model == model) {
                return weapons[i];
            }
        }
        return null;
    }

    @Override
    public void giveWeapon(WeaponModel weaponModel, int i) {
        // addWeapon will also give the REAL weapon
        //player.giveWeapon(weaponModel, i);
        addWeapon(weaponModel, i);
    }

    public void giveWeapon(LtrpWeaponData weaponData) {
        logger.debug("giveWeapon LtrpWeaponData called. Model:"+ weaponData.getModel() + " ammo:" + weaponData.getAmmo());
        // addWeapon will also give the REAL weapon
        //player.giveWeapon(weaponData);
        addWeapon(weaponData);
    }

    @Override
    public void giveWeapon(WeaponData weaponData) {
        addWeapon(weaponData);
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
        super.setWantedLevel(i);
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


    public AudioHandle getAudioHandle() {
        return audioHandle;
    }

    @Override
    public void stopAudioStream() {
        if(isAudioConnected() && getAudioHandle() != null) {
            getAudioHandle().stop();
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
    public void selectTextDraw(net.gtaun.shoebill.data.Color color) {
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
    public void setChatBubble(String s, net.gtaun.shoebill.data.Color color, float v, int i) {
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

    @Override
    public void destroy() {
        isDestroyed = true;
        if(infoTextTextdraw != null) infoTextTextdraw.destroy();
        if(infoTextTimer != null) infoTextTimer.destroy();
        if(countdown != null) countdown.destroy();
        if(infoBox != null) infoBox.destroy();
        if(audioHandle != null) audioHandle.destroy();
        if(offers != null) offers.clear();
        if(muteLabel != null) muteLabel.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }
}
