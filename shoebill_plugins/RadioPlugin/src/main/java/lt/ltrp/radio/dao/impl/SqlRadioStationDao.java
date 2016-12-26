package lt.ltrp.radio.dao.impl;

import lt.ltrp.radio.dao.RadioStationDao;
import lt.ltrp.radio.data.RadioStation;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class SqlRadioStationDao implements RadioStationDao {

    private DataSource ds;
    private Logger logger;

    public SqlRadioStationDao(DataSource ds, Logger logger) {
        this.ds = ds;
        this.logger = logger;
        try {
            insertTable();
        } catch (SQLException e) {
            logger.error("Could not create table", e);
        }
    }

    @Override
    public List<RadioStation> get() {
        String sql = "SELECT * FROM radio_stations";
        List<RadioStation> radioStations = new ArrayList<>();
        try (
                Connection con = ds.getConnection();
                Statement stmt = con.createStatement();
                ResultSet result = stmt.executeQuery(sql);
                ) {
            while(result.next()) {
                RadioStation station = new RadioStation(result.getInt("id"), result.getString("url"), result.getString("name"));
                radioStations.add(station);
            }
        } catch(SQLException e){
            logger.error("Could not retrieve radio stations", e);
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
            stmt.setInt(3, station.getUuid());
            stmt.execute();
        } catch(SQLException e) {
            logger.error("Could not update radio station " + station, e);
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
            station.setUuid(id);
        } catch(SQLException e) {
            logger.error("Could not insert new radio station " + station, e);
        }
    }

    @Override
    public void remove(RadioStation station) {
        String sql = "DELETE FROM radio_stations WHERE id = ? LIMIT 1";
        try (
                Connection con = ds.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, station.getUuid());
            stmt.execute();
        } catch(SQLException e) {
            logger.error("Could not remove radio station " + station, e);
        }
    }

    private void insertTable() throws SQLException {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("radio_stations.sql")))) {
            StringBuilder sql = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
                sql.append(line);

            try(Connection connection = ds.getConnection()) {
                try(Statement stmt = connection.createStatement()) {
                    stmt.execute(sql.toString());
                }
            }
        } catch (IOException e) {
            throw new SQLException("Could not read table file \"radio_stations.sql\"", e);
        }
    }
}
