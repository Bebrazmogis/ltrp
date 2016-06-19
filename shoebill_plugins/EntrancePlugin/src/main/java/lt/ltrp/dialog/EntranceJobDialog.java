package lt.ltrp.dialog;

import lt.ltrp.EntrancePlugin;
import lt.ltrp.object.Entrance;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class EntranceJobDialog {


    public static AbstractDialog create(LtrpPlayer player, EventManager eventManager, ListDialog parent, Entrance entrance) {
            return JobListDialog.create(player, eventManager)
                    .caption("Pasirinkite ��jimo darb�:")
                    .buttonOk("Pasirinkti")
                    .buttonCancel("Atgal")
                    .parentDialog(parent)
                    .selectJobHandler((d, job) -> {
                        entrance.setJob(job);
                        EntrancePlugin.get(EntrancePlugin.class).updateEntrance(entrance);
                        player.sendMessage(entrance.getColor(), "I��jimas atnaujintas. Nuo �iol �eiti tik tie, kas dirba " + job.getName());
                        parent.show();
                    })
                    .onClickCancel(AbstractDialog::showParentDialog)
                    .build();
    }
}
