package lt.ltrp.dialog;

import lt.ltrp.player.fine.data.PlayerFine;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.util.event.EventManager;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Bebras
 *         2015.12.30.
 */
public class CrimeListDialog extends PageListDialog {

    private Collection<PlayerFine> crimes;

    public CrimeListDialog(LtrpPlayer player, EventManager eventManager, Collection<PlayerFine> crimes) {
        super(player, eventManager);
        this.crimes = crimes;
        Optional<PlayerFine> fine = crimes.stream().findFirst();
        if(fine.isPresent()) {
            this.setCaption(fine.get().getPlayer().getName() + " nusikaltimai. ");
        }
        else
            setCaption("Nusikaltimai");
    }

    @Override
    public void show() {
        items.clear();

        for(PlayerFine crime : crimes) {
            String crimetext = String.format("%s\t%s\t%s",
                    crime.getPlayer().getName(), crime.getDescription(), crime.getIssuedBy().getName());
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
