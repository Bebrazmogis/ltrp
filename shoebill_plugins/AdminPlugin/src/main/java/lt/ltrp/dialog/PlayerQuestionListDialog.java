package lt.ltrp.dialog;

import lt.ltrp.data.PlayerQuestion;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.PageListDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.25.
 */
public class PlayerQuestionListDialog extends PageListDialog {

    private Collection<PlayerQuestion> questions;

    public PlayerQuestionListDialog(Player player, EventManager eventManager, Collection<PlayerQuestion> questions) {
        super(player, eventManager);
        this.questions = questions;
        setCaption("Þaidëjø klausimai(" + questions.size() + ")");
        setButtonOk("Pasirinkti");
        setButtonCancel("Uþdaryti");
    }

    @Override
    public void show() {
        items.clear();

        for(PlayerQuestion question : questions) {
            items.add(
                    ListDialogItem.create()
                            .data(question)
                            .itemText(String.format("[%c]%s: %s", (question.isAnswered() ? '+' : '-'), question.getPlayer().getName(), question.getQuestion()))
                            .onSelect(i -> PlayerQuestionMsgBoxDialog.create(player, eventManagerNode.getParent(), this, question).show())
                            .build()
            );
        }

        super.show();
    }
}
