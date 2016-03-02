package lt.ltrp;

import lt.ltrp.dao.BankDao;
import lt.ltrp.dao.MySqlBankDao;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.resource.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Bebras
 *         2016.03.02.
 */
public class BankPlugin extends Plugin {

    private static final String[] RESOURCES = new String[] {
            "bank_accounts.sql",
            "bank_properties.sql",
            "bank_wire_transfer_log.sql"
    };

    private BankDao bankDao;
    private BankController bankController;

    @Override
    public void onEnable() throws Throwable {
        initBankDao();
        createTables();
        bankController = new BankController(getEventManager(), bankDao);
    }

    @Override
    public void onDisable() throws Throwable {
        bankDao.close();
        bankController.destroy();
    }

    public BankController getBankController() {
        return bankController;
    }

    private void initBankDao() {
        if(bankDao == null) {
            try {
                Connection connection = Shoebill.get().getResourceManager().getPlugin(DatabasePlugin.class).getDataSource().getConnection();
                bankDao = new MySqlBankDao(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createTables() {
        try (
                Connection connection = Shoebill.get().getResourceManager().getPlugin(DatabasePlugin.class).getDataSource().getConnection();
                Statement statement = connection.createStatement();
                ) {
            for(String s : RESOURCES) {
                statement.execute(readResource(s));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private String readResource(String name) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream in = classLoader.getResourceAsStream(name);
        if(in != null) {
            String query = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String s;
            try {
                while((s = reader.readLine()) != null)
                    query += s;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return query;
        }
        return null;
    }
}
