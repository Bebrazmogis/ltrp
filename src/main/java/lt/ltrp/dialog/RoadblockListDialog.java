package lt.ltrp.dialog;


import lt.ltrp.job.policeman.Roadblock;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.util.AdminLog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.30.
 */
public class RoadblockListDialog extends PageListDialog {

    public RoadblockListDialog(LtrpPlayer player, EventManager eventManager) {
        super(player, eventManager);
    }

    @Override
    public void show(int page) {
        super.show(page);
    }

    @Override
    public void show() {
        if(getCurrentPage() == 0) {
            items.add(new ListDialogItem("{FF0000}Pa�alinti visas", handler -> {
                Roadblock.get().forEach(rd -> rd.destroy());
                LtrpPlayer.sendGlobalMessage("***" + getPlayer() + " pa�alino visus egzistuojan�ius u�tvarus.");
                if(getPlayer().isAdmin() || getPlayer().getAdminLevel() > 1) {
                    AdminLog.log(getPlayer(), "Admin " + getPlayer().getName() + " destroyed all roadblocks");
                }
            }));
        }

        int count = 0;
        for(final Roadblock roadblock : Roadblock.get()) {
            String text = String.format("U�tvara %d\tAtstumas %.1f", count++, roadblock.getLocation().distance(getPlayer().getLocation()));
            items.add(new ListDialogItem(text, dialog -> RoadblockDialog.create(getPlayer(), parentEventManager, roadblock)));
        }
        super.show();
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

}
