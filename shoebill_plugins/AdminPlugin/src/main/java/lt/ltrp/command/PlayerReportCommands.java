package lt.ltrp.command;

import lt.ltrp.AdminPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.object.Player;

import java.time.Instant;

/**
 * @author Bebras
 *         2016.05.25.
 */
public class PlayerReportCommands {


    public PlayerReportCommands() {

    }

    @Command
    @CommandHelp("Prane�a administratoriams apie taisykles pa�eid�iant� �aid�j�")
    public boolean report(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target,
                          @CommandParameter(name = "�aid�jo pa�eidimas")String reason) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Instant lastReport = AdminPlugin.get(AdminPlugin.class).getPlayerLastReportInstant(player);
        if(target == null || reason == null)
            return false;
        if(lastReport != null && Instant.now().getEpochSecond() - lastReport.getEpochSecond() > AdminPlugin.REPORT_COMMAND_DELAY_SECONDS)
            player.sendErrorMessage("J�s dar negalite si�sti antro raporto.");
        else if(player.equals(target))
            player.sendErrorMessage("Negalite prane�ti saves, jei turite klasim� naudokite /askq");
        else {
            LtrpPlayer.sendAdminMessage(String.format("Veik�jas %s (ID:%d) prane�� apie (ID %d) %s, problema: %s ",
                    player.getName(), player.getId(), target.getId(), target.getName(), reason));
            LtrpPlayer.sendAdminMessage("** KOMANDOS: /are [VEIK�JO ID] patvirtint/priimti prane�im� |  /dre [VEIK�JO ID] [KOD�L ATMET�T PRANE�IM�] - atmesti");
            player.sendMessage(Color.GREEN, "Sveikiname, J�s� prane�imas buvo s�kmingai i�si�stas visiems budintiems Administratoriams. Administratorius susisieks su Jumis d�l tolimesni� veiksm�..");
            AdminPlugin.get(AdminPlugin.class).addReport(player, target, reason);
        }
        return true;
    }


    @Command
    public boolean askq(Player p, @CommandParameter(name = "I�samus klausimas")String question) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(question == null)
            return false;
        else if(player.isModerator() || player.isAdmin())
            player.sendErrorMessage("Administracija klausti negali. � savo klausimus galite atsakyti patys.");
        else {
            AdminPlugin.get(AdminPlugin.class).addQuestion(player, question);
            LtrpPlayer.sendModMessage("�aid�jas " + player.getName() + " pateik� klausim�:");
            LtrpPlayer.sendModMessage(question);
            long testersOnline = LtrpPlayer.get().stream().filter(LtrpPlayer::isModerator).count();
            player.sendMessage("J�s� pateiktas klausimas buvo nusi�stas budintiems moderatoriams, palaukite (Prisijungusiu moderatori� " + testersOnline + ")");
        }
        return true;
    }

}
