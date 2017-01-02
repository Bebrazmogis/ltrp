package lt.ltrp.player.vehicle.dialog;

import lt.ltrp.player.vehicle.data.VehicleFine;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.vehicle.object.PlayerVehicle;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class VehicleFineListDialog extends PageListDialog {

    public static AbstractVehicleFineListDialogBuilder create(LtrpPlayer player, EventManager eventManager, PlayerVehicle vehicle, Collection<VehicleFine> fines) {
        return new VehicleFineListDialogBuilder(player, eventManager, vehicle, fines);
    }

    private Collection<VehicleFine> fines;
    private PlayerVehicle vehicle;
    private SelectFineHandler selectFineHandler;

    protected VehicleFineListDialog(Player player, EventManager eventManager, PlayerVehicle vehicle, Collection<VehicleFine> fines) {
        super(player, eventManager);
        this.fines = fines;
        this.vehicle = vehicle;
        setCaption(vehicle.getModelName() + " baudos( "+ fines.size() + ")");
        setButtonOk("Pasirinkti");
        setButtonCancel("Uþdaryti");
    }

    public void setSelectFineHandler(SelectFineHandler selectFineHandler) {
        this.selectFineHandler = selectFineHandler;
    }

    @Override
    public void show() {

        fines.forEach(f -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            items.add(
                    ListDialogItem.create()
                            .itemText(String.format("%s\t%s..\t%s",
                                    dateFormat.format(f.getCreatedAt()),
                                    StringUtils.limit(f.getCrime(), 26),
                                    f.isPaid() ? "sumokëta" : "nesumokëta"
                            ))
                            .data(f)
                            .build()
            );
        });

        super.show();
    }

    @Override
    protected void onClickOk(ListDialogItem item) {
        Object data = item.getData();
        if(data instanceof VehicleFine && selectFineHandler != null)
            selectFineHandler.onSelectFine(this, (VehicleFine)data);
        else
            super.onClickOk(item);
    }


    @SuppressWarnings("unchecked")
    public static class AbstractVehicleFineListDialogBuilder<DialogType extends VehicleFineListDialog, DialogBuilderType extends AbstractVehicleFineListDialogBuilder<DialogType, DialogBuilderType>>
        extends AbstractPageListDialogBuilder<DialogType, DialogBuilderType> {

        protected AbstractVehicleFineListDialogBuilder(DialogType dialog) {
            super(dialog);
        }

        public DialogBuilderType onSelectFine(SelectFineHandler handler) {
            dialog.setSelectFineHandler(handler);
            return (DialogBuilderType)this;
        }
    }

    static class VehicleFineListDialogBuilder extends AbstractVehicleFineListDialogBuilder<VehicleFineListDialog, VehicleFineListDialogBuilder> {

        protected VehicleFineListDialogBuilder(LtrpPlayer player, EventManager eventManager, PlayerVehicle vehicle, Collection<VehicleFine> fines) {
            super(new VehicleFineListDialog(player, eventManager, vehicle, fines));
        }
    }

    @FunctionalInterface
    public interface SelectFineHandler {
        void onSelectFine(VehicleFineListDialog dialog, VehicleFine fine);
    }
}
