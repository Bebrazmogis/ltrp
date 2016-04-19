package lt.ltrp.dmv.dialog;

import lt.ltrp.dmv.QuestionTest;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.02.10.
 */
public class QuestionTestEndMsgDialog {

    public static MsgboxDialog create(LtrpPlayer p, EventManager eventManager, QuestionTest test) {
        return MsgboxDialog.create(p, eventManager)
                .caption("Testo pabaiga!")
                .buttonOk("Gerai")
                .message("Testas " + (test.isPassed() ? "Iðlaikytas" : "Neiðlaikytas") +
                    "\nKlausimø skaièius: " + test.getQuestionCount() +
                    "\nTeisingai atsakyta: " + test.getCorrectAnswerCount())
                .build();
    }

}
