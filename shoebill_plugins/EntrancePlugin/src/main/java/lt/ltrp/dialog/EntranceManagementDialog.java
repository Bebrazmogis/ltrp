package lt.ltrp.dialog;

import lt.ltrp.EntrancePlugin;
import lt.ltrp.object.Entrance;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class EntranceManagementDialog {

    public static ListDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, Entrance entrance) {
        EntrancePlugin plugin = EntrancePlugin.get(EntrancePlugin.class);
        return ListDialog.create(player, eventManager)
                .caption(entrance.getText() != null ? entrance.getText() : "--")
                .parentDialog(parent)
                .buttonOk("Pasirinkti")
                .buttonCancel("Atgal")
                .onClickCancel(AbstractDialog::showParentDialog)
                .item("Informacija", i -> {
                    i.getCurrentDialog().show();
                })
                .item("Keisti pavadinim�", i -> {
                    EntranceNameInputDialog.create(player, eventManager, i.getCurrentDialog(), entrance)
                            .show();
                })
                .item("Nustatyi i��jimo ID", i -> {
                    EntranceUUIDInputDialog.create(player, eventManager, i.getCurrentDialog(), (d, ex) -> {
                        entrance.setExit(ex);
                        plugin.updateEntrance(entrance);
                        i.getCurrentDialog().show();
                        player.sendMessage(entrance.getColor(), "I��jimas atnaujintas.");
                    });
                })
                .item("Keisti teksto spalv�", i -> {
                    EntranceColorListDialog.create(player, eventManager, i.getCurrentDialog(), entrance)
                            .show();
                })
                .item("Keisti pickup model�", i -> {
                    SampModelInputDialog.create(player, eventManager)
                            .caption("Pickup modelio keitimas")
                            .buttonOk("Keisti")
                            .buttonCancel("Atgal")
                            .message("�veskite nauj� pickup modelio ID.")
                            .parentDialog(i.getCurrentDialog())
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .onClickOk((d, m) -> {
                                entrance.setPickupModelId(m);
                                plugin.updateEntrance(entrance);
                                player.sendMessage(entrance.getColor(), "I��jimas atnaujintas.");
                                i.getCurrentDialog().show();
                            })
                            .build()
                            .show();
                })
                .item("Perkelti ��jim� � mano pozicij�", i -> {
                    entrance.setLocation(player.getLocation());
                    plugin.updateEntrance(entrance);
                    player.sendMessage(entrance.getColor(), "I��jimas atnaujintas.");
                    i.getCurrentDialog().show();
                })
                .item("Paskirti darb�", i -> {
                    EntranceJobDialog.create(player, eventManager, i.getCurrentDialog(), entrance)
                            .show();
                })
                .item("Pa�alinti darb�", entrance::isJob, i -> {
                    entrance.setJob(null);
                    plugin.updateEntrance(entrance);
                    player.sendMessage(entrance.getColor(), "I��jimas atnaujintas. Dabar �eiti gal�s bet kas.");
                    i.getCurrentDialog().show();
                })
                .item("U�drausti transporto priemones", entrance::allowsVehicles, i -> {
                    entrance.setAllowVehicles(false);
                    plugin.updateEntrance(entrance);
                    player.sendMessage(entrance.getColor(), "I��jimas atnaujintas. Nebebus galima �va�iuoti su transporto priemon�mis.");
                    i.getCurrentDialog().show();
                })
                .item("Leisti �va�iuoti su transporto priemon�m", () -> !entrance.allowsVehicles(), i -> {
                    entrance.setAllowVehicles(true);
                    plugin.updateEntrance(entrance);
                    player.sendMessage(entrance.getColor(), "I��jimas atnaujintas. � vid� bus galima �va�iuoti ir su transporto priemon�mis.");
                    i.getCurrentDialog().show();
                })
                .build();
    }

}
