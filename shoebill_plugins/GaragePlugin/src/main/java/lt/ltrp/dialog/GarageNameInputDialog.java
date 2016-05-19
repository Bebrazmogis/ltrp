package lt.ltrp.dialog;

import lt.ltrp.GarageController;
import lt.ltrp.event.property.garage.GarageEditEvent;
import lt.ltrp.object.Garage;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.19.
 */
public class GarageNameInputDialog {
    public static InputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, Garage garage) {
        return InputDialog.create(player, eventManager)
                .parentDialog(parent)
                .caption("Gara�o pavadinimo keitimas")
                .message("{FFFFFF}" + (garage.getName() != null && !garage.getName().isEmpty() ? "Dabartinis gara�o pavadinimas: " + garage.getName() : "") + "\n" +
                        "�veskite gara�o namo pavadinim�\n" +
                        "� Pavadinimas turi sudaryti ne ma�iau kaip " + Garage.MIN_NAME_LENGTH + " simboli�\n" +
                        "� Pavadinimas turi b�ti kult�ringas, u� keiksma�od�ius pavadinime bus baud�iama\n" +
                        "� Galite prid�ti spalv� � tekst� naudojant {SPALVA}, pvz {RAUDONA}Tekstas atrodys {FF0000}Tekstas{FFFFFF}")
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .onClickCancel(AbstractDialog::showParentDialog)
                .onClickOk((d, i) -> {
                    if (i.isEmpty() || i.length() < Garage.MIN_NAME_LENGTH) {
                        player.sendErrorMessage("Tekstas per trumpas!");
                        d.show();
                    } else {
                        String parsed = StringUtils.parseTextColors(i);
                        garage.setName(parsed);
                        eventManager.dispatchEvent(new GarageEditEvent(garage, player));
                        GarageController.get().getDao().update(garage);
                    }
                })
                .build();
    }
}
