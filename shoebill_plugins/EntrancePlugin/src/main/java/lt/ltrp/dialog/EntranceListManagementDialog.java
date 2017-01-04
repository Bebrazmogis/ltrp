package lt.ltrp.dialog;

import lt.ltrp.LtrpGamemodeConstants;
import lt.ltrp.object.Entrance;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class EntranceListManagementDialog {

    public static ListDialog create(LtrpPlayer player, EventManager eventManager) {
        return ListDialog.create(player, eventManager)
                .caption(LtrpGamemodeConstants.Name + " ��jim� valdymas")
                .item("Pagalba", i -> {
                    MsgboxDialog.create(player, eventManager)
                            .caption("��jim� informacija")
                            .buttonOk("Gerai")
                            .line("��jimai kiek pasikeit�, dabar ��jimas neprivalo tur�ti antro galo.")
                            .line("Kitaip tariant, NEB�RA i��jim�. VISKAS yra ��jimai.")
                            .line("Nori per�jimo nuo ta�ko A iki ta�ko B? Teks sukurti du ��jimus ir abiems jiems parinkti prie�ing�.")
                            .parentDialog(i.getCurrentDialog())
                            .onClickOk(AbstractDialog::showParentDialog)
                            .build()
                            .show();
                })
                .item(() -> {
                            Entrance entrance = Entrance.getClosest(player.getLocation(), 6f);
                            return String.format("Naudoti artimiausi� ��jim�(ID: %d, atstumas: %.2f)", entrance.getUUID(), entrance.getLocation().distance(player.getLocation()));
                        },
                        () -> Entrance.getClosest(player.getLocation(), 6f) != null,
                        i -> {
                            EntranceManagementDialog.create(player, eventManager, i.getCurrentDialog(), Entrance.getClosest(player.getLocation(), 6f)).show();
                        }
                )
                .item("�vesti ��jimo ID", i -> {
                    EntranceUUIDInputDialog.create(player, eventManager, i.getCurrentDialog(), (d, en) -> {
                        EntranceManagementDialog.create(player, eventManager, d, en).show();
                    });
                })
                .item("Kurti nauj� ��jim�", i -> {
                    Entrance entrance = Entrance.create(player.getLocation(), "��jimas..");
                    EntranceManagementDialog.create(player, eventManager, i.getCurrentDialog(), entrance);
                })
                .build();
    }

}
