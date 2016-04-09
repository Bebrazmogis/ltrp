package lt.ltrp.item.drug;

import lt.ltrp.data.Color;
import lt.ltrp.player.object.LtrpPlayer;
import lt.maze.fader.FaderPlugin;

/**
 * @author Bebras
 *         2016.04.05.
 */
public class FadeEffect implements DetoxEffect {

    private int seconds;
    private LtrpPlayer player;
    private boolean finished;

    public FadeEffect(LtrpPlayer player,int seconds) {
        this.seconds = seconds;
        this.player = player;
    }

    @Override
    public void start() {
        FaderPlugin.fadeColorForPlayer(player, new Color(0, 0, 0, 0), Color.BLACK, (seconds*1000) / FaderPlugin.getFrameRate(), true);
        finished = true;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void destroy() {

    }
}
