package lt.ltrp.player.vehicle.dialog;

import lt.ltrp.player.vehicle.PlayerVehiclePlugin;
import lt.ltrp.player.vehicle.event.PlayerVehicleAddPermissionEvent;
import lt.ltrp.player.vehicle.event.PlayerVehicleRemovePermissionEvent;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.vehicle.object.PlayerVehicle;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.11.
 *
 *         This player-stats allows vehicle owners to add/remove permission for a user
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
        setButtonCancel("At�aukti");
        this.vehicle = vehicle;
    }

    @Override
    public void show() {
        items.clear();

        items.add(new ListDialogItem("Prid�ti nauj�", i -> {
            VehiclePermissionListDialog.create((LtrpPlayer)player, eventManagerNode)
                    .onSelectPermission((d, perm) -> {
                        MsgboxDialog.create(player, eventManagerNode)
                                .caption("D�mesio")
                                .message("Ar tikrai norite prid�ti " + username + " teis� \"" + perm.name() + "\"?")
                                .buttonOk("Taip")
                                .buttonCancel("Ne")
                                .onClickOk(h -> {
                                    PlayerVehiclePlugin.get(PlayerVehiclePlugin.class).getVehiclePermissionDao().add(vehicle, userId, perm);
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
                        .caption("D�mesio")
                        .message("Ar tikrai norite pa�alinti " + username + " teis� \"" + p.name() + "\"?")
                        .buttonOk("Taip")
                        .buttonCancel("Ne")
                        .onClickOk(h -> {
                            PlayerVehiclePlugin.get(PlayerVehiclePlugin.class).getVehiclePermissionDao().remove(vehicle, userId, p);
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
