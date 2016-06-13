package lt.ltrp.dialog;

import lt.ltrp.data.PlayerSettings;
import lt.ltrp.event.player.PlayerEditSettingsEvent;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.TabListDialog;
import net.gtaun.shoebill.common.dialog.TabListDialogItem;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.10.
 */
public class PlayerSettingsListDialog {


    public static ListDialog create(LtrpPlayer player, EventManager eventManager, PlayerSettings settings) {
        return TabListDialog.create(player, eventManager)
                .caption(settings.getPlayer().getName() + " Þaidimo nustatymai")
                .header(0, "Nustatymas")
                .header(1, "Bûsena")
                .item(TabListDialogItem.create()
                        .column(0, ListDialogItem.create().itemText("Asmeniniø þinuèiø blokavimas").build())
                        .column(1, getBoolSetting(settings.isPmDisabled()))
                        .onSelect(item -> {
                            settings.setPmDisabled(!settings.isPmDisabled());
                        })
                        .build())
                .item(TabListDialogItem.create()
                        .column(0, ListDialogItem.create().itemText("OOC chat'o blokavimas").build())
                        .column(1, getBoolSetting(settings.isOocDisabled()))
                        .onSelect(item -> {
                            settings.setOocDisabled(!settings.isOocDisabled());
                        })
                        .build())
                .item(TabListDialogItem.create()
                        .column(0, ListDialogItem.create().itemText("Naujienø blokavimas").build())
                        .column(1, getBoolSetting(settings.isNewsDisabled()))
                        .onSelect(item -> {
                            settings.setNewsDisabled(!settings.isNewsDisabled());
                        })
                        .build())
                .item(TabListDialogItem.create()
                        .column(0, ListDialogItem.create().itemText("Garso efektø blokavimas").build())
                        .column(1, getBoolSetting(settings.isSoundsDisabled()))
                        .onSelect(item -> {
                            settings.setSoundsDisabled(!settings.isSoundsDisabled());
                        })
                        .build())
                .item(TabListDialogItem.create()
                        .column(0, ListDialogItem.create().itemText("Muzikos/radijo garsø blokavimas").build())
                        .column(1, getBoolSetting(settings.isMusicDisabled()))
                        .onSelect(item -> {
                            settings.setMusicDisabled(!settings.isMusicDisabled());
                        })
                        .build())
                .item(TabListDialogItem.create()
                        .enabled(() -> settings.getPlayer().isAdmin() || settings.getPlayer().isModerator())
                        .column(0, ListDialogItem.create().itemText("Þaidëjø klausimø blokavimas").build())
                        .column(1, getBoolSetting(settings.isModChatDisabled()))
                        .onSelect(item -> {
                            settings.setModChatDisabled(!settings.isModChatDisabled());
                        })
                        .build())
                .item(TabListDialogItem.create()
                        .enabled(() -> settings.getPlayer().isAdmin() || settings.getPlayer().isModerator())
                        .column(0, ListDialogItem.create().itemText("Þaidëjø nuþudymø þinuèiø blokavimas").build())
                        .column(1, getBoolSetting(settings.isKillMessagesDisabled()))
                        .build())
                .buttonOk("Keisti")
                .buttonCancel("Uþdaryti")
                .onClickOk((d, i) -> {
                    eventManager.dispatchEvent(new PlayerEditSettingsEvent(player, settings));
                    d.show();
                })
                .build();
    }


    private static ListDialogItem getBoolSetting(boolean setting) {
        return ListDialogItem.create().itemText(String.format("{FFFFFF}[ %s ]{FFFFFF}", setting ? "{00FF00}+" : "{FF0000}-" )).build();
    }

}
