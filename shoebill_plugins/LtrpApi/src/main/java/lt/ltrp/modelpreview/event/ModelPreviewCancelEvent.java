package lt.ltrp.modelpreview.event;

import lt.ltrp.modelpreview.ModelPreview;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2015.12.30.
 */
public class ModelPreviewCancelEvent extends ModelPreviewEvent {


    public ModelPreviewCancelEvent(LtrpPlayer player, ModelPreview preview) {
        super(player, preview);
    }
}
