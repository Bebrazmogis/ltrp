package lt.ltrp.command;

import lt.ltrp.PoliceJobPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.PoliceFaction;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class CivilianCommands {

    @Command
    public boolean bell(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PoliceFaction faction = PoliceJobPlugin.get(PoliceJobPlugin.class).getPoliceFaction();
        if(faction.getLocation().getWorldId() != player.getLocation().getWorldId() || player.getLocation().getInteriorId() != faction.getLocation().getInteriorId())
            player.sendErrorMessage("Ðià komandà galite naudoti tik bûdami policijos bûstinëje.");
        else {
            player.sendActionMessage("paskambina varpeliu.");
            faction.sendMessage(Color.POLICE, String.format("[LSPD DÛRØ SKAMBUTIS] Asmuo %s paskambino á dûrø skambutá ir laukia Jûsø.", player.getCharName()));
        }
        return true;
    }

}
