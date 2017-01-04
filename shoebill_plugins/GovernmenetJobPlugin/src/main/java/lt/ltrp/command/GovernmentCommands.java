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
    @CommandHelp("Patikrina valstybës biudþetà")
    public boolean checkBudget(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(!GovernmentJobPlugin.get(GovernmentJobPlugin.class).getGovernmentFaction().getLeaders().contains(player.getUUID()))
            player.sendErrorMessage("Jûs neesate miesto meras!");
        else {
            player.sendMessage(Color.GREEN, "|_____BENDRAS LOS SANTOS MIESTO BIUDÞETAS_____|");
            player.sendMessage(Color.WHITE, "Bendras Los Santos miesto biudþetas sieka " + LtrpWorld.get().getMoney() + Currency.SYMBOL);
        }
        return true;
    }

 // TODO THIS DOES NOT WORK
    @Command
    @CommandHelp("Paima tam tikrà pinigø sumà ið biudþeto")
    public boolean takeMoney(Player p,
                             @CommandParameter(name = "3 lygio administratoriaus ID/Dalis varod")LtrpPlayer admin,
                             @CommandParameter(name = "Suma")int amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(!GovernmentJobPlugin.get(GovernmentJobPlugin.class).getGovernmentFaction().getLeaders().contains(player.getUUID()))
            player.sendErrorMessage("Jûs neesate miesto meras!");
        else if(admin == null)
            return false;
        else if(!admin.isAdmin())
            player.sendErrorMessage(admin.getName() + " nëra administratorius");
        else if(admin.getAdminLevel() < 3)
            player.sendErrorMessage(admin.getName() + " administratoriaus lygis per maþas, minimalus yra 3.");
        else if(player.getDistanceToPlayer(admin) > 3f)
            player.sendErrorMessage("Administratorius " + admin.getName() + " yra per toli.");
        else if(amount <= 0)
            player.sendErrorMessage("Suma negali bûti maþesnë uþ 0.");
        else if(amount > LtrpWorld.get().getMoney())
            player.sendErrorMessage("Suma negali bûti didesnë uþ " + LtrpWorld.get().getMoney());
        else {
            player.giveMoney(amount);
            LtrpWorld.get().addMoney(-amount);
            //LtrpGamemodeImpl.getGamemode(LtrpGamemodeImpl.class).getDao().save(LtrpWorld.get());
            LtrpPlayer.sendAdminMessage(String.format("Miesto meras %s(%d) paëmë %d ið biudþeto, tai autorizavæs administratorius %s.",
                    player.getName(), player.getId(), amount, admin.getName()));
            AdminLog.log(admin, "Leido paimti " + amount + " ið miesto biudþeto merui " + player.getUUID());
        }
        return true;
    }
}
