package lt.maze;

import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.02.15.
 */
public class AudioHandle implements Destroyable {

    public static AudioHandle get(Player p, int id) {
        return AudioPlugin.getInstance().getHandle(p, id);
    }

    public static AudioHandle play(Player p, int audiodid, boolean paused, boolean loop, boolean downmix) {
        int handleid = Functions.Audio_Play(p.getId(), audiodid, paused, loop, downmix);
        AudioHandle handle = new AudioHandle(handleid, audiodid, paused, loop, downmix, p);
        AudioPlugin.getInstance().addHandle(p, handle);
        return handle;
    }

    public static AudioHandle play(Player p, int audiodid) {
        return play(p, audiodid, false, false, false);
    }


    public static AudioHandle playStreamed(Player p, String url, boolean paused, boolean loop, boolean downmix) {
        int handleid = Functions.Audio_PlayStreamed(p.getId(), url, paused, loop, downmix);
        AudioHandle handle = new AudioHandle(handleid, url, paused, loop, downmix, p);
        AudioPlugin.getInstance().addHandle(p, handle);
        return handle;
    }

    public static AudioHandle play(Player p, String url) {
        return playStreamed(p, url, false, false, false);
    }

    public static AudioHandle playSequence(Player p, AudioSequence sequence, boolean paused, boolean loop, boolean downmix) {
        int handleid = Functions.Audio_PlaySequence(p.getId(), sequence.getId(), paused, loop, downmix);
        AudioHandle handle = new AudioHandle(handleid, sequence, paused, loop, downmix, p);
        AudioPlugin.getInstance().addHandle(p, handle);
        return handle;
    }

    public static AudioHandle playSequence(Player p, AudioSequence sequence) {
        return playSequence(p, sequence, false, false, false);
    }


    private int id, audioid;
    private boolean paused, loop, downmix, destroyed;
    private String url;
    private AudioSequence sequence;
    private Player player;
    private PositionGetHandler handler;

    private AudioHandle(int id, int audioid, boolean paused, boolean loop, boolean downmix, Player p) {
        this.id = id;
        this.audioid = audioid;
        this.paused = paused;
        this.loop = loop;
        this.downmix = downmix;
        this.player = p;
    }

    private AudioHandle(int id,  String url, boolean paused, boolean loop, boolean downmix, Player p) {
        this.id = id;
        this.paused = paused;
        this.loop = loop;
        this.downmix = downmix;
        this.url = url;
        this.player = p;
    }

    private AudioHandle(int id, AudioSequence sequence, boolean paused, boolean loop, boolean downmix, Player p) {
        this.id = id;
        this.paused = paused;
        this.loop = loop;
        this.downmix = downmix;
        this.sequence = sequence;
        this.player = p;
    }

    public void pause() {
        this.paused = true;
        Functions.Audio_Pause(getPlayer().getId(), getId());
    }

    public void resume() {
        this.paused = false;
        Functions.Audio_Resume(getPlayer().getId(), getId());
    }

    public void stop() {
        Functions.Audio_Stop(getPlayer().getId(), getId());
    }

    public void restart() {
        Functions.Audio_Restart(getPlayer().getId(), getId());
    }

    public void getPosition(PositionGetHandler handler) {
        this.handler = handler;
    }

    public void getPosition() {
        Functions.Audio_GetPosition(getPlayer().getId(), getId(), "Audio_OnGetPosition");
    }

    public void setPosition(int seconds) {
        Functions.Audio_SetPosition(getPlayer().getId(), getId(), seconds);
    }

    public void setVolume(int volume) {
        Functions.Audio_SetVolume(getPlayer().getId(), getId(), volume);
    }

    public void setFX(AudioFX fx) {
        Functions.Audio_SetFX(getPlayer().getId(), getId(), fx.getId());
    }

    public void removeFX(AudioFX fx) {
        Functions.Audio_RemoveFX(getPlayer().getId(), getId(), fx.getId());
    }

    public void set3DPosition(Radius radius) {
        set3DPosition(radius.getX(), radius.getY(), radius.getZ(), radius.getRadius());
    }

    public void set3DPosition(Vector3D vector, float distance) {
        set3DPosition(vector.getX(), vector.getY(), vector.getZ(), distance);
    }

    public void remove3DPosition() {
        Functions.Audio_Remove3DPosition(getPlayer().getId(), getId());
    }

    public void set3DPosition(float x, float y, float z, float distance) {
        Functions.Audio_Set3DPosition(getPlayer().getId(), getId(), x, y, z, distance);
    }

    protected PositionGetHandler getHandler() {
        return handler;
    }

    public int getId() {
        return id;
    }

    public int getAudioid() {
        return audioid;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isLoop() {
        return loop;
    }

    public boolean isDownmix() {
        return downmix;
    }

    public String getUrl() {
        return url;
    }

    public AudioSequence getSequence() {
        return sequence;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void destroy() {
        this.destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @FunctionalInterface
    public interface PositionGetHandler {
        void onPositionGet(AudioHandle handle, int seconds);
    }
}
