package lt.ltrp.player;

import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Timer;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class PlayerCountdown implements Destroyable{

    private LtrpPlayer player;
    private int time;
    private int timeleft;
    private PlayerCountdownCallback callback;
    private boolean paused, destroyed, frozen;
    private Timer timer;

    /**
     * Creates and starts a countdown
     * @param player player for which this countdown is running
     * @param time duration(in seconds) of this countdown
     * @param freeze if true, the player will be frozen for the duration of this countdown
     * @param callback the callback which will be called once the countdown is finished
     */
    public PlayerCountdown(LtrpPlayer player, int time, boolean freeze, PlayerCountdownCallback callback) {
        this.player = player;
        this.time = time;
        this.timeleft = time;
        this.callback = callback;
        this.frozen = freeze;
        start();
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public void setPlayer(LtrpPlayer player) {
        this.player = player;
    }

    public int getTimeleft() {
        return timeleft;
    }

    public void setTimeleft(int timeleft) {
        this.timeleft = timeleft;
    }

    public boolean isFrozen() {
        return frozen;
    }

    private void start() {
        if(isDestroyed()) {
            return;
        }

        if(frozen) {
            player.toggleControllable(false);
        }

        if(timer != null) {
            timer.destroy();
        }
        timer = Timer.create(1000, timeleft, new Timer.TimerCallback() {
            @Override
            public void onTick(int i) {
                timeleft--;
                if(player.getInfoBox() != null) {
                    player.getInfoBox().setCountDown(timeleft);
                }
                if(callback != null) {
                    callback.onTick(player, timeleft);
                }
            }
            @Override
            public void onStop() {
                if(player.getInfoBox() != null) {
                    player.getInfoBox().setCountDown(null);
                }
                stop();
            }
        });
    }

    public void pause() {
        if(!paused) {
            paused = true;
            timer.stop();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void resume() {
        if(paused) {
            paused = false;
            start();
        }
    }


    public void stop() {
        timer.stop();
        if(callback != null) {
            callback.onStop(player);
        }
        if(frozen) {
            player.toggleControllable(true);
        }
    }

    public void restart() {
        timeleft = time;
        if(paused || timer.isRunning()) {
            stop();
        }
        start();
    }

    @Override
    public void destroy() {
        destroyed = true;
        if(timer != null) {
            timer.stop();
            timer.destroy();
        }
        timer = null;
        player = null;
        callback = null;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @FunctionalInterface
    public interface PlayerCountdownCallback {
        void onStop(LtrpPlayer player);
        default void onTick(LtrpPlayer player, int timeremaining) {};
    }


}
