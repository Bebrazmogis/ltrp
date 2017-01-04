package lt.maze.ysf.object.impl;

import lt.maze.ysf.Functions;
import lt.maze.ysf.constant.YSFObjectMaterialSlotUse;
import lt.maze.ysf.data.ObjectMaterial;
import lt.maze.ysf.data.ObjectMaterialText;
import lt.maze.ysf.object.YSFObject;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.amx.types.ReferenceInt;
import net.gtaun.shoebill.amx.types.ReferenceString;
import net.gtaun.shoebill.constant.ObjectMaterialSize;
import net.gtaun.shoebill.constant.ObjectMaterialTextAlign;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.entities.Player;
import net.gtaun.shoebill.entities.SampObject;
import net.gtaun.shoebill.entities.Vehicle;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class YSFObjectImpl extends YSFObject {

    private SampObject object;
    private Vector3D attachedOffset;
    private Vector3D attachedRotation;

    public YSFObjectImpl(SampObject object) {
        this.object = object;
    }


    //native GetObjectAttachedOffset(objectid, &Float:fX, &Float:fY, &Float:fZ, &Float:fRotX, &Float:fRotY, &Float:fRotZ);
    private void getAttachedOffsetData() {
        ReferenceFloat x = new ReferenceFloat(0f);
        ReferenceFloat y = new ReferenceFloat(0f);
        ReferenceFloat z = new ReferenceFloat(0f);
        ReferenceFloat rx = new ReferenceFloat(0f);
        ReferenceFloat ry= new ReferenceFloat(0f);
        ReferenceFloat rz = new ReferenceFloat(0f);
        Functions.GetObjectAttachedOffset(object.getId(), x, y, z, rx, ry, rz);
        attachedOffset = new Vector3D(x.getValue(), y.getValue(), z.getValue());
        attachedRotation = new Vector3D(rx.getValue(), ry.getValue(), rz.getValue());
    }

    @Override
    public int getId() {
        return object.getId();
    }

    @Override
    public int getModelId() {
        return object.getModelId();
    }

    @Override
    public float getSpeed() {
        return object.getSpeed();
    }

    @Override
    public float getDrawDistance() {
        return object.getDrawDistance();
    }

    @Override
    public void setMoveSpeed(float speed) {
        Functions.SetObjectMoveSpeed(object.getId(), speed);
    }

    @Override
    public float getMoveSpeed() {
        return Functions.GetObjectMoveSpeed(object.getId());
    }

    @Override
    public Vehicle getAttachedVehicle() {
        return object.getAttachedVehicle();
    }

    @Override
    public Location getLocation() {
        return object.getLocation();
    }

    @Override
    public void setLocation(Location location) {
        object.setLocation(location);
    }

    @Override
    public Vector3D getRotation() {
        return object.getRotation();
    }


    @Override
    public void setRotation(Vector3D vector3D) {
        object.setRotation(vector3D);
    }

    @Override
    public boolean isMoving() {
        return object.isMoving();
    }

    @Override
    public int move(float v, float v1, float v2, float v3) {
        return object.move(v, v1, v2, v3);
    }

    @Override
    public int move(float v, float v1, float v2, float v3, float v4, float v5, float v6) {
        return object.move(v, v1, v2, v3, v4, v5,v6);
    }

    @Override
    public int move(Vector3D vector3D, float v) {
        return object.move(vector3D, v);
    }

    @Override
    public int move(Vector3D vector3D, float v, Vector3D vector3D1) {
        return object.move(vector3D, v, vector3D1);
    }

    @Override
    public void stop() {
        object.stop();
    }

    @Override
    public void attach(Player player, float v, float v1, float v2, float v3, float v4, float v5) {
        object.attach(player, v, v1, v2, v3, v4, v5);
    }

    @Override
    public void attach(Player player, Vector3D vector3D, Vector3D vector3D1) {
        object.attach(player, vector3D, vector3D1);
    }

    @Override
    public void attach(SampObject sampObject, float v, float v1, float v2, float v3, float v4, float v5, boolean b) {
        object.attach(sampObject, v, v1, v2, v3, v4, v5, b);
    }

    @Override
    public void attach(SampObject sampObject, Vector3D vector3D, Vector3D vector3D1, boolean b) {
        object.attach(sampObject, vector3D, vector3D1, b);
    }

    @Override
    public void attach(Vehicle vehicle, float v, float v1, float v2, float v3, float v4, float v5) {
        object.attach(vehicle, v, v1, v2, v3, v4, v5);
    }

    @Override
    public void attach(Vehicle vehicle, Vector3D vector3D, Vector3D vector3D1) {
        object.attach(vehicle, vector3D, vector3D1);
    }

    @Override
    public void attachCamera(Player player) {
        object.attachCamera(player);
    }

    @Override
    public void setMaterial(int i, int i1, String s, String s1, Color color) {
        object.setMaterial(i, i1, s, s1, color);
    }

    @Override
    public void setMaterial(int i, int i1, String s, String s1) {
        object.setMaterial(i, i1, s, s1);
    }

    @Override
    public void setMaterialText(String s, int i, ObjectMaterialSize objectMaterialSize, String s1, int i1, boolean b, Color color, Color color1, ObjectMaterialTextAlign objectMaterialTextAlign) {
        object.setMaterialText(s, i, objectMaterialSize, s1, i1, b, color, color1, objectMaterialTextAlign);
    }

    @Override
    public void setMaterialText(String s) {
        object.setMaterialText(s);
    }

    @Override
    public void setNoCameraCol() {
        object.setNoCameraCol();
    }

    @Override
    public Player getAttachedPlayer() {
        return object.getAttachedPlayer();
    }

    @Override
    public SampObject getAttachedObject() {
        return object.getAttachedObject();
    }

    @Override
    public Vector3D getAttachedOffset() {
        getAttachedOffsetData();
        return attachedOffset;
    }

    @Override
    public Vector3D getAttachedRotation() {
        getAttachedOffsetData();
        return attachedRotation;
    }

    //native GetObjectMaterial(objectid, materialindex, &modelid, txdname[], txdnamelen = sizeof(txdname), texturename[], texturenamelen = sizeof(texturename), &materialcoor);
    @Override
    public ObjectMaterial getMaterial(int index) {
        ReferenceInt model = new ReferenceInt(0);
        ReferenceString txd = new ReferenceString("", 255);
        ReferenceString texture = new ReferenceString("", 255);
        ReferenceInt color = new ReferenceInt(0);
        Functions.GetObjectMaterial(object.getId(), index, model, txd, txd.getLength(), texture, texture.getLength(), color);
        return new ObjectMaterial(this, index, model.getValue(), txd.getValue(), texture.getValue(), new Color(color.getValue()));
    }

    //native GetObjectMaterialText(objectid, materialindex, text[], textlen = sizeof(text), &materialsize, fontface[], fontfacelen = sizeof(fontface), &fontsize, &bold, &fontcolor, &backcolor, &textalignment);
    @Override
    public ObjectMaterialText getMaterialText(int index) {
        ReferenceString text = new ReferenceString("", 255);
        ReferenceInt size = new ReferenceInt(0);
        ReferenceString font = new ReferenceString("", 128);
        ReferenceInt fontsize = new ReferenceInt(0);
        ReferenceInt bold = new ReferenceInt(0);
        ReferenceInt fontcolor = new ReferenceInt(0);
        ReferenceInt bgcolor = new ReferenceInt(0);
        ReferenceInt align = new ReferenceInt(0);
        Functions.GetObjectMaterialText(object.getId(), index, text, text.getLength(), size, font, font.getLength(), fontsize, bold, fontcolor, bgcolor, align);
        return new ObjectMaterialText(this, index, text.getValue(),
                ObjectMaterialSize.get(size.getValue()), font.getValue(),
                fontsize.getValue(), bold.getValue() != 0, new Color(fontcolor.getValue()),
                new Color(bgcolor.getValue()), ObjectMaterialTextAlign.get(align.getValue()));
    }

    @Override
    public boolean isNoCameraCol() {
        return Functions.IsObjectNoCameraCol(object.getId()) != 0;
    }

    @Override
    public YSFObjectMaterialSlotUse isMaterialSlotUsed(int materialIndex) {
        return YSFObjectMaterialSlotUse.get(Functions.IsObjectMaterialSlotUsed(object.getId(), materialIndex));
    }

    @Override
    public void destroy() {
        object.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return object.isDestroyed();
    }
}
