package lt.ltrp;

import lt.ltrp.dao.BanDao;
import lt.ltrp.dao.JailDao;
import lt.ltrp.dao.WarnDao;
import lt.ltrp.dao.impl.MySqlBanDaoImpl;
import lt.ltrp.dao.impl.MySqlJailDaoImpl;
import lt.ltrp.dao.impl.MySqlWarnDaoImpl;
import lt.ltrp.data.*;
import lt.ltrp.event.PlayerBanEvent;
import lt.ltrp.event.PlayerRequestSpawnEvent;
import lt.ltrp.event.PlayerWarnEvent;
import lt.ltrp.event.player.PlayerJailEvent;
import lt.ltrp.event.player.PlayerUnJailEvent;
import lt.ltrp.object.LtrpPlayer;
import lt.maze.streamer.event.PlayerLeaveDynamicAreaEvent;
import lt.maze.streamer.object.DynamicArea;
import lt.maze.streamer.object.DynamicPolygon;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.WeaponData;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.05.20.
 */
public class PenaltyPlugin extends Plugin {

    private static final int MAX_WARNS = 3;
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

    private Logger logger;
    private DynamicArea icJailArea;
    private DynamicArea oocJailArea;
    private EventManagerNode node;
    private Timer jailTimeTimer;
    private JailDao jailDao;
    private BanDao banDao;
    private WarnDao warnDao;

    public PenaltyPlugin() {
        icJailArea = DynamicPolygon.create(IC_JAIL_POINTS);
        oocJailArea = DynamicPolygon.create(OOC_JAIL_POINTS);
    }


    @Override
    protected void onEnable() throws Throwable {
        logger = getLogger();
        node = getEventManager().createChildNode();

        final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
        dependencies.add(SpawnPlugin.class);
        int missing = 0;
        for(Class<? extends Plugin> clazz : dependencies) {
            if(ResourceManager.get().getPlugin(clazz) == null)
                missing++;
            else
                dependencies.remove(clazz);
        }
        if(missing > 0) {
            node.registerHandler(ResourceEnableEvent.class, e -> {
                Resource r = e.getResource();
                if(r instanceof Plugin && dependencies.contains(r.getClass())) {
                    dependencies.remove(r.getClass());
                    if(dependencies.size() == 0)
                        load();
                }
            });
        } else load();
    }


    private void load() {
        DatabasePlugin databasePlugin = ResourceManager.get().getPlugin(DatabasePlugin.class);
        this.jailDao = new MySqlJailDaoImpl(databasePlugin.getDataSource());
        this.banDao = new MySqlBanDaoImpl(databasePlugin.getDataSource());
        this.warnDao = new MySqlWarnDaoImpl(databasePlugin.getDataSource());
        scheduleTimers();
        registerEventHandlers();
    }

    @Override
    protected void onDisable() throws Throwable {
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

        });

        this.node.registerHandler(PlayerUnJailEvent.class, e -> {
            LtrpPlayer player = e.getPlayer();
            player.sendMessage(Color.NEWS, "Jûs esate paleidþiamas ið kalëjimo!");

        });

        this.node.registerHandler(PlayerConnectEvent.class, HandlerPriority.HIGH, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            BanData banData = getBanData(player);
            if(banData != null) {
                if(banData.isPermanent() || !banData.isExpired()) {
                    player.sendErrorMessage("Dëmesio, Jûsø veikëjui ir IP adresui yra uþdrausta jungtis á ðá serverá");
                    player.sendErrorMessage("Daugiau informacijos dël draudimo lankytis paðalinimo galite rasti forum.ltrp.lt");
                    player.sendMessage(Color.WHITE, "Uþdraustas veikëjas: " + player.getCharName());
                    player.sendMessage(Color.LIGHTGREY, "Nurodyta prieþastis: " + banData.getReason());
                    player.sendMessage(Color.WHITE, String.format("Blokuojamas IP adresas: %s | Blokuojami veikëjai: %s", banData.getIp() != null ? banData.getIp() : "-", player.getCharName()));
                    if(banData.getAdminId() != LtrpPlayer.INVALID_USER_ID)
                        player.sendMessage(Color.LIGHTGREY, "Draudimà lankytis suteikë: " + PlayerController.get().getUsernameByUUID(banData.getAdminId()));
                    if(!banData.isPermanent())
                        player.sendMessage(Color.LIGHTYELLOW, "Draudimo pabaigos data: " + new SimpleDateFormat().format(banData.getExpirationDate()));

                    player.kick();
                    e.interrupt();
                    e.disallow();
                }
                if(banData.isExpired()) {
                    unBan(player.getUUID());
                }
            }
        });

        this.node.registerHandler(PlayerRequestSpawnEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            SpawnData spawnData = e.getSpawnData();
            JailData jailData = getJailData(player);
            if(jailData != null) {
                Location l = jailData.getType() == JailData.JailType.InCharacter ? IC_JAIL_ENTRANCE : OOC_JAIL_ENTRANCE;
                player.setSpawnInfo(l, 0f, spawnData.getSkin(), Player.NO_TEAM, new WeaponData(), new WeaponData(), new WeaponData());
                logger.info("RequestSpawnEvent player " + player.getUUID() + " needs to be in jail");
            }
        });
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
        JailData jailData = new JailData(0, player, jailType, minutes * 60, minutes * 60, jailedBy.getUUID(), new Date(new java.util.Date().getTime()));
        switch(jailType) {
            case OutOfCharacter:
                player.setLocation(OOC_JAIL_ENTRANCE);
                break;
            case InCharacter:
                player.setLocation(IC_JAIL_ENTRANCE);
                break;
        }
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
        ban(player, new BanData(player.getUUID(), bannedBy != null ? bannedBy.getUUID() : LtrpPlayer.INVALID_USER_ID, reason, hours,  new Date(Instant.now().toEpochMilli()), null));
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
        ban(player, new BanData(player.getUUID(), bannedBy != null ? bannedBy.getUUID() : LtrpPlayer.INVALID_USER_ID, reason,  new Date(Instant.now().toEpochMilli()), null));
    }

    /**
     * Bans a player for some amount of time. This method includes an IP ban
     * @param player player to be banned(and his IP)
     * @param reason reason for the ban
     * @param hours duration of the ban(in hours)
     * @param bannedBy the administrator that initiated this ban
     */
    public void banIp(LtrpPlayer player, String reason, int hours, LtrpPlayer bannedBy) {
        ban(player, new BanData(player.getUUID(), bannedBy != null ? bannedBy.getUUID() : LtrpPlayer.INVALID_USER_ID, reason, player.getIp(), hours, new Date(Instant.now().toEpochMilli()), null));
    }

    /**
     *  Bans a player permanently. This method includes an IP ban
     * @param player player to be banned(and his IP)
     * @param reason reason for the ban
     * @param bannedBy the administrator that initiated this ban
     */
    public void banIp(LtrpPlayer player, String reason, LtrpPlayer bannedBy) {
        ban(player, new BanData(player.getUUID(), bannedBy != null ? bannedBy.getUUID() : LtrpPlayer.INVALID_USER_ID, reason, player.getIp(), new Date(Instant.now().toEpochMilli()), null));
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
            banData.setDeletedAt(new Date(Instant.now().toEpochMilli()));
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
     * Adds a warning to a player. If he has {@link lt.ltrp.PenaltyPlugin#MAX_WARNS} or more warnings he is automatically banned.
     * @param player player to warn
     * @param reason reason
     * @param warnedBy administrator that initiated this warning
     */
    public void warn(LtrpPlayer player, String reason, LtrpPlayer warnedBy) {
        WarnData warnData = new WarnData(player.getUUID(), warnedBy.getUUID(), reason, new Date(Instant.now().toEpochMilli()));
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
