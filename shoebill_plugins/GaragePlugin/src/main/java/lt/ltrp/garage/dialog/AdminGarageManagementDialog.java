package lt.ltrp.garage.dialog;

import lt.ltrp.LtrpGamemodeConstants;
import lt.ltrp.event.property.garage.GarageCreateEvent;
import lt.ltrp.object.Garage;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.19.
 */
public class AdminGarageManagementDialog {
    private static final float MAX_DISTANCE = 8f;

    public static ListDialog create(LtrpPlayer player, EventManager eventManager) {
        return ListDialog.create(player, eventManager)
                .caption(LtrpGamemodeConstants.Name + " serverio garaþø valdymas")
                .item(() -> {
                            Garage g = Garage.get(player);
                            return "{623E43}Naudoti dabartiná garaþà(ID: " + g.getUUID() + ")";
                        },
                        () -> Garage.get(player) != null,
                        i -> {
                            Garage garage = Garage.get(player);
                            if (garage != null)
                                GarageManagementOptionDialog.create(player, eventManager, i.getCurrentDialog(), Garage.get(player)).show();
                            else
                                i.getCurrentDialog().show();
                        })
                .item(() -> {
                    Garage garage = Garage.getClosest(player.getLocation(), MAX_DISTANCE);
                    return String.format("Naudoti artimiausià garaþà(ID: %d, atstumas: %.2f)", garage.getUUID(), garage.getEntrance().distance(player.getLocation()));
                }, () -> {
                    return Garage.get(player) == null && Garage.getClosest(player.getLocation(), MAX_DISTANCE) != null;
                }, i -> {
                    Garage garage = Garage.getClosest(player.getLocation(), MAX_DISTANCE);
                    if(garage != null)
                        GarageManagementOptionDialog.create(player, eventManager, i.getCurrentDialog(), garage).show();
                    else
                        i.getCurrentDialog().show();
                })
                .item("Ávesti garaþo ID", i -> {
                    showUUIDDialog(player, eventManager, i.getCurrentDialog(), (d, garage) -> {
                        player.sendDebug(garage);
                        GarageManagementOptionDialog.create(player, eventManager, i.getCurrentDialog(), garage).show();
                    });
                })
                .item("{FF6E62}Kurti naujà garaþà", i -> {
                    Garage garage = Garage.create(0, player.getLocation(), null, player.getLocation(), null, 0);
                    eventManager.dispatchEvent(new GarageCreateEvent(garage));
                    GarageManagementOptionDialog.create(player, eventManager, i.getCurrentDialog(), garage).show();
                })
                .item("Statistika", i -> {
                    showStats(player, eventManager, i.getCurrentDialog());
                })
                .build();
    }


    private static void showStats(LtrpPlayer player, EventManager eventManager, AbstractDialog parent) {
        MsgboxDialog.create(player, eventManager)
                .caption("Serverio garaþø statistika")
                .line("Garaþø kiekis: " + Garage.get().size())
                .line("Parduodamø garaþø kiekis: " + Garage.get().stream().filter(h -> !h.isOwned()).count())
                .line("Brangiausias garaþas:" + Garage.get().stream().map(Garage::getPrice).max(Integer::compare))
                .build()
                .show();
    }



    private static void showUUIDDialog(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, GarageUUIDInputDialog.ClickOkHandler clickOkHandler) {
        GarageUUIDInputDialog.create(player, eventManager)
                .buttonCancel("Atgal")
                .onInputError((d, s) -> {
                    player.sendErrorMessage("Tokio garaþo nëra!");
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

}
