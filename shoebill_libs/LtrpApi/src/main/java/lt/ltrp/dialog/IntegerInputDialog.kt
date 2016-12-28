package lt.ltrp.dialog;

import lt.maze.dialog.InputDialog
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.event.dialog.DialogResponseEvent
import net.gtaun.util.event.EventManager

/**
 * @author Bebras
 *         2016.03.01.
 */
open class IntegerInputDialog(player: Player, eventManager: EventManager) : InputDialog(player, eventManager, false) {

    var clickOkHandler2: ((IntegerInputDialog, Int) -> Unit)? = null
    var inputErrorHandler: ((IntegerInputDialog, String) -> Unit)? = null

    override fun onClickOk(event: DialogResponseEvent) {
        val text = event.inputText
        try {
            val value = text.toInt()
            onClickOk(value)
        } catch (e: NumberFormatException) {
            onInputError(text)
        }
    }

    open fun onClickOk(value: Int) {
        clickOkHandler2?.invoke(this, value)
    }

    open fun onInputError(text: String) {
        inputErrorHandler?.invoke(this, text)
    }

    @Suppress("UNCHECKED_CAST")
    abstract class AbstractIntegerInputDialogBuilder<T: IntegerInputDialog, V: AbstractIntegerInputDialogBuilder<T, V>>(dialog: T) :
            AbstractInputDialogBuilder<T, V>(dialog) {

        fun onClickOk(handler: (IntegerInputDialog, Int) -> Unit): V {
            dialog.clickOkHandler2 = handler
            return this as V
        }

        fun onInputError(handler: (IntegerInputDialog, String) -> Unit): V {
            dialog.inputErrorHandler = handler
            return this as V
        }
    }

    class IntegerInputDialogBuilder(dialog: IntegerInputDialog) : AbstractInputDialogBuilder<IntegerInputDialog, IntegerInputDialogBuilder>(dialog) {}

    companion object {
        fun create(player: Player, eventManager: EventManager, init: IntegerInputDialog.() -> Unit): IntegerInputDialog {
            return IntegerInputDialogBuilder(IntegerInputDialog(player, eventManager)).build()
        }
    }

}

/*
public class IntegerInputDialog extends InputDialog {

    protected ClickOkHandler handler;
    protected InputErrorHandler inputErrorHandler;

    public IntegerInputDialog(LtrpPlayer player, EventManager parentEventManager) {
        super(player, parentEventManager);
    }

    public static AbstractIntegerInputDialogBuilder<?, ?> create(LtrpPlayer player, EventManager parentEventManager) {
        return new IntegerInputDialogBuilder(player, parentEventManager);
    }

    public void setInputErrorHandler(InputErrorHandler h) {
        this.inputErrorHandler = h;
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
        } catch(java.lang.NumberFormatException e) {
            if(inputErrorHandler != null)
                inputErrorHandler.onInputError(this, text);
            show();
        }
    }

    @FunctionalInterface
    public interface ClickOkHandler {
        void onClickOk(IntegerInputDialog dialog, int val);
    }

    @FunctionalInterface
    public interface InputErrorHandler {
        void onInputError(IntegerInputDialog dialog, String input);
    }

    @SuppressWarnings("unchecked")
    public static abstract class AbstractIntegerInputDialogBuilder
            <DialogType extends IntegerInputDialog, DialogBuilderType extends AbstractIntegerInputDialogBuilder<DialogType, DialogBuilderType>>
            extends AbstractDialogBuilder<DialogType, DialogBuilderType> {
        protected AbstractIntegerInputDialogBuilder(DialogType dialog) {
            super(dialog);
        }

        public DialogBuilderType message(String message) {
            dialog.setMessage(message);
            return (DialogBuilderType) this;
        }

        public DialogBuilderType message(DialogTextSupplier messageSupplier) {
            dialog.setMessage(messageSupplier);
            return (DialogBuilderType) this;
        }

        public DialogBuilderType onInputError(InputErrorHandler handler1) {
            dialog.setInputErrorHandler(handler1);
            return (DialogBuilderType) this;
        }

        public DialogBuilderType onClickOk(ClickOkHandler handler) {
            dialog.setClickOkHandler(handler);
            return (DialogBuilderType) this;
        }

        public DialogBuilderType line(String line) {
            dialog.addLine(line);
            return (DialogBuilderType) this;
        }
    }

    public static class IntegerInputDialogBuilder extends AbstractIntegerInputDialogBuilder<IntegerInputDialog, IntegerInputDialogBuilder> {
        private IntegerInputDialogBuilder(LtrpPlayer player, EventManager parentEventManager) {
            super(new IntegerInputDialog(player, parentEventManager));
        }
    }

}
*/