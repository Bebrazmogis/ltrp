package lt.ltrp;

import lt.ltrp.dao.SpawnDao;
import lt.ltrp.dao.impl.MySqlSpawnDaoImpl;
import lt.ltrp.data.SpawnData;
import lt.ltrp.event.PlayerRequestSpawnEvent;
import lt.ltrp.event.PlayerSpawnLocationChangeEvent;
import lt.ltrp.event.PlayerSpawnSetUpEvent;
import lt.ltrp.object.*;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class SpawnPlugin extends Plugin{

    public static final AngledLocation DEFAULT_SPAWN_LOCATION = new AngledLocation();

    private EventManagerNode node;
    private SpawnDao spawnDao;
    private Map<LtrpPlayer, SpawnData> playerSpawnData;

    @Override
    protected void onEnable() throws Throwable {
        this.node =  getEventManager().createChildNode();
        this.playerSpawnData= new HashMap<>();


        final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
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
        spawnDao = new MySqlSpawnDaoImpl(databasePlugin.getDataSource());

        node.registerHandler(PlayerConnectEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            SpawnData spawnData = getSpawnData(player);
            Location location = null;
            if(spawnData != null) {
                switch(spawnData.getType()) {
                    case House:
                        House house = House.get(spawnData.getId());
                        if(house != null) location = house.getEntrance();
                        break;
                    case Business:
                        Business business = Business.get(spawnData.getId());
                        if(business != null) location = business.getEntrance();
                        break;
                    case Faction:
                        Faction faction = (Faction)Job.get(spawnData.getId());
                        if(faction != null) location = faction.getLocation();
                        break;
                    case Garage:
                        Garage garage = Garage.get(spawnData.getId());
                        if(garage != null) location = garage.getEntrance();
                        break;
                    case Default:
                        location = DEFAULT_SPAWN_LOCATION;
                        break;
                }
                // This might happen if the spawn location is no longer valid(for example the house is destroyed)
                if(location == null) {
                    spawnData = SpawnData.DEFAULT;
                    setSpawnData(player, SpawnData.DEFAULT);
                    location = DEFAULT_SPAWN_LOCATION;
                }
                player.setSpawnInfo(location, 0.0f, spawnData.getSkin(), Player.NO_TEAM, spawnData.getWeaponData()[0], spawnData.getWeaponData()[1], spawnData.getWeaponData()[2]);

                node.dispatchEvent(new PlayerRequestSpawnEvent(player, spawnData));
                // This SHOULD be called after all handler function for PlayerRequestSpawnEvent
                node.dispatchEvent(new PlayerSpawnSetUpEvent(player));
                player.spawn();
            }
        });
    }

    @Override
    protected void onDisable() throws Throwable {
        node.cancelAll();
        playerSpawnData.clear();
        playerSpawnData = null;
    }


    public SpawnData getSpawnData(LtrpPlayer player) {
        return playerSpawnData.get(player);
    }

    public SpawnData getSpawnData(int userId) {
        return spawnDao.get(userId);
    }

    public void setSpawnData(LtrpPlayer player, SpawnData spawnData) {
        playerSpawnData.put(player, spawnData);
        spawnDao.update(player, spawnData);
        node.dispatchEvent(new PlayerSpawnLocationChangeEvent(player, spawnData));
    }

    public void setSpawnData(int userId, SpawnData spawnData) {
        spawnDao.update(userId, spawnData);
    }
}
