package lt.ltrp.dao.impl;

import lt.ltrp.LoadingException;
import lt.ltrp.RadioStation;
import lt.ltrp.Util.Sql;
import lt.ltrp.dao.RadioStationDao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class SqlRadioStationDao implements RadioStationDao {

    private DataSource ds;

    public SqlRadioStationDao(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public List<RadioStation> get() throws LoadingException {
        String sql = "SELECT * FROM radio_stations";
        List<RadioStation> radioStations = new ArrayList<>();
        try (
                Connection con = ds.getConnection();
                Statement stmt = con.createStatement();
                ResultSet result = stmt.executeQuery(sql);
                ) {
            while(result.next()) {
                RadioStation station = new RadioStation(result.getInt("id"), result.getString("name"), result.getString("url"));
                radioStations.add(station);
            }
        } catch(SQLException e){
            throw new LoadingException("Radio stations could not be loaded", e);
        }
        return radioStations;
    }

    @Override
    public void update(RadioStation station) {
        String sql = "UPDATE radio_stations SET name = ?, url = ? WHERE id = ?";
        try (
                Connection con = ds.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
            ) {
            stmt.setString(1, station.getName());
            stmt.setString(2, station.getUrl());
            stmt.setInt(3, station.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(RadioStation station) {
        String sql = "INSERT INTO radio_stations (name, url) VALUES (?, ?)";
        try (
                Connection con = ds.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setString(1, station.getName());
            stmt.setString(2, station.getUrl());
            stmt.execute();
            int id = stmt.getGeneratedKeys().getInt(1);
            station.setId(id);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(RadioStation station) {
        String sql = "DELETE FROM radio_stations WHERE id = ? LIMIT 1";
        try (
                Connection con = ds.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, station.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
