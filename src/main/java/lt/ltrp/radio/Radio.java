package lt.ltrp.radio;

import net.gtaun.shoebill.object.Destroyable;

/**
 * Created by Bebras on 2016.03.26.
 *
 * This interface defines the method that all IG radios must have
 * The radio itself can be a house, vehicle or static radio.
 */
public interface Radio extends Destroyable {

    /**
     * Pays the radio station
     * @param radioStation station to play
     */
    void play(RadioStation radioStation);

    /**pl
     * Stops playing
     */
    void stop();

    /**
     * Sets the current volume for everyone hearing this radio
     * Only works for clients using {@link lt.maze.audio.AudioPlugin}
     * @param volume volume to set, 1 - 100, everything beyon that will be set to the nearest boundry
     */
    void setVolume(int volume);

    /**
     *
     * @return the current RadioStation or null if nothing is playing
     */
    RadioStation getStation();

    /**
     *
     * @return returns the current volume set for this radio
     */
    int getVolume();

    /**
     *
     * @return returns true if this radio is playing, false otherwise
     */
    boolean isPlaying();

}
