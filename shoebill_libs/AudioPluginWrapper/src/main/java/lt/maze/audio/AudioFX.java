package lt.maze.audio;

/**
 * @author Bebras
 *         2016.02.15.
 */
public enum  AudioFX {



    Chorus(0),
    Compression(1),
    Distortion(2),
    Echo(3),
    Flanger(4),
    Gargle(5),
    I3DL2Reverb(6),
    ParametricEqualizer(7),
    Reverb(8);

    static AudioFX get(int id) {
        for(AudioFX fx : values()) {
            if(fx.getId() == id) {
                return fx;
            }
        }
        return null;
    }

    int id;

    AudioFX(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
