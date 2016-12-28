package lt.ltrp.house.dialog;


import lt.ltrp.LtrpGamemodeConstants;
import lt.ltrp.house.event.HouseCreateEvent;
import lt.ltrp.house.object.House;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;import java.lang.Integer;import java.lang.String;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class AdminHouseManagementListDialog {
    private static final float MAX_DISTANCE = 8f;

    public static ListDialog create(LtrpPlayer player, EventManager eventManager) {
        return ListDialog.create(player, eventManager)
                .caption(LtrpGamemodeConstants.Name + " serverio namø valdymas")
                .item(() -> {
                            House h = House.get(player);
                            return "{623E43}Naudoti dabartiná namà(ID: " + h.getUUID() + ")";
                        },
                        () -> House.get(player) != null,
                        i -> {
                            House house = House.get(player);
                            if (house != null)
                                showHouseOptions(player, eventManager, i.getCurrentDialog(), House.get(player));
                            else
                                i.getCurrentDialog().show();
                        })
                .item(() -> {
                    House house = House.getClosest(player.getLocation(), MAX_DISTANCE);
                    return String.format("Naudoti artimiausià namà(ID: %d, atstumas: %.2f)", house.getUUID(), house.getEntrance().distance(player.getLocation()));
                }, () -> {
                    return House.get(player) == null && House.getClosest(player.getLocation(), MAX_DISTANCE) != null;
                }, i -> {
                    House house = House.getClosest(player.getLocation(), MAX_DISTANCE);
                    if(house != null)
                        showHouseOptions(player, eventManager, i.getCurrentDialog(), house);
                    else
                        i.getCurrentDialog().show();
                })
                .item("Ávesti namo ID", i -> {
                    showUUIDDialog(player, eventManager, i.getCurrentDialog(), (d, house) -> {
                        showHouseOptions(player, eventManager, i.getCurrentDialog(), house);
                    });
                })
                .item("{FF6E62}Kurti naujà namà", i -> {
                    House h = House.create(0, player.getLocation(), null, 0);
                    eventManager.dispatchEvent(new HouseCreateEvent(h, player));
                    showHouseOptions(player, eventManager, i.getCurrentDialog(), h);
                })
                .item("Statistika", i -> {
                    showStats(player, eventManager, i.getCurrentDialog());
                })
                .build();
    }


    private static void showStats(LtrpPlayer player, EventManager eventManager, AbstractDialog parent) {
        MsgboxDialog.create(player, eventManager)
                .caption("Serverio namø statistika")
                .line("Namø kiekis: " + House.get().size())
                .line("Parduodamø namø kiekis: " + House.get().stream().filter(h -> !h.isOwned()).count())
                .line("Brangiausias namas:" + House.get().stream().map(House::getPrice).max(Integer::compare))
                .build()
                .show();
    }


    private static void showHouseOptions(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, House house) {
        AdminHouseManagementOptionListDialog.create(player, eventManager, parent, house).show();
    }

    private static void showUUIDDialog(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, HouseUUIDInputDialog.ClickOkHandler clickOkHandler) {
        HouseUUIDInputDialog.create(player, eventManager)
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


}
