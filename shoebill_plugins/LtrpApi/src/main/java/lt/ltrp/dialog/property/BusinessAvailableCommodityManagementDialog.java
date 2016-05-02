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
                    .caption("Vis� versl� preki� valdymas")
                    .buttonOk("Pasirinkti")
                    .buttonCancel("Atgal")
                    .onClickCancel(AbstractDialog::showParentDialog)
                    .item("Pa�alinti prek� i� pardavimo", () -> commodityList.size() > 0, i -> {
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
                            player.sendMessage(Color.BUSINESS, "Prek� " + c.getName() + " s�kmingai pa�alinta.");
                        });
                        dialog.setCaption("Pasirinkite prek� kuri� norite pa�alinti");
                        dialog.show();
                    })
                    .item("Sukurti nauj� parduodam� prek�", i -> {
                        ListDialog.create(player, eventManager)
                                .caption("Naujos prek�s k�rimas")
                                .buttonOk("T�sti")
                                .buttonCancel("Atgal")
                                .parentDialog(i.getCurrentDialog())
                                .onClickCancel(AbstractDialog::showParentDialog)
                                .item("Prek� - daiktas", ii -> {
                                    ArrayList<ItemType> types = new ArrayList<ItemType>();
                                    for (ItemType type : ItemType.values())
                                        types.add(type);
                                    ItemTypeListDialog.create(player, eventManager, types, (ddd, type) -> {
                                        InputDialog.create(player, eventManager)
                                                .caption("Prek�s k�rimas: pavadinimas")
                                                .buttonOk("Kurti")
                                                .buttonCancel("Atgal")
                                                .message("�veskite daikto pavadinim�.\n�vestas pavadinimas bus rodomas versluose")
                                                .onClickOk((dddd, name) -> {
                                                    if (name.isEmpty())
                                                        dddd.show();
                                                    else {
                                                        BusinessCommodityItem commodity = new BusinessCommodityItem(0, name, type, eventManager);
                                                        PropertyController.get().getBusinessDao().insert(t, commodity);
                                                        player.sendMessage(Color.BUSINESS, "Prek� " + commodity.getName() + " prid�ta");
                                                    }
                                                })
                                                .onClickCancel(AbstractDialog::showParentDialog)
                                                .build()
                                                .show();
                                    }).show();
                                })
                                .item("Prek� - G�rimas", ii -> {
                                    BusinessCommodityNameInputDialog.create(player, eventManager, ii.getCurrentDialog(), (nameDialog, name) -> {
                                        ListDialog.create(player, eventManager)
                                                .caption("Pasirinkite efekt�, rodom� �sigijus prek�")
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
                                                    player.sendMessage(Color.BUSINESS, "Prek� prid�ta s�kmingai");
                                                    ii.getCurrentDialog().show();
                                                })
                                                .build()
                                                .show();
                                    }).show();
                                })
                                .item("Prek� - Maistas", ii -> {
                                    BusinessCommodityNameInputDialog.create(player, eventManager, ii.getCurrentDialog(), (nameDialog, name) -> {
                                        IntegerInputDialog.create(player, eventManager)
                                                .caption(nameDialog.getCaption() + " 2")
                                                .message("�veskite skai�i�, kiek �aid�jui bus prid�ta gyvybi� suvalgius �i� prek�.")
                                                .line("Skai�ius netur�t� b�ti didesnis u� 100 ar ma�esnis u� 1.")
                                                .buttonOk("Kurit")
                                                .buttonCancel("Atgal")
                                                .onClickCancel(dd -> ii.getCurrentDialog().show())
                                                .onClickOk((hpDialog, hp) -> {
                                                    BusinessCommodityFood commodity = new BusinessCommodityFood(0, name, hp);
                                                    PropertyController.get().getBusinessDao().insert(t, commodity);
                                                    player.sendMessage(Color.BUSINESS, "Prek� " + commodity.getName() + " prid�ta");
                                                    ii.getCurrentDialog().show();
                                                })
                                                .build()
                                                .show();
                                    }).show();
                                })
                                .item("Prek� be funkcionalumo", ii -> {
                                    InputDialog.create(player, eventManager)
                                            .caption("Prek�s k�rimas: pavadinimas")
                                            .buttonOk("Kurti")
                                            .buttonCancel("Atgal")
                                            .message("�veskite daikto pavadinim�.\n�vestas pavadinimas bus rodomas versluose")
                                            .onClickOk((dddd, name) -> {
                                                if (name.isEmpty())
                                                    dddd.show();
                                                else {
                                                    PropertyController.get().getBusinessDao().insert(t, new BusinessCommodity(0, name));
                                                    player.sendMessage(Color.BUSINESS, "Prek� " + name + " sukurta");
                                                }
                                            })
                                            .onClickCancel(AbstractDialog::showParentDialog)
                                            .build()
                                            .show();
                                })
                                .item("Pagalba", ii -> {
                                    MsgboxDialog.create(player, eventManager)
                                            .caption("Daikt� k�rimo pagalba")
                                            .buttonOk("Gerai")
                                            .line("Yra keli preki� tipai:")
                                            .line("\t� Prek� - Daiktas - tai prek�, kuri� �aid�jui nusipirkus ji atsiras jo inventoriuje.")
                                            .line("\t�ios prek�s k�rimui privaloma pasirinkti daikt� kuris bus gaunamas j� nusipirkus")
                                            .line("\n")
                                            .line("\t� Prek� be funkcionalumo - tai prek� kuri neturi jokio poveiki �aid�jui ,i�skyrus tai kad bus nuimami pinigai")
                                            .line("\t� Visa prek�s prasm�, kurti RP galimybes")
                                            .line("\n")
                                            .line("\t�Prek� - G�rimas - tai prek� kuri� �aid�jas nusipirk�s rankoje tur�s g�rim�, o paspaud�s pel�s klavi�� i�gers")
                                            .line("\n")
                                            .line("\t�Prek� - Maistas - tai prek� kuri gydo �aid�j�, j� suvalgius �aid�jas atgaus pasirinkt� kiek� gyvybi�")
                                            .parentDialog(ii.getCurrentDialog())
                                            .onClickOk(AbstractDialog::showParentDialog)
                                            .build()
                                            .show();
                                })
                                .build()
                                .show();

                    })
                    .item("Keisti esamos prek�s pavadinim�", () -> commodityList.size() > 0, i -> {
                        AbstractDialog dialog = BusinessCommodityListDialog.create(player, eventManager, commodityList, (dd, p, c) -> {
                            InputDialog.create(player, eventManager)
                                    .caption("Prek�s " + c.getName() + " pavadinimo keitimas")
                                    .message("Dabartinis prek�s pavadinimas yra \"" + c.getName() + "\"\n" +
                                            "�veskite nauj� prek�s pavadinim�")
                                    .buttonOk("I�saugoti")
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
                        dialog.setCaption("Pasirinkite prek�, kurios pavadinim� norite keisti");
                        dialog.show();
                    })
                    .build()
                    .show();
        });
    }

}
