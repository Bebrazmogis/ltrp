package lt.ltrp.api.dialog.dialogmenu;

import lt.ltrp.api.object.LtrpPlayer;

/**
 * @author Bebras
 *         2015.12.12.
 */
public abstract class DialogMenu {

    private DialogMenuCloseHandler dialogMenuCloseHandler;

    public abstract void show(LtrpPlayer player);
    public abstract boolean isShown();

    public void setCloseHandler(DialogMenuCloseHandler handler) {
        dialogMenuCloseHandler = handler;
    }

    protected DialogMenuCloseHandler getDialogMenuCloseHandler() {
        return dialogMenuCloseHandler;
    }

    @FunctionalInterface
    public interface DialogMenuCloseHandler {
        void onDialogMenuClose(LtrpPlayer player);
    }
}
