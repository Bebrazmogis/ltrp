package lt.ltrp.house.weed.dialog;

import lt.ltrp.house.HouseController;
import lt.ltrp.house.weed.HouseWeedController;
import lt.ltrp.house.weed.object.HouseWeedSapling;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.17.
 */
public class HouseWeedSaplingGrowthStageDialog {

    public static HouseWeedSaplingGrowthStageListDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, HouseWeedSapling weed) {
        return HouseWeedSaplingGrowthStageListDialog.create(player, eventManager)
                .caption("Namo þolës augimo stadijos pasirinkimas")
                .buttonOk("Pasirinkti")
                .buttonCancel("Atgal")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk((d, s) -> {
                    weed.setGrowthStage(s);
                    HouseWeedController.instance.updateWeed(weed);
                    parent.show();
                })
                .build();
    }

}
