package lt.maze.streamer.constant;

import lt.maze.streamer.Constants;

/**
 * @author Bebras
 *         2016.02.16.
 */
public enum StreamerAreaType {

    Circle(Constants.STREAMER_AREA_TYPE_CIRCLE),
    Cylinder(Constants.STREAMER_AREA_TYPE_CYLINDER),
    Sphere(Constants.STREAMER_AREA_TYPE_SPHERE),
    Rectangle(Constants.STREAMER_AREA_TYPE_RECTANGLE),
    Cuboid(Constants.STREAMER_AREA_TYPE_CUBOID),
    Polygon(Constants.STREAMER_AREA_TYPE_POLYGON);

    static StreamerAreaType get(int id) {
        for(StreamerAreaType type : values()) {
            if(type.id == id) {
                return type;
            }
        }
        return null;
    }

    int id;

    StreamerAreaType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


}
