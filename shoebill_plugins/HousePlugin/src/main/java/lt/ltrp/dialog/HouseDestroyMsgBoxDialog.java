package lt.ltrp.dialog;

import lt.ltrp.house.HouseController;
import lt.ltrp.house.object.House;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class HouseDestroyMsgBoxDialog {

    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, House house) {
        return MsgboxDialog.create(player, eventManager)
                .caption("{FF1100}Namo naikinimas")
                .line("Dëmesio! Ðio veiksmo atstatyti neámanoma.")
                .line("\nNamas bus paðalintas negráþtamai, o jame esantys pinigai (" + house.getMoney() + ") paðalinti.")
                .line("\nAr tikrai norite paðalinti namà \"" + house.getName() + "\"?")
                .buttonOk("Taip")
                .buttonCancel("Ne")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk(d -> {
                    HouseController.get().getHouseDao().remove(house);
                    house.destroy();
                })
                .build();
    }

}
