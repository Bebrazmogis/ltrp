package lt.ltrp.player;



import lt.ltrp.LtrpGamemode;
import lt.ltrp.Util.PawnFunc;
import lt.ltrp.command.PlayerCommandManager;
import lt.ltrp.dao.PlayerDao;
import lt.ltrp.event.player.PlayerDataLoadEvent;
import lt.ltrp.event.player.PlayerLogInEvent;
import lt.ltrp.event.player.PlayerSpawnSetUpEvent;
import lt.ltrp.item.FixedSizeInventory;
import lt.ltrp.item.Item;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.common.command.CommandEntry;
import net.gtaun.shoebill.common.command.CustomCommandHandler;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.shoebill.event.amx.AmxUnloadEvent;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.player.PlayerSpawnEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
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

        PlayerCommandManager playerCommandManager = new PlayerCommandManager(HandlerPriority.NORMAL, managerNode);
       /* playerCommandManager.setUsageMessageSupplier((p, cmd, prefix, params, help) -> {
            System.out.println("Amount of command entries:" + playerCommandManager.getCommandEntries().size());
            List<CommandEntry> entries = playerCommandManager.getCommandEntries();
            for(CommandEntry entry : entries) {
                System.out.println("PATH:"+entry.getPath());
                System.out.println("Class:"+entry.getClass());
            }
            return "hello";
        });
        */
        playerCommandManager.replaceTypeParser(LtrpPlayer.class, s -> {
            int id = Player.INVALID_ID;
            try {
                id = Integer.parseInt(s);
            } catch(NumberFormatException e) {
                return null;
            }
            return LtrpPlayer.get(id);
        });
        playerCommandManager.replaceTypeParser(Vehicle.class, s -> {
            int id = Vehicle.INVALID_ID;
            try {
                id = Integer.parseInt(s);
            } catch(NumberFormatException e) {
                return null;
            }
            return Vehicle.get(id);
        });
        playerCommandManager.registerCommands(new AdminCommands());
        playerCommandManager.registerCommands(new GeneralCommands());
        playerCommandManager.registerCommand("test", new Class[]{LtrpPlayer.class}, new String[]{"player"}, (e, something) -> {
            System.out.println("its test alright");
            return true;
        });
        //playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);



        managerNode.registerHandler(PlayerCommandEvent.class, e -> {
            System.out.println("PlayerController :: constructor. PlayerCommandEvent received. Command:" + e.getCommand());
        });


        managerNode.registerHandler(PlayerConnectEvent.class, HandlerPriority.HIGHEST, e -> {
            Logger.getLogger(PlayerController.class.getName()).log(Level.INFO, "PlayerConnectEvent received");
            if(e.getPlayer().isNpc()) {
                return;
            }
            LtrpPlayer player = new LtrpPlayer(e.getPlayer(), playerDao.getUserId(e.getPlayer()));
            SpawnController spawn = new SpawnController(managerNode, player);
            spawn.run();

            new AuthController(managerNode, player);
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
            player.setLoggedIn(true);
            player.sendMessage("{FFFFFF}Sveikiname sugrįžus, Jūs prisijungėte su veikėju " + player.getName() + ". Sėkmės serveryje!");
            // If the users' spawn is already set up
            if(spawnsSetUp.containsKey(player) && spawnsSetUp.get(player)) {
                spawnPlayer(player);
                new Thread(() -> {
                    playerDao.loadData(player);
                    Item[] items = LtrpGamemode.getDao().getItemDao().getItems(LtrpPlayer.class, player.getUserId());
                    Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "PlayerController :: PlayerLoginEvent :: " + items.length + " loaded for user id " + player.getUserId());
                    player.setInventory(new FixedSizeInventory(player.getName() + " kuprinės"));
                    player.getInventory().add(items);
                    managerNode.dispatchEvent(new PlayerDataLoadEvent(player));
                }).start();
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
        } else System.out.println("PLAYER IS NULLLL");
        return false;
    }

}