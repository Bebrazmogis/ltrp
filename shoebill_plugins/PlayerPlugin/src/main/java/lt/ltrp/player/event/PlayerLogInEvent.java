package lt.ltrp.player.event;


import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class PlayerLogInEvent extends PlayerEvent {

    private int failedAttempts;

    public PlayerLogInEvent(LtrpPlayer player, int failedAttempts) {
        super(player);
        this.failedAttempts = failedAttempts;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

}
