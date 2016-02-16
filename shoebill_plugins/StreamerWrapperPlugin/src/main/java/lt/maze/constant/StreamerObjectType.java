package lt.maze.constant;

import lt.maze.Constants;

/**
 * @author Bebras
 *         2016.02.16.
 */
public enum  StreamerObjectType {

    Global(Constants.STREAMER_OBJECT_TYPE_GLOBAL),
    Player(Constants.STREAMER_OBJECT_TYPE_PLAYER),
    Dynamic(Constants.STREAMER_OBJECT_TYPE_DYNAMIC);

    static StreamerObjectType get(int id) {
        for(StreamerObjectType type : StreamerObjectType.values()) {
            if(type.getId() == id) {
                return type;
            }
        }
        return null;
    }

    int id;

    StreamerObjectType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
