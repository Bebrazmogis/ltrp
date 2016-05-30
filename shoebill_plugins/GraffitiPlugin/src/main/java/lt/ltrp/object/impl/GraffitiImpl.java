package lt.ltrp.object.impl;

import lt.ltrp.data.GraffitiColor;
import lt.ltrp.data.GraffitiFont;
import lt.ltrp.data.GraffitiObject;
import lt.ltrp.event.GraffitiDestroyEvent;
import lt.ltrp.object.Graffiti;
import lt.ltrp.object.LtrpPlayer;
import lt.maze.streamer.data.DynamicObjectMaterialText;
import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.constant.ObjectMaterialSize;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.util.event.EventManager;

import java.sql.Timestamp;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class GraffitiImpl implements Graffiti {

    private int uuid;
    private int authorUserId;
    private String text;
    private GraffitiObject objectType;
    private GraffitiFont font;
    private GraffitiColor color;
    private int approvedByUserId;
    private Timestamp createdAt;

    private boolean destroyed;
    private DynamicObject object;
    private EventManager eventManager;


    public GraffitiImpl(int uuid, int authorUserId, String text, GraffitiObject objectType, Vector3D position, Vector3D rotation, GraffitiFont font, GraffitiColor color, int approvedByUserId, Timestamp createdAt, EventManager eventManager) {
        this.uuid = uuid;
        this.authorUserId = authorUserId;
        this.text = text;
        this.objectType = objectType;
        this.font = font;
        this.color = color;
        this.approvedByUserId = approvedByUserId;
        this.createdAt = createdAt;
        this.object = DynamicObject.create(objectType.getModelId(), position, rotation);
        this.object.setMaterialText(0, new DynamicObjectMaterialText(text, objectType.getMaterialSize(), font.getName(), font.getSize(), 1, false, color.getColor(), new Color(0x000000)));
        this.eventManager = eventManager;
        for(int i = 1; i < 4; i++)
            this.object.setMaterialText(0, new DynamicObjectMaterialText("", ObjectMaterialSize.SIZE_128x128, "", 1, 0, false, new Color(0x00000000), new Color(0x00000000)));
    }


    public void edit(LtrpPlayer player) {
        this.object.edit(player);
    }

    @Override
    public void setUUID(int i) {
        this.uuid = i;
    }

    @Override
    public int getUUID() {
        return uuid;
    }


    public int getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(int authorUserId) {
        this.authorUserId = authorUserId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public GraffitiObject getObjectType() {
        return objectType;
    }

    public void setObjectType(GraffitiObject objectType) {
        this.objectType = objectType;
    }

    public Vector3D getPosition() {
        return object.getPosition();
    }

    public void setPosition(Vector3D position) {
        this.object.setPosition(position);
    }

    public Vector3D getRotation() {
        return object.getRotation();
    }

    public void setRotation(Vector3D rotation) {
        this.object.setRotation(rotation);
    }

    public GraffitiFont getFont() {
        return font;
    }

    public void setFont(GraffitiFont font) {
        this.font = font;
    }

    public GraffitiColor getColor() {
        return color;
    }

    public void setColor(GraffitiColor color) {
        this.color = color;
    }

    public int getApprovedByUserId() {
        return approvedByUserId;
    }

    public void setApprovedByUserId(int approvedByUserId) {
        this.approvedByUserId = approvedByUserId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GraffitiImpl && ((GraffitiImpl) obj).getUUID() == getUUID();
    }

    @Override
    public String toString() {
        return String.format("%s[author=%d,object=%s, text=%s, position=%s, rotation=%s, font=%s, color=%s, approved_by=%d",
                getClass().getName(), getAuthorUserId(), getObjectType(), getText(), getPosition(), getRotation(), getFont(), getColor(), getApprovedByUserId());
    }

    @Override
    public void destroy() {
        destroyed = true;
        this.object.destroy();
        eventManager.dispatchEvent(new GraffitiDestroyEvent(this));
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    protected void finalize() throws Throwable {
        if(!isDestroyed())
            destroy();
        super.finalize();
    }
}
