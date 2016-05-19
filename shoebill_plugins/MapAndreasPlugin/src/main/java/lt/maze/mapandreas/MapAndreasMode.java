package lt.maze.mapandreas;

/**
 * @author Bebras
 *         2015.12.03.
 */
public enum MapAndreasMode {

/*
    #define MAP_ANDREAS_MODE_NONE			0
    #define MAP_ANDREAS_MODE_MINIMAL		1
    #define MAP_ANDREAS_MODE_MEDIUM			2	// currently unused
    #define MAP_ANDREAS_MODE_FULL			3
    #define MAP_ANDREAS_MODE_NOBUFFER		4
    */

    None(0),
    Minimal(1),
    Medium(2),
    Full(3),
    NoBuffer(4);


    private int value;

    MapAndreasMode(int val) {
        this.value = val;
    }

    public int getValue() {
        return value;
    }

}
