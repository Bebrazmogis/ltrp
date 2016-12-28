package lt.ltrp.dao;

import lt.ltrp.LoadingException;
import lt.ltrp.object.AircraftDmv;
import lt.ltrp.object.BoatDmv;
import lt.ltrp.object.CarDmv;

/**
 * @author Bebras
 *         2015.12.13.
 */
public interface DmvDao {

    /**
     * A DMV with this ID should be consider invalid
     */
    static final int INVALID_ID = 0;


    /**
     * Loads a CarDmv with the specified ID
     * @param id CarDmv ID to be loaded
     * @return a CarDmw or null if it couldn't be loaded
     */
    CarDmv getCarDmv(int id) throws LoadingException;

    /**
     * Loads a BoatDmv with the specified ID
     * @param id ID of the boat dmv
     * @return CarDmv or null if it couldn't be loaded
     * @throws LoadingException if an error occurs while loading
     */
    BoatDmv getBoatDmv(int id) throws LoadingException;

    AircraftDmv getAircraftDmv(int id) throws LoadingException;


}
