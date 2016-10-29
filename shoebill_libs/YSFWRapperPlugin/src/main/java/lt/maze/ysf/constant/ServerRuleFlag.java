package lt.maze.ysf.constant;

/**
 * @author Bebras
 *         2016.04.03.
 */
public enum ServerRuleFlag {

    DEBUG(1),
    READONLY(2),
    RULE(4),
    UNREMOVABLE(8);


    private final int value;

    private ServerRuleFlag(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
