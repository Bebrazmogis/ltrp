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
            player.sendErrorMessage("�i� komand� galite naudoti tik b�dami policijos b�stin�je.");
        else {
            player.sendActionMessage("paskambina varpeliu.");
            faction.sendMessage(Color.POLICE, String.format("[LSPD D�R� SKAMBUTIS] Asmuo %s paskambino � d�r� skambut� ir laukia J�s�.", player.getCharName()));
        }
        return true;
    }

}
