package lt.ltrp.player;



import lt.ltrp.LtrpGamemode;
import lt.ltrp.Util.PawnFunc;
import lt.ltrp.command.PlayerCommandManager;
import lt.ltrp.dao.PlayerDao;
import lt.ltrp.data.Animation;
import lt.ltrp.dmv.Dmv;
import lt.ltrp.dmv.DmvManager;
import lt.ltrp.event.player.PlayerDataLoadEvent;
import lt.ltrp.event.player.PlayerLogInEvent;
import lt.ltrp.event.player.PlayerSpawnSetUpEvent;
import lt.ltrp.item.FixedSizeInventory;
import lt.ltrp.item.Item;
import lt.ltrp.job.ContractJob;
import lt.ltrp.job.Faction;
import lt.ltrp.job.Job;
import lt.ltrp.job.JobManager;
import lt.ltrp.vehicle.LtrpVehicle;
import lt.maze.streamer.StreamerPlugin;
import lt.maze.streamer.constant.StreamerType;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.common.command.CommandEntry;
import net.gtaun.shoebill.common.command.CustomCommandHandler;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.shoebill.constant.WeaponSkill;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.WeaponData;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.shoebill.event.amx.AmxUnloadEvent;
import net.gtaun.shoebill.event.player.*;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerWeaponSkill;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

public class PlayerController {

    private EventManagerNode managerNode;
    private PlayerDao playerDao;
    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);

    private Map<LtrpPlayer, Boolean> spawnsSetUp = new HashMap<>();
    private List<LtrpPlayer> firstSpawns = new ArrayList<>();
    private Map<LtrpPlayer, Location> playerDeathLocations = new HashMap<>();


    public PlayerController(EventManager manager) {

        playerDao = LtrpGamemode.getDao().getPlayerDao();


        managerNode = manager.createChildNode();

        PlayerCommandManager playerCommandManager = new PlayerCommandManager(HandlerPriority.NORMAL, managerNode);

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
        playerCommandManager.replaceTypeParser(ContractJob.class, s -> {
            int id = ContractJob.INVALID_ID;
            try {
                id = Integer.parseInt(s);
            } catch(NumberFormatException e) {
                return null;
            }
            return JobManager.getContractJob(id);
        });

        playerCommandManager.replaceTypeParser(Faction.class, s -> {
            int id = Faction.INVALID_ID;
            try {
                id = Integer.parseInt(s);
            } catch(NumberFormatException e) {
                return null;
            }
            return JobManager.getFaction(id);
        });


        playerCommandManager.registerCommands(new AdminCommands());
        playerCommandManager.registerCommands(new GeneralCommands());
        playerCommandManager.registerCommand("test", new Class[]{LtrpPlayer.class}, new String[]{"player"}, (e, something) -> {
            System.out.println("its test alright");
            List<ListDialogItem> items = new ArrayList<ListDialogItem>();
            for(Field f : e.getClass().getFields()) {
                ListDialogItem item = new ListDialogItem();
                item.setItemText(String.format("%s\t%s", f.getName(), f.toGenericString()));
                items.add(item);
            }
            PageListDialog.create(e, LtrpGamemode.get().getEventManager())
                    .caption("Okay")
                    .items(items)
                    .build().show();
            return true;
        });
        playerCommandManager.registerCommand("test2", new Class[]{}, new String[]{}, (p,l) -> {
            System.out.println("its test2 alright");

            p.sendMessage("Job:" + p.getJob());
            p.sendMessage(" Admin level;"  + p.getAdminLevel());
            return true;
        });






        //playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);



        managerNode.registerHandler(PlayerCommandEvent.class, e -> {
            logger.info("PlayerController :: constructor. PlayerCommandEvent received. Command:" + e.getCommand());
        });


        managerNode.registerHandler(PlayerConnectEvent.class, HandlerPriority.HIGHEST, e -> {
            logger.info("PlayerConnectEvent received: " + e.getPlayer().getName());
            if(e.getPlayer().isNpc()) {
                return;
            }
            LtrpPlayer player = new LtrpPlayer(e.getPlayer(), playerDao.getUserId(e.getPlayer()));
            SpawnController spawn = new SpawnController(managerNode, player);
            spawn.run();

            // Various options and settings

            player.setColor(new Color(0, 0, 0, 0)); // Make the users radai blip invisible

            // Authentication
            new AuthController(managerNode, player);


        });


        managerNode.registerHandler(PlayerSpawnSetUpEvent.class, e -> {
            logger.info("PlayerSpawnSetupEvent received");
            LtrpPlayer player = e.getPlayer();
            firstSpawns.add(player);
            if(player.isLoggedIn()) {
                spawnPlayer(player);
                loadDataThreaded(player);
            } else {
                spawnsSetUp.put(e.getPlayer(), true);

            }
        });

        managerNode.registerHandler(PlayerLogInEvent.class, e -> {
            logger.info("PlayerLogInEvent received");
            LtrpPlayer player = e.getPlayer();
            player.setLoggedIn(true);
            player.sendMessage("{FFFFFF}Sveikiname sugrįžus, Jūs prisijungėte su veikėju " + player.getName() + ". Sėkmės serveryje!");
            // If the users' spawn is already set up
            if(spawnsSetUp.containsKey(player) && spawnsSetUp.get(player)) {
                spawnPlayer(player);
                loadDataThreaded(player);
            }
            // Legacy code for Pawn loading.
            AmxCallable onPlayerLoginPawn = PawnFunc.getPublicMethod("getPublicMethod");
            if(onPlayerLoginPawn != null) {
                onPlayerLoginPawn.call(player.getId(), player.getUserId());
            }
        });

        managerNode.registerHandler(PlayerDisconnectEvent.class, HandlerPriority.BOTTOM, e -> {
            logger.info("PlayerDisconnectEvent received");
            LtrpPlayer p = LtrpPlayer.get(e.getPlayer());
            if(spawnsSetUp.containsKey(p)) {
                spawnsSetUp.remove(p);
            }
            LtrpPlayer.remove(p);
        });

        managerNode.registerHandler(PlayerRequestClassEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null && player.isLoggedIn()) {
                player.spawn();
            } else {
                e.getPlayer().sendMessage(Color.RED, "Jūs neesate prisijungęs.");
            }
        });

        managerNode.registerHandler(PlayerSpawnEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null && player.isLoggedIn()) {
                player.setCameraBehind();
                // Another fail-safe
                if(player.getLevel() == 0) {
                    player.setLevel(1);
                    logger.error(String.format("User %s(UDI:%d) level is 0.", player.getName(), player.getUserId()));
                }
                // This means the user JUST spawned
                if(firstSpawns.contains(player)) {
                    firstSpawns.remove(player);

                    setDefaultWeaponSkillLevel(player);
                    //preloadAnimLimbs(player);
                    player.setTeam(Player.NO_TEAM);
                    player.setScore(player.getLevel());
                }

                if(player.isMasked()) {
                    player.setMasked(false);
                }

                if(player.isInComa()) {
                    player.applyAnimation("CRACK", "crackdeth2", 4f, 1, 0, 0, 0, 0, 0);
                    // We start the coma countdown
                    player.setCountdown(new PlayerCountdown(player, 30, true, p -> {
                        player.setHealth(0f);
                    }, false, "~w~Iki mirties"));
                }
                StreamerPlugin.getInstance().update(player, StreamerType.Object);
            } else {
                e.getPlayer().kick(); // fail-safe
            }
        });

        managerNode.registerHandler(PlayerDeathEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null) {

                // If he was doing something, he isn't doing it anymore
                if(player.getCountdown() != null) {
                    player.getCountdown().forceStop();
                }

                player.cancelEdit();
                player.cancelSelectTextDraw();
                player.cancelDialog();

                logger.debug("death location:" + player.getLocation() + " death skin: "+ player.getSkin());
                // We put him in a coma
                if(!player.isInComa()) {
                    player.setInComa(true);

                    player.setSpawnInfo(player.getLocation(), player.getSkin(), Player.NO_TEAM, new WeaponData(), new WeaponData(), new WeaponData());

                    // Actual death
                } else {
                    player.setSpawnInfo(new AngledLocation(180f, 600f, 10f, 0f), player.getSkin(), Player.NO_TEAM, new WeaponData(), new WeaponData(), new WeaponData());
                    // TODO pakeisti koordinates ligoninės
                    player.setInComa(false);
                }

                LtrpPlayer killer = LtrpPlayer.get(e.getKiller());
                if(killer != null) {
                    LtrpPlayer.sendAdminMessage("Žaidėjas " + killer.getName() + " nužudė " + player.getName() + " su ginklu " + e.getReason().getName());
                }
                LtrpGamemode.getDao().getPlayerDao().update(player);
            }
        });

        managerNode.registerHandler(PlayerCommandEvent.class, HandlerPriority.BOTTOM, e -> {
            //e.setProcessed();
        });

        addPawnFunctions();

    }

    private void spawnPlayer(LtrpPlayer player) {
        player.spawn();
        player.sendGameText(5000, 1, "~w~Sveikas ~n~~h~~g~" + player.getName());
    }


    private void addPawnFunctions() {
        logger.info("PlayerController :: addPawnFunctions. Called.");
        managerNode.registerHandler(AmxLoadEvent.class, e-> {
            e.getAmxInstance().registerFunction("updatePlayerInfoText", params -> {
                LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
                if(player != null && player.getInfoBox() != null) {
                    player.getInfoBox().update();
                }
                return player == null ? 0 : 1;
            }, Integer.class);

            e.getAmxInstance().registerFunction("isDmvVehicle", params -> {
                LtrpVehicle vehicle = LtrpVehicle.getById((Integer)params[0]);
                if(vehicle != null) {
                    return DmvManager.getInstance().isDmvVehicle(vehicle) ? 1 : 0;
                }
                return 0;
            }, Integer.class);

            e.getAmxInstance().registerFunction("isPlayerLoggedIn", params -> {
                LtrpPlayer player = LtrpPlayer.get((Integer) params[0]);
                if (player != null) {
                    return player.isLoggedIn() ? 1 : 0;
                }
                return 0;
            }, Integer.class);

            e.getAmxInstance().registerFunction("saveAccount", params -> {
                LtrpPlayer player = LtrpPlayer.get((Integer) params[0]);
                if (player != null) {
                    playerDao.update(player);
                }
                return 0;
            }, Integer.class);

            new GettersSetters(e.getAmxInstance());
           logger.info("PlayerController :: addPawnFunctions :: lambda. Function registered");
        });
        logger.info("PlayerController :: addPawnFunctions.Pawn functions added");



        managerNode.registerHandler(AmxUnloadEvent.class, e -> {
            e.getAmxInstance().unregisterFunction("isPlayerLoggedIn");
            e.getAmxInstance().unregisterFunction("updatePlayerInfoText");
            e.getAmxInstance().unregisterFunction("isDmvVehicle");

        });
    }

    private void loadDataThreaded(LtrpPlayer player) {
        new Thread(() -> {
            playerDao.loadData(player);
            Item[] items = LtrpGamemode.getDao().getItemDao().getItems(LtrpPlayer.class, player.getUserId());
            logger.info("PlayerController :: PlayerLoginEvent :: " + items.length + " loaded for user id " + player.getUserId());
            player.setInventory(new FixedSizeInventory(player.getName() + " kuprinės"));
            player.getInventory().add(items);

            player.setVehicleMetadata(playerDao.getVehiclePermissions(player));

            PlayerLicenses licenses = LtrpGamemode.getDao().getPlayerDao().get(player);
            player.setLicenses(licenses);
            managerNode.dispatchEvent(new PlayerDataLoadEvent(player));
        }).start();
    }

    private void setDefaultWeaponSkillLevel(LtrpPlayer player) {
        PlayerWeaponSkill skill = player.getWeaponSkill();
        skill.setLevel(WeaponSkill.PISTOL, 1);
        skill.setLevel(WeaponSkill.SHOTGUN, 200);
        skill.setLevel(WeaponSkill.SAWNOFF_SHOTGUN, 1);
        skill.setLevel(WeaponSkill.SPAS12_SHOTGUN, 200);
        skill.setLevel(WeaponSkill.MICRO_UZI, 1);
        skill.setLevel(WeaponSkill.MP5, 300);
        skill.setLevel(WeaponSkill.AK47, 200);
        skill.setLevel(WeaponSkill.M4, 200);
        skill.setLevel(WeaponSkill.SNIPERRIFLE, 200);
    }

    private void preloadAnimLimbs(LtrpPlayer player) {
        for(String animLib : Animation.ANIMATION_LIBS) {
            player.applyAnimation(animLib, "null", 0f, 0, 0, 0, 0,0, 0);
        }
    }


    private boolean isPlayerLoggedIn(int id) {
        LtrpPlayer player = LtrpPlayer.get(id);
        if(player != null) {
            return player.isLoggedIn();
        } else System.out.println("PLAYER IS NULLLL");
        return false;
    }

}