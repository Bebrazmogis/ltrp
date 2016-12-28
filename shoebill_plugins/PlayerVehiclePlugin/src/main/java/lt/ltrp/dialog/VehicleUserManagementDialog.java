package lt.ltrp.dialog;

import lt.ltrp.PlayerPlugin;
import lt.ltrp.PlayerVehiclePlugin;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PlayerVehicle;
import lt.ltrp.player.PlayerController;
import lt.ltrp.player.dao.PlayerDao;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.31.
 *
 *         This player-stats lets owners see WHO has permissions to their vehicles
 */
public class VehicleUserManagementDialog {


    public static ListDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, PlayerVehicle vehicle) {
        PlayerVehiclePlugin plugin = PlayerVehiclePlugin.get(PlayerVehiclePlugin.class);
        Collection< ListDialogItem > items = new ArrayList<>();
        vehicle.getPermissions().keySet().forEach(userId -> {
            if(userId != player.getUUID()) {
                items.add(new ListDialogItem(PlayerController.instance.getUsernameByUUID(userId), i -> {
                    new VehicleUserPermissionDialog(player, eventManager, vehicle, userId, PlayerController.instance.getUsernameByUUID(userId))
                            .show();
                }));
            }
        });
        if(items.size() == 0)
            player.sendErrorMessage("Niekas neturi jokiø teisiø prie jûsø tr. priemonës! Pasidalinti transporto priemone galite su /setpermission");
        return ListDialog.create(player, eventManager)
                .caption(vehicle.getName() + " vartotojai.")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .items(items)
                .buttonOk("Pasirinkti")
                .buttonCancel("Uþdaryti")
                .build();


    }

}
