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
                .caption(LtrpGamemodeConstants.Name + " áëjimø valdymas")
                .item("Pagalba", i -> {
                    MsgboxDialog.create(player, eventManager)
                            .caption("Áëjimø informacija")
                            .buttonOk("Gerai")
                            .line("Áëjimai kiek pasikeitæ, dabar áëjimas neprivalo turëti antro galo.")
                            .line("Kitaip tariant, NEBËRA iğëjimø. VISKAS yra áëjimai.")
                            .line("Nori perëjimo nuo tağko A iki tağko B? Teks sukurti du áëjimus ir abiems jiems parinkti prieğingà.")
                            .parentDialog(i.getCurrentDialog())
                            .onClickOk(AbstractDialog::showParentDialog)
                            .build()
                            .show();
                })
                .item(() -> {
                            Entrance entrance = Entrance.getClosest(player.getLocation(), 6f);
                            return String.format("Naudoti artimiausià áëjimà(ID: %d, atstumas: %.2f)", entrance.getUUID(), entrance.getLocation().distance(player.getLocation()));
                        },
                        () -> Entrance.getClosest(player.getLocation(), 6f) != null,
                        i -> {
                            EntranceManagementDialog.create(player, eventManager, i.getCurrentDialog(), Entrance.getClosest(player.getLocation(), 6f)).show();
                        }
                )
                .item("Ávesti áëjimo ID", i -> {
                    EntranceUUIDInputDialog.create(player, eventManager, i.getCurrentDialog(), (d, en) -> {
                        EntranceManagementDialog.create(player, eventManager, d, en).show();
                    });
                })
                .item("Kurti naujà áëjimà", i -> {
                    Entrance entrance = Entrance.create(player.getLocation(), "áëjimas..");
                    EntranceManagementDialog.create(player, eventManager, i.getCurrentDialog(), entrance);
                })
                .build();
    }

}
