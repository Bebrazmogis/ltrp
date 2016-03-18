package lt.ltrp.modelpreview.event;

import lt.ltrp.modelpreview.ModelPreview;
import lt.ltrp.player.LtrpPlayer;

/**
 * @author Bebras
 *         2015.12.30.
 */
public class ModelPreviewSelectModelEvent extends ModelPreviewEvent {

    private int model;

    public ModelPreviewSelectModelEvent(LtrpPlayer player, ModelPreview preview, int model) {
        super(player, preview);
        this.model = model;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }
}
