package lt.ltrp.radio.dialog;

import lt.ltrp.radio.RadioStation;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.List;

/**
 * @author Bebras
 *         2016.02.15.
 */
public class RadioStationListDialog extends PageListDialog {

    private List<RadioStation> radioStationList;
    private ClickOkHandler clickOkHandler;

    public RadioStationListDialog(Player player, EventManager eventManager, List<RadioStation> radioStations) {
        super(player, eventManager);
        this.setCaption("Radijo stotys");
        this.radioStationList = radioStations;
    }

    public void setClickOkHandler(ClickOkHandler clickOkHandler) {
        this.clickOkHandler = clickOkHandler;
        super.setClickOkHandler((d, i) -> {
            if(clickOkHandler != null) {
                clickOkHandler.onClickOk((RadioStationListDialog)d, (RadioStation)i.getData());
            }
        });
    }

    @Override
    public void show() {
        items.clear();
        for(RadioStation station : radioStationList) {
            ListDialogItem item = new ListDialogItem();
            item.setData(station);
            item.setItemText(station.getName());
            items.add(item);
        }
        super.show();
    }


    @FunctionalInterface
    public interface ClickOkHandler extends ListDialog.ClickOkHandler{
        void onClickOk(RadioStationListDialog dialog, RadioStation station);
        @Override
        default void onClickOk(ListDialog dialog, ListDialogItem item) {}
    }
}
