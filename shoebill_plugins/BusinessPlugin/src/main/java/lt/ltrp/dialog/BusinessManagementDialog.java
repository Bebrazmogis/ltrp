package lt.ltrp.dialog;

import lt.ltrp.data.Color;
import lt.ltrp.event.property.BusinessDoorLockToggleEvent;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.TabListDialog;
import net.gtaun.shoebill.common.dialog.TabListDialogItem;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessManagementDialog {

    public static ListDialog creatE(LtrpPlayer player, EventManager eventManager, Business business) {
        return TabListDialog.create(player, eventManager)
                .item(TabListDialogItem.create()
                        .column(0, ListDialogItem.create().itemText("- Pavadinimas").build())
                        .column(1, ListDialogItem.create().itemText("[ " + business.getName() + " ]").build())
                        .onSelect(i -> BusinessNameInputDialog.create(player, eventManager, business).show())
                        .build())
                .item(TabListDialogItem.create()
                        .column(0, new ListDialogItem("- Áëjimo kaina"))
                        .column(1, new ListDialogItem("[ " + business.getEntrancePrice() + " ]"))
                        .onSelect(i -> BusinessEntrancePriceInputDialog.create(player, eventManager, business).show())
                        .build())
                .item(TabListDialogItem.create()
                        .column(0, new ListDialogItem("Banke"))
                        .column(1, new ListDialogItem("[ " + business.getMoney() + " ]"))
                        .onSelect(i -> BusinessBankListDialog.create(player, eventManager, business).show())
                        .build())
                .item(TabListDialogItem.create()
                        .column(0, new ListDialogItem("Durys"))
                        .column(1, new ListDialogItem("[ " + (business.isLocked() ? "uþrakintos" : "atrakintos") + " ]"))
                        .onSelect(i -> {
                            business.setLocked(!business.isLocked());
                            eventManager.dispatchEvent(new BusinessDoorLockToggleEvent(business, player, business.isLocked()));
                        })
                        .build())
                .item(TabListDialogItem.create()
                        .column(0, new ListDialogItem("Tipas"))
                        .column(1, new ListDialogItem("[ " + business.getBusinessType().getName() + " ]"))
                        .onSelect(i -> {
                            i.getCurrentDialog().show();
                            // TODO if admin, might allow change
                        })
                        .build())
                .item(TabListDialogItem.create()
                        .column(0, new ListDialogItem("Prekiø kiekis"))
                        .column(1, new ListDialogItem("[ " + business.getResources() + " ]"))
                        .onSelect(i -> i.getCurrentDialog().show())
                        .build())
                .item(TabListDialogItem.create()
                        .column(0, new ListDialogItem("Prekiø valdymas"))
                        .column(1, new ListDialogItem("[ " + business.getCommodityCount() + " ]"))
                        .onSelect(i -> BusinessCommodityManagementDialog.create(player, eventManager, business).show())
                        .build())
                .item(TabListDialogItem.create()
                        .column(0, new ListDialogItem("Prekiø kaina"))
                        .column(1, new ListDialogItem("[ " + (business.getResourcePrice() != 0 ? business.getResourcePrice() : "neperkamos") + " ]"))
                        .onSelect(i -> player.sendMessage(Color.BUSINESS, "Naudokite /cargoprice"))
                        .build())
            .build();
    }



}
