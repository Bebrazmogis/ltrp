package lt.ltrp.dialog.radio;

import lt.ltrp.dialog.IntegerInputDialog;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.data.RadioStation;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.util.event.EventManager;import java.lang.FunctionalInterface;

/**
 * @author Bebras
 *         2016.03.17.
 */
public class RadioOptionListDialog {

    public static ListDialog create(LtrpPlayer player, EventManager eventManager,
                                    SetVolumeHandler audioHandler,
                                    SelectRadioStationHandler radioStationHandler,
                                    TurnRadioOffHandler radioOffHandler) {
        return ListDialog.create(player, eventManager)
                .caption("Radijo valdymas")
                .buttonOk("Pasirinkti")
                .buttonCancel("I�eiti")
                .item("Pasirinkti stot�", i -> {
                    RadioStationListDialog dialog = new RadioStationListDialog(player, eventManager, RadioStation.get());
                    dialog.setClickOkHandler((d, s) -> {
                        if (radioStationHandler != null)
                            radioStationHandler.onSelectRadioStation(dialog, s);
                    });
                    dialog.show();
                })
                .item("�vesti savo radijo stot�", i -> {
                    InputDialog.create(player, eventManager)
                            .caption("Radijo valdymas: mano stotis")
                            .message("�veskite radijos stoties URL")
                            .buttonOk("Groti")
                            .buttonCancel("Atgal")
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .onClickOk((d, url) -> {
                                if (url == null || url.isEmpty()) {
                                    d.show();
                                } else {
                                    if (radioStationHandler != null) {
                                        radioStationHandler.onSelectRadioStation(i.getCurrentDialog(), new RadioStation(0, url, url));
                                    }
                                }
                            })
                            .build()
                            .show();
                })
                .item("Keisti gars�", i -> {
                    IntegerInputDialog.create(player, eventManager)
                            .caption("Radijo valdymas -> Garos nustatymai")
                            .buttonOk("Pasirinkti")
                            .buttonCancel("Atgal")
                            .message("Pasirinkite radijos gars�. \nLeid�iami dyd�iai 0 - 100.")
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .onClickOk((dd, vol) -> {
                                if (vol < 0 || vol > 100) {
                                    player.sendErrorMessage("Garsas turi b�ti tarp 0 - 100");
                                } else {
                                    if (audioHandler != null)
                                        audioHandler.onSetVolume(i.getCurrentDialog(), vol);
                                }
                            })
                            .build()
                            .show();
                })
                .item("I�jungti", i -> {
                    if(radioOffHandler != null) {
                        radioOffHandler.onTurnRadioOff(i.getCurrentDialog());
                    }
                })
                .build();
    }

    @FunctionalInterface
    public interface SetVolumeHandler {
        void onSetVolume(ListDialog dialog, int vol);
    }

    @FunctionalInterface
    public interface SelectRadioStationHandler {
        void onSelectRadioStation(ListDialog dialog, RadioStation station);
    }

    @FunctionalInterface
    public interface TurnRadioOffHandler {
        void onTurnRadioOff(ListDialog dialog);
    }
}
