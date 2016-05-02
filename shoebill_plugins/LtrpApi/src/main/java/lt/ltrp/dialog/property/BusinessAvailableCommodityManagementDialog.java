package lt.ltrp.dialog.property;

import lt.ltrp.PropertyController;
import lt.ltrp.constant.ItemType;
import lt.ltrp.data.property.business.commodity.BusinessCommodity;
import lt.ltrp.data.Color;
import lt.ltrp.data.property.business.commodity.BusinessCommodityDrink;
import lt.ltrp.data.property.business.commodity.BusinessCommodityFood;
import lt.ltrp.data.property.business.commodity.BusinessCommodityItem;
import lt.ltrp.dialog.IntegerInputDialog;
import lt.ltrp.dialog.item.ItemTypeListDialog;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.constant.SpecialAction;
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
                        AbstractDialog dialog = BusinessCommodityListDialog.create(player, eventManager, commodityList, (dd, p, c) -> {
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
                                                        BusinessCommodityItem commodity = new BusinessCommodityItem(0, name, type, eventManager);
                                                        PropertyController.get().getBusinessDao().insert(t, commodity);
                                                        player.sendMessage(Color.BUSINESS, "Prekë " + commodity.getName() + " pridëta");
                                                    }
                                                })
                                                .onClickCancel(AbstractDialog::showParentDialog)
                                                .build()
                                                .show();
                                    }).show();
                                })
                                .item("Prekë - Gërimas", ii -> {
                                    BusinessCommodityNameInputDialog.create(player, eventManager, ii.getCurrentDialog(), (nameDialog, name) -> {
                                        ListDialog.create(player, eventManager)
                                                .caption("Pasirinkite efektà, rodomà ásigijus prekæ")
                                                .item(SpecialAction.DRINK_BEER, "Alus", null)
                                                .item(SpecialAction.DRINK_SPRUNK, "Sprunk", null)
                                                .item(SpecialAction.DRINK_WINE, "Vynas", null)
                                                .buttonOk("Pasirinkti")
                                                .buttonCancel("Atgal")
                                                .onClickCancel(dd -> ii.getCurrentDialog().show())
                                                .onClickOk((actionDialog, item) -> {
                                                    SpecialAction action = (SpecialAction) item.getData();
                                                    BusinessCommodityDrink commodityDrink = new BusinessCommodityDrink(0, name, action, 5000, eventManager);
                                                    PropertyController.get().getBusinessDao().insert(t, commodityDrink);
                                                    player.sendMessage(Color.BUSINESS, "Prekë pridëta sëkmingai");
                                                    ii.getCurrentDialog().show();
                                                })
                                                .build()
                                                .show();
                                    }).show();
                                })
                                .item("Prekë - Maistas", ii -> {
                                    BusinessCommodityNameInputDialog.create(player, eventManager, ii.getCurrentDialog(), (nameDialog, name) -> {
                                        IntegerInputDialog.create(player, eventManager)
                                                .caption(nameDialog.getCaption() + " 2")
                                                .message("Áveskite skaièiø, kiek þaidëjui bus pridëta gyvybiø suvalgius ðià prekæ.")
                                                .line("Skaièius neturëtø bûti didesnis uþ 100 ar maþesnis uþ 1.")
                                                .buttonOk("Kurit")
                                                .buttonCancel("Atgal")
                                                .onClickCancel(dd -> ii.getCurrentDialog().show())
                                                .onClickOk((hpDialog, hp) -> {
                                                    BusinessCommodityFood commodity = new BusinessCommodityFood(0, name, hp);
                                                    PropertyController.get().getBusinessDao().insert(t, commodity);
                                                    player.sendMessage(Color.BUSINESS, "Prekë " + commodity.getName() + " pridëta");
                                                    ii.getCurrentDialog().show();
                                                })
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
                                                    PropertyController.get().getBusinessDao().insert(t, new BusinessCommodity(0, name));
                                                    player.sendMessage(Color.BUSINESS, "Prekë " + name + " sukurta");
                                                }
                                            })
                                            .onClickCancel(AbstractDialog::showParentDialog)
                                            .build()
                                            .show();
                                })
                                .item("Pagalba", ii -> {
                                    MsgboxDialog.create(player, eventManager)
                                            .caption("Daiktø kûrimo pagalba")
                                            .buttonOk("Gerai")
                                            .line("Yra keli prekiø tipai:")
                                            .line("\t• Prekë - Daiktas - tai prekë, kurià þaidëjui nusipirkus ji atsiras jo inventoriuje.")
                                            .line("\tÐios prekës kûrimui privaloma pasirinkti daiktà kuris bus gaunamas jà nusipirkus")
                                            .line("\n")
                                            .line("\t• Prekë be funkcionalumo - tai prekë kuri neturi jokio poveiki þaidëjui ,iðskyrus tai kad bus nuimami pinigai")
                                            .line("\t• Visa prekës prasmë, kurti RP galimybes")
                                            .line("\n")
                                            .line("\t•Prekë - Gërimas - tai prekë kurià þaidëjas nusipirkæs rankoje turës gërimà, o paspaudæs pelës klaviðà iðgers")
                                            .line("\n")
                                            .line("\t•Prekë - Maistas - tai prekë kuri gydo þaidëjà, jà suvalgius þaidëjas atgaus pasirinktà kieká gyvybiø")
                                            .parentDialog(ii.getCurrentDialog())
                                            .onClickOk(AbstractDialog::showParentDialog)
                                            .build()
                                            .show();
                                })
                                .build()
                                .show();

                    })
                    .item("Keisti esamos prekës pavadinimà", () -> commodityList.size() > 0, i -> {
                        AbstractDialog dialog = BusinessCommodityListDialog.create(player, eventManager, commodityList, (dd, p, c) -> {
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
