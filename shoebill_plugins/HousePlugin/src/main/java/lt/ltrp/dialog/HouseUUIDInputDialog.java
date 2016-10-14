package lt.ltrp.dialog;

import lt.ltrp.object.Business;
import lt.ltrp.house.object.House;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.DialogTextSupplier;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

import java.lang.FunctionalInterface;import java.lang.Integer;import java.lang.Override;import java.lang.String;import java.util.OptionalInt;

/**
 * @author Bebras
 *         2016.05.16.
 */
public class HouseUUIDInputDialog extends InputDialog {

    protected ClickOkHandler handler;
    protected InputErrorHandler inputErrorHandler;


    public HouseUUIDInputDialog(LtrpPlayer player, EventManager parentEventManager) {
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
            House h = House.get(val);
            if(h == null) {
                if(inputErrorHandler != null) inputErrorHandler.onInputError(this, text);
            }
            else {
                if(handler != null)
                    handler.onClickOk(this, h);
            }
        } catch(java.lang.NumberFormatException e) {
            if(inputErrorHandler != null)
                inputErrorHandler.onInputError(this, text);
        }
    }

    @FunctionalInterface
    public interface ClickOkHandler {
        void onClickOk(HouseUUIDInputDialog dialog, House house);
    }

    @FunctionalInterface
    public interface InputErrorHandler {
        void onInputError(HouseUUIDInputDialog dialog, String input);
    }

    public static AbstractIntegerInputDialogBuilder<?, ?> create(LtrpPlayer player, EventManager parentEventManager) {
        return new IntegerInputDialogBuilder(player, parentEventManager);
    }

    public static abstract class AbstractIntegerInputDialogBuilder
            <DialogType extends HouseUUIDInputDialog, DialogBuilderType extends AbstractIntegerInputDialogBuilder<DialogType, DialogBuilderType>>
            extends AbstractDialog.AbstractDialogBuilder<DialogType, DialogBuilderType> {
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

    public static class IntegerInputDialogBuilder extends AbstractIntegerInputDialogBuilder<HouseUUIDInputDialog, IntegerInputDialogBuilder> {
        private IntegerInputDialogBuilder(LtrpPlayer player, EventManager parentEventManager) {
            super(new HouseUUIDInputDialog(player, parentEventManager));
        }
    }

}
