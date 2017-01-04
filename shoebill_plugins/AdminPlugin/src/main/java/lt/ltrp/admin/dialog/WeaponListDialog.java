package lt.ltrp.dialog;

import lt.ltrp.data.LtrpWeaponData;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class WeaponListDialog extends PageListDialog {

    private LtrpWeaponData[] weaponData;

    public WeaponListDialog(Player player, EventManager eventManager, LtrpWeaponData[] weaponData) {
        super(player, eventManager);
        this.weaponData = weaponData;
        setButtonOk("Gerai");
        setNextPageItemText("Toliau");
        setPrevPageItemText("Atgal");
        setCaption("Ginklai(" + weaponData.length + ")");
    }

    @Override
    public void show() {
        items.clear();

        for(LtrpWeaponData wep : weaponData) {
            items.add(
                    ListDialogItem.create()
                            .itemText(String.format("%s - %d", wep.getModel().getName(), wep.getAmmo()))
                            .data(wep)
                            .onSelect(i -> {
                                i.getCurrentDialog().show();
                            })
                            .build()
            );
        }

        super.show();
    }
}
