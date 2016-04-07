package lt.maze.ysf.constant;

/**
 * @author Bebras
 *         2016.04.03.
 */
public enum YSFObjectMaterialSlotUse {

    NONE(0),
    Material(1),
    MaterialText(2);

    public static YSFObjectMaterialSlotUse get(int value) {
        for(YSFObjectMaterialSlotUse o : values()) {
            if(o.value == value) {
                return o;
            }
        }
        return null;
    }

    private final int value;

    private YSFObjectMaterialSlotUse(int val) {
        this.value = val;
    }

}
