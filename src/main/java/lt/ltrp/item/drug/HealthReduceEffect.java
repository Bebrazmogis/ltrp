package lt.ltrp.item.drug;

import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.05.
 */
public class HealthReduceEffect implements DetoxEffect {

    private LtrpPlayer player;
    private float healthReduce;

    private boolean finished;

    public HealthReduceEffect(LtrpPlayer player, float healthReduce) {
        this.player = player;
        this.healthReduce = healthReduce;
    }

    @Override
    public void start() {
        if(player.getHealth() > healthReduce) {
            player.setHealth(player.getHealth() - healthReduce);
        }
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
