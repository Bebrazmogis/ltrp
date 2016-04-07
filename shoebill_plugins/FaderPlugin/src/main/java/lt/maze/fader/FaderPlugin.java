package lt.maze.fader;

import lt.maze.fader.event.PlayerFadeComplete;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Textdraw;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManagerNode;
import org.slf4j.Logger;

import java.util.HashMap;

/**
 * @author Bebras
 *         2016.04.05.
 */
public class FaderPlugin extends Plugin {

    private static Logger logger;
    private static FaderPlugin instance;

    private Textdraw faderTextdraw;
    private Timer faderTimer;
    private HashMap<Player, PlayerFade> playerFadeData;
    private int framerate = 100;

    @Override
    protected void onEnable() throws Throwable {
        logger = getLogger();
        instance = this;
        this.playerFadeData = new HashMap<>();

        faderTextdraw = Textdraw.create(0f, 0f, "~r~");
        faderTextdraw.setTextSize(640f, 480f);
        faderTextdraw.setLetterSize(0f, 50f);
        faderTextdraw.setUseBox(true);

        faderTimer = Timer.create(framerate, i -> {
            playerFadeData.keySet().forEach(p -> {
                PlayerFade fade = playerFadeData.get(p);
                fade.setFramesLeft(fade.getFramesLeft()-1);
                if(fade.isFadeBack() && fade.getFramesTotal() / 2 > fade.getFramesLeft())
                    fade.fadeOut();
                else
                    fade.fadeIn();

                if(fade.getFramesLeft() == 0) {
                    faderTextdraw.hide(p);
                    getEventManager().dispatchEvent(new PlayerFadeComplete(p));
                } else {
                    faderTextdraw.setBoxColor(fade.getColor());
                    faderTextdraw.show(p);
                }
            });
        });
        faderTimer.start();

        logger.info("Logger plugin initialized");
    }


    @Override
    protected void onDisable() throws Throwable {
        if(faderTimer != null)
            faderTimer.destroy();
        playerFadeData.keySet().forEach(faderTextdraw::hide);
        playerFadeData.clear();
        faderTextdraw.destroy();
        logger.info("FaderPlugin shutting down");
    }

    /**
     * Starts a fading for player, the time it will take is @param frames * {@link lt.maze.fader.FaderPlugin#framerate}
     * @param p player fade screen for
     * @param startr starting red color
     * @param startg starting green color
     * @param startb starting blue color
     * @param starta starting alpha(opacity)
     * @param endr end red color
     * @param endg end green color
     * @param endb end blue color
     * @param enda end alpha(opacity)
     * @param frames frames it will take to fade
     * @param fadeback if set to true, it will fade back to normal
     * @return returns the time in milli seconds this fading will take
     */
    public static int fadeColorForPlayer(Player p, int startr, int startg, int startb, int starta, int endr, int endg, int endb, int enda, int frames, boolean fadeback) {
        if(instance.playerFadeData.containsKey(p)) {
            instance.playerFadeData.remove(p);
            instance.faderTextdraw.hide(p);
        }
        PlayerFade f;
        if(fadeback) {
            if(frames % 2 != 0)
                frames++;
            f = new PlayerFade(startr, startg, startb, starta,
                    (endr - startr) / frames * 2,
                    (endg - startg) / frames * 2,
                    (endb - startb) / frames * 2,
                    (enda - starta) / frames * 2,
                    frames,
                    fadeback);
        } else {
            f = new PlayerFade(startr, startg, startb, starta,
                    (endr - startr) / frames,
                    (endg - startg) / frames,
                    (endb - startb) / frames,
                    (enda - starta) / frames,
                    frames,
                    fadeback);
        }
        instance.playerFadeData.put(p, f);
        return frames * instance.framerate;
    }

    public static int fadeColorForPlayer(Player p, Color startColor, Color endColor, int frames, boolean fadeback) {
        return fadeColorForPlayer(p, startColor.getR(), startColor.getG(), startColor.getB(), startColor.getA(), endColor.getR(), endColor.getG(), endColor.getB(), endColor.getA(),
                frames, fadeback);
    }

    public static int getFrameRate() {
        return instance == null ? -1 : instance.framerate;
    }

    public static void setFrameRate(int framerate) {
        if(instance != null)
            instance.framerate = framerate;
    }
}
