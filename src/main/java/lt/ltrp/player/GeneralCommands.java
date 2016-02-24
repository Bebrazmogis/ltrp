package lt.ltrp.player;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.command.CommandParam;
import lt.ltrp.data.Color;
import lt.ltrp.player.dialog.FightStyleDialog;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.object.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bebras
 *         2015.11.28.
 */
public class GeneralCommands {


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
        return false;
    }

    @Command
    @CommandHelp("Parodo j�s� turimas licenzijas pasirinktam �aid�jui")
    public boolean licenses(LtrpPlayer player, @CommandParam("�aid�jo ID/Dalis vardo")LtrpPlayer target) {
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
        return false;
    }

    @Command
    @CommandHelp("Leid�ia i�mokti naujus kovos stilius")
    public boolean learnfight(LtrpPlayer player) {
        if(player.getLocation().distance(LtrpGamemode.GYM_LOCATION) > 10f) {
            player.sendErrorMessage("J�s turite b�ti sporto sal�je!");
        } else {
            FightStyleDialog.create(player, LtrpGamemode.get().getEventManager()).show();
        }
        return false;
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
        return false;
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
        return false;
    }

}
