package lt.ltrp.dialog.property;

import lt.ltrp.PropertyController;
import lt.ltrp.constant.ItemType;
import lt.ltrp.data.BusinessCommodity;
import lt.ltrp.data.Color;
import lt.ltrp.data.ShopBusinessCommodity;
import lt.ltrp.dialog.item.ItemTypeListDialog;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class BusinessAvailableCommodityManagementDialog {

    public static ListDialog create(LtrpPlayer player, EventManager eventManager) {
        return BusinessTypeListDialog.create(player, eventManager, (d, t) -> {
            List<BusinessCommodity> commodityList = Business.getAvailableCommodities(t);
            ListDialog.create(player, eventManager)
                    .caption("Visø verslø prekiø valdymas")
                    .buttonOk("Pasirinkti")
                    .buttonCancel("Atgal")
                    .onClickCancel(AbstractDialog::showParentDialog)
                    .item("Paðalinti prekæ ið pardavimo", () -> commodityList.size() > 0, i -> {
                        // We let the user to select a commodity from available ones
                        AbstractDialog dialog = BusinessCommodityListDialog.create(player, eventManager, commodityList, (dd, c) -> {
                            // Remove the commodity from available list
                            PropertyController.get().getBusinessDao().remove(t, c);
                            // Remove that commodity from businesses that sell it
                            Business.get().stream().filter(b -> b.getCommodities().contains(c)).forEach(b -> {
                                Optional<BusinessCommodity> commodity = b.getCommodities().stream().filter(cc -> c.getUUID() == c.getUUID()).findFirst();
                                if (commodity.isPresent())
                                    PropertyController.get().getBusinessDao().remove(commodity.get());
                            });
                            player.sendMessage(Color.BUSINESS, "Prekë " + c.getName() + " sëkmingai paðalinta.");
                        });
                        dialog.setCaption("Pasirinkite prekæ kurià norite paðalinti");
                        dialog.show();
                    })
                    .item("Sukurti naujà parduodamà prekæ", i -> {
                        ListDialog.create(player, eventManager)
                                .caption("Naujos prekës kûrimas")
                                .buttonOk("Tæsti")
                                .buttonCancel("Atgal")
                                .parentDialog(i.getCurrentDialog())
                                .onClickCancel(AbstractDialog::showParentDialog)
                                .item("Prekë - daiktas", ii -> {
                                    ArrayList<ItemType> types = new ArrayList<ItemType>();
                                    for (ItemType type : ItemType.values())
                                        types.add(type);
                                    ItemTypeListDialog.create(player, eventManager, types, (ddd, type) -> {
                                        InputDialog.create(player, eventManager)
                                                .caption("Prekës kûrimas: pavadinimas")
                                                .buttonOk("Kurti")
                                                .buttonCancel("Atgal")
                                                .message("Áveskite daikto pavadinimà.\nÁvestas pavadinimas bus rodomas versluose")
                                                .onClickOk((dddd, name) -> {
                                                    if (name.isEmpty())
                                                        dddd.show();
                                                    else {
                                                        ShopBusinessCommodity commodity = new ShopBusinessCommodity(0, name, type);
                                                        PropertyController.get().getBusinessDao().insert(t, commodity);
                                                        player.sendMessage(Color.BUSINESS, "Prekë " + commodity.getName() + " pridëta");
                                                    }
                                                })
                                                .onClickCancel(AbstractDialog::showParentDialog)
                                                .build()
                                                .show();
                                    }).show();
                                })
                                .item("Prekë be funkcionalumo", ii -> {
                                    InputDialog.create(player, eventManager)
                                            .caption("Prekës kûrimas: pavadinimas")
                                            .buttonOk("Kurti")
                                            .buttonCancel("Atgal")
                                            .message("Áveskite daikto pavadinimà.\nÁvestas pavadinimas bus rodomas versluose")
                                            .onClickOk((dddd, name) -> {
                                                if (name.isEmpty())
                                                    dddd.show();
                                                else {
                                                    PropertyController.get().getBusinessDao().insert(new BusinessCommodity(0, name));
                                                    player.sendMessage(Color.BUSINESS, "Prekë " + name + " sukurta");
                                                }
                                            })
                                            .onClickCancel(AbstractDialog::showParentDialog)
                                            .build()
                                            .show();
                                })
                                .item("Psgalba", ii -> {
                                    MsgboxDialog.create(player, eventManager)
                                            .caption("Daiktø kûrimo pagalba")
                                            .buttonOk("Gerai")
                                            .line("Yra keli prekiø tipai:")
                                            .line("\t• Prekë - Daiktas - tai prekë, kurià þaidëjui nusipirkus ji atsiras jo inventoriuje.")
                                            .line("\tÐios prekës kûrimui privaloma pasirinkti daiktà kuris bus gaunamas jà nusipirkus")
                                            .line("\n")
                                            .line("\t• Prekë be funkcionalumo - tai prekë kuri neturi jokio poveiki þaidëjui ,iðskyrus tai kad bus nuimami pinigai")
                                            .line("\t• Visa prekës prasmë, kurti RP galimybes")
                                            .parentDialog(ii.getCurrentDialog())
                                            .onClickOk(AbstractDialog::showParentDialog)
                                            .build()
                                            .show();
                                })
                                .build()
                                .show();

                    })
                    .item("Keisti esamos prekæs pavadinimà", () -> commodityList.size() > 0, i -> {
                        AbstractDialog dialog = BusinessCommodityListDialog.create(player, eventManager, commodityList, (dd, c) -> {
                            InputDialog.create(player, eventManager)
                                    .caption("Prekës " + c.getName() + " pavadinimo keitimas")
                                    .message("Dabartinis prekës pavadinimas yra \"" + c.getName() + "\"\n" +
                                            "Áveskite naujà prekës pavadinimà")
                                    .buttonOk("Iðsaugoti")
                                    .buttonCancel("Atgal")
                                    .onClickCancel(AbstractDialog::showParentDialog)
                                    .onClickOk((ddd, name) -> {
                                        if (name.isEmpty())
                                            ddd.show();
                                        else {
                                            c.setName(name);
                                            PropertyController.get().getBusinessDao().update(c);
                                            Business.get().forEach(b -> {
                                                Optional<BusinessCommodity> commodity = b.getCommodities().stream().filter(com -> com.getUUID() == c.getUUID()).findFirst();
                                                if (commodity.isPresent()) {
                                                    commodity.get().setName(name);
                                                    PropertyController.get().getBusinessDao().update(commodity.get());
                                                }
                                            });
                                        }
                                    })
                                    .build()
                                    .show();
                        });
                        dialog.setCaption("Pasirinkite prekæ, kurios pavadinimà norite keisti");
                        dialog.show();
                    })
                    .build()
                    .show();
        });
    }

}
