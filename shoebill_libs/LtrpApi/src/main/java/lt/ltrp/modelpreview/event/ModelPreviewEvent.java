package lt.ltrp.modelpreview.event;

import lt.ltrp.modelpreview.ModelPreview;
import net.gtaun.shoebill.entities.Player;
import net.gtaun.shoebill.event.player.PlayerEvent;


/**
 * @author Bebras
 *         2015.12.30.
 */
public class ModelPreviewEvent extends PlayerEvent {

    private ModelPreview preview;

    protected ModelPreviewEvent(Player player, ModelPreview preview) {
        super(player);
        this.preview = preview;
    }

    public ModelPreview getPreview() {
        return preview;
    }

}
