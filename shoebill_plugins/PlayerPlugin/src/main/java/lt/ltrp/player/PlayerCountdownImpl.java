package lt.ltrp.player;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.object.PlayerCountdown;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Timer;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class PlayerCountdownImpl implements PlayerCountdown, Destroyable{

    private LtrpPlayer player;
    private int time;
    private int timeleft;
    private PlayerCountdown.PlayerCountdownCallback callback;
    private boolean paused, destroyed, frozen, stoppable;
    private Timer timer;
    private String infoBoxCaption;

    /**
     * Creates and starts a countdown
     * @param player player for which this countdown is running
     * @param time duration(in seconds) of this countdown
     * @param freeze if true, the player will be frozen for the duration of this countdown
     * @param callback the callback which will be called once the countdown is finished
     */
    public PlayerCountdownImpl(LtrpPlayer player, int time, boolean freeze, PlayerCountdownCallback callback) {
        this(player, time, freeze, callback, true, null);
    }

    public PlayerCountdownImpl(LtrpPlayer player, int time, boolean frozen, PlayerCountdownCallback callback, boolean stoppable, String infoBoxCaption) {
        this.player = player;
        this.time = time;
        this.frozen = frozen;
        this.callback = callback;
        this.stoppable = stoppable;
        this.infoBoxCaption = infoBoxCaption;
        this.timeleft = time;
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

    public boolean isStoppable() {
        return stoppable;
    }

    public void start() {
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
                    if(infoBoxCaption != null)
                        player.getInfoBox().setCountDown(infoBoxCaption, timeleft);
                    else
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
                forceStop();
            }
        });
        timer.start();
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

    /**
     * Continues the countdown after pausing it, if it isn't paused does nothing
     */
    public void resume() {
        if(paused) {
            paused = false;
            start();
        }
    }

    public void forceStop() {
        timer.stop();
        if(callback != null) {
            callback.onStop(player, getTimeleft() == 0);
        }
        if(frozen) {
            player.toggleControllable(true);
        }
    }

    public void stop() {
        if(stoppable) {
            forceStop();
        }
    }

    /**
     * Restarts the countdown from the initial time
     */
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



}
