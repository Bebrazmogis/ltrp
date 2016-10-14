package lt.ltrp.dialog;

import lt.ltrp.house.event.HouseEditEvent;
import lt.ltrp.house.object.House;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;import java.lang.String;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class HouseNameInputDialog {


    public static InputDialog create(LtrpPlayer player, EventManager eventManager, House house) {
        return InputDialog.create(player, eventManager)
                .caption("Namo pavadinimo keitimas")
                .message("{FFFFFF}" + (house.getName() != null && !house.getName().isEmpty() ? "Dabartinis namo pavadinimas: " + house.getName() : "") + "\n" +
                        "�veskite nauj� namo pavadinim�\n" +
                        "� Pavadinimas turi sudaryti ne ma�iau kaip " + House.MIN_NAME_LENGTH + " simboli�\n" +
                        "� Pavadinimas turi b�ti kult�ringas, u� keiksma�od�ius pavadinime bus baud�iama\n" +
                        "� Galite prid�ti spalv� � tekst� naudojant {SPALVA}, pvz {RAUDONA}Tekstas atrodys {FF0000}Tekstas{FFFFFF}")
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .onClickCancel(AbstractDialog::show)
                .onClickOk((d, i) -> {
                    if (i.isEmpty() || i.length() < House.MIN_NAME_LENGTH) {
                        player.sendErrorMessage("Tekstas per trumpas!");
                        d.show();
                    } else {
                        String parsed = StringUtils.parseTextColors(i);
                        house.setName(parsed);
                        eventManager.dispatchEvent(new HouseEditEvent(house, player));
                    }
                })
                .build();
    }

}
