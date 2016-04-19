package lt.ltrp.medic;


import lt.ltrp.command.Commands;
import lt.ltrp.data.Color;
import lt.ltrp.JobController;
import lt.ltrp.data.JobData;
import lt.ltrp.object.MedicFaction;
import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.object.Player;

import java.util.Random;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class MedicCommands extends Commands {

    private MedicFaction job;
    private MedicManager manager;

    public MedicCommands(MedicFaction job, MedicManager manager) {
        this.job = job;
        this.manager = manager;
    }

    @BeforeCheck
    public boolean bfC(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        JobData jobData = JobController.get().getJobData(player);
        if(jobData.getJob().equals(job)) {
            return true;
        } else {
            player.sendErrorMessage("Jûs turite bûti medikas kad galëtume naudoti ðià komandà.");
        }
        return false;
    }


    @Command
    @CommandHelp("Pradeda/uþbaigia budëjimà")
    public boolean duty(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(manager.isOnDuty(player)) {
            player.sendMessage("[LSFD] Jûs baigiate darbà kaip departamento darbuotojas..");
        } else {
            player.sendMessage("[LSFD] Jûs pradëjote darbà kaip departamento darbuotojas, nuo ðiol galite naudotis departamento komandomis");
            LtrpPlayer.sendGlobalMessage(Color.LIGHTRED, "[Los Santos pagalbos skyrius] " + player.getCharName() + " pradëjo darbà departamente. Skubios pagalbos departamento numeris iðkvietimams: /call 911.");
            player.removeJobWeapons();
        }
        manager.setOnDuty(player, !manager.isOnDuty(player));
        return true;
    }

    @Command
    @CommandHelp("Prikelia þaidëja ið komos")
    public boolean heal(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        } else if(player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli");
        } else {
            target.setHealth(100f);
            if(target.isInComa()) {
                target.setInComa(false);
                target.getCountdown().forceStop();
                target.sendMessage("Daktaras " + player.getCharName() + " sëkmingai padëjo Jums iðgyti, bei pasveikti. Gydymo iðlaidos 50$.");
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
        JobData jobData = JobController.get().getJobData(player);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        } else if(player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli");
        } else if(jobData.getJob().equals(JobController.get().getJobData(target).getJob())) {
            player.sendErrorMessage(target.getCharName() + " jums nedirba!");
        } else {
            target.setSkin(277 + new Random().nextInt(3));
            target.giveWeapon(new LtrpWeaponData(WeaponModel.CHAINSAW, 1, true));
            target.giveWeapon(new LtrpWeaponData(WeaponModel.FIREEXTINGUISHER, 5000, true));
        }
        return true;
    }
}
