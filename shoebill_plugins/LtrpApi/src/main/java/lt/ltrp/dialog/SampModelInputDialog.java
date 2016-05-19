package lt.ltrp.dialog;

import lt.ltrp.object.LtrpPlayer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.25.
 */
public class SampModelInputDialog extends IntegerInputDialog {

    public SampModelInputDialog(LtrpPlayer player, EventManager parentEventManager) {
        super(player, parentEventManager);
        this.setCaption("SAMP modelio ávedimas");
        this.setButtonOk("Gerai");
        this.setButtonCancel("Atðaukti");
    }

    @Override
    public void onClickOk(String text) {
        int value;
        try {
            value = Integer.parseInt(text);
        } catch(NumberFormatException e) {
            if(inputErrorHandler != null)
                inputErrorHandler.onInputError(this, text);
            return;
        }
        if(value < 0 || value > 19999) {
            if(inputErrorHandler != null)
                inputErrorHandler.onInputError(this, text);
        } else
            if(handler != null)
                handler.onClickOk(this, value);
    }
}
