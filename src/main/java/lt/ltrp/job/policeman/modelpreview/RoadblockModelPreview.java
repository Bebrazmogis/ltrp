package lt.ltrp.job.policeman.modelpreview;

import lt.ltrp.Initializable;
import lt.ltrp.ModelPreview;
import lt.ltrp.data.Color;
import lt.ltrp.event.modelpreview.ModelPreviewCancelEvent;
import lt.ltrp.event.modelpreview.ModelPreviewSelectModelEvent;
import lt.ltrp.event.modelpreview.ModelPreviewSelectModelHandler;
import lt.ltrp.job.policeman.Roadblock;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.TextDrawFont;
import net.gtaun.shoebill.event.player.PlayerClickTextDrawEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Textdraw;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class RoadblockModelPreview implements Initializable, ModelPreview {

    private static final RoadblockModelPreview instance = new RoadblockModelPreview();

    private final Color backgroundColor = new Color(255, 255 ,255, 100);
    private final int[] models = new int[] {
            978, 981, 1228, 1423, 1251, 8548, 2599, 1238, 19979, 19978, 19384, 1425
    };

    private Textdraw background;
    private Textdraw[] modelTextdraws;
    private Textdraw exitButton;
    private Map<LtrpPlayer, ModelPreviewSelectModelHandler> shownToPlayersHandlers;
    private EventManager eventManager;
    private boolean initialized;
    private List<HandlerEntry> handlers;



    public static RoadblockModelPreview get() {
        return instance;
    }

    private RoadblockModelPreview() {

    }

    @Override
    public void initialize(EventManager eventManager) {
        this.eventManager = eventManager;
        shownToPlayersHandlers = new HashMap<>();
        handlers = new ArrayList<>();

        background = Textdraw.create(75f, 150f, " ");
        background.setUseBox(true);
        background.setLetterSize(5f, 5f);
        background.setBoxColor(backgroundColor);
        background.setColor(new Color(0, 0, 0, 255));
        background.setTextSize(550f, 180f);
        background.setBackgroundColor(backgroundColor);

        float x = 80.0f;
        float y = 155.0f;
        int lineCount = 0, totalCount = 0;
        modelTextdraws = new Textdraw[models.length];
        for(int model : models) {
            if(lineCount++ == 3) {
                lineCount = 0;
                x = 80.0f;
                y += 52f;
            }
            modelTextdraws[totalCount] = Textdraw.create(x, y, " ");
            modelTextdraws[totalCount].setPreviewModel(model);
            modelTextdraws[totalCount].setFont(TextDrawFont.MODEL_PREVIEW);
            modelTextdraws[totalCount].setTextSize(50f, 50f);
            x += 52f;
            totalCount++;
        }

        exitButton = Textdraw.create(540f, 140f, "Exit");
        exitButton.setTextSize(10f, 10f);

        handlers.add(eventManager.registerHandler(PlayerDisconnectEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if (player != null && shownToPlayersHandlers.containsKey(player)) {
                hide(player);
            }
        }));

        handlers.add(eventManager.registerHandler(PlayerClickTextDrawEvent.class, e -> {
            LtrpPlayer p = LtrpPlayer.get(e.getPlayer());
            if(shownToPlayersHandlers.containsKey(p)) {
                if (e.getTextdraw().equals(exitButton)) {
                    hide(p);
                    eventManager.dispatchEvent(new ModelPreviewCancelEvent(p, this));
                } else {
                    for (int i = 0; i < modelTextdraws.length; i++) {
                        if (modelTextdraws[i].equals(e.getTextdraw())) {
                            if(shownToPlayersHandlers.get(p) != null) {
                                shownToPlayersHandlers.get(p).handle(this, models[i]);
                            }
                            eventManager.dispatchEvent(new ModelPreviewSelectModelEvent(p, this, models[i]));
                        }
                    }
                }
            }
        }));

        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void uninitialize() {
        handlers.forEach(HandlerEntry::cancel);
        initialized = false;
    }


    @Override
    public void show(LtrpPlayer player) {
        if(isShown(player)) {
            hide(player);
        }
        background.show(player);
        exitButton.show(player);
        for(Textdraw t : modelTextdraws) {
            t.show(player);
        }
        player.selectTextDraw(Color.WHITE);
        shownToPlayersHandlers.put(player, null);
    }

    public void show(LtrpPlayer player, ModelPreviewSelectModelHandler handler) {
        this.show(player);
        this.shownToPlayersHandlers.put(player, handler);
    }

    @Override
    public void hide(LtrpPlayer player) {
        background.hide(player);
        exitButton.hide(player);
        for(Textdraw t : modelTextdraws) {
            t.hide(player);
        }
        player.cancelSelectTextDraw();
    }

    @Override
    public boolean isShown(LtrpPlayer player) {
        return initialized && shownToPlayersHandlers.containsKey(player);
    }


}
