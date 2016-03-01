package lt.ltrp.dialog;

import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Bebras
 *         2016.03.01.
 */
public class IntegerInputDialog extends InputDialog {

    private ClickOkHandler handler;

    public IntegerInputDialog(LtrpPlayer player, EventManager parentEventManager) {
        super(player, parentEventManager);
    }


    @Override
    @Deprecated
    public void setClickOkHandler(net.gtaun.shoebill.common.dialog.InputDialog.ClickOkHandler handler) {
        throw new NotImplementedException();
    }

    public void setClickOkHandler(ClickOkHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onClickOk(String text) {
        try {
            int val = Integer.parseInt(text);
            if(handler != null)
                handler.onClickOk(this, val);
        } catch(NumberFormatException e) {
            show();
        }
    }

    @FunctionalInterface
    public interface ClickOkHandler {
        void onClickOk(IntegerInputDialog dialog, int val);
    }

}
