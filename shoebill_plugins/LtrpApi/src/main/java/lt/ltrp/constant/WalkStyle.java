package lt.ltrp.constant;

import lt.ltrp.data.Animation;

/**
 * @author Bebras
 *         2016.06.13.
 */
public enum WalkStyle {

    GANG1(new Animation("PED", "WALK_GANG1", 4.1f, true, true, true, false, true, 0, true)),
    GANG2(new Animation("PED", "WALK_GANG2", 4.1f, true, true, true, false, true, 0, true)),
    FAT(new Animation("PED", "WALK_FATWALK", 4.1f, true, true, true, false, true, 0, true)),
    WUZI(new Animation("PED", "WUZI_WALK", 4.1f, true, true, true, false, true, 0, true)),
    WUZI2(new Animation("PED", "WALK_WUZI", 4.1f, true, true, true, false, true, 0, true)),
    PLAYER(new Animation("PED", "WALK_PLAYER", 4.1f, true, true, true, false, true, 0, true)),
    WOMAN_NORMAL(new Animation("PED", "WOMAN_WALKNORM", 4.1f, true, true, true, false, true, 0, true)),
    WOMAN_PRO(new Animation("PED", "WOMAN_WALKPRO", 4.1f, true, true, true, false, true, 0, true));

    Animation animation;

    WalkStyle(Animation animation) {
        this.animation = animation;
    }

    public Animation getAnimation() {
        return animation;
    }
}
