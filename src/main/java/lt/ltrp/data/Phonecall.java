package lt.ltrp.data;

import lt.ltrp.item.ItemPhone;
import lt.ltrp.player.LtrpPlayer;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class Phonecall {


    private LtrpPlayer caller;
    private ItemPhone callerPhone;
    private ItemPhone recipientPhone;
    private PhonecallState state;
    private int answerTimestamp;

    public Phonecall(LtrpPlayer caller, ItemPhone callerphone, ItemPhone recipient) {
        this.caller = caller;
        this.callerPhone = callerphone;
        this.recipientPhone = recipient;
        this.state = PhonecallState.Ringing;
    }

    public ItemPhone getCallerPhone() {
        return callerPhone;
    }

    public void setCallerPhone(ItemPhone callerPhone) {
        this.callerPhone = callerPhone;
    }

    public LtrpPlayer getCaller() {
        return caller;
    }

    public void setCaller(LtrpPlayer caller) {
        this.caller = caller;
    }

    public ItemPhone getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(ItemPhone recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public PhonecallState getState() {
        return state;
    }

    public void setState(PhonecallState state) {
        this.state = state;
    }

    public int getAnswerTimestamp() {
        return answerTimestamp;
    }

    public void setAnswerTimestamp(int answerTimestamp) {
        this.answerTimestamp = answerTimestamp;
    }

    public enum PhonecallState {
        Ringing,
        Talking,
    }

}
