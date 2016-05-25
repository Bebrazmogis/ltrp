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
                .item("Keisti pavadinimà", i -> {
                    EntranceNameInputDialog.create(player, eventManager, i.getCurrentDialog(), entrance)
                            .show();
                })
                .item("Nustatyi iðëjimo ID", i -> {
                    EntranceUUIDInputDialog.create(player, eventManager, i.getCurrentDialog(), (d, ex) -> {
                        entrance.setExit(ex);
                        plugin.updateEntrance(entrance);
                        i.getCurrentDialog().show();
                        player.sendMessage(entrance.getColor(), "Iðëjimas atnaujintas.");
                    });
                })
                .item("Keisti teksto spalvà", i -> {
                    EntranceColorListDialog.create(player, eventManager, i.getCurrentDialog(), entrance)
                            .show();
                })
                .item("Keisti pickup modelá", i -> {
                    SampModelInputDialog.create(player, eventManager)
                            .caption("Pickup modelio keitimas")
                            .buttonOk("Keisti")
                            .buttonCancel("Atgal")
                            .message("Áveskite naujà pickup modelio ID.")
                            .parentDialog(i.getCurrentDialog())
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .onClickOk((d, m) -> {
                                entrance.setPickupModelId(m);
                                plugin.updateEntrance(entrance);
                                player.sendMessage(entrance.getColor(), "Iðëjimas atnaujintas.");
                                i.getCurrentDialog().show();
                            })
                            .build()
                            .show();
                })
                .item("Perkelti áëjimà á mano pozicijà", i -> {
                    entrance.setLocation(player.getLocation());
                    plugin.updateEntrance(entrance);
                    player.sendMessage(entrance.getColor(), "Iðëjimas atnaujintas.");
                    i.getCurrentDialog().show();
                })
                .item("Paskirti darbà", i -> {
                    EntranceJobDialog.create(player, eventManager, i.getCurrentDialog(), entrance)
                            .show();
                })
                .item("Paðalinti darbà", entrance::isJob, i -> {
                    entrance.setJob(null);
                    plugin.updateEntrance(entrance);
                    player.sendMessage(entrance.getColor(), "Iðëjimas atnaujintas. Dabar áeiti galës bet kas.");
                    i.getCurrentDialog().show();
                })
                .item("Uþdrausti transporto priemones", entrance::allowsVehicles, i -> {
                    entrance.setAllowVehicles(false);
                    plugin.updateEntrance(entrance);
                    player.sendMessage(entrance.getColor(), "Iðëjimas atnaujintas. Nebebus galima ávaþiuoti su transporto priemonëmis.");
                    i.getCurrentDialog().show();
                })
                .item("Leisti ávaþiuoti su transporto priemonëm", () -> !entrance.allowsVehicles(), i -> {
                    entrance.setAllowVehicles(true);
                    plugin.updateEntrance(entrance);
                    player.sendMessage(entrance.getColor(), "Iðëjimas atnaujintas. Á vidø bus galima ávaþiuoti ir su transporto priemonëmis.");
                    i.getCurrentDialog().show();
                })
                .build();
    }

}
