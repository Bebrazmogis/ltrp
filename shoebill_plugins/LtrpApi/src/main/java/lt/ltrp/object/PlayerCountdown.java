package lt.ltrp.object;

import net.gtaun.shoebill.object.Destroyable;

/**
 * @author Bebras
 *         2016.04.07.
 */
public interface PlayerCountdown extends Destroyable {

    public static PlayerCountdown create(LtrpPlayer player, int time, boolean freeze, PlayerCountdownCallback callback) {
        return create(player, time, freeze, callback, true, null);
    }

    public static PlayerCountdown create(LtrpPlayer player, int time, boolean frozen, PlayerCountdownCallback callback, boolean stoppable, String infoBoxCaption) {
        return null;
    }


    LtrpPlayer getPlayer();
    void setPlayer(LtrpPlayer player);

    int getTimeleft();
    void setTimeleft(int timeleft);

    boolean isFrozen();
    boolean isStoppable();
    void start();
    public void pause();
    boolean isPaused();
    void resume();
    void forceStop();
    void stop();
    void restart();

    @FunctionalInterface
    public interface PlayerCountdownCallback {
        void onStop(LtrpPlayer player, boolean finished);
        default void onTick(LtrpPlayer player, int timeremaining) {};
    }
}
