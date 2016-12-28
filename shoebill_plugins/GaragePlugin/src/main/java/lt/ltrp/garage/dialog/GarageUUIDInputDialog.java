package lt.ltrp.garage.dialog;

import lt.ltrp.object.Garage;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.util.event.EventManager;

import java.util.OptionalInt;

/**
 * @author Bebras
 *         2016.05.19.
 */
public class GarageUUIDInputDialog extends InputDialog {


    protected ClickOkHandler handler;
    protected InputErrorHandler inputErrorHandler;


    public GarageUUIDInputDialog(LtrpPlayer player, EventManager parentEventManager) {
        super(player, parentEventManager);
        OptionalInt optionaluuid = Garage.get().stream().mapToInt(b -> b.getUUID()).max();
        this.setCaption("Garaþo UUID ávedimas");
        this.setButtonOk("Gerai");
        this.setButtonCancel("Atðaukti");
        this.setMessage("Áveskite garaþo unikalø ID. Minimalus ID yra 1, maksimalus " + (optionaluuid.isPresent() ? optionaluuid.getAsInt() : 1) + "" +
                "\n\nServeryje yra " + Garage.get().size() + " garaþai." +
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
            Garage h = Garage.get(val);
            if(h == null) {
                if(inputErrorHandler != null) inputErrorHandler.onInputError(this, text);
            }
            else {
                if(handler != null) handler.onClickOk(this, h);
            }
        } catch(java.lang.NumberFormatException e) {
            if(inputErrorHandler != null)
                inputErrorHandler.onInputError(this, text);
        }
    }

    @FunctionalInterface
    public interface ClickOkHandler {
        void onClickOk(GarageUUIDInputDialog dialog, Garage house);
    }

    @FunctionalInterface
    public interface InputErrorHandler {
        void onInputError(GarageUUIDInputDialog dialog, String input);
    }

    public static AbstractGarageUUIDInputDialogBuilder<?, ?> create(LtrpPlayer player, EventManager parentEventManager) {
        return new GarageUUIDInputDialogBuilder(player, parentEventManager);
    }

    public static abstract class AbstractGarageUUIDInputDialogBuilder
            <DialogType extends GarageUUIDInputDialog, DialogBuilderType extends AbstractGarageUUIDInputDialogBuilder<DialogType, DialogBuilderType>>
            extends AbstractDialog.AbstractDialogBuilder<DialogType, DialogBuilderType> {
        protected AbstractGarageUUIDInputDialogBuilder(DialogType dialog) {
            super(dialog);
        }



        public DialogBuilderType onInputError(InputErrorHandler handler1) {
            dialog.setInputErrorHandler(handler1);
            return (DialogBuilderType) this;
        }

        public DialogBuilderType onClickOk(ClickOkHandler handler) {
            dialog.setClickOkHandler(handler);
            return (DialogBuilderType) this;
        }

    }

    public static class GarageUUIDInputDialogBuilder extends AbstractGarageUUIDInputDialogBuilder<GarageUUIDInputDialog, GarageUUIDInputDialogBuilder> {
        private GarageUUIDInputDialogBuilder(LtrpPlayer player, EventManager parentEventManager) {
            super(new GarageUUIDInputDialog(player, parentEventManager));
        }
    }
}
