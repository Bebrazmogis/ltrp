package lt.ltrp.dialog;

import lt.ltrp.dialog.IntegerInputDialog;
import lt.ltrp.event.property.BusinessEditEvent;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.26.
 */
public class BusinessResourceInputDialog {

    public static IntegerInputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, Business business) {
        return IntegerInputDialog.create(player, eventManager)
                .parentDialog(parent)
                .caption("Verslo prekiø kiekis")
                .line("Verslo prekiø kiekio keitimas.")
                .line("\nDabartinis prekiø kiekis " + business.getResources())
                .line("\nÁveskite naujà prekiø kieká:")
                .line("\t• Minimalus prekiø kiekis 0.")
                .line("\t• Maksimalus prekiø kiekis " + Business.MAX_RESOURCES + ".")
                .onClickOk((d, i) -> {
                    if (i > 0 && i < Business.MAX_RESOURCES) {
                        business.setResources(i);
                        if (d.getParentDialog() != null) d.getParentDialog().show();
                        eventManager.dispatchEvent(new BusinessEditEvent(business, player), business, player);
                    } else
                        d.show();
                })
                .onClickCancel(d -> {
                    if (d.getParentDialog() != null) d.getParentDialog().show();
                })
                .build();
    }

}
