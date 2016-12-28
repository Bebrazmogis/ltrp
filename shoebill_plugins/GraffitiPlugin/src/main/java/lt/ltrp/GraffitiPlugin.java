package lt.ltrp;

import kotlin.reflect.jvm.internal.KClassImpl;
import lt.ltrp.command.AdminGraffitiCommands;
import lt.ltrp.command.SprayCommands;
import lt.ltrp.dao.GraffitiColorDao;
import lt.ltrp.dao.GraffitiDao;
import lt.ltrp.dao.GraffitiFontDao;
import lt.ltrp.dao.GraffitiObjectDao;
import lt.ltrp.business.dao.impl.*;
import lt.ltrp.data.*;
import lt.ltrp.event.GraffitiCreateEvent;
import lt.ltrp.event.PlayerEndGraffitiPaintingEvent;
import lt.ltrp.event.PlayerStartGraffitiPaintingEvent;
import lt.ltrp.object.Graffiti;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.impl.GraffitiImpl;
import lt.ltrp.player.util.PlayerUtils;
import lt.ltrp.resource.DependentPlugin;
import lt.maze.streamer.StreamerPlugin;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.common.timers.TemporaryTimer;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import org.slf4j.Logger;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class GraffitiPlugin extends DependentPlugin {

    public static final int GRAFFITI_PAINT_PERMISSION_TIME = 3 * 60 * 1000;
    public static final String DEFAULT_GRAFFITI_TEXT = "Tekstas";

    private EventManagerNode eventManager;
    private Logger logger;
    private Collection<Graffiti> graffitiCollection;
    private List<GraffitiObject> graffitiObjectCollection;
    private List<GraffitiFont> graffitiFontCollection;
    private List<GraffitiColor> graffitiColors;
    private GraffitiDao graffitiDao;
    private GraffitiColorDao graffitiColorDao;
    private GraffitiFontDao graffitiFontDao;
    private GraffitiObjectDao graffitiObjectDao;

    private Map<LtrpPlayer, Graffiti> playerGraffiti;
    private Collection<SprayPermission> sprayPermissions;

    private PlayerCommandManager playerCommandManager;


    public GraffitiPlugin() {
        addDependency(new KClassImpl<>(StreamerPlugin.class));
        addDependency(new KClassImpl<>(DatabasePlugin.class));
    }

    @Override
    public void onDependenciesLoaded() {
        this.graffitiCollection = new ArrayList<>();
        this.graffitiObjectCollection = new ArrayList<>();
        this.graffitiFontCollection = new ArrayList<>();
        this.graffitiColors = new ArrayList<>();
        this.playerGraffiti = new HashMap<>();
        this.sprayPermissions = new ArrayList<>();
        this.eventManager = getEventManager().createChildNode();
        this.logger = getLogger();

        DatabasePlugin database = ResourceManager.get().getPlugin(DatabasePlugin.class);
        graffitiColorDao = new MySqlGraffitiColorDaoImpl(database.getDataSource());
        graffitiFontDao = new MySqlGraffitiFontDaoImpl(database.getDataSource());
        graffitiObjectDao = new MySqlGraffitiObjectDaoImpl(database.getDataSource());
        graffitiDao = new MySqlGraffitiDaoImpl(database.getDataSource(), eventManager, (MySqlGraffitiObjectDaoImpl)graffitiObjectDao, (MySqlGraffitiFontDaoImpl)graffitiFontDao, (AbstractMySqlGraffitiColorDao)graffitiColorDao);

        graffitiCollection.addAll(graffitiDao.get());
        graffitiFontCollection.addAll(graffitiFontDao.get());
        graffitiObjectCollection.addAll(graffitiObjectDao.get());
        graffitiColors.addAll(graffitiColorDao.get());

        registerEvents();
        replaceTypeParsers();
        registerCommands();

        logger.info(getDescription().getName() + " loaded");
    }

    private void registerEvents() {
        eventManager.registerHandler(PlayerDisconnectEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(playerGraffiti.containsKey(player))
                playerGraffiti.remove(player);
            SprayPermission permission = getSprayPermission(player);
            if(permission != null)
                sprayPermissions.remove(permission);
        });
    }

    private SprayPermission getSprayPermission(LtrpPlayer player) {
        Optional<SprayPermission> permission = sprayPermissions.stream().filter(sp -> sp.getPlayer().equals(player)).findFirst();
        return permission.isPresent() ? permission.get() : null;
    }

    private void replaceTypeParsers() {
        PlayerCommandManager.replaceTypeParser(Graffiti.class, (s) -> Graffiti.get(Integer.parseInt(s)));
    }

    private void registerCommands() {
        playerCommandManager = new PlayerCommandManager(eventManager);
        CommandGroup group = new CommandGroup();
        group.setNotFoundHandler((p, g, c) -> {
            LtrpPlayer player = LtrpPlayer.get(p);
            p.sendMessage(Color.BLANCHEDALMOND, "Teisingas komandos naudojimas: /spray [veiksmas]");
            p.sendMessage(Color.BLANCHEDALMOND, "GALIMI VEIKSMAI: create, delete, pos, text, font, size, colour, save");
            if(player.isAdmin())
                p.sendMessage(Color.LIGHTRED, "Galimi administratoriaus veiksmai: allow, dissallow, list");
            return true;
        });
        group.registerCommands(new SprayCommands(eventManager));
        group.registerCommands(new AdminGraffitiCommands(eventManager));
        playerCommandManager.registerChildGroup(group, "spray");
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        eventManager.cancelAll();
        playerCommandManager.uninstallAllHandlers();
        playerCommandManager.destroy();
        playerCommandManager = null;
        graffitiCollection.forEach(Graffiti::destroy);
        graffitiCollection.clear();
        graffitiObjectCollection.clear();
        graffitiFontCollection.clear();
        graffitiColors.clear();
        playerGraffiti.clear();
        sprayPermissions.clear();
        logger.info(getDescription().getName() + " disabled");
    }



    public Collection<Graffiti> getGraffiti() {
        return graffitiCollection;
    }

    public GraffitiObjectDao getGraffitiObjectDao() {
        return graffitiObjectDao;
    }

    public GraffitiFontDao getGraffitiFontDao() {
        return graffitiFontDao;
    }

    public GraffitiColorDao getGraffitiColorDao() {
        return graffitiColorDao;
    }

    public GraffitiDao getGraffitiDao() {
        return graffitiDao;
    }

    public List<GraffitiFont> getGraffitiFontCollection() {
        return graffitiFontCollection;
    }

    public List<GraffitiObject> getGraffitiObjectCollection() {
        return graffitiObjectCollection;
    }

    public List<GraffitiColor> getGraffitiColors() {
        return graffitiColors;
    }

    public Graffiti getPlayerGraffiti(LtrpPlayer player) {
        return playerGraffiti.get(player);
    }

    public Graffiti startPainting(LtrpPlayer player) {
        Random random = new Random();
        Graffiti graffiti = null;
        SprayPermission permission = getSprayPermission(player);
        if(permission.isValid()) {
            GraffitiObject object = graffitiObjectCollection.get(random.nextInt(graffitiObjectCollection.size()));
            GraffitiFont font = graffitiFontCollection.get(random.nextInt(graffitiFontCollection.size()));
            GraffitiColor color = graffitiColors.get(random.nextInt(graffitiColors.size()));
            Vector3D position = PlayerUtils.getInFront(player, 2.5f);
            LtrpPlayer approvedBy = permission.getAllowedBy();

            graffiti = Graffiti.create(player.getUUID(), DEFAULT_GRAFFITI_TEXT, object, position, new Vector3D(0f, 0f, player.getAngle()), font, color, approvedBy.getUUID(), new Timestamp(Instant.now().getEpochSecond()));
            playerGraffiti.put(player, graffiti);
            eventManager.dispatchEvent(new PlayerStartGraffitiPaintingEvent(graffiti, player));
        }
        return graffiti;
    }

    public boolean isPaintingGraffiti(LtrpPlayer player) {
        return playerGraffiti.containsKey(player);
    }

    public boolean isAllowedToPaint(LtrpPlayer player) {
        SprayPermission permission = getSprayPermission(player);
        return player.isAdmin() ||  permission.isValid();
    }

    public void allowToPaint(LtrpPlayer player, LtrpPlayer allowedBy) {
        SprayPermission permission = new SprayPermission(player, allowedBy);
        TemporaryTimer.create(GRAFFITI_PAINT_PERMISSION_TIME, 1, (i) -> {
            permission.setValid(false);
            sprayPermissions.remove(permission);
        }).start();
    }

    public void endPaintSession(LtrpPlayer player) {
        Graffiti graffiti = playerGraffiti.get(player);
        if(graffiti != null) {
            playerGraffiti.remove(player);
        }
        SprayPermission permission = getSprayPermission(player);
        if(permission != null) {
            sprayPermissions.remove(permission);
            permission.setValid(false);
            eventManager.dispatchEvent(new PlayerEndGraffitiPaintingEvent(graffiti, player));
        }

    }


    public Graffiti createGraffiti(int uuid, int authorUserId, String text, GraffitiObject objectType, Vector3D vector3D, Vector3D vector3D1, GraffitiFont font, GraffitiColor color, int approvedByUserId, Timestamp createdAt) {
        GraffitiImpl impl = new GraffitiImpl(uuid, authorUserId, text, objectType, vector3D, vector3D1, font, color, approvedByUserId, createdAt, eventManager);
        graffitiCollection.add(impl);
        eventManager.dispatchEvent(new GraffitiCreateEvent(impl));
        return impl;
    }

    public Graffiti getGraffiti(int uuid) {
        Optional<Graffiti> op = getGraffiti().stream().filter(g -> g.getUUID() == uuid).findFirst();
        return op.isPresent() ? op.get() : null;
    }
}
