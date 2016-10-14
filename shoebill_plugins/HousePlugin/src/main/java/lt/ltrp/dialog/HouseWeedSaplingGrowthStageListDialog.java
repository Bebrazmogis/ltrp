package lt.ltrp.dialog;

import lt.ltrp.house.weed.data.HouseWeedSapling;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.util.event.EventManager;import java.lang.FunctionalInterface;import java.lang.Override;

/**
 * @author Bebras
 *         2016.05.17.
 */
public class HouseWeedSaplingGrowthStageListDialog extends ListDialog {

    private ClickOkHandler clickOkHandler;

    public HouseWeedSaplingGrowthStageListDialog(LtrpPlayer player, EventManager eventManager) {
        super(player, eventManager);
    }

    public static AbstractHouseWeedSaplingGrowthStageDialogBuilder<?, ?> create(LtrpPlayer p, EventManager eventManager) {
        return new HouseWeedSaplingGrowthStageDialogBuilder(p, eventManager);
    }

    public void setClickOkHandler(ClickOkHandler clickOkHandler) {
        this.clickOkHandler = clickOkHandler;
    }

    @Override
    public void show() {
        items.clear();

        for(HouseWeedSapling.GrowthStage stage : HouseWeedSapling.GrowthStage.values()) {
            items.add(ListDialogItem.create().itemText(stage.getName()).data(stage).build());
        }
        super.show();
    }

    @Override
    protected void onClickOk(ListDialogItem item) {
        if(clickOkHandler != null)
            clickOkHandler.onSelectGrowthStage(this, (HouseWeedSapling.GrowthStage)item.getData());
        else
            super.onClickOk(item);
    }

    public static abstract class AbstractHouseWeedSaplingGrowthStageDialogBuilder
            <DialogType extends HouseWeedSaplingGrowthStageListDialog, DialogBuilderType extends AbstractHouseWeedSaplingGrowthStageDialogBuilder<DialogType, DialogBuilderType>>
            extends AbstractDialogBuilder<DialogType, DialogBuilderType> {
        protected AbstractHouseWeedSaplingGrowthStageDialogBuilder(DialogType dialog) {
            super(dialog);
        }

        public DialogBuilderType onClickOk(ClickOkHandler handler) {
            dialog.setClickOkHandler(handler);
            return (DialogBuilderType) this;
        }
    }

    public static class HouseWeedSaplingGrowthStageDialogBuilder extends AbstractHouseWeedSaplingGrowthStageDialogBuilder<HouseWeedSaplingGrowthStageListDialog, HouseWeedSaplingGrowthStageDialogBuilder> {
        private HouseWeedSaplingGrowthStageDialogBuilder(LtrpPlayer player, EventManager parentEventManager) {
            super(new HouseWeedSaplingGrowthStageListDialog(player, parentEventManager));
        }
    }

    @FunctionalInterface
    public interface ClickOkHandler {
        void onSelectGrowthStage(HouseWeedSaplingGrowthStageListDialog dialog, HouseWeedSapling.GrowthStage stage);
    }
}
