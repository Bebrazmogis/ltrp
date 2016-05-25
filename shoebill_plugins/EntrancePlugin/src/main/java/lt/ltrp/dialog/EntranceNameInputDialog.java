package lt.ltrp.dialog;

import lt.ltrp.EntrancePlugin;
import lt.ltrp.object.Entrance;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class EntranceNameInputDialog {
    public static InputDialog create(LtrpPlayer player, EventManager eventManager, ListDialog parent, Entrance entrance) {
        return InputDialog.create(player, eventManager)
                .caption("Áëjimo teksto keitimas")
                .buttonOk("Keisti")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .message("{FFFFFF}" + (entrance.getText() != null && !entrance.getText().isEmpty() ? "Dabartinis áëjimo tekstas: " + entrance.getText() : "") + "\n" +
                        "Áveskite naujà áëjimo tekstà\n" +
                        "• Tekstà turi sudaryti ne maþiau kaip " + Entrance.MIN_TEXT_LENGTH + " simboliø\n" +
                        "• Tekstà turi bûti kultûringas, uþ keiksmaþodþius pavadinime bus baudþiama\n" +
                        "• Galite pridëti spalvø á tekstà naudojant {SPALVA}, pvz {RAUDONA}Tekstas atrodys {FF0000}Tekstas{FFFFFF}")
                .buttonCancel("Atgal")
                .onClickOk((d, s) -> {
                    if (s.isEmpty() || s.length() < Entrance.MIN_TEXT_LENGTH) {
                        player.sendErrorMessage("Tekstas per trumpas!");
                        d.show();
                    } else {
                        String parsed = StringUtils.parseTextColors(s);
                        entrance.setText(parsed);
                        EntrancePlugin.get(EntrancePlugin.class).updateEntrance(entrance);
                        parent.show();
                        player.sendMessage(entrance.getColor(), "Iðëjimo tekstas atnaujintas.");
                    }
                })
                .build();
    }
}
