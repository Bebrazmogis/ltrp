package lt.ltrp.dialog;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.event.property.BusinessCreateEvent;
import lt.ltrp.event.property.BusinessEditEvent;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.25.
 */
public final class AdminBusinessManagementDialog {

    private static final float MAX_DISTANCE = 8f;

    public static ListDialog create(LtrpPlayer player, EventManager eventManager) {
        return ListDialog.create(player, eventManager)
                .caption(LtrpGamemode.Name + " serverio verslø valdymas")
                .item(() -> {
                    Business b = Business.getClosest(player.getLocation());
                    return String.format("{623E43}Artimiausio verslo ID %d, atstumas %.2f", b.getUUID(), b.getEntrance().distance(player.getLocation()));
                }, () -> Business.get(player) == null && Business.getClosest(player.getLocation()) != null, i -> i.getCurrentDialog().show())
                .item(
                        () -> {
                            Business b = getBusiness(player);
                            return "{623E43}Dabartinio verslo ID: " + b.getUUID();
                        },
                        () -> {
                            return Business.get(player) != null
                                    || Business.getClosest(player.getLocation(), MAX_DISTANCE) != null;
                        }, i -> i.getCurrentDialog().show())
                .item("Statistika", i -> {
                    showStats(player, eventManager);
                })
                .item("Verslo informacija", i -> {
                    Business b = getBusiness(player);
                    if(b != null)
                        BusinessInfoMsgBoxDialog.create(player, eventManager, i.getCurrentDialog(), b).show();
                    else
                        showUUIDDialog(player, eventManager, i.getCurrentDialog(), (d, biz) -> {
                            BusinessInfoMsgBoxDialog.create(player, eventManager, null, biz).show();
                        });
                })
                .item("{FF6E62}Kurti naujà verslà", i -> {
                    Business b = Business.create("nëra", player.getLocation(), null, 1);
                    eventManager.dispatchEvent(new BusinessCreateEvent(b, player));
                    BusinessNameInputDialog.create(player, eventManager, b).show();
                })
                .item("{FF6E62}Paðalinti verslà", i -> {
                    Business b = getBusiness(player);
                    if (b != null)
                        BusinessDestroyMsgBoxDialog.create(player, eventManager, i.getCurrentDialog(), b).show();
                    else
                        showUUIDDialog(player, eventManager, i.getCurrentDialog(), (d, biz) -> {
                            BusinessDestroyMsgBoxDialog.create(player, eventManager, null, biz).show();
                        });
                })
                .item("Perkelti verslo áëjima á mano pozicijà", i -> {
                    showUUIDDialog(player, eventManager, i.getCurrentDialog(), (d, biz) -> {
                        biz.setEntrance(player.getLocation());
                        eventManager.dispatchEvent(new BusinessEditEvent(biz, player));
                    });
                })
                .item("Perkelti verslo iðëjimà á mano pozicijà", i -> {
                    showUUIDDialog(player, eventManager, i.getCurrentDialog(), (d, biz) -> {
                        Location loc = player.getLocation();
                        loc.setWorldId(biz.getUUID());
                        biz.setExit(loc);
                        eventManager.dispatchEvent(new BusinessEditEvent(biz, player));
                    });
                })
                .item("Paðalinti savininkà", () -> {
                    Business b = getBusiness(player);
                    return b == null || b.getOwner() != LtrpPlayer.INVALID_USER_ID;
                }, i -> {
                    Business b = getBusiness(player);
                    if(b != null)
                        BusinessRemoveOwnerMsgBoxDialog.create(player, eventManager, i.getCurrentDialog(), b).show();
                    else
                        showUUIDDialog(player, eventManager, i.getCurrentDialog(), (d, biz) -> {
                            BusinessRemoveOwnerMsgBoxDialog.create(player, eventManager, null, biz).show();
                        });
                })
                .item("Keisti pavadinimà", i -> {
                    Business b = getBusiness(player);
                    if(b != null)
                        BusinessNameInputDialog.create(player, eventManager, b).show();
                    else
                        showUUIDDialog(player, eventManager, i.getCurrentDialog(), (d, biz) -> {
                            BusinessNameInputDialog.create(player, eventManager, biz).show();
                        });
                })
                .item("Keisti kainà", i -> {
                    Business b = getBusiness(player);
                    if (b != null)
                        BusinessPriceInputDialog.create(player, eventManager, i.getCurrentDialog(), b).show();
                    else
                        showUUIDDialog(player, eventManager, i.getCurrentDialog(), (d, biz) -> {
                            BusinessPriceInputDialog.create(player, eventManager, null, biz).show();
                        });
                })
                .item("Keisti tipà", i -> {
                    Business b = getBusiness(player);
                    if(b != null)
                        BusinessTypeListDialog.create(player, eventManager, (d, t) -> {
                            b.setBusinessType(t);
                            eventManager.dispatchEvent(new BusinessEditEvent(b, player));
                        }).show();
                    else
                        showUUIDDialog(player, eventManager, i.getCurrentDialog(), (d, biz) -> {
                            BusinessTypeListDialog.create(player, eventManager, (dd, t) -> {
                                biz.setBusinessType(t);
                                eventManager.dispatchEvent(new BusinessEditEvent(biz, player));
                            }).show();
                        });
                })
                .item("Keisti pickup modelá", i -> {
                    SampModelInputDialog.create(player, eventManager)
                            .caption("Verslo áëjimo pickup modelio keitimas")
                            .onClickOk((d, val) -> {

                            })
                            .build();
                })
                .item("Keisti esamø prekiø kieká", i -> {
                    Business b = getBusiness(player);
                    if(b != null)
                        BusinessResourceInputDialog.create(player, eventManager, i.getCurrentDialog(), b).show();
                    else
                        showUUIDDialog(player, eventManager, i.getCurrentDialog(), (d, biz) -> BusinessResourceInputDialog.create(player, eventManager, i.getCurrentDialog(), biz));
                })
                .buttonOk("Pasirinkti")
                .buttonCancel("Atðaukti")
                .build();
    }

    private static void showUUIDDialog(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, BusinessUUIDInputDialog.ClickOkHandler clickOkHandler) {
        BusinessUUIDInputDialog.create(player, eventManager)
                .buttonCancel("Atgal")
                .onInputError((d, s) -> {
                    player.sendErrorMessage("Tokio verslo nëra!");
                    d.show();
                })
                .onClickCancel(d -> {
                    if (parent != null)
                        parent.show();
                    else
                        d.show();
                })
                .onClickOk(clickOkHandler)
                .build()
                .show();
    }

    private static Business getBusiness(LtrpPlayer p) {
        if(Business.get(p) != null)
            return Business.get(p);
        else
            return Business.getClosest(p.getLocation(), MAX_DISTANCE);
    }

    private static void showStats(LtrpPlayer player,  EventManager eventManager) {
        Optional<Business> mostExpensive = Business.get().stream().max((b1, b2) -> b2.getPrice() - b1.getPrice());
        long sold = Business.get().stream().filter(b -> !b.isOwned()).count();

        if(mostExpensive.isPresent()) {
            Business expensive = mostExpensive.get();
            MsgboxDialog.create(player, eventManager)
                    .caption(LtrpGamemode.Name + " verslø informacija")
                    .line("Serveryje yra " + Business.get().size() + " verslai")
                    .line("Brangiausias verslas \"" + expensive.getName() + "\"(" + expensive.getUUID()+ ")")
                    .line("Verslø be savininko:" + sold)
                    .buttonOk("Gerai")
                    .build()
                    .show();
        }
        else {
            player.sendErrorMessage("no data");
        }
    }

}
