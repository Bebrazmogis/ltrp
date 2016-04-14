package lt.ltrp.job.policeman.dialog;

import lt.ltrp.player.data.PlayerCrime;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.util.event.EventManager;

import java.util.List;

/**
 * @author Bebras
 *         2015.12.30.
 */
public class CrimeListDialog extends PageListDialog {

    private List<PlayerCrime> crimes;

    public CrimeListDialog(LtrpPlayer player, EventManager eventManager, List<PlayerCrime> crimes) {
        super(player, eventManager);
        this.crimes = crimes;
        this.setCaption(crimes.get(0).getPlayerName() + " nusikaltimai. ");
    }

    @Override
    public void show() {
        items.clear();

        for(PlayerCrime crime : crimes) {
            String crimetext = String.format("%s\t%s\t%s", crime.getPlayerName(), crime.getCrime(), crime.getReporterName());
            if(crimetext.length() > 100) {
                crimetext = crimetext.substring(0, 100).trim() + "...";

            }
            items.add(new ListDialogItem(crimetext, d -> CrimeDialog.create(getPlayer(), parentEventManager, crime, this)));
        }
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

}
