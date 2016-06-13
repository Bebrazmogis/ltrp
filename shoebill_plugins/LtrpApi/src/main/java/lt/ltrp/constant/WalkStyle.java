package lt.ltrp.constant;

import lt.ltrp.data.Animation;

/**
 * @author Bebras
 *         2016.06.13.
 */
public enum WalkStyle {


    GANG1(1, new Animation("PED", "WALK_GANG1", 4.1f, true, true, true, false, true, 0, true)),
    GANG2(2, new Animation("PED", "WALK_GANG2", 4.1f, true, true, true, false, true, 0, true)),
    FAT(3, new Animation("PED", "WALK_FATWALK", 4.1f, true, true, true, false, true, 0, true)),
    WUZI(4, new Animation("PED", "WUZI_WALK", 4.1f, true, true, true, false, true, 0, true)),
    WUZI2(5, new Animation("PED", "WALK_WUZI", 4.1f, true, true, true, false, true, 0, true)),
    PLAYER(6, new Animation("PED", "WALK_PLAYER", 4.1f, true, true, true, false, true, 0, true)),
    WOMAN_NORMAL(7, new Animation("PED", "WOMAN_WALKNORM", 4.1f, true, true, true, false, true, 0, true)),
    WOMAN_PRO(8, new Animation("PED", "WOMAN_WALKPRO", 4.1f, true, true, true, false, true, 0, true));

    int id;
    Animation animation;

    WalkStyle(int id, Animation animation) {
        this.animation = animation;
        this.id = id;
    }

    public Animation getAnimation() {
        return animation;
    }

    public int getId() {
        return id;
    }

    static WalkStyle getById(int id) {
        for(WalkStyle w : values()) {
            if(w.getId() == id)
                return w;
        }
        return null;
    }
}
