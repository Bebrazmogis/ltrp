package lt.ltrp.constant;

import lt.ltrp.data.Animation;

/**
 * @author Bebras
 *         2016.06.13.
 */
public enum TalkStyle {

    CHAT1(new Animation("PED", "IDLE_CHAT", 4.1f, true, true, true, false, true, 0, true)),
    CHAT2(new Animation("MISC", "IDLE_CHAT_02", 4.1f, true, true, true, false, true, 0, true)),
    GANG1(new Animation("GHANDS", "GSIGN1", 4.1f, true, true, true, false, true, 0, true)),
    GANG2(new Animation("GANGS", "PRTIAL_GNGTLKD", 4.1f, true, true, true, false, true, 0, true)),
    GANG3(new Animation("GANGS", "PRTIAL_GNGTLKH", 4.1f, true, true, true, false, true, 0, true));

    Animation animation;

    TalkStyle(Animation animation) {
        this.animation = animation;
    }

    public Animation getAnimation() {
        return animation;
    }
}
