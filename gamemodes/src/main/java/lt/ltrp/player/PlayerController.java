package lt.ltrp.player;



import lt.ltrp.LtrpGamemode;
import lt.ltrp.Util.PawnFunc;
import lt.ltrp.dao.PlayerDao;
import lt.ltrp.event.player.PlayerDataLoadEvent;
import lt.ltrp.event.player.PlayerLogInEvent;
import lt.ltrp.event.player.PlayerSpawnSetUpEvent;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.shoebill.event.amx.AmxUnloadEvent;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.player.PlayerSpawnEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerController {

    private EventManagerNode managerNode;
    private PlayerDao playerDao;

    private Map<LtrpPlayer, Boolean> spawnsSetUp = new HashMap<>();

    public PlayerController(EventManager manager) {

        playerDao = LtrpGamemode.getDao().getPlayerDao();

        managerNode = manager.createChildNode();

        managerNode.registerHandler(PlayerConnectEvent.class, HandlerPriority.HIGHEST, e -> {
            Logger.getLogger(PlayerController.class.getName()).log(Level.INFO, "PlayerConnectEvent received");
            if(e.getPlayer().isNpc()) {
                return;
            }
            LtrpPlayer player = new LtrpPlayer(e.getPlayer(), playerDao.getUserId(e.getPlayer()));
            SpawnController spawn = new SpawnController(managerNode, player);
            spawn.run();

            new AuthController(managerNode, player);

            System.out.println("\n\n\n\n\n\n\n\n");
            float x = 0.0f, y = 0.0f, z = 0.0f;
            AmxCallable func = PawnFunc.getNativeMethod("AFunction");
            if(func != null) {
                func.call("default_spawn", x ,y ,z);
            }
            else System.out.println("its null :|");
            System.out.println("REsult. " + x + " " + y + " " + z);
        });


        managerNode.registerHandler(PlayerSpawnSetUpEvent.class, e -> {
            Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "PlayerSpawnSetupEvent received");
            LtrpPlayer player = e.getPlayer();
            if(player.isLoggedIn()) {
                spawnPlayer(player);
            } else {
                spawnsSetUp.put(e.getPlayer(), true);

            }
        });

        managerNode.registerHandler(PlayerLogInEvent.class, e -> {
            Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "PlayerLogInEvent received");
            LtrpPlayer player = e.getPlayer();
            player.sendMessage("{FFFFFF}Sveikiname sugrįžus, Jūs prisijungėte su veikėju " + player.getName() + ". Sėkmės serveryje!");
            // If the users' spawn is already set up
            if(spawnsSetUp.containsKey(player) && spawnsSetUp.get(player)) {
                spawnPlayer(player);
                new Thread(() -> {
                    playerDao.loadData(player);
                    managerNode.dispatchEvent(new PlayerDataLoadEvent(player));
                }).start();
            } else {
                player.setLoggedIn(true);
            }
            // Legacy code for Pawn loading.
            AmxCallable onPlayerLoginPawn = PawnFunc.getNativeMethod("OnPlayerLoginEx");
            if(onPlayerLoginPawn != null) {
                onPlayerLoginPawn.call(player.getId(), player.getUserId());
            }
        });

        manager.registerHandler(PlayerDisconnectEvent.class, e -> {
            Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "PlayerDisconnectEvent received");
            Player p = e.getPlayer();
            if(spawnsSetUp.containsKey(p)) {
                spawnsSetUp.remove(p);
            }
            LtrpPlayer.players.remove(p);
        });

        manager.registerHandler(PlayerSpawnEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null) {
                player.setCameraBehind();

            }
        });

        addPawnFunctions();

    }

    private void spawnPlayer(LtrpPlayer player) {
        player.spawn();
        player.sendGameText(5000, 1, "~w~Sveikas ~n~~h~~g~" + player.getName());
    }


    private void addPawnFunctions() {
        Logger.getLogger(PlayerController.class.getSimpleName()).log(Level.INFO, "PlayerController :: addPawnFunctions. Called.");
        managerNode.registerHandler(AmxLoadEvent.class, e-> {
                    e.getAmxInstance().registerFunction("isPlayerLoggedIn", objects -> {
                                objects[1] = isPlayerLoggedIn((Integer) objects[0]) ? 1 : 0;
                        return 1;
                    }, Integer.class, Integer.class);
            Logger.getLogger(PlayerController.class.getSimpleName()).log(Level.INFO, "PlayerController :: addPawnFunctions :: lambda. Function registered");
        });
        Logger.getLogger(PlayerController.class.getSimpleName()).log(Level.INFO, "PlayerController :: addPawnFunctions.Pawn functions added");

        managerNode.registerHandler(AmxUnloadEvent.class, e-> {
            e.getAmxInstance().unregisterFunction("isPlayerLoggedIn");
        });
    }




    private boolean isPlayerLoggedIn(int id) {
        LtrpPlayer player = LtrpPlayer.get(id);
        if(player != null) {
            return player.isLoggedIn();
        }
        return false;
    }

}