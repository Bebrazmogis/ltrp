package lt.ltrp.house.weed.dialog;

import lt.ltrp.house.weed.HouseWeedController;
import lt.ltrp.house.weed.constant.GrowthStage;
import lt.ltrp.house.weed.object.HouseWeedSapling;
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
                .caption("Þolës " + weed.getUUID() + " valdymas")
                .item("Sunaikinti", i -> {
                    weed.destroy();
                    HouseWeedController.instance.destroyWeed(weed);
                    parent.show();
                })
                .item("Keisti stadijà(" + weed.getGrowthStage().getPrettyName() + ")", i -> {
                    HouseWeedSaplingGrowthStageDialog.create(player, eventManager, i.getCurrentDialog(), weed).show();
                })
                .item("Nuimti derliø",
                        () -> weed.getGrowthStage().equals(GrowthStage.Grown),
                        i -> {
                            int yield = weed.getYieldAmount();
                            weed.setHarvestedBy(player);
                            weed.destroy();
                            HouseWeedController.instance.updateWeed(weed);
                            WeedItem item = WeedItem.create(eventManager);
                            item.setAmount(yield);
                            boolean success = player.getInventory().tryAdd(item);
                            if(success)
                                player.sendMessage("Nuëmete derliø, gavote " + yield + " gramus marihuanos");
                            else
                                player.sendErrorMessage("Jûsø inventoriuje neuþtenka vietos");

                        })
                .buttonOk("Pasirinkti")
                .buttonCancel("Atgal")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .build();
    }
}
