package lt.maze.constant;

import lt.maze.Constants;

/**
 * @author Bebras
 *         2016.02.16.
 */
public enum StreamerType {


    Object(Constants.STREAMER_TYPE_OBJECT),
    Pickup(Constants.STREAMER_TYPE_PICKUP),
    Checkpoint(Constants.STREAMER_TYPE_CP),
    RaceCheckpoint(Constants.STREAMER_TYPE_RACE_CP),
    MapIcon(Constants.STREAMER_TYPE_MAP_ICON),
    Label(Constants.STREAMER_TYPE_3D_TEXT_LABEL),
    Area(Constants.STREAMER_TYPE_AREA);

    static StreamerType get(int val) {
        for(StreamerType type : values()) {
            if(type.getValue() == val) {
                return type;
            }
        }
        return null;
    }

    int value;

    StreamerType(int val) {
        this.value = val;
    }

    public int getValue() {
        return value;
    }


}
