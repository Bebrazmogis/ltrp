package lt.maze.ysf.data;

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.entities.SampObject;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class ObjectMaterial {

    private SampObject object;
    private int materialIndex;
    private int modelId;
    private String txdName;
    private String textureName;
    private Color color;

    public ObjectMaterial(SampObject object, int materialIndex, int modelId, String txdName, String textureName, Color color) {
        this.object = object;
        this.materialIndex = materialIndex;
        this.modelId = modelId;
        this.txdName = txdName;
        this.textureName = textureName;
        this.color = color;
    }

    public SampObject getObject() {
        return object;
    }

    public void setObject(SampObject object) {
        this.object = object;
    }

    public int getMaterialIndex() {
        return materialIndex;
    }

    public void setMaterialIndex(int materialIndex) {
        this.materialIndex = materialIndex;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public String getTxdName() {
        return txdName;
    }

    public void setTxdName(String txdName) {
        this.txdName = txdName;
    }

    public String getTextureName() {
        return textureName;
    }

    public void setTextureName(String textureName) {
        this.textureName = textureName;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
