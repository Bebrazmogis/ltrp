package lt.ltrp.job.dialog;

import lt.ltrp.job.data.Roadblock;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.30.
 */
public class RoadblockDialog {


    public static ListDialog create(LtrpPlayer player, EventManager eventManager, Roadblock roadblock) {
        return ListDialog.create(player, eventManager)
                .caption("Uþtvaro valdymas")
                .item("Paðalinti", dialog -> roadblock.destroy())
                .build();
    }


}
