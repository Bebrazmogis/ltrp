package lt.ltrp.dialog;

import lt.ltrp.constant.Currency;
import lt.ltrp.data.WeaponShopWeapon;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.TabListDialog;
import net.gtaun.shoebill.common.dialog.TabListDialogItem;
import net.gtaun.util.event.EventManager;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class WeaponShopWeaponListDialog extends TabListDialog {

    public static AbstractWeaponShopWeaponListDialogBuilder<?,?> create(LtrpPlayer player, EventManager eventManager, Collection<WeaponShopWeapon> weapons) {
        return new WeaponShopWeaponListDialogBuilder(new WeaponShopWeaponListDialog(player, eventManager, weapons));
    }

    private Collection<WeaponShopWeapon> weapons;
    private SelectWeaponHandler clickOkHandler;

    protected WeaponShopWeaponListDialog(LtrpPlayer player, EventManager eventManager, Collection<WeaponShopWeapon> weapons) {
        super(player, eventManager);
        this.weapons = weapons;
        this.setCaption("Parduotuvës ginklai");
        this.setButtonOk("Pasirinkti");
    }

    public void setClickOkHandler(SelectWeaponHandler clickOkHandler) {
        this.clickOkHandler = clickOkHandler;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    @Override
    public void show() {
        items.clear();
        setHeader(0, "Pavadinimas");
        setHeader(1, "Kaina");
        setHeader(1, "Kulkos");
        weapons.forEach(w -> {
            String name = w.getName() == null ? w.getWeaponModel().getName() : w.getName();
            items.add(TabListDialogItem.create()
                    .column(0, ListDialogItem.create().itemText(name).data(w).build())
                    .column(1, ListDialogItem.create().itemText("" + w.getPrice() + Currency.SYMBOL).build())
                    .column(2, ListDialogItem.create().itemText("" + w.getAmmo()).build())
                    .data(w)
                    .build());
        });
        super.show();
    }

    @Override
    protected void onClickOk(ListDialogItem item) {
        if(clickOkHandler != null)
            clickOkHandler.onSelect(this, (WeaponShopWeapon)item.getData());
        super.onClickOk(item);
    }

    @FunctionalInterface
    public interface SelectWeaponHandler {
        void onSelect(WeaponShopWeaponListDialog dialog, WeaponShopWeapon weapon);
    }

    @SuppressWarnings("unchecked")
    public static abstract class AbstractWeaponShopWeaponListDialogBuilder<DialogType extends WeaponShopWeaponListDialog,
            DialogBuilderType extends AbstractWeaponShopWeaponListDialogBuilder<DialogType, DialogBuilderType>>
            extends AbstractTabListDialogBuilder<DialogType, DialogBuilderType> {

        protected AbstractWeaponShopWeaponListDialogBuilder(DialogType dialog) {
            super(dialog);
        }

        public DialogBuilderType onSelect(SelectWeaponHandler clickOkHandler) {
            dialog.setClickOkHandler(clickOkHandler);
            return (DialogBuilderType) this;
        }
    }

    public static class WeaponShopWeaponListDialogBuilder extends AbstractWeaponShopWeaponListDialogBuilder<WeaponShopWeaponListDialog, WeaponShopWeaponListDialogBuilder> {

        protected WeaponShopWeaponListDialogBuilder(WeaponShopWeaponListDialog dialog) {
            super(dialog);
        }
    }
}
