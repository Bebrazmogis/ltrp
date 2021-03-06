package lt.ltrp;

import kotlin.reflect.jvm.internal.KClassImpl;
import lt.ltrp.dao.BanDao;
import lt.ltrp.dao.JailDao;
import lt.ltrp.dao.WarnDao;
import lt.ltrp.player.vehicle.dao.impl.MySqlBanDaoImpl;
import lt.ltrp.player.vehicle.dao.impl.MySqlJailDaoImpl;
import lt.ltrp.player.vehicle.dao.impl.MySqlWarnDaoImpl;
import lt.ltrp.data.*;
import lt.ltrp.event.*;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.PlayerController;
import lt.ltrp.resource.DependentPlugin;
import lt.ltrp.spawn.data.SpawnData;
import lt.ltrp.spawn.event.PlayerRequestSpawnEvent;
import lt.maze.streamer.event.PlayerLeaveDynamicAreaEvent;
import lt.maze.streamer.object.DynamicArea;
import lt.maze.streamer.object.DynamicSphere;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.data.WeaponData;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

/**
 * @author Bebras
 *         2016.05.20.
 */
public class PenaltyPlugin extends DependentPlugin {

    public static final int MAX_WARNS = 3;
    private static final float[] IC_JAIL_POINTS = {
            1748.73694f, -1530.12915f,
            1759.70703f, -1528.74841f,
            1779.69531f, -1529.53101f,
            1825.33826f, -1532.40613f,
            1822.68469f, -1542.04663f,
            1806.10510f, -1539.72644f,
            1799.35583f, -1544.60132f,
            1793.10828f, -1544.24243f,
            1782.16882f, -1541.64026f,
            1763.82410f, -1557.46204f,
            1748.40503f, -1562.58337f,
            1763.28503f, -1562.31506f,
            1748.40503f, -1562.58337f
    };
    private static final float[] OOC_JAIL_POINTS = {
            2695.57910f, 2705.68799f,
            2694.98145f, 2701.04932f,
            2690.45239f, 2697.79907f,
            2685.85840f, 2698.04224f,
            2682.30859f, 2701.48462f,
            2681.92065f, 2705.84766f,
            2684.15820f, 2710.69653f,
            2688.89673f, 2711.89136f,
            2693.28223f, 2710.55469f
    };

    private static final Location IC_JAIL_ENTRANCE = new Location(1771.8267f, -1547.4008f, 8.9070f);
    private static final Location OOC_JAIL_ENTRANCE = new Location(2688.8000f, 2704.5161f, 22.7215f);
    private static final Location JAIL_RELEASE_LOCATION = new Location(1553.9462f, -1675.5209f, 15.1905f);
    private static final Location IC_PRISON_ENTRANCE = new Location(1810.24f,-1549.45f,5700.43f);

    private Logger logger;
    private DynamicArea icJailArea;
    private DynamicArea oocJailArea;
    private EventManagerNode node;
    private Timer jailTimeTimer;
    private JailDao jailDao;
    private BanDao banDao;
    private WarnDao warnDao;

    public PenaltyPlugin() {
        addDependency(new KClassImpl<>(DatabasePlugin.class));
        addDependency(new KClassImpl<>(SpawnPlugin.class));
    }


    @Override
    public void onDependenciesLoaded() {
        logger = getLogger();
        node = getEventManager().createChildNode();
        //icJailArea = DynamicPolygon.create(IC_JAIL_POINTS);
        //oocJailArea = DynamicPolygon.create(OOC_JAIL_POINTS);
        icJailArea = DynamicSphere.create(0f, 0f, 0f, 0f, 0, 0, null);
        oocJailArea = DynamicSphere.create(new Vector3D(), 0f);

        DatabasePlugin databasePlugin = ResourceManager.get().getPlugin(DatabasePlugin.class);
        this.jailDao = new MySqlJailDaoImpl(databasePlugin.getDataSource());
        this.banDao = new MySqlBanDaoImpl(databasePlugin.getDataSource());
        this.warnDao = new MySqlWarnDaoImpl(databasePlugin.getDataSource());
        scheduleTimers();
        registerEventHandlers();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        icJailArea.destroy();
        oocJailArea.destroy();
        jailTimeTimer.stop();
        jailTimeTimer.destroy();
        node.cancelAll();
    }


    private void registerEventHandlers() {
        this.node.registerHandler(PlayerLeaveDynamicAreaEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            JailData data = getJailData(player);
            DynamicArea area = e.getArea();
            // If the player should be in jail as is out of its bounds we return him.
            if(data != null) {
                logger.warn("Player " + player.getUUID() + " left jail area. JailData:" + data);
                if(area.equals(icJailArea) && data.getType().equals(JailData.JailType.OutOfCharacter)) {
                    player.setLocation(IC_JAIL_ENTRANCE);
                } else if(area.equals(oocJailArea) && data.getType().equals(JailData.JailType.InCharacter)) {
                    player.setLocation(OOC_JAIL_ENTRANCE);
                }
            }
        });


        this.node.registerHandler(PlayerJailEvent.class, e -> {
            LtrpPlayer p = e.getPlayer();
            p.removeJobWeapons();
        });

        this.node.registerHandler(PlayerUnJailEvent.class, e -> {
            LtrpPlayer player = e.getPlayer();
            player.sendMessage(Color.NEWS, "J�s esate paleid�iamas i� kal�jimo!");

        });

        this.node.registerHandler(PlayerConnectEvent.class, HandlerPriority.HIGH, e -> {
            Player player = e.getPlayer();
            BanData banData = getBanData(player.getName());
            if(banData != null) {
                if(banData.isPermanent() || !banData.isExpired()) {
                    player.sendMessage(Color.LIGHTRED, "D�mesio, J�s� veik�jui ir IP adresui yra u�drausta jungtis � �� server�");
                    player.sendMessage(Color.LIGHTRED, "Daugiau informacijos d�l draudimo lankytis pa�alinimo galite rasti forum.ltrp.lt");
                    player.sendMessage(Color.WHITE, "U�draustas veik�jas: " + player.getName());
                    player.sendMessage(Color.LIGHTGREY, "Nurodyta prie�astis: " + banData.getReason());
                    player.sendMessage(Color.WHITE, String.format("Blokuojamas IP adresas: %s | Blokuojami veik�jai: %s", banData.getIp() != null ? banData.getIp() : "-", player.getName()));
                    if(banData.getAdminId() != LtrpPlayer.INVALID_USER_ID)
                        player.sendMessage(Color.LIGHTGREY, "Draudim� lankytis suteik�: " + PlayerController.get().getUsernameByUUID(banData.getAdminId()));
                    if(!banData.isPermanent())
                        player.sendMessage(Color.LIGHTYELLOW, "Draudimo pabaigos data: " + new SimpleDateFormat().format(banData.getExpirationDate()));

                    player.kick();
                    e.interrupt();
                    e.disallow();
                }
                if(banData.isExpired()) {
                    unBan(PlayerController.instance.getData(player.getName()).getUUID());
                }
            }
        });

        this.node.registerHandler(PlayerRequestSpawnEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            SpawnData spawnData = e.getSpawnData();
            JailData jailData = getJailData(player);
            if(jailData != null) {
                Location l = getJailEntrance(jailData.getType());
                player.setSpawnInfo(l, 0f, spawnData.getSkin(), Player.NO_TEAM, new WeaponData(), new WeaponData(), new WeaponData());
                logger.info("RequestSpawnEvent player " + player.getUUID() + " needs to be in jail");
            }
        });
    }

    public Location getJailEntrance(JailData.JailType type) {
        switch(type) {
            case InCharacter:
                return IC_JAIL_ENTRANCE;
            case OutOfCharacter:
                return OOC_JAIL_ENTRANCE;
            case InCharacterPrison:
                return IC_PRISON_ENTRANCE;
            default:
                return null;
        }
    }

    /**
     *
     */
    private void scheduleTimers() {
        this.jailTimeTimer = Timer.create(60000, -1, i -> {
            LtrpPlayer.get()
                    .stream()
                    .filter(p -> getJailData(p) != null)
                    .forEach(p -> {
                        JailData jd = getJailData(p);
                        jd.setRemainingTime(jd.getRemainingTime() - 1);
                        if (jd.getRemainingTime() <= 0) {
                            unJail(p);
                        } else {
                            p.getInfoBox().setJailTime(jd.getRemainingTime());
                        }
                    });
        });
        this.jailTimeTimer.start();
    }

    /**
     * Places a player in jail
     * @param player player to be jailed
     * @param jailType jailType value
     * @param minutes duration of jail in minutes
     * @param jailedBy the user responsible for this jail entry
     */
    public void jail(LtrpPlayer player, JailData.JailType jailType, int minutes, LtrpPlayer jailedBy) {
        JailData jailData = new JailData(0, player, jailType, minutes * 60, minutes * 60, jailedBy.getUUID(), LocalDateTime.now());
        player.setLocation(getJailEntrance(jailType));
        jailDao.insert(jailData);
        node.dispatchEvent(new PlayerJailEvent(player, jailData));
    }

    /**
     * Frees a player from jail
     * @param player player to be freed
     */
    public void unJail(LtrpPlayer player) {
        JailData jailData = getJailData(player);
        if(jailData != null) {
            player.setLocation(JAIL_RELEASE_LOCATION);
            jailDao.remove(jailData);
            node.dispatchEvent(new PlayerUnJailEvent(player, jailData));
        }
    }

    /**
     *
     * @param player player
     * @return Returns the {@link lt.ltrp.data.JailData} object associated with the player
     */
    public JailData getJailData(LtrpPlayer player) {
        return jailDao.get(player);
    }

    /**
     * Bans a player temporarily, the ban is based on players username.
     * @param player player to be banned
     * @param reason reason for this ban
     * @param hours duration of this ban in hours
     * @param bannedBy admin that banned this user, may be null
     */
    public void banPlayer(LtrpPlayer player, String reason, int hours, LtrpPlayer bannedBy) {
        ban(player, new BanData(player.getUUID(), bannedBy != null ? bannedBy.getUUID() : LtrpPlayer.INVALID_USER_ID, reason, hours,  LocalDateTime.now(), null));
    }

    /**
     * Bans a player permanently without recording admin. The ban is based on players username
     * Should be used for automatic bans such as anti-cheat.
     * @param player player to ban
     * @param reason reason for this ban
     */
    public void banPlayer(LtrpPlayer player, String reason) {
        banPlayer(player, reason, null);
    }

    /**
     * Bans a player permanently. The ban is based on players username
     * @param player player to ban
     * @param reason reason for the ban
     * @param bannedBy the administrator who initiated the ban
     */
    public void banPlayer(LtrpPlayer player, String reason, LtrpPlayer bannedBy) {
        ban(player, new BanData(player.getUUID(), bannedBy != null ? bannedBy.getUUID() : LtrpPlayer.INVALID_USER_ID, reason,  LocalDateTime.now(), null));
    }

    /**
     * Bans a player for some amount of time. This method includes an IP ban
     * @param player player to be banned(and his IP)
     * @param reason reason for the ban
     * @param hours duration of the ban(in hours)
     * @param bannedBy the administrator that initiated this ban
     */
    public void banIp(LtrpPlayer player, String reason, int hours, LtrpPlayer bannedBy) {
        ban(player, new BanData(player.getUUID(), bannedBy != null ? bannedBy.getUUID() : LtrpPlayer.INVALID_USER_ID, reason, player.getIp(), hours, LocalDateTime.now(), null));
    }

    /**
     *  Bans a player permanently. This method includes an IP ban
     * @param player player to be banned(and his IP)
     * @param reason reason for the ban
     * @param bannedBy the administrator that initiated this ban
     */
    public void banIp(LtrpPlayer player, String reason, LtrpPlayer bannedBy) {
        ban(player, new BanData(player.getUUID(), bannedBy != null ? bannedBy.getUUID() : LtrpPlayer.INVALID_USER_ID, reason, player.getIp(), LocalDateTime.now(), null));
    }

    /**
     * Bans a player permanently without an administrator.
     * Ban is based on IP and username.
     * Should only be used for automatic bans.
     * @param player player to be banned(and his IP)
     * @param reason reason for this ban
     */
    public void banIp(LtrpPlayer player, String reason) {
        banIp(player, reason, null);
    }

    private void ban(LtrpPlayer player, BanData banData) {
        player.kick();
        banDao.insert(banData);
        node.dispatchEvent(new PlayerBanEvent(player, banData));
    }

    /**
     * Unbans an IP address.
     * @param ip address to be unbanned
     */
    public void unBan(String ip) {
        BanData banData = banDao.getByIp(ip);
        if(banData != null) {
            banData.setDeletedAt(LocalDateTime.now());
            banDao.update(banData);
        }
    }

    /**
     * Unbans a user by his UUID
     * @param userId UUID
     */
    public void unBan(int userId) {
        BanData banData = banDao.getByUser(userId);
        if(banData != null) {
            banDao.remove(banData);
        }
    }

    /**
     *
     * @param player the banned player
     * @return returns the {@link lt.ltrp.data.BanData} object associated with the current ban, null if the player is not banned
     */
    public BanData getBanData(LtrpPlayer player) {
        return banDao.getByUserOrIp(player.getUUID(), player.getIp());
    }

    /**
     * Finds the usernames bandata
     * @param name
     * @return BanData for the specified name, or null if not banned
     */
    public BanData getBanData(String name) {
        return banDao.getByName(name);
    }

    /**
     * Adds a warning to a player. If he has {@link lt.ltrp.PenaltyPlugin#MAX_WARNS} or more warnings he is automatically banned.
     * @param player player to warn
     * @param reason reason
     * @param warnedBy administrator that initiated this warning
     */
    public void warn(LtrpPlayer player, String reason, LtrpPlayer warnedBy) {
        WarnData warnData = new WarnData(player.getUUID(), warnedBy.getUUID(), reason, LocalDateTime.now());
        warnDao.insert(warnData);
        node.dispatchEvent(new PlayerWarnEvent(player, warnData));
        if(getWarnCount(player) >= MAX_WARNS) {
            banPlayer(player, MAX_WARNS + " warns.");
        }
    }

    /**
     *
     * @param player player
     * @return Returns the warning count for a user
     */
    public int getWarnCount(LtrpPlayer player) {
        return warnDao.getCount(player.getUUID());
    }

}
