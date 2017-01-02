package lt.ltrp.object.impl;

import lt.ltrp.object.DetoxEffect;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.object.Timer;

/**
 * @author Bebras
 *         2016.04.05.
 */
public class FallAnimEffect implements DetoxEffect {

    private LtrpPlayer player;
    private boolean finished;
    private Timer delayTimer;

    public FallAnimEffect(LtrpPlayer player) {
        this.player = player;
    }

    @Override
    public void start() {
        if(!player.isInAnyVehicle()) {
            player.applyAnimation("PED", "KO_SHOT_FRONT", 4.1f, false, true, true, true);
            delayTimer = Timer.create(500, 1, ii -> {
                player.applyAnimation("PED", "CAR_CRAWLOUTRHS", 4.1f, false, true, true, false);
                delayTimer = null;
                finished = true;
            });
            delayTimer.start();
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void destroy() {
        if(delayTimer != null)
            delayTimer.destroy();
    }
}
