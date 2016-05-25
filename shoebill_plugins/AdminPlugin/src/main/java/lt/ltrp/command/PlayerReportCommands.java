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
    @CommandHelp("Praneða administratoriams apie taisykles paþeidþiantá þaidëjà")
    public boolean report(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target,
                          @CommandParameter(name = "Þaidëjo paþeidimas")String reason) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Instant lastReport = AdminPlugin.get(AdminPlugin.class).getPlayerLastReportInstant(player);
        if(target == null || reason == null)
            return false;
        if(lastReport != null && Instant.now().getEpochSecond() - lastReport.getEpochSecond() > AdminPlugin.REPORT_COMMAND_DELAY_SECONDS)
            player.sendErrorMessage("Jûs dar negalite siøsti antro raporto.");
        else if(player.equals(target))
            player.sendErrorMessage("Negalite praneðti saves, jei turite klasimà naudokite /askq");
        else {
            LtrpPlayer.sendAdminMessage(String.format("Veikëjas %s (ID:%d) praneðë apie (ID %d) %s, problema: %s ",
                    player.getName(), player.getId(), target.getId(), target.getName(), reason));
            LtrpPlayer.sendAdminMessage("** KOMANDOS: /are [VEIKËJO ID] patvirtint/priimti praneðimà |  /dre [VEIKËJO ID] [KODËL ATMETËT PRANEÐIMÀ] - atmesti");
            player.sendMessage(Color.GREEN, "Sveikiname, Jûsø praneðimas buvo sëkmingai iðsiøstas visiems budintiems Administratoriams. Administratorius susisieks su Jumis dël tolimesniø veiksmø..");
            AdminPlugin.get(AdminPlugin.class).addReport(player, target, reason);
        }
        return true;
    }


    @Command
    public boolean askq(Player p, @CommandParameter(name = "Iðsamus klausimas")String question) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(question == null)
            return false;
        else if(player.isModerator() || player.isAdmin())
            player.sendErrorMessage("Administracija klausti negali. Á savo klausimus galite atsakyti patys.");
        else {
            AdminPlugin.get(AdminPlugin.class).addQuestion(player, question);
            LtrpPlayer.sendModMessage("Þaidëjas " + player.getName() + " pateikë klausimà:");
            LtrpPlayer.sendModMessage(question);
            long testersOnline = LtrpPlayer.get().stream().filter(LtrpPlayer::isModerator).count();
            player.sendMessage("Jûsø pateiktas klausimas buvo nusiûstas budintiems moderatoriams, palaukite (Prisijungusiu moderatoriø " + testersOnline + ")");
        }
        return true;
    }

}
