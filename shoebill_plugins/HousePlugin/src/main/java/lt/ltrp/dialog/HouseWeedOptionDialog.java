package lt.ltrp.dialog;

import lt.ltrp.HouseController;
import lt.ltrp.data.HouseWeedSapling;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.WeedItem;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class HouseWeedOptionDialog {
    public static ListDialog create(LtrpPlayer player, EventManager eventManager, ListDialog parent, HouseWeedSapling weed) {
        return ListDialog.create(player, eventManager)
                .caption("�ol�s " + weed.getId() + " valdymas")
                .item("Sunaikinti", i -> {
                    weed.destroy();
                    HouseController.get().getHouseDao().remove(weed);
                    parent.show();
                })
                .item("Keisti stadij�(" + weed.getStage().getName() + ")", i -> {
                    HouseWeedSaplingGrowthStageDialog.create(player, eventManager, i.getCurrentDialog(), weed).show();
                })
                .item("Nuimti derli�",
                        () -> weed.getStage().equals(HouseWeedSapling.GrowthStage.Grown),
                        i -> {
                            int yield = weed.getYield();
                            weed.setHarvestedByUser(player.getUUID());
                            weed.destroy();
                            HouseController.get().getHouseDao().update(weed);
                            WeedItem item = WeedItem.create(eventManager);
                            item.setAmount(yield);
                            boolean success = player.getInventory().tryAdd(item);
                            if(success)
                                player.sendMessage("Nu�mete derli�, gavote " + yield + " gramus marihuanos");
                            else
                                player.sendErrorMessage("J�s� inventoriuje neu�tenka vietos");

                        })
                .buttonOk("Pasirinkti")
                .buttonCancel("Atgal")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .build();
    }
}
