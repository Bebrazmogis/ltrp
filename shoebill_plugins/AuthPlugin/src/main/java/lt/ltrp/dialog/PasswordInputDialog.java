package lt.ltrp.dialog;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.05.19.
 */
public class PasswordInputDialog extends InputDialog {

    public static AbstractPasswordInputDialogBuilder<?, ?> create(LtrpPlayer player, EventManager eventManager) {
        return new PasswordInputDialogBuilder(player, eventManager);
    }

    public PasswordInputDialog(Player player, EventManager parentEventManager) {
        super(player, parentEventManager, true);

        this.setCaption("Prisijungimas");
        this.addLine("{FFFFFF} Sveiki prisijungæ á {cca267}Lithuanian role-play (ltrp.lt){FFFFFF} serverá, dabar galite prisijungti");
        this.addLine("\n\n");
        this.addLine("Vartotojas: {cca267}" + player.getName());
        this.addLine("{FFFFFF}  Skripto versija: {cca267} " + LtrpGamemode.Version + " {FFFFFF}, atnaujintas: {cca267} " + LtrpGamemode.BuildDate);
        this.addLine("\n\n");
        this.addLine("{FFFFFF}Áveskite slaptaþodá:");
        this.setButtonOk("Jungtis");
        this.setButtonCancel("Iðeiti");
    }


    public static class AbstractPasswordInputDialogBuilder<DialogType extends PasswordInputDialog, DialogBuilderType extends AbstractPasswordInputDialogBuilder<DialogType, DialogBuilderType>>
        extends AbstractInputDialogBuilder<DialogType, DialogBuilderType> {

        protected AbstractPasswordInputDialogBuilder(DialogType dialog) {
            super(dialog);
        }
    }

    public static class PasswordInputDialogBuilder extends AbstractPasswordInputDialogBuilder<PasswordInputDialog, PasswordInputDialogBuilder> {
        private PasswordInputDialogBuilder(LtrpPlayer player, EventManager eventManager) {
            super(new PasswordInputDialog(player, eventManager));
        }
    }
}
