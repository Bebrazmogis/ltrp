package lt.maze.audio;

import net.gtaun.shoebill.entities.Destroyable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.02.15.
 */
public class AudioSequence implements Destroyable {

    public AudioSequence get(int id) {
        return AudioPlugin.getInstance().getSequence(id);
    }

    public static AudioSequence create() {
        int id = Functions.Audio_CreateSequence();
        AudioSequence sequence = new AudioSequence(id);
        AudioPlugin.getInstance().addSequence(sequence);
        return sequence;
    }

    private int id;
    private boolean destroyed;
    private List<Integer> audioIds;

    private AudioSequence(int id) {
        this.id = id;
        this.audioIds = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void add(int audioid) {
        audioIds.add(audioid);
        Functions.Audio_AddToSequence(getId(), audioid);
    }

    public void remove(int audioid) {
        audioIds.remove(audioid);
        Functions.Audio_RemoveFromSequence(getId(), audioid);
    }

    public List<Integer> getAudioIds() {
        return audioIds;
    }

    @Override
    public void destroy() {
        destroyed = true;
        Functions.Audio_DestroySequence(getId());
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
