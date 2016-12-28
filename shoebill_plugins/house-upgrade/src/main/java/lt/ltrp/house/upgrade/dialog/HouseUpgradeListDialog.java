package lt.ltrp.house.upgrade.dialog;

import lt.ltrp.house.upgrade.constant.HouseUpgradeType;
import lt.ltrp.house.upgrade.data.HouseUpgrade;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.entities.Player;
import net.gtaun.util.event.EventManager;

import java.lang.FunctionalInterface;import java.lang.Override;import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2016.05.17.
 */
public class HouseUpgradeListDialog extends ListDialog {

    public static AbstractHouseUpgradeDialogBuilder<?, ?> create(LtrpPlayer player, EventManager eventManager, Set<HouseUpgrade> upgrades) {
        return create(player, eventManager, upgrades.stream().map(HouseUpgrade::getType).collect(Collectors.toList()));
    }

    public static AbstractHouseUpgradeDialogBuilder<?, ?> create(LtrpPlayer player, EventManager eventManager, Collection<HouseUpgradeType> upgrades) {
        return new HouseUpgradeListDialogBuilder(player, eventManager, upgrades);
    }

    private Collection<HouseUpgradeType> upgrades;
    private ClickOkHandler handler;

    public HouseUpgradeListDialog(Player player, EventManager eventManager, Collection<HouseUpgradeType> upgrades) {
        super(player, eventManager);
        this.upgrades = upgrades;
        this.setCaption("Namo atnaujinimai");
    }


    public void setClickOkHandler(ClickOkHandler handler) {
        this.handler = handler;
    }

    @Override
    public void show() {
        items.clear();
        upgrades.forEach(u -> {
            items.add(ListDialogItem.create().data(u).itemText(u.getName()).build());
        });
        super.show();
    }

    @Override
    protected void onClickOk(ListDialogItem item) {
        if(handler != null) handler.onClickOk(this, (HouseUpgradeType)item.getData());
        else super.onClickOk(item);;
    }

    @FunctionalInterface
    public interface ClickOkHandler {
        void onClickOk(HouseUpgradeListDialog dialog, HouseUpgradeType upgrade);
    }

    public static abstract class AbstractHouseUpgradeDialogBuilder
            <DialogType extends HouseUpgradeListDialog, DialogBuilderType extends AbstractHouseUpgradeDialogBuilder<DialogType, DialogBuilderType>>
            extends AbstractDialogBuilder<DialogType, DialogBuilderType> {
        protected AbstractHouseUpgradeDialogBuilder(DialogType dialog) {
            super(dialog);
        }

        public DialogBuilderType onClickOk(ClickOkHandler handler) {
            dialog.setClickOkHandler(handler);
            return (DialogBuilderType) this;
        }
    }

    public static class HouseUpgradeListDialogBuilder extends AbstractHouseUpgradeDialogBuilder<HouseUpgradeListDialog, HouseUpgradeListDialogBuilder> {
        private HouseUpgradeListDialogBuilder(LtrpPlayer player, EventManager parentEventManager, Collection<HouseUpgradeType> upgrades) {
            super(new HouseUpgradeListDialog(player, parentEventManager, upgrades));
        }
    }
}
