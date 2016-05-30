package lt.ltrp.object;

import lt.ltrp.GraffitiPlugin;
import lt.ltrp.data.GraffitiColor;
import lt.ltrp.data.GraffitiFont;
import lt.ltrp.data.GraffitiObject;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Destroyable;

import java.sql.Timestamp;

/**
 * @author Bebras
 *         2016.05.30.
 */
public interface Graffiti extends Destroyable, Entity {


    int MAX_NAME_LENGTH = 16;

    void edit(LtrpPlayer player);
    GraffitiColor getColor();
    void setColor(GraffitiColor color);

    GraffitiFont getFont();
    void setFont(GraffitiFont font);

    String getText();
    void setText(String text);

    Vector3D getPosition();

    static Graffiti create(int uuid, int authorUserId, String text, GraffitiObject objectType, Vector3D position, Vector3D rotation, GraffitiFont font, GraffitiColor color, int approvedByUserId, Timestamp createdAt) {
        return GraffitiPlugin.get(GraffitiPlugin.class).createGraffiti(uuid, authorUserId, text, objectType, position, rotation, font, color, approvedByUserId, createdAt);
    }

    static Graffiti create(int authorUserId, String text, GraffitiObject objectType, Vector3D position, Vector3D rotation, GraffitiFont font, GraffitiColor color, int approvedByUserId, Timestamp createdAt) {
        return create(0, authorUserId, text, objectType, position, rotation, font, color, approvedByUserId, createdAt);
    }

    static Graffiti get(int uuid) {
        return GraffitiPlugin.get(GraffitiPlugin.class).getGraffiti(uuid);
    }

    int getApprovedByUserId();

    Vector3D getRotation();

    GraffitiObject getObjectType();

    int getAuthorUserId();

    Timestamp getCreatedAt();
}
