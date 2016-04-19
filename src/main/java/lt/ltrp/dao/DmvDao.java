package lt.ltrp.dao;

import lt.ltrp.LoadingException;
import lt.ltrp.dmv.*;
import lt.ltrp.dmv.aircraft.AircraftDmv;
import lt.ltrp.dmv.boat.BoatDmv;
import lt.ltrp.dmv.car.CarDmv;

import java.util.List;

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
