package lt.ltrp.data;

import lt.ltrp.object.Entity;
import net.gtaun.shoebill.constant.ObjectMaterialSize;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class GraffitiObject implements Entity {

    private int uuid;
    private int modelId;
    private ObjectMaterialSize materialSize;

    public GraffitiObject(int uuid, int modelId, ObjectMaterialSize materialSize) {
        this.uuid = uuid;
        this.modelId = modelId;
        this.materialSize = materialSize;
    }

    public GraffitiObject() {
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public ObjectMaterialSize getMaterialSize() {
        return materialSize;
    }

    public void setMaterialSize(ObjectMaterialSize materialSize) {
        this.materialSize = materialSize;
    }

    @Override
    public void setUUID(int i) {
        this.uuid = i;
    }

    @Override
    public int getUUID() {
        return uuid;
    }
}
