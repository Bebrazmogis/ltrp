package lt.ltrp.command;

import lt.ltrp.*;
import lt.ltrp.constant.Currency;
import lt.ltrp.data.Color;
import lt.ltrp.player.job.data.PlayerJobData;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.util.AdminLog;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class GovernmentCommands extends Commands {



    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.getJobData() != null && player.getJobData().getJob().equals(GovernmentJobPlugin.get(GovernmentJobPlugin.class).getGovernmentFaction())) {
            return true;
        }
        return false;
    }


    @Command
    @CommandHelp("Patikrina valstyb�s biud�et�")
    public boolean checkBudget(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(!GovernmentJobPlugin.get(GovernmentJobPlugin.class).getGovernmentFaction().getLeaders().contains(player.getUUID()))
            player.sendErrorMessage("J�s neesate miesto meras!");
        else {
            player.sendMessage(Color.GREEN, "|_____BENDRAS LOS SANTOS MIESTO BIUD�ETAS_____|");
            player.sendMessage(Color.WHITE, "Bendras Los Santos miesto biud�etas sieka " + LtrpWorld.get().getMoney() + Currency.SYMBOL);
        }
        return true;
    }

 // TODO THIS DOES NOT WORK
    @Command
    @CommandHelp("Paima tam tikr� pinig� sum� i� biud�eto")
    public boolean takeMoney(Player p,
                             @CommandParameter(name = "3 lygio administratoriaus ID/Dalis varod")LtrpPlayer admin,
                             @CommandParameter(name = "Suma")int amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(!GovernmentJobPlugin.get(GovernmentJobPlugin.class).getGovernmentFaction().getLeaders().contains(player.getUUID()))
            player.sendErrorMessage("J�s neesate miesto meras!");
        else if(admin == null)
            return false;
        else if(!admin.isAdmin())
            player.sendErrorMessage(admin.getName() + " n�ra administratorius");
        else if(admin.getAdminLevel() < 3)
            player.sendErrorMessage(admin.getName() + " administratoriaus lygis per ma�as, minimalus yra 3.");
        else if(player.getDistanceToPlayer(admin) > 3f)
            player.sendErrorMessage("Administratorius " + admin.getName() + " yra per toli.");
        else if(amount <= 0)
            player.sendErrorMessage("Suma negali b�ti ma�esn� u� 0.");
        else if(amount > LtrpWorld.get().getMoney())
            player.sendErrorMessage("Suma negali b�ti didesn� u� " + LtrpWorld.get().getMoney());
        else {
            player.giveMoney(amount);
            LtrpWorld.get().addMoney(-amount);
            //LtrpGamemodeImpl.getGamemode(LtrpGamemodeImpl.class).getDao().save(LtrpWorld.get());
            LtrpPlayer.sendAdminMessage(String.format("Miesto meras %s(%d) pa�m� %d i� biud�eto, tai autorizav�s administratorius %s.",
                    player.getName(), player.getId(), amount, admin.getName()));
            AdminLog.log(admin, "Leido paimti " + amount + " i� miesto biud�eto merui " + player.getUUID());
        }
        return true;
    }
}
