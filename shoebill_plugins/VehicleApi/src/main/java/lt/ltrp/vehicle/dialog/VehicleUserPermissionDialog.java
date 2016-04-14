package lt.ltrp.vehicle.dialog;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.event.PlayerVehicleAddPermissionEvent;
import lt.ltrp.vehicle.event.PlayerVehicleRemovePermissionEvent;
import lt.ltrp.vehicle.object.PlayerVehicle;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.11.
 */
public class VehicleUserPermissionDialog extends PageListDialog {

    private PlayerVehicle vehicle;
    private String username;
    private int userId;

    public VehicleUserPermissionDialog(LtrpPlayer player, EventManager eventManager, PlayerVehicle vehicle, int userId, String username) {
        super(player, eventManager);
        this.username = username;
        this.userId = userId;
        setCaption(vehicle.getModelName() + ": " + username);
        setButtonOk("Pasirinkti");
        setButtonCancel("Atðaukti");
        this.vehicle = vehicle;
    }

    @Override
    public void show() {
        items.clear();

        items.add(new ListDialogItem("Pridëti naujà", i -> {
            VehiclePermissionListDialog.create((LtrpPlayer)player, eventManagerNode)
                    .onSelectPermission((d, perm) -> {
                        MsgboxDialog.create(player, eventManagerNode)
                                .caption("Dëmesio")
                                .message("Ar tikrai norite pridëti " + username + " teisæ \"" + perm.name() + "\"?")
                                .buttonOk("Taip")
                                .buttonCancel("Ne")
                                .onClickOk(h -> {
                                    vehicle.addPermission(userId, perm);
                                    eventManagerNode.dispatchEvent(new PlayerVehicleAddPermissionEvent((LtrpPlayer)player, vehicle, userId, perm));
                                })
                                .onClickCancel(dd -> i.getCurrentDialog().show())
                                .build()
                                .show();
                    })
                    .build()
                    .show();
        }));
        vehicle.getPermissions(userId).forEach(p -> {
            items.add(new ListDialogItem(p.name(), i -> {
                MsgboxDialog.create(player, eventManagerNode)
                        .caption("Dëmesio")
                        .message("Ar tikrai norite paðalinti " + username + " teisæ \"" + p.name() + "\"?")
                        .buttonOk("Taip")
                        .buttonCancel("Ne")
                        .onClickOk(h -> {
                            vehicle.addPermission(userId, p);
                            eventManagerNode.dispatchEvent(new PlayerVehicleRemovePermissionEvent((LtrpPlayer)player, vehicle, userId, p));
                        })
                        .onClickCancel(d -> i.getCurrentDialog().show())
                        .build()
                        .show();
            }));
        });

        super.show();
    }

}
