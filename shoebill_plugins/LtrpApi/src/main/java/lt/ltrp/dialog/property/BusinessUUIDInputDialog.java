package lt.ltrp.dialog.property;

import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.DialogTextSupplier;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

import java.util.OptionalInt;

/**
 * @author Bebras
 *         2016.04.26.
 */
public class BusinessUUIDInputDialog extends InputDialog {

    protected ClickOkHandler handler;
    protected InputErrorHandler inputErrorHandler;


    public BusinessUUIDInputDialog(LtrpPlayer player, EventManager parentEventManager) {
        super(player, parentEventManager);
        OptionalInt optionaluuid = Business.get().stream().mapToInt(b -> b.getUUID()).max();
        this.setCaption("Verslo UUID ávedimas");
        this.setButtonOk("Gerai");
        this.setButtonCancel("Atðaukti");
        this.setMessage("Áveskite verslo unikalø ID. Minimalus ID yra 1, maksimalus " + (optionaluuid.isPresent() ? optionaluuid.getAsInt() : 1) + "" +
                "\n\nServeryje yra " + Business.get().size() + " verslai." +
                "\nID nebûtinai yra nuoseklûs.");
    }

    public void setClickOkHandler(ClickOkHandler handler) {
        this.handler = handler;
    }

    public void setInputErrorHandler(InputErrorHandler h) {
        this.inputErrorHandler = h;
    }

    @Override
    public void onClickOk(String text) {
        try {
            int val = Integer.parseInt(text);
            Business b = Business.get(val);
            if(b == null) {
                if(inputErrorHandler != null) inputErrorHandler.onInputError(this, text);
            }
            else {
                if(handler != null)
                    handler.onClickOk(this, b);
            }
        } catch(java.lang.NumberFormatException e) {
            if(inputErrorHandler != null)
                inputErrorHandler.onInputError(this, text);
        }
    }

    @FunctionalInterface
    public interface ClickOkHandler {
        void onClickOk(BusinessUUIDInputDialog dialog, Business business);
    }

    @FunctionalInterface
    public interface InputErrorHandler {
        void onInputError(BusinessUUIDInputDialog dialog, String input);
    }

    public static AbstractIntegerInputDialogBuilder<?, ?> create(LtrpPlayer player, EventManager parentEventManager) {
        return new IntegerInputDialogBuilder(player, parentEventManager);
    }

    public static abstract class AbstractIntegerInputDialogBuilder
            <DialogType extends BusinessUUIDInputDialog, DialogBuilderType extends AbstractIntegerInputDialogBuilder<DialogType, DialogBuilderType>>
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

    public static class IntegerInputDialogBuilder extends AbstractIntegerInputDialogBuilder<BusinessUUIDInputDialog, IntegerInputDialogBuilder> {
        private IntegerInputDialogBuilder(LtrpPlayer player, EventManager parentEventManager) {
            super(new BusinessUUIDInputDialog(player, parentEventManager));
        }
    }

}
