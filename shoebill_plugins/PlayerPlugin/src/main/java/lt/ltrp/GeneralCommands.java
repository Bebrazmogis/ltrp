package lt.ltrp;


import lt.ltrp.constant.Currency;
import lt.ltrp.data.Color;
import lt.ltrp.data.PlayerLicense;
import lt.ltrp.dialog.FightStyleDialog;
import lt.ltrp.dialog.PlayerSettingsListDialog;
import lt.ltrp.event.player.PlayerAskQuestionEvent;
import lt.ltrp.event.player.PlayerSendPrivateMessageEvent;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerEntry;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.11.28.
 */
public class GeneralCommands {

    private EventManager eventManager;
    private Map<LtrpPlayer, Long> askqUseTimestamps;
    private HandlerEntry disconnectEntry;

    public GeneralCommands(EventManager eventManager) {
        this.eventManager = eventManager;
        this.askqUseTimestamps = new HashMap<>();
        disconnectEntry = eventManager.registerHandler(PlayerDisconnectEvent.class, e -> askqUseTimestamps.remove(LtrpPlayer.get(e.getPlayer())));
    }

    @Override
    protected void finalize() {
        disconnectEntry.cancel();
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player != null) {
            Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "GeneralCommands :: beforeCheck. Player logged in? " + player.isLoggedIn());
            return player.isLoggedIn();
        }
        return false;
    }


    @Command(name = "javainv")
    @CommandHelp("Leidþia perþiûrëti turimus daiktus")
    public boolean inv(Player player) {
        LtrpPlayer p = LtrpPlayer.get(player);
        Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "GeneralCommands :: inv called");
        p.sendMessage(p.getInventory().getName());
        p.getInventory().show(p);
        return true;
    }

    @Command
    @CommandHelp("Parodo jûsø turimas licenzijas pasirinktam þaidëjui")
    public boolean licenses(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player =LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        } else if(player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli!");
        } else {
            target.sendMessage(Color.GREEN, String.format("|________%s licencijos________|", player.getCharName()));
            for(PlayerLicense license : player.getLicenses().get()) {
                if(license != null)
                    player.sendMessage(Color.WHITE, String.format("Licenzijos tipas:%s. Etapas: %s Iðlaikymo data: %s Áspëjimø skaièius: %d",
                        license.getType().getName(), license.getStage() == 2 ? "Praktika" : "Teorija", license.getDateAquired(), license.getWarnings().length));
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Leidþia iðmokti naujus kovos stilius")
    public boolean learnfight(LtrpPlayer player) {
        if(player.getLocation().distance(PlayerController.GYM_LOCATION) > 10f) {
            player.sendErrorMessage("Jûs turite bûti sporto salëje!");
        } else {
            FightStyleDialog.create(player, eventManager).show();
        }
        return true;
    }

    @Command
    @CommandHelp("Atðaukia vykdomà veiksmà")
    public boolean stop(Player player) {
        LtrpPlayer p =LtrpPlayer.get(player);
        if(p.getCountdown() == null) {
            p.sendErrorMessage("Jûs neatlikinëjate jokio veiksmo!");
        } else if(!p.getCountdown().isStoppable()) {
            p.sendErrorMessage("Ðio veiksmo atðaukti negalima!");
        } else {
            p.getCountdown().stop();
            p.sendMessage("Veiksmas sëkmingia atðauktas.");
            p.setCountdown(null);
            return true;
        }
        return true;
    }

    @Command
    @CommandHelp("Esant komos bûsenoje, leidþia susitaikyti su mirtimi")
    public boolean die(Player player) {
        LtrpPlayer p =LtrpPlayer.get(player);
        if(!p.isInComa()) {
            p.sendErrorMessage("Jûs neesate komos bûsenoje!");
        } else if(p.getCountdown().getTimeleft() > 420) {
            p.sendErrorMessage("Dar nepraëjo 3 minutës.");
        } else {
            p.setHealth(0f);
            p.clearAnimations(1);
            p.getCountdown().forceStop();
            return true;
        }
        return true;
    }

    @Command
    @CommandHelp("Nusiunèia þaidëjui privaèià, OOC þinutæ")
    public boolean pm(Player player,
                      @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                      @CommandParameter(name = "Þinutës tekstas")String text) {
        LtrpPlayer p =LtrpPlayer.get(player);
        if(target == null) {
            p.sendErrorMessage("Tokio þaidëjo nëra!");
        } else if(target.getSettings().isPmDisabled()) {
            p.sendErrorMessage(target.getName() + " þaidëjas yra iðjungæs PM þinuèiø gavimà.");
        } else {
            target.playSound(1057);
            target.sendMessage(Color.PM_RECEIVED, String.format("(( Gauta PÞ nuo %s[ID:%d]: %s ))", p.getName(), p.getId(), text));
            p.sendMessage(Color.PM_SENT, String.format("(( PÞ iðsiûsta %s[ID:%d]: %s ))", target.getName(), target.getId(), text));
            eventManager.dispatchEvent(new PlayerSendPrivateMessageEvent(p, target, text));
        }
        return true;
    }

    @Command
    @CommandHelp("Atidaro þaidimo nustatymø meniu")
    public boolean settings(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerSettingsListDialog.create(player, eventManager, player.getSettings())
                .show();
        return true;
    }

    @Command
    @CommandHelp("Iðsiunèia klausimà administracijai")
    public boolean askq(Player p, @CommandParameter(name = "Klausimas")String message) {
        LtrpPlayer player = LtrpPlayer.get(p);
        long current = Instant.now().getEpochSecond();
        if(askqUseTimestamps.containsKey(player) && current - askqUseTimestamps.get(player) > 60) {
            player.sendErrorMessage("Klausti klausimus galite tik kas minutæ!");
        } else {
            askqUseTimestamps.put(player, current);
            eventManager.dispatchEvent(new PlayerAskQuestionEvent(player, message));
        }
        return true;
    }

    @Command
    @CommandHelp("Parodo prisijungusiø moderatoriø sàraðà")
    public boolean moderators(Player pl) {
        LtrpPlayer p = LtrpPlayer.get(pl);
        Collection<LtrpPlayer> onlineMods = LtrpPlayer.get()
                .stream()
                .filter(LtrpPlayer::isModerator)
                .collect(Collectors.toList());
        if(onlineMods.size() > 0) {
            p.sendMessage(Color.MODERATOR, "|_________________PRISIJUNGÆ MODERATORIAI_________________|");
            onlineMods.forEach(m -> {
                if(AdminController.get().getModeratorsOnDuty().contains(m))
                    p.sendMessage(Color.GREEN, String.format("Moderatorius %s (%s) ájungæs budinèio moderatoriaus rëþimà.", p.getName(), p.getForumName()));
                else
                    p.sendMessage(Color.LIGHTRED, String.format("Moderatorius %s (%s) iðjungæs budinèio moderatoriaus rëþimà.", p.getName(), p.getForumName()));
            });
        } else {
            p.sendErrorMessage("Nëra nei vieno prisingusio moderatoriaus!");
        }
        return true;
    }

    @Command
    @CommandHelp("Prideda pasirinktà sumà á miesto biudþetà")
    public boolean charity(Player p, @CommandParameter(name = "Suma")int amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(amount > player.getMoney() || amount < 0)
            player.sendErrorMessage("Jûs tiek pinigø neturite");
        else {
            player.giveMoney(-amount);
            player.sendMessage(Color.NEWS, "Parëmëte miesto biudþetà " + amount + Currency.SYMBOL);
            LtrpWorld.get().addMoney(amount);
        }
        return true;
    }

    // TODO cmd:togooc
    // TODO cmd:togpm
    // TODO cmd:togadmin
    // TODO cmd:tognews
    // TODO cmd:o
    // TODO cmd:ad
}
