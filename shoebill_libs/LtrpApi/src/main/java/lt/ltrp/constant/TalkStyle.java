package lt.ltrp.constant;

import lt.ltrp.data.Animation;

/**
 * @author Bebras
 *         2016.06.13.
 */
public enum TalkStyle {

    CHAT1(1, new Animation("PED", "IDLE_CHAT", 4.1f, true, true, true, false, true, 0, true)),
    CHAT2(2, new Animation("MISC", "IDLE_CHAT_02", 4.1f, true, true, true, false, true, 0, true)),
    GANG1(3, new Animation("GHANDS", "GSIGN1", 4.1f, true, true, true, false, true, 0, true)),
    GANG2(4, new Animation("GANGS", "PRTIAL_GNGTLKD", 4.1f, true, true, true, false, true, 0, true)),
    GANG3(5, new Animation("GANGS", "PRTIAL_GNGTLKH", 4.1f, true, true, true, false, true, 0, true));

    Animation animation;
    int id;

    TalkStyle(int id, Animation animation) {
        this.animation = animation;
        this.id = id;
    }

    public Animation getAnimation() {
        return animation;
    }

    public int getId() {
        return id;
    }

    public static TalkStyle getById(int id) {
        for(TalkStyle w : values()) {
            if(w.getId() == id)
                return w;
        }
        return null;
    }
}
