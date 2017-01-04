package lt.ltrp.player.description.dialog;


import lt.ltrp.PlayerPlugin
import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.util.StringUtils
import lt.ltrp.constant.NEWS
import net.gtaun.shoebill.common.dialog.AbstractDialog
import net.gtaun.shoebill.common.dialog.DialogHandler
import net.gtaun.shoebill.common.dialog.InputDialog
import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.EventManager

/**
 * @author Bebras
 *         2016.06.01.
 *
 *         This dialog appends text to a target players description
 */
object PlayerDescriptionInputDialog {
    fun create(player: LtrpPlayer, eventManager: EventManager, parent: AbstractDialog, target: LtrpPlayer): InputDialog {
        return InputDialog.create(player, eventManager)
                .caption { target.charName + " veikëjo apraðymo pildymas" }
                .parentDialog { parent }
                .onCancel(DialogHandler { it.showParentDialog() })
                .buttonOk("Saugoti")
                .buttonCancel("Atgal")
                .message {
                    val builder = StringBuilder()
                    builder.appendln("Áraðykite, kà norite pridëti prie veikëjo apraðymo")
                    builder.appendln("Dabartinis tekstas:")
                    builder.appendln(StringUtils.addLineBreaks(target.description, 60))
                    builder.toString()
                }
                .onClickOk(InputDialog.ClickOkHandler { dialog, text ->
                    target.description += text
                    ResourceManager.get().getPlugin(PlayerPlugin::class.java).update(target)
                    player.sendMessage(Color.NEWS, "Apraðymas sëkmingai papildytas")
                    parent.show()
                })
                .build()
    }
}
/*
public class PlayerDescriptionInputDialog {

    public static InputDialog create(LtrpPlayer player, EventManager eventManager, AbstractDialog parent, LtrpPlayer target) {
        return InputDialog.create(player, eventManager)
                .caption(target.getCharName() + " veikëjo apraðymo pildymas")
                .parentDialog(parent)
                .onClickCancel(AbstractDialog::showParentDialog)
                .buttonOk("Saugoti")
                .buttonCancel("Atgal")
                .line("Áraðykite, kà norite pridëti prie veikëjo apraðymo")
                .line("\nDabartinis tekstas:")
                .line(StringUtils.addLineBreaks(target.getDescription(), 60))
                .onClickOk((d, s) -> {
                    target.setDescription(target.getDescription() + s);
                    PlayerController.instance.update(target);
                    player.sendMessage(Color.NEWS, "Apraðymas sëkmingai papildytas");
                    if(parent != null) parent.show();
                })
                .build();
    }
}
*/