package lt.ltrp.player;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.Util.PawnFunc;
import lt.ltrp.dao.PlayerDao;
import lt.ltrp.event.player.PlayerSpawnSetUpEvent;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class SpawnController implements Runnable {

    private LtrpPlayer player;
    private EventManagerNode managerNode;

    public SpawnController(EventManager manager, LtrpPlayer player) {
        this.player = player;
        this.managerNode = manager.createChildNode();
    }

    @Override
    public void run() {
        PlayerDao playerDao = LtrpGamemode.getDao().getPlayerDao();
        SpawnData spawnData = playerDao.getSpawnData(player);
        CrashData crashData = playerDao.getCrashData(player);
        JailData jailData = playerDao.getJailData(player);

        // The player doesn't exist
        if(spawnData == null)
            return;


        if(jailData != null) {
            player.jail(jailData);
        } else {
            if(crashData != null) {
                player.setSpawnInfo(crashData.getLocation(), 0.0f, spawnData.getSkin(), Player.NO_TEAM, spawnData.getWeaponData()[0], spawnData.getWeaponData()[1], spawnData.getWeaponData()[2]);
            } else {
                Location location;
                float x = 0.0f, y = 0.0f, z = 0.0f;
                int interior, world, index;
                switch(spawnData.getType()) {
                    case House:
                        AmxCallable getIndex = PawnFunc.getNativeMethod("GetHouseIndex"),
                            getPos = PawnFunc.getNativeMethod("GetHouseEntrancePos"),
                            getInt = PawnFunc.getNativeMethod("GetHouseEntranceInteriorID"),
                            getWorld = PawnFunc.getNativeMethod("GetHouseEntranceVirtualWorld");
                        index = (Integer)getIndex.call(spawnData.getId());
                        getPos.call(index, x, y, z);
                        interior = (Integer)getInt.call(index);
                        world = (Integer)getWorld.call(index);
                        location = new Location(x, y, z, interior, world);
                        break;
                    case Business:
                        getIndex = PawnFunc.getNativeMethod("GetBusinessIndex");
                        getPos = PawnFunc.getNativeMethod("GetBusinessEntrancePos");
                        getInt = PawnFunc.getNativeMethod("GetBusinessEntranceVirtualWorld");
                        getWorld = PawnFunc.getNativeMethod("GetBusinessEntranceInteriorID");
                        index = (Integer)getIndex.call(spawnData.getId());
                        getPos.call(index, x, y, z);
                        interior = (Integer)getInt.call(index);
                        world = (Integer)getWorld.call(index);
                        location = new Location(x, y, z, interior, world);
                        break;
                    case Faction:
                        getIndex = PawnFunc.getNativeMethod("GetFactionIndex");
                        getPos = PawnFunc.getNativeMethod("GetFactionPosition");
                        getInt = PawnFunc.getNativeMethod("GetFactionVirtualWorld");
                        getWorld = PawnFunc.getNativeMethod("GetFactionInterior");
                        index = (Integer)getIndex.call(spawnData.getId());
                        getPos.call(index, x, y, z);
                        interior = (Integer)getInt.call(index);
                        world = (Integer)getWorld.call(index);
                        location = new Location(x, y, z, interior, world);
                        break;
                    case Garage:
                        getIndex = PawnFunc.getNativeMethod("GetGarageIndex");
                        getPos = PawnFunc.getNativeMethod("GetGarageEntrancePos");
                        getInt = PawnFunc.getNativeMethod("GetGarageEntranceVirtualWorld");
                        getWorld = PawnFunc.getNativeMethod("GetGarageEntranceInteriorID");
                        index = (Integer)getIndex.call(spawnData.getId());
                        getPos.call(index, x, y, z);
                        interior = (Integer)getInt.call(index);
                        world = (Integer)getWorld.call(index);
                        location = new Location(x, y, z, interior, world);
                        break;
                    default:
                        getPos = PawnFunc.getNativeMethod("Data_GetCoordinates");
                        getInt = PawnFunc.getNativeMethod("Data_GetInterior");
                        getWorld = PawnFunc.getNativeMethod("Data_GetVirtualWorld");
                        getPos.call("default_spawn", x, y, z);
                        interior = (Integer)getInt.call("default_spawn");
                        world = (Integer)getWorld.call("default_spawn");
                        location = new Location(x, y, z, interior, world);
                        break;
                }
                player.setSpawnInfo(location, 0.0f, spawnData.getSkin(), Player.NO_TEAM, spawnData.getWeaponData()[0], spawnData.getWeaponData()[1], spawnData.getWeaponData()[2]);
            }
            managerNode.dispatchEvent(new PlayerSpawnSetUpEvent(player));
        }
        if(crashData != null) {
            playerDao.remove(player, crashData);
        }
    }
}
