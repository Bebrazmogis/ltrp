package lt.ltrp.dialog;

import lt.ltrp.house.event.HouseEditEvent;
import lt.ltrp.house.object.House;
import lt.ltrp.house.upgrade.HouseUpgradeController;
import lt.ltrp.house.upgrade.constant.HouseUpgradeType;
import lt.ltrp.house.upgrade.dialog.HouseUpgradeListDialog;
import lt.ltrp.house.weed.dialog.HouseWeedListDialog;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.Arrays;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class AdminHouseManagementOptionListDialog {

    public static ListDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, House house) {
        return ListDialog.create(player, eventManager)
                .parentDialog(parent)
                .buttonOk("Pasirinkti")
                .buttonCancel("Atgal")
                .item("Namo informacija", i -> {
                    HouseInfoMessageDialog.create(player, eventManager, i.getCurrentDialog(), house).show();
                })
                .item("{FF6E62}Pa�alinti nam�", i -> {
                    HouseDestroyMsgBoxDialog.create(player, eventManager, i.getCurrentDialog(), house).show();
                })
                .item("Perkelti namo ��jima � mano pozicij�", i -> {
                    house.setEntrance(player.getLocation());
                    eventManager.dispatchEvent(new HouseEditEvent(house, player));
                })
                .item("Perkelti namo i��jim� � mano pozicij�", i -> {
                    Location loc = player.getLocation();
                    loc.setWorldId(house.getUUID());
                    house.setExit(loc);
                    eventManager.dispatchEvent(new HouseEditEvent(house, player));
                })
                .item("Pa�alinti savinink�", house::isOwned, i -> {
                    HouseRemoveOwnerMsgBoxDialog.create(player, eventManager, i.getCurrentDialog(), house).show();
                })
                .item("Keisti pavadinim�", i -> {
                    HouseNameInputDialog.create(player, eventManager, house).show();
                })
                .item("Keisti kain�", i -> {
                    HousePriceInputDialog.create(player, eventManager, i.getCurrentDialog(), house).show();
                })
                .item("Keisti pickup model�", i -> {
                    SampModelInputDialog.create(player, eventManager)
                            .caption("Namo ��jimo pickup modelio keitimas")
                            .parentDialog(i.getCurrentDialog())
                            .message("�veskite naujo modelio ID.")
                            .line("Dabartinis modelis:" + house.getPickupModelId())
                            .buttonCancel("Atgal")
                            .buttonOk("Keisti")
                            .onClickOk((d, val) -> {
                                house.setPickupModelId(val);
                                eventManager.dispatchEvent(new HouseEditEvent(house, player));
                                i.getCurrentDialog().show();
                            })
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .build()
                            .show();
                })
                .item(
                        () -> String.format("Auginamos �ol�s informacija(%d)", house.getWeedSaplings().size()),
                        () -> house.getWeedSaplings().size() > 0,
                        i -> HouseWeedListDialog.create(player, eventManager, i.getCurrentDialog(), house).show()
                )
                .item(() -> "Pa�alinti atnaujinim�(" + house.getUpgrades().size() + ")",
                        () -> house.getUpgrades().size() > 0,
                        i -> {
                        HouseUpgradeListDialog.create(player, eventManager, house.getUpgrades())
                                    .caption("Pasirinkite atnaujinim� kur� norite pa�alinti")
                                    .buttonOk("�alinti")
                                    .buttonCancel("Atgal")
                                    .parentDialog(i.getCurrentDialog())
                                    .onClickCancel(AbstractDialog::showParentDialog)
                                    .onClickOk((d, u) -> {
                                        HouseUpgradeController.instance.remove(house, u);
                                        i.getCurrentDialog().show();
                                    })
                                    .build()
                                    .show();
                })
                .item("Prid�ti atnaujinim�(" + house.getUpgrades().size() + ")",
                        () -> house.getUpgrades().size() != HouseUpgradeType.values().length,
                        i -> {
                            HouseUpgradeListDialog.create(player, eventManager, Arrays.asList(HouseUpgradeType.values()))
                                    .caption("Pasirinkite atnaujinim� kur� norite prid�ti")
                                    .buttonOk("Prid�ti")
                                    .buttonCancel("Atgal")
                                    .parentDialog(i.getCurrentDialog())
                                    .onClickCancel(AbstractDialog::showParentDialog)
                                    .onClickOk((d, u) -> {
                                        HouseUpgradeController.instance.insert(house, u);
                                        i.getCurrentDialog().show();
                                    })
                                    .build()
                                    .show();
                })
                .item("Keisti nuomos kain�", i -> {
                    HouseRentInputDialog.create(player, eventManager, i.getCurrentDialog(), house).show();
                })
                .build();
    }


}
