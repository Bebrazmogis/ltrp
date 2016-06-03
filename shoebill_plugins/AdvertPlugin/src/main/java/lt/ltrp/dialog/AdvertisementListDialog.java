package lt.ltrp.dialog;

import lt.ltrp.data.Advert;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.util.event.EventManager;

import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.06.01.
 */
public class AdvertisementListDialog extends PageListDialog {

    private SimpleDateFormat adDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    private Collection<Advert> ads;

    public AdvertisementListDialog(LtrpPlayer player, EventManager eventManager, Collection<Advert> ads) {
        super(player, eventManager);
        this.ads = ads;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    @Override
    public void show() {
        items.clear();

        ads.forEach(ad -> {
            items.add(
                    ListDialogItem.create()
                            .itemText(String.format("%s\t%s\t%d",
                                    adDateFormat.format(ad.getCreatedAt()),
                                    StringUtils.limit(ad.getAdText(), 30),
                                    ad.getPhoneNumber()))
                            .onSelect(i -> AdvertisementMsgBoxDialog.create(getPlayer(), eventManagerNode, i.getCurrentDialog(), ad).show())
                            .build()
            );
        });

        super.show();
    }

    public static AdvertisementListDialogBuilder create(LtrpPlayer player, EventManager eventManager, Collection<Advert> ads) {
        return new AdvertisementListDialogBuilder(new AdvertisementListDialog(player ,eventManager, ads));
    }


    public static class AdvertisementListDialogBuilder extends AbstractPageListDialogBuilder<AdvertisementListDialog, AdvertisementListDialogBuilder> {

        protected AdvertisementListDialogBuilder(AdvertisementListDialog dialog) {
            super(dialog);
        }
    }

}
