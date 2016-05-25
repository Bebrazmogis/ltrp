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
                .caption("��jimo teksto keitimas")
                .buttonOk("Keisti")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .message("{FFFFFF}" + (entrance.getText() != null && !entrance.getText().isEmpty() ? "Dabartinis ��jimo tekstas: " + entrance.getText() : "") + "\n" +
                        "�veskite nauj� ��jimo tekst�\n" +
                        "� Tekst� turi sudaryti ne ma�iau kaip " + Entrance.MIN_TEXT_LENGTH + " simboli�\n" +
                        "� Tekst� turi b�ti kult�ringas, u� keiksma�od�ius pavadinime bus baud�iama\n" +
                        "� Galite prid�ti spalv� � tekst� naudojant {SPALVA}, pvz {RAUDONA}Tekstas atrodys {FF0000}Tekstas{FFFFFF}")
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
                        player.sendMessage(entrance.getColor(), "I��jimo tekstas atnaujintas.");
                    }
                })
                .build();
    }
}
