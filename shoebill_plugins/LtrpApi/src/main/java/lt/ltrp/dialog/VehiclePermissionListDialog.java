package lt.ltrp.dialog;



import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.constant.PlayerVehiclePermission;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.11.
 */
public class VehiclePermissionListDialog extends PageListDialog {

    private SelectPermissionHandler selectPermissionHandler;

    protected VehiclePermissionListDialog(LtrpPlayer player, EventManager eventManager) {
        super(player, eventManager);
    }

    public void setSelectPermissionHandler(SelectPermissionHandler selectPermissionHandler) {
        this.selectPermissionHandler = selectPermissionHandler;
    }

    @Override
    public void show() {
        items.clear();

        for(PlayerVehiclePermission perm : PlayerVehiclePermission.values()) {
            items.add(new ListDialogItem(perm, perm.name(), null));
        }
        super.show();
    }

    @Override
    public void onClickOk(ListDialogItem item) {
        if(item.getData() != null && selectPermissionHandler != null) {
            selectPermissionHandler.onSelectPermission(this, (PlayerVehiclePermission) item.getData());
        }
    }


    @FunctionalInterface
    public interface SelectPermissionHandler {
        void onSelectPermission(VehiclePermissionListDialog dialog, PlayerVehiclePermission permission);
    }

    public static AbstractVehiclePermissionListDialogBuilder create(LtrpPlayer player, EventManager eventManager) {
        return new VehiclePermissionListDialogBuilder(player, eventManager);
    }

    public static abstract class AbstractVehiclePermissionListDialogBuilder<DialogType extends VehiclePermissionListDialog,
            DialogBuilderType extends AbstractVehiclePermissionListDialogBuilder<DialogType, DialogBuilderType>>
    extends AbstractPageListDialogBuilder<DialogType, DialogBuilderType> {

        protected AbstractVehiclePermissionListDialogBuilder(DialogType dialog) {
            super(dialog);
        }

        public DialogBuilderType onSelectPermission(SelectPermissionHandler handler) {
            dialog.setSelectPermissionHandler(handler);
            return (DialogBuilderType) this;
        }
    }

    public static class VehiclePermissionListDialogBuilder
            extends AbstractVehiclePermissionListDialogBuilder<VehiclePermissionListDialog, VehiclePermissionListDialogBuilder> {

        protected VehiclePermissionListDialogBuilder(LtrpPlayer player, EventManager eventManager) {
            super(new VehiclePermissionListDialog(player, eventManager));
        }
    }
}
