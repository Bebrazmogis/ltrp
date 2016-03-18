package lt.ltrp.dao;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lt.ltrp.DatabasePlugin;
import lt.ltrp.LtrpGamemode;
import lt.ltrp.RadioStation;
import lt.ltrp.dao.impl.*;
import lt.ltrp.item.SqlItemDao;
import net.gtaun.shoebill.Shoebill;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * @author Bebras
 *         2015.11.12.
 */
public abstract class DAOFactory {




    public static DAOFactory getInstance() {
        DAOFactory instance = null;

        Properties p = new Properties(System.getProperties());
        //p.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.slf4j.Slf4jMLog");
        //p.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "ALL"); // Off or any other level
        System.setProperties(p);

        DatabasePlugin dbPlugin = Shoebill.get().getResourceManager().getPlugin(DatabasePlugin.class);
        DataSource dataSource = dbPlugin.getDataSource();

        try {
            instance = new JdbcDAO(dataSource);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return instance;
    }

    public abstract Connection getConnection() throws SQLException;

    public abstract PlayerDao getPlayerDao();
    public abstract PhoneDao getPhoneDao();
    public abstract ItemDao getItemDao();
    public abstract HouseDao getHouseDao();
    public abstract JobDao getJobDao();
    public abstract DmvDao getDmvDao();
    public abstract VehicleDao getVehicleDao();
    public abstract RadioStationDao getRadioStationDao();
}

class JdbcDAO extends DAOFactory {

    private DataSource ds;
    private PlayerDao playerDao;
    private PhoneDao phoneDao;
    private ItemDao itemDao;
    private HouseDao houseDao;
    private JobDao jobDao;
    private DmvDao dmvDao;
    private VehicleDao vehicleDao;
    private RadioStationDao radioStationDao;

    public JdbcDAO(DataSource ds) throws IOException, SQLException {
        this.ds = ds;
        /*BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("database.sql")));
        String line;
        StringBuilder builder = new StringBuilder();
        while((line = in.readLine()) != null) {
            if(line.contains(";")) {
                builder.append(line.substring(0, line.indexOf(";")));
                Statement statement = null;
                Connection con = null;
                try {
                    con = ds.getConnection();
                    statement = con.createStatement();
                    statement.execute(builder.toString());
                } catch(SQLException e) {
                    String error = "Query failed: " + builder.toString() + " Reason:" + e.getMessage() + ".";
                    if(statement != null)
                        error += statement.getWarnings();
                    Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, error);
                    e.printStackTrace();
                } finally {
                    if(con != null) {
                        con.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                builder = new StringBuilder();
                builder.append(line.substring(line.indexOf(";")+1));
            }
            else {
                builder.append(line);
            }
        }

        System.out.println("File read");
        in.close();
        */
        this.playerDao = new SqlPlayerDaoImpl(ds);
        this.phoneDao = new SqlPhoneDaoImpl(ds);
        this.itemDao = new SqlItemDao(ds);
        this.houseDao = new SqlHouseDao(ds);
        this.dmvDao = new FileDmvDaoImpl(LtrpGamemode.get().getDataDir());
        this.vehicleDao = new SqlVehicleDaoImpl(ds);
        this.jobDao = new MySqlJobDaoImpl(ds, vehicleDao);
        this.radioStationDao = new SqlRadioStationDao(ds);
        System.out.println("JDBCDAO initialized");
    }


    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }


    @Override
    public PlayerDao getPlayerDao() {
        return playerDao;
    }

    @Override
    public PhoneDao getPhoneDao() {
        return phoneDao;
    }

    @Override
    public ItemDao getItemDao() {
        return itemDao;
    }

    @Override
    public HouseDao getHouseDao() {
        return houseDao;
    }

    @Override
    public JobDao getJobDao() {
        return jobDao;
    }

    @Override
    public DmvDao getDmvDao() {
        return dmvDao;
    }

    @Override
    public VehicleDao getVehicleDao() {
        return vehicleDao;
    }

    @Override
    public RadioStationDao getRadioStationDao() {
        return radioStationDao;
    }

}

