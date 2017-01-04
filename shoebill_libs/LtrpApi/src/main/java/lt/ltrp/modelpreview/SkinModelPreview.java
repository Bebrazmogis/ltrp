package lt.ltrp.modelpreview;


import lt.ltrp.util.Skin;
import net.gtaun.shoebill.entities.Player;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.03.05.
 */
public class SkinModelPreview  {

    private SkinModelPreview() {

    }


    public static ModelPreview create(Player player, EventManager eventManager, BasicModelPreview.SelectModelHandler handler) {
        Collection<Integer> models = new ArrayList<>();
        for(int skinId : Skin.SKIN_IDS)
            models.add(skinId);
        return BasicModelPreview.create(player, eventManager)
                .onSelectModel(handler)
                .models(models)
                .build();
    }


}
