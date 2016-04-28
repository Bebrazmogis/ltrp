package lt.ltrp.dialog.property;

import lt.ltrp.event.property.BusinessNameChangeEvent;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class BusinessNameInputDialog {

    public static InputDialog create(LtrpPlayer player, EventManager eventManager, Business business) {
        return InputDialog.create(player, eventManager)
                .caption("Verslo pavadinimo keitimas")
                .message("{FFFFFF}" + (business.getName() != null && !business.getName().isEmpty() ? "Dabartinis verslo pavadinimas: " + business.getName() : "") + "\n" +
                        "Áveskite naujà verslo pavadinimà\n" +
                        "• Pavadinimas turi sudaryti ne maþiau kaip " + Business.MIN_NAME_LENGTH + " simboliø\n" +
                        "• Pavadinimas turi bûti kultûringas, uþ keiksmaþodþius pavadinime bus baudþiama\n" +
                        "• Galite pridëti spalvø á tekstà naudojant {SPALVA}, pvz {RAUDONA}Tekstas atrodys {FF0000}Tekstas{FFFFFF}")
                .buttonOk("Keisti")
                .buttonCancel("Atgal")
                .onClickCancel(AbstractDialog::show)
                .onClickOk((d, i) -> {
                    if (i.isEmpty() || i.length() < Business.MIN_NAME_LENGTH) {
                        player.sendErrorMessage("Tekstas per trumpas!");
                        d.show();
                    } else {
                        String parsed = StringUtils.parseTextColors(i) + "{FFFFFF}";
                        business.setName(parsed);
                        eventManager.dispatchEvent(new BusinessNameChangeEvent(business, player, parsed));
                    }
                })
                .build();
    }

}
