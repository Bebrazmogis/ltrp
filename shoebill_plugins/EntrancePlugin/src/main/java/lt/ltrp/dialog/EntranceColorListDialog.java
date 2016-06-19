package lt.ltrp.dialog;

import lt.ltrp.EntrancePlugin;
import lt.ltrp.colorpicker.ColorPicker;
import lt.ltrp.data.Color;
import lt.ltrp.object.Entrance;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.constant.VehicleColor;
import net.gtaun.util.event.EventManager;

import java.util.Arrays;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class EntranceColorListDialog {



    public static ListDialog create(LtrpPlayer player, EventManager eventManager, ListDialog parent, Entrance entrance) {
        EntrancePlugin plugin = EntrancePlugin.get(EntrancePlugin.class);
        return ListDialog.create(player, eventManager)
                .caption("��jimo spalvos keitimas")
                .buttonOk("Pasirinkti")
                .buttonCancel("Atgal")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .item("Pasirinkti spalv� i� meniu", i -> {
                    ColorPicker.create(player, eventManager, Arrays.asList(VehicleColor.getColors()))
                            .onSelectColor((d, c) -> {
                                entrance.setColor(VehicleColor.getColorFromId(c));
                                plugin.updateEntrance(entrance);
                                parent.show();
                                player.sendMessage(entrance.getColor(), "I��jimo teksto spalva atnaujinta");
                            })
                            .build()
                            .show();
                })
                .item("�vesti spalvos kod�(pa�engusiems)", i -> {
                    HexIntegerInputDialog.create(player, eventManager)
                            .caption("Spalvos kodo �vedimas")
                            .message("�veskite ��jimo spalvos kod�.")
                            .buttonOk("T�sti")
                            .buttonCancel("Atgal")
                            .parentDialog(i.getCurrentDialog())
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .onClickOk((d, hex) -> {
                                entrance.setColor(new Color(hex));
                                plugin.updateEntrance(entrance);
                                parent.show();
                                player.sendMessage(entrance.getColor(), "I��jimo teksto spalva atnaujinta");
                            })
                            .build()
                            .show();
                })
                .build();
    }
}
