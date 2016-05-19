package lt.ltrp.dialog;

import lt.ltrp.data.property.business.commodity.BusinessCommodity;
import lt.ltrp.event.property.BusinessCommodityAddEvent;
import lt.ltrp.event.property.BusinessCommodityPriceUpdateEvent;
import lt.ltrp.event.property.BusinessCommodityRemoveEvent;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.TabListDialog;
import net.gtaun.util.event.EventManager;

import java.util.List;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessCommodityManagementDialog {


    public static ListDialog create(LtrpPlayer player, EventManager eventManager, Business business) {
        return ListDialog.create(player, eventManager)
                .item("Prid�ti nauj� prek� � pardavim�", () -> business.getCommodityCount() != business.getCommodityLimit(), i -> {
                    List<BusinessCommodity> list = Business.getAvailableCommodities(business.getBusinessType());
                    /*Business.getAvailableCommodities(business.getBusinessType())
                    for (BusinessCommodity c : Business.getAvailableCommodities(business.getBusinessType()))
                        list.add(c);*/
                    TabListDialog dialog = BusinessCommodityListDialog.create(player, eventManager, list, (d, p, c) -> {
                        if(business.getCommodities().contains(c)) {
                            player.sendErrorMessage("Verslas jau pardavin�ja �i� prek�.");
                            d.show();
                        } else {
                            BusinessCommodity bc = c.clone();
                            bc.setBusiness(business);
                            bc.setNumber(business.getCommodityCount()); // This should give it the next number(index)
                            business.addCommodity(bc);
                            eventManager.dispatchEvent(new BusinessCommodityAddEvent(business, p, bc));
                            i.getCurrentDialog().show();
                        }
                    });
                    dialog.setCaption("Pasirinkite prek�, kuri� norite pa�alinti");
                    dialog.show();
                })
                .item("Pa�alinti esam� prek� i� pardavimo", () -> business.getCommodityCount() > 0, i -> {
                    TabListDialog dialog = BusinessCommodityListDialog.create(player, eventManager, business.getCommodities(), (d, p, c) -> {
                        business.removeCommodity(c);
                        eventManager.dispatchEvent(new BusinessCommodityRemoveEvent(business, p, c));
                        i.getCurrentDialog().show();
                    });
                    dialog.setCaption("Pasirinkite prek�, kuri� norite pa�alinti");
                    dialog.show();
                })
                .item("Keisti prek�s kain�", () -> business.getCommodityCount() > 0, i -> {
                    TabListDialog dialog = BusinessCommodityListDialog.create(player, eventManager, business.getCommodities(), (dd, p, c) -> {
                        IntegerInputDialog.create(player, eventManager)
                                .caption("Preki� redagavimas: kainos keitimas")
                                .message("Dabartin� prek�s kaina:" + c.getPrice() + "\n" +
                                        "�veskite nauj� prek�s kain�. Ji turi b�ti didens� u� 0.")
                                .buttonOk("Keisti")
                                .buttonCancel("Atgal")
                                .onClickCancel(AbstractDialog::showParentDialog)
                                .onClickOk((d, price) -> {
                                    if (price < 0)
                                        d.show();
                                    else {
                                        c.setPrice(price);
                                        eventManager.dispatchEvent(new BusinessCommodityPriceUpdateEvent(business, player, c));
                                        i.getCurrentDialog().show();
                                    }
                                })
                                .build()
                                .show();

                    });
                    dialog.setCaption("Pasirinkite prek�, kurios kain� norite pakeisti");
                    dialog.setClickCancelHandler(d -> i.getCurrentDialog().show());
                    dialog.show();
                })
                .build();
    }

}
