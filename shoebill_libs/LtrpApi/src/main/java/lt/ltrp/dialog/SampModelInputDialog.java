package lt.ltrp.dialog;

import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.util.event.EventManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author Bebras
 *         2016.04.25.
 */
public class SampModelInputDialog extends IntegerInputDialog {

    public SampModelInputDialog(LtrpPlayer player, EventManager parentEventManager) {
        super(player, parentEventManager);
        this.setTitle("SAMP modelio ávedimas");
        this.setButtonOk("Gerai");
        this.setButtonCancel("Atðaukti");
    }

    @Override
    public void onClickOk(@NotNull DialogResponseEvent event) {
        String text = event.getInputText();
        int value;
        try {
            value = Integer.parseInt(text);
        } catch(NumberFormatException e) {
            if(getInputErrorHandler() != null)
                getInputErrorHandler().invoke(this, text);
            return;
        }
        if(value < 0 || value > 19999) {
            if(getInputErrorHandler() != null)
                getInputErrorHandler().invoke(this, text);
        } else
        if(getClickOkHandler2() != null)
            getClickOkHandler2().invoke(this, value);
    }
}
