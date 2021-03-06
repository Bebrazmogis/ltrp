package lt.ltrp.dialog;

import lt.ltrp.LtrpGamemodeConstants;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.19.
 */
public class PasswordInputDialog extends InputDialog {

    public static AbstractPasswordInputDialogBuilder<?, ?> create(Player player, EventManager eventManager) {
        return new PasswordInputDialogBuilder(player, eventManager);
    }

    public PasswordInputDialog(Player player, EventManager parentEventManager) {
        super(player, parentEventManager, true);

        this.setCaption("Prisijungimas");
        this.addLine("{FFFFFF} Sveiki prisijung� � {cca267}Lithuanian role-play (ltrp.lt){FFFFFF} server�, dabar galite prisijungti");
        this.addLine("\n\n");
        this.addLine("Vartotojas: {cca267}" + player.getName());
        this.addLine("{FFFFFF}  Skripto versija: {cca267} " + LtrpGamemodeConstants.Version + " {FFFFFF}, atnaujintas: {cca267} " + LtrpGamemodeConstants.BuildDate);
        this.addLine("\n\n");
        this.addLine("{FFFFFF}�veskite slapta�od�:");
        this.setButtonOk("Jungtis");
        this.setButtonCancel("I�eiti");
    }


    public static class AbstractPasswordInputDialogBuilder<DialogType extends PasswordInputDialog, DialogBuilderType extends AbstractPasswordInputDialogBuilder<DialogType, DialogBuilderType>>
        extends AbstractInputDialogBuilder<DialogType, DialogBuilderType> {

        protected AbstractPasswordInputDialogBuilder(DialogType dialog) {
            super(dialog);
        }
    }

    public static class PasswordInputDialogBuilder extends AbstractPasswordInputDialogBuilder<PasswordInputDialog, PasswordInputDialogBuilder> {
        private PasswordInputDialogBuilder(Player player, EventManager eventManager) {
            super(new PasswordInputDialog(player, eventManager));
        }
    }
}
