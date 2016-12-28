package lt.ltrp.business.dialog;

import lt.ltrp.player.PlayerController;
import lt.ltrp.constant.Currency;
import lt.ltrp.data.property.business.commodity.BusinessCommodity;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.27.
 */
public class BusinessInfoMsgBoxDialog {

    public static MsgboxDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, Business b) {
        System.out.println("Business type:" + b.getBusinessType());
        System.out.println("type name:" + b.getBusinessType().getName());
        String message = String.format("Pagrindinë informacija\n\nUnikalus ID: %d\nPavadinimas: %s{FFFFFF}\nSavininkas: %s\nPinigai: %d\nTipas: %s\nResursai: %d/%d" +
                        "\n\nPrekës\n\nParduodamø prekiø kiekis: %d\nParduodamø prekiø limitas: %d",
                b.getUUID(),
                b.getName(),
                b.isOwned() ? PlayerController.instance.getUsernameByUUID(b.getOwner()) : "nëra",
                b.getMoney(),
                b.getBusinessType().getName(),
                b.getResources(), Business.MAX_RESOURCES,
                b.getCommodityCount(),
                b.getCommodityLimit());
        for (BusinessCommodity c : b.getCommodities()) {
            message += String.format("\n%s\t%d%c", c.getName(), c.getPrice(), Currency.SYMBOL);
        }
        return MsgboxDialog.create(player, eventManager)
                .caption("Verslo informacija")
                .parentDialog(parent)
                .buttonOk("Gerai")
                .buttonCancel("")
                .message(message)
                .onClickOk(AbstractDialog::showParentDialog)
                .build();
    }

}
