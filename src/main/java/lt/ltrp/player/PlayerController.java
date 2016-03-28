package lt.ltrp.player;



import lt.ltrp.BankPlugin;
import lt.ltrp.LtrpGamemode;
import lt.ltrp.Util.PawnFunc;
import lt.ltrp.constant.Currency;
import lt.ltrp.dao.PlayerDao;
import lt.ltrp.data.Animation;
import lt.ltrp.dmv.DmvManager;
import lt.ltrp.event.PaydayEvent;
import lt.ltrp.event.player.PlayerDataLoadEvent;
import lt.ltrp.event.player.PlayerLogInEvent;
import lt.ltrp.event.player.PlayerOfferExpireEvent;
import lt.ltrp.event.player.PlayerSpawnSetUpEvent;
import lt.ltrp.item.FixedSizeInventory;
import lt.ltrp.item.Item;
import lt.ltrp.job.*;
import lt.ltrp.property.Business;
import lt.ltrp.property.Garage;
import lt.ltrp.property.House;
import lt.ltrp.vehicle.LtrpVehicle;
import lt.maze.streamer.StreamerPlugin;
import lt.maze.streamer.constant.StreamerType;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.constant.WeaponSkill;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Color;
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

import java.util.*;

public class PlayerController {

    protected static final int MINUTES_FOR_PAYDAY = 20;

    private EventManagerNode managerNode;
    private PlayerDao playerDao;
    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);

    private Map<LtrpPlayer, Boolean> spawnsSetUp = new HashMap<>();
    private List<LtrpPlayer> firstSpawns = new ArrayList<>();
    private Timer javaMinuteTimer;
    private PlayerCommandManager playerCommandManager;
    private PlayerJailController playerJailController;

    public PlayerController(EventManager manager, JobManager jobManager) {
        playerDao = LtrpGamemode.getDao().getPlayerDao();
        this.playerJailController = new PlayerJailController(manager, playerDao);

        managerNode = manager.createChildNode();

        playerCommandManager = new PlayerCommandManager( managerNode);
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
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


        playerCommandManager.registerCommands(new AdminCommands(jobManager, managerNode));
        playerCommandManager.registerCommands(new GeneralCommands());



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

            player.setColor(Color.WHITE); // Make the users radai blip invisible

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
                    player.applyAnimation("CRACK", "crckdeth2", 4f, 1, 0, 0, 0, 0, 0);
                    // We start the coma countdown
                    player.setCountdown(new PlayerCountdown(player, 600, true, (p, success) -> {
                        if(success)
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
                    player.setDeaths(player.getDeaths()+1);
                }

                LtrpPlayer killer = LtrpPlayer.get(e.getKiller());
                if(killer != null) {
                    LtrpPlayer.sendAdminMessage("Žaidėjas " + killer.getName() + " nužudė " + player.getName() + " su ginklu " + e.getReason().getName());
                }
                LtrpGamemode.getDao().getPlayerDao().update(player);
            }
        });

        managerNode.registerHandler(PaydayEvent.class, HandlerPriority.HIGH, e -> {
            BankPlugin bankPlugin = Shoebill.get().getResourceManager().getPlugin(BankPlugin.class);
           LtrpPlayer.get().forEach(p -> {
               // fail-safe
               if(Player.get(p.getId()) == null)
                   LtrpPlayer.remove(p);

               int houseTax = (int)House.get().stream().filter(h -> h.getOwnerUserId() == p.getUserId()).count() * LtrpGamemode.getHouseTax();
               int businessTax = (int)Business.get().stream().filter(b -> b.getOwnerUserId() == p.getUserId()).count() * LtrpGamemode.getBusinessTax();
               int garageTax = (int)Garage.get().stream().filter(g -> g.getOwnerUserId() == p.getUserId()).count() * LtrpGamemode.getGarageTax();
               int vehicleTax = LtrpGamemode.getDao().getVehicleDao().getPlayerVehicleCount(p) * LtrpGamemode.getVehicleTax();

               if(p.getMinutesOnlineSincePayday() > MINUTES_FOR_PAYDAY) {
                   int paycheck = 0;

                   if (p.getJob() != null) {
                       paycheck = p.getJobRank().getSalary();
                       p.setJobHours(p.getJobHours() + 1);
                       if(p.getJob() instanceof ContractJob) {
                           p.setJobContract(p.getJobContract() - 1);
                       }
                       if (p.getJob() instanceof ContractJob && p.getJobRank() instanceof ContractJobRank) {
                           ContractJobRank nextRank = (ContractJobRank) p.getJob().getRank(p.getJobRank().getNumber() + 1);
                           // If the user doesn't have the highest rank yet
                           if (nextRank != null) {
                               if (nextRank.getXpNeeded() <= p.getJobExperience()) {
                                   p.setJobRank(nextRank);
                               }
                           }
                       }
                   } else {
                       paycheck = 100;
                   }
                   BankAccount bankAccount = bankPlugin.getBankController().getAccount(p);
                   int totalTaxes = houseTax + businessTax + garageTax + vehicleTax;
                   p.sendMessage(Color.LIGHTGREEN, "|______________ Los Santos banko ataskaita______________ |");
                   p.sendMessage(Color.WHITE, String.format("| Gautas atlyginimas: %d%c | Papildomi mokesčiai: %d%c |", paycheck, Currency.SYMBOL,totalTaxes, Currency.SYMBOL));
                   p.sendMessage(Color.WHITE, String.format("| Buvęs banko balansas: %d%c |", bankAccount.getMoney(), Currency.SYMBOL));
                   p.sendMessage(Color.WHITE, String.format("| Galutinė gauta suma: %d%c |", paycheck, Currency.SYMBOL));
                   bankAccount.addMoney(-totalTaxes);
                   bankPlugin.getBankController().update(bankAccount);
                   p.sendMessage(Color.WHITE, String.format("| Dabartinis banko balansas: %d%c |", bankAccount.getMoney(), Currency.SYMBOL));
                   p.addTotalPaycheck(paycheck);
                   p.sendMessage(Color.WHITE, String.format("| Sukauptas atlyginimas: %d%c", p.getTotalPaycheck(), Currency.SYMBOL));
                   if (houseTax > 0)
                       p.sendMessage(Color.WHITE, String.format("| Mokestis už nekilnojama turtą: %d%c |", houseTax, Currency.SYMBOL));
                   if (businessTax > 0)
                       p.sendMessage(Color.WHITE, String.format("| Verslo mokestis: %d%c |", businessTax, Currency.SYMBOL));
                   if (vehicleTax > 0)
                       p.sendMessage(Color.WHITE, String.format("| Tr. Priemonių mokestis: %d%c |", vehicleTax, Currency.SYMBOL));

                   p.sendGameText(1, 5000, " ~y~Mokesciai~n~~g~Alga");
                   p.setOnlineHours(p.getOnlineHours() + 1);

                   p.setMinutesOnlineSincePayday(0);
               } else {
                   p.sendErrorMessage("Apgailestaujame, bet atlyginimo už šią valandą negausite, kadangi Jūs nebuvote prisijungęs pakankamai.");
               }
               LtrpGamemode.getDao().getPlayerDao().update(p);
           });
        });


        managerNode.registerHandler(PlayerOfferExpireEvent.class, HandlerPriority.BOTTOM, e -> {
            e.getPlayer().getOffers().remove(e.getOffer());
        });

        managerNode.registerHandler(PlayerCommandEvent.class, HandlerPriority.BOTTOM, e -> {
            //e.setProcessed();
        });

        javaMinuteTimer = new Timer("Java minute timer");
        javaMinuteTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LtrpPlayer.get().forEach(p -> p.setMinutesOnlineSincePayday(p.getMinutesOnlineSincePayday() + 1));
            }
        }, 0, 60000);

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
            player.setInventory(new FixedSizeInventory(player.getName() + " kuprinės", player));
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

    public void destroy() {
        managerNode.cancelAll();
        managerNode.destroy();
        javaMinuteTimer.cancel();
        playerCommandManager.destroy();
        playerJailController.destroy();
    }

}