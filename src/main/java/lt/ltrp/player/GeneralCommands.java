package lt.ltrp.player;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.command.CommandParam;
import lt.ltrp.data.Color;
import lt.ltrp.player.dialog.FightStyleDialog;
import lt.ltrp.player.dialog.PlayerSettingsListDialog;
import lt.ltrp.player.event.PlayerSendPrivateMessageEvent;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bebras
 *         2015.11.28.
 */
public class GeneralCommands {

    private EventManager eventManager;

    public GeneralCommands(EventManager eventManager) {
        this.eventManager = eventManager;
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
    @CommandHelp("Leid�ia per�i�r�ti turimus daiktus")
    public boolean inv(Player player) {
        LtrpPlayer p = LtrpPlayer.get(player);
        Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "GeneralCommands :: inv called");
        p.sendMessage(p.getInventory().getName());
        p.getInventory().show(p);
        return true;
    }

    @Command
    @CommandHelp("Parodo j�s� turimas licenzijas pasirinktam �aid�jui")
    public boolean licenses(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player =LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else if(player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli!");
        } else {
            target.sendMessage(Color.GREEN, String.format("|________%s licencijos________|", player.getCharName()));
            for(PlayerLicense license : player.getLicenses().get()) {
                if(license != null)
                    player.sendMessage(Color.WHITE, String.format("Licenzijos tipas:%s. Etapas: %s I�laikymo data: %s �sp�jim� skai�ius: %d",
                        license.getType().getName(), license.getStage() == 2 ? "Praktika" : "Teorija", license.getDateAquired(), license.getWarnings().length));
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Leid�ia i�mokti naujus kovos stilius")
    public boolean learnfight(LtrpPlayer player) {
        if(player.getLocation().distance(LtrpGamemode.GYM_LOCATION) > 10f) {
            player.sendErrorMessage("J�s turite b�ti sporto sal�je!");
        } else {
            FightStyleDialog.create(player, LtrpGamemode.get().getEventManager()).show();
        }
        return true;
    }

    @Command
    @CommandHelp("At�aukia vykdom� veiksm�")
    public boolean stop(Player player) {
        LtrpPlayer p =LtrpPlayer.get(player);
        if(p.getCountdown() == null) {
            p.sendErrorMessage("J�s neatlikin�jate jokio veiksmo!");
        } else if(!p.getCountdown().isStoppable()) {
            p.sendErrorMessage("�io veiksmo at�aukti negalima!");
        } else {
            p.getCountdown().stop();
            p.sendMessage("Veiksmas s�kmingia at�auktas.");
            p.setCountdown(null);
            return true;
        }
        return true;
    }

    @Command
    @CommandHelp("Esant komos b�senoje, leid�ia susitaikyti su mirtimi")
    public boolean die(Player player) {
        LtrpPlayer p =LtrpPlayer.get(player);
        if(!p.isInComa()) {
            p.sendErrorMessage("J�s neesate komos b�senoje!");
        } else if(p.getCountdown().getTimeleft() > 420) {
            p.sendErrorMessage("Dar nepra�jo 3 minut�s.");
        } else {
            p.setHealth(0f);
            p.clearAnimations(1);
            p.getCountdown().forceStop();
            return true;
        }
        return true;
    }

    @Command
    @CommandHelp("Nusiun�ia �aid�jui priva�i�, OOC �inut�")
    public boolean pm(Player player,
                      @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                      @CommandParameter(name = "�inut�s tekstas")String text) {
        LtrpPlayer p =LtrpPlayer.get(player);
        if(target == null) {
            p.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else if(target.getSettings().isPmDisabled()) {
            p.sendErrorMessage(target.getName() + " �aid�jas yra i�jung�s PM �inu�i� gavim�.");
        } else {
            target.playSound(1057);
            target.sendMessage(Color.PM_RECEIVED, String.format("(( Gauta P� nuo %s[ID:%d]: %s ))", p.getName(), p.getId(), text));
            p.sendMessage(Color.PM_SENT, String.format("(( P� i�si�sta %s[ID:%d]: %s ))", target.getName(), target.getId(), text));
            eventManager.dispatchEvent(new PlayerSendPrivateMessageEvent(p, target, text));
        }
        return true;
    }

    @Command
    @CommandHelp("Atidaro �aidimo nustatym� meniu")
    public boolean settings(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerSettingsListDialog.create(player, eventManager, player.getSettings())
                .show();
        return true;
    }

}
