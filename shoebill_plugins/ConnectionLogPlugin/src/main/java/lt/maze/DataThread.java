package lt.maze;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2016.06.01.
 */
public class DataThread extends Thread {

    private Map<String, String> usernameToIp;
    private DataSource dataSource;

    public DataThread(DataSource dataSource) {
        this.usernameToIp = new HashMap<>();
        this.dataSource = dataSource;
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            usernameToIp.forEach((username, ip) -> {
                try {
                    String url = "http://ip-api.com/json/" + ip;
                    HttpURLConnection con = (HttpURLConnection)new URL(url).openConnection();
                    con.setRequestMethod("GET");
                    int responseCode = con.getResponseCode();
                    JSONObject object = new JSONObject(new JSONTokener(con.getInputStream()));
                    //logger.info("Response code for request " + url + ": "+ responseCode);
                    String sql = "INSERT INTO player_connections (username, ip, isp, country, created_at) VALUES (?, ?, ?, ?, ?)";
                    try (
                            Connection connection = dataSource.getConnection();
                            PreparedStatement stmt = connection.prepareStatement(sql);
                    ) {
                        stmt.setString(1, username);
                        stmt.setString(2, ip);
                        stmt.setString(3, object.getString("isp"));
                        stmt.setString(4, object.getString("country"));
                        stmt.setTimestamp(5, new Timestamp(Instant.now().toEpochMilli()));
                        stmt.execute();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }  catch (SQLException e1) {
                    e1.printStackTrace();
                }
            });
        }
    }

    public void addData(String username, String ip) {
        usernameToIp.put(username, ip);
    }
}
