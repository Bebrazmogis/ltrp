package lt.ltrp.data;

import lt.ltrp.object.ItemPhone;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PhoneCall {


    private LtrpPlayer caller, answeredBy;
    private ItemPhone callerPhone;
    private ItemPhone recipientPhone;
    private int contactNumber;
    private PhoneCallState state;
    private int answerTimestamp;

    public PhoneCall(LtrpPlayer caller, ItemPhone callerphone, ItemPhone recipientphone) {
        this.caller = caller;
        this.callerPhone = callerphone;
        this.recipientPhone = recipientphone;
        this.state = PhoneCallState.Ringing;
    }

    public PhoneCall(LtrpPlayer caller, ItemPhone callerPhone, int contactNumber) {
        this.caller = caller;
        this.callerPhone = callerPhone;
        this.contactNumber = contactNumber;
        this.state = PhoneCallState.Ringing;
    }

    public LtrpPlayer getAnsweredBy() {
        return answeredBy;
    }

    public void setAnsweredBy(LtrpPlayer answeredBy) {
        this.answeredBy = answeredBy;
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

    public PhoneCallState getState() {
        return state;
    }

    public void setState(PhoneCallState state) {
        this.state = state;
    }

    public int getAnswerTimestamp() {
        return answerTimestamp;
    }

    public void setAnswerTimestamp(int answerTimestamp) {
        this.answerTimestamp = answerTimestamp;
    }

    public enum PhoneCallState {
        Ringing,
        Talking,
        Ended
    }

}
