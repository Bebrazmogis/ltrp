package lt.ltrp.dao;

import lt.ltrp.object.Entrance;

import java.util.List;

/**
 * @author Bebras
 *         2016.05.22.
 */
public interface EntranceDao {

    List<Entrance> get();
    Entrance get(int id);
    void update(Entrance entrance);
    void delete(Entrance entrance);
    void insert(Entrance entrance);

}
