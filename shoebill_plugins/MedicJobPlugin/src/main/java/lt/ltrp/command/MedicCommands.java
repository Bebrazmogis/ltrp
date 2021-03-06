package lt.ltrp.command;


import lt.ltrp.job.JobController;
import lt.ltrp.MedicJobPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.player.job.data.PlayerJobData;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.MedicFaction;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.Random;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class MedicCommands extends Commands {

    private MedicFaction job;
    private EventManager eventManager;
    private MedicJobPlugin medicJobPlugin;

    public MedicCommands(MedicFaction job, EventManager eventManager) {
        this.job = job;
        this.eventManager = eventManager;
        this.medicJobPlugin = MedicJobPlugin.get(MedicJobPlugin.class);
    }

    @BeforeCheck
    public boolean bfC(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = player.getJobData();
        if(jobData != null && jobData.getJob().equals(job)) {
            return true;
        } else {
            player.sendErrorMessage("J�s turite b�ti medikas kad gal�tume naudoti �i� komand�.");
        }
        return false;
    }


    @Command
    @CommandHelp("Pradeda/u�baigia bud�jim�")
    public boolean duty(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(medicJobPlugin.isOnDuty(player)) {
            player.sendMessage("[LSFD] J�s baigiate darb� kaip departamento darbuotojas..");
        } else {
            player.sendMessage("[LSFD] J�s prad�jote darb� kaip departamento darbuotojas, nuo �iol galite naudotis departamento komandomis");
            LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, "[Los Santos pagalbos skyrius] " + player.getCharName() + " prad�jo darb� departamente. Skubios pagalbos departamento numeris i�kvietimams: /call 911.");
            player.removeJobWeapons();
        }
        medicJobPlugin.setOnDuty(player, !medicJobPlugin.isOnDuty(player));
        return true;
    }

    @Command
    @CommandHelp("Prikelia �aid�ja i� komos")
    public boolean heal(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else if(player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli");
        } else {
            target.setHealth(100f);
            if(target.isInComa()) {
                target.setInComa(false);
                target.getCountdown().forceStop();
                target.sendMessage("Daktaras " + player.getCharName() + " s�kmingai pad�jo Jums i�gyti, bei pasveikti. Gydymo i�laidos 50$.");
                target.giveMoney(-50);
                target.clearAnimations(1);
                player.applyAnimation("MEDIC", "CPR", 4.1f, false, false, false, false, 0, false);
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Paskiria darbuotoja gaisrininku")
    public boolean setFd(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = player.getJobData();
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else if(player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli");
        } else if(target.getJobData() == null || jobData.getJob().equals(target.getJobData().getJob())) {
            player.sendErrorMessage(target.getCharName() + " jums nedirba!");
        } else {
            target.setSkin(277 + new Random().nextInt(3));
            target.giveWeapon(new LtrpWeaponData(WeaponModel.CHAINSAW, 1, true));
            target.giveWeapon(new LtrpWeaponData(WeaponModel.FIREEXTINGUISHER, 5000, true));
        }
        return true;
    }
}
