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
    @CommandHelp("Leidþia perþiûrëti turimus daiktus")
    public boolean inv(Player player) {
        LtrpPlayer p = LtrpPlayer.get(player);
        Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "GeneralCommands :: inv called");
        p.sendMessage(p.getInventory().getName());
        p.getInventory().show(p);
        return false;
    }

    @Command
    @CommandHelp("Parodo jûsø turimas licenzijas pasirinktam þaidëjui")
    public boolean licenses(LtrpPlayer player, @CommandParam("Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        } else if(player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli!");
        } else {
            target.sendMessage(Color.GREEN, String.format("|________%s licencijos________|", player.getCharName()));
            for(PlayerLicense license : player.getLicenses().get()) {
                player.sendMessage(Color.WHITE, String.format("Licenzijos tipas:%s Iðlaikymo data: %s Áspëjimø skaièius: %d",
                        license.getType(), license.getDateAquired(), license.getWarnings().length));
            }
        }
        return false;
    }

    @Command
    @CommandHelp("Leidþia iðmokti naujus kovos stilius")
    public boolean learnfight(LtrpPlayer player) {
        if(player.getLocation().distance(LtrpGamemode.GYM_LOCATION) > 10f) {
            player.sendErrorMessage("Jûs turite bûti sporto salëje!");
        } else {
            FightStyleDialog.create(player, LtrpGamemode.get().getEventManager()).show();
        }
        return false;
    }
}
