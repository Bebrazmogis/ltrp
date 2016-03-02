package lt.maze.streamer.data;


import net.gtaun.shoebill.data.Color;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class DynamicObjectMaterial {

    private int modelId;
    private String txdName, textureName;
    private Color color;

    public DynamicObjectMaterial(int modelId, String txdName, String textureName, Color color) {
        this.modelId = modelId;
        this.txdName = txdName;
        this.textureName = textureName;
        this.color = color;
        if(this.color == null) {
            this.color = new Color(0);
        }
    }

    public int getModelId() {
        return modelId;
    }

    public String getTxdName() {
        return txdName;
    }

    public String getTextureName() {
        return textureName;
    }

    public Color getColor() {
        return color;
    }
}
