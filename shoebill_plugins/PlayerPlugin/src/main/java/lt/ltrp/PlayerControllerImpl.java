package lt.ltrp;


import lt.ltrp.command.PlayerAcceptOffers;
import lt.ltrp.command.PlayerAnimationCommands;
import lt.ltrp.command.PlayerChatCommands;
import lt.ltrp.constant.Currency;
import lt.ltrp.dao.PlayerDao;
import lt.ltrp.data.*;
import lt.ltrp.event.PaydayEvent;
import lt.ltrp.event.player.PlayerDataLoadEvent;
import lt.ltrp.event.player.PlayerEditSettingsEvent;
import lt.ltrp.event.player.PlayerLogInEvent;
import lt.ltrp.event.player.PlayerOfferExpireEvent;
import lt.ltrp.object.*;
import lt.ltrp.object.impl.LtrpPlayerImpl;
import lt.ltrp.player.BankAccount;
import lt.ltrp.util.PawnFunc;
import lt.ltrp.util.PlayerLog;
import lt.maze.AfkPlugin;
import lt.maze.streamer.StreamerPlugin;
import lt.maze.streamer.constant.StreamerType;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.common.timers.TemporaryTimer;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.constant.WeaponSkill;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.WeaponData;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.shoebill.event.amx.AmxUnloadEvent;
import net.gtaun.shoebill.event.player.*;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerAttach;
import net.gtaun.shoebill.object.PlayerKeyState;
import net.gtaun.shoebill.object.PlayerWeaponSkill;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

public class PlayerControllerImpl implements PlayerController {

    protected static final int MINUTES_FOR_PAYDAY = 20;
    public static final Color DEFAULT_PLAYER_COLOR = new Color(0xFFFFFF00);

    private EventManagerNode managerNode;
    private PlayerDao playerDao;
    public static Collection<LtrpPlayer> playerList = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);

    private List<LtrpPlayer> firstSpawns = new ArrayList<>();
    private Timer javaMinuteTimer;
    private PlayerLog playerLog;
    private boolean destroyed;

    public PlayerControllerImpl(EventManager manager, PlayerDao playerDao, PlayerCommandManager playerCommandManager) {
        Instance.instance = this;
        this.playerDao = playerDao;
        managerNode = manager.createChildNode();

        this.playerLog = new PlayerLog(managerNode);

        playerCommandManager.registerCommands(new GeneralCommands(managerNode));
        playerCommandManager.registerCommands(new PlayerAcceptOffers());
        playerCommandManager.registerCommands(new PlayerChatCommands(managerNode));
        playerCommandManager.registerCommands(new PlayerAnimationCommands());


        managerNode.registerHandler(PlayerConnectEvent.class, HandlerPriority.NORMAL, e -> {
            logger.info("PlayerConnectEvent received: " + e.getPlayer().getName());
            if(e.getPlayer().isNpc()) {
                return;
            }
                LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
                playerList.add(player);

                // Various options and settings
                player.setColor(Color.WHITE); // Make the users radar blip invisible
        });

        managerNode.registerHandler(PlayerLogInEvent.class, e -> {
            logger.info("PlayerLogInEvent received");
            LtrpPlayer player = e.getPlayer();
            player.sendMessage("{FFFFFF}Sveikiname sugrįžus, Jūs prisijungėte su veikėju " + player.getName() + ". Sėkmės serveryje!");

            player.sendGameText(5000, 1, "~w~Sveikas ~n~~h~~g~" + player.getName());
            loadDataThreaded(player);

            player.setLastLogin(new Timestamp(Instant.now().toEpochMilli()));
            playerDao.updateLastLogin(player);

            // Legacy code for Pawn loading.
            AmxCallable onPlayerLoginPawn = PawnFunc.getPublicMethod("getPublicMethod");
            if(onPlayerLoginPawn != null) {
                onPlayerLoginPawn.call(player.getId(), player.getUUID());
            }
        });

        managerNode.registerHandler(PlayerTextEvent.class, HandlerPriority.HIGH, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player.isMuted()) {
                player.sendErrorMessage("Jums uždrausta kalbėti!");
            } else {
                String msg;
                if(!player.isInAnyVehicle()) {
                    msg = String.format("%s sako: %s", player.getCharName(), e.getText());
                } else {
                    LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
                    msg = String.format("(%s) %s sako: %s",
                            vehicle.isSeatWindowOpen(player.getVehicleSeat()) ? "Langas Atidarytas" : "Langas uždarytas",
                            player.getCharName(),
                            e.getText()
                            );
                }
                return;
            }
            e.interrupt();
        });

        // In the end... we play an animation if the player has set it
        managerNode.registerHandler(PlayerTextEvent.class, HandlerPriority.BOTTOM, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player.getTalkStyle() != null) {
                player.applyAnimation(player.getTalkStyle().getAnimation());
                int time = 200 * e.getText().length();
                TemporaryTimer.create(time, 1, (i) -> {
                    if(player.getAnimation().equals(player.getTalkStyle().getAnimation())
                            )player.clearAnimations();
                });
            }
        });

        managerNode.registerHandler(PlayerDisconnectEvent.class, HandlerPriority.BOTTOM, e -> {
            logger.info("PlayerDisconnectEvent received");
            LtrpPlayer p = LtrpPlayer.get(e.getPlayer());
            if(p == null)
                return;

            playerList.remove(p);

            String leaveMessage;
            switch(e.getReason()) {
                case LEFT:
                    leaveMessage = String.format("%s paliko serverį (Klientas atsijungė).", p.getName());
                    break;
                case KICK:
                    leaveMessage = String.format("%s paliko serverį (Klientas išmestas).", p.getName());
                    break;
                default:
                    leaveMessage = String.format("%s paliko serverį (įvyko kliento klaida/nutrųko ryšys).", p.getName());
                    break;
            }
            p.sendFadeMessage(Color.WHITE, leaveMessage, 20f);

            // If he disconnects while in coma, automatically we add a death
            if(p.isInComa()) {
                p.setDeaths(p.getDeaths() + 1);
            }

            for(PlayerAttach.PlayerAttachSlot s : p.getAttach().getSlots())
                if(s.isUsed()) s.remove();

            playerDao.update(p);
            p.destroy();
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
                    logger.error(String.format("User %s(UDI:%d) level is 0.", player.getName(), player.getUUID()));
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
                    player.applyAnimation("CRACK", "crckdeth2", 4f, true, false, false, false, 0, false);
                    // We start the coma countdown
                    player.setCountdown(PlayerCountdown.create(player, 600, true, (p, success) -> {
                        if (success)
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

                playerDao.update(player);
            }
        });

        managerNode.registerHandler(PaydayEvent.class, HandlerPriority.HIGH, e -> {
            BankPlugin bankPlugin = Shoebill.get().getResourceManager().getPlugin(BankPlugin.class);
           LtrpPlayer.get().forEach(p -> {
               // fail-safe
               if(Player.get(p.getId()) == null)
                   playerList.remove(p);

               Taxes taxes = LtrpWorld.get().getTaxes();
               int houseTax = (int) House.get().stream().filter(h -> h.getOwner() == p.getUUID()).count() * taxes.getHouseTax();
               int businessTax = (int) Business.get().stream().filter(b -> b.getOwner() == p.getUUID()).count() * taxes.getBusinessTax();
               int garageTax = (int) Garage.get().stream().filter(g -> g.getOwner() == p.getUUID()).count() * taxes.getGarageTax();
               int vehicleTax = PlayerVehiclePlugin.get(PlayerVehiclePlugin.class).getVehicleDao().getPlayerVehicleCount(p) * taxes.getVehicleTax();

               if(p.getMinutesOnlineSincePayday() > MINUTES_FOR_PAYDAY) {
                   int paycheck = 0;

                   PlayerJobData jobData = JobController.get().getJobData(p);
                   if (jobData != null) {
                       paycheck = jobData.getJobRank().getSalary();
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
               playerDao.update(p);
           });
        });

        managerNode.registerHandler(PlayerWeaponShotEvent.class, HandlerPriority.HIGHEST, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            WeaponModel weaponModel = e.getWeapon();
            LtrpWeaponData weaponData = player.getWeaponData(weaponModel);
            if(weaponData != null) {
                weaponData.setAmmo(player.getArmedWeaponAmmo());
            }

            new Thread(() -> {
                if(weaponData.getAmmo() > 0) {
                    PlayerPlugin.get(PlayerPlugin.class).getPlayerWeaponDao().update(weaponData);
                } else {
                    PlayerPlugin.get(PlayerPlugin.class).getPlayerWeaponDao().remove(weaponData);
                }
            }).start();
        });

        managerNode.registerHandler(PlayerStreamInEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            LtrpPlayer forPlayer = LtrpPlayer.get(e.getForPlayer());
            forPlayer.showNameTagForPlayer(player, player.isMasked());

            // If the streamed in player is muted and the forPlayer is admin, show mute label
            if(player.isMuted() && (forPlayer.isAdmin() || forPlayer.isModerator())) {
                ((LtrpPlayerImpl)player).updateMuteLabel();
            }
        });

        managerNode.registerHandler(PlayerOfferExpireEvent.class, HandlerPriority.BOTTOM, e -> {
            e.getPlayer().getOffers().remove(e.getOffer());
        });

        managerNode.registerHandler(PlayerCommandEvent.class, HandlerPriority.BOTTOM, e -> {
            //e.setProcessed();
        });

        managerNode.registerHandler(PlayerEditSettingsEvent.class, e -> {
            playerDao.update(e.getSettings());
        });

        managerNode.registerHandler(PlayerKeyStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            PlayerKeyState old = e.getOldState();
            PlayerKeyState newKeys = player.getKeyState();
            if(!old.isKeyPressed(PlayerKey.SPRINT) && newKeys.isKeyPressed(PlayerKey.SPRINT)) {
                Animation animation = player.getAnimation();
                SpecialAction action = player.getSpecialAction();
                if(animation != null && animation.isStoppable()) {
                    player.clearAnimations();
                } else if(action == SpecialAction.SPECIAL_ACTION_PISSING ||
                        action == SpecialAction.HANDSUP ||
                        action == SpecialAction.DANCE1 ||
                        action == SpecialAction.DANCE2 ||
                        action == SpecialAction.DANCE3 ||
                        action == SpecialAction.DANCE4) {
                    player.setSpecialAction(SpecialAction.NONE);
                }
            }
            if(!old.isKeyPressed(PlayerKey.WALK) && newKeys.isKeyPressed(PlayerKey.WALK)) {
                if(!player.isInAnyVehicle() && player.getWalkStyle() != null) {
                    player.applyAnimation(player.getWalkStyle().getAnimation());
                    player.sendInfoText("Norint sustoti spauskite ~r~SPACE");
                }
            }
        });

        javaMinuteTimer = new Timer("Java minute timer");
        javaMinuteTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LtrpPlayer.get().forEach(p -> {
                    p.setMinutesOnlineSincePayday(p.getMinutesOnlineSincePayday() + 1);
                    int seconds = AfkPlugin.get(AfkPlugin.class).getPlayerAfkSeconds(p);
                    if(seconds >= 7*60) {
                        LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, "Žaidėjas " + p.getName() + " buvo išmestas iš serverio. Priežastis: AFK");
                        p.kick();
                    }
                });


            }
        }, 0, 60000);

        addPawnFunctions();

    }



    private void addPawnFunctions() {
        logger.info("PlayerController :: addPawnFunctions. Called.");
        managerNode.registerHandler(AmxLoadEvent.class, e-> {
            e.getAmxInstance().registerFunction("updatePlayerInfoText", params -> {
                LtrpPlayer player = LtrpPlayer.get(Player.get((Integer) params[0]));
                if(player != null && player.getInfoBox() != null) {
                    player.getInfoBox().update();
                }
                return player == null ? 0 : 1;
            }, Integer.class);

            e.getAmxInstance().registerFunction("isPlayerLoggedIn", params -> {
                LtrpPlayer player = LtrpPlayer.get(Player.get((Integer) params[0]));
                if (player != null) {
                    return player.isLoggedIn() ? 1 : 0;
                }
                return 0;
            }, Integer.class);

            e.getAmxInstance().registerFunction("saveAccount", params -> {
                LtrpPlayer player = LtrpPlayer.get(Player.get((Integer) params[0]));
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
            Item[] items = ItemController.get().getItemDao().getItems(player);
            //logger.info("PlayerController :: PlayerLoginEvent :: " + items.length + " loaded for user id " + player.getUUID());
            player.setInventory(Inventory.create(managerNode, player, player.getName() + " kuprinės", 20));
            player.getInventory().add(items);

           // player.setVehicleMetadata(playerDao.getVehiclePermissions(player));

            PlayerLicenses licenses = playerDao.get(player);
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

    public void destroy() {
        playerLog.destroy();
        managerNode.cancelAll();
        managerNode.destroy();
        javaMinuteTimer.cancel();
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public Collection<LtrpPlayer> getPlayers() {
        return playerList;
    }

    @Override
    public PlayerDao getPlayerDao() {
        return playerDao;
    }

    @Override
    public String getUsernameByUUID(int i) {
        Optional<LtrpPlayer>opP = LtrpPlayer.get().stream().filter(p -> p.getUUID() == i).findFirst();
        return opP.isPresent() ? opP.get().getName() : playerDao.getUsername(i);
    }
}