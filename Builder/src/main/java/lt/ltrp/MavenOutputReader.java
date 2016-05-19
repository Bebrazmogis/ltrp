package lt.ltrp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Bebras
 *         2016.04.16.
 */
public class MavenOutputReader extends Thread {

    private boolean success;
    private InputStream in;

    public MavenOutputReader(InputStream in) {
        this.in = in;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public void run() {
        String line;
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            while(!isInterrupted()) {
                if(reader.ready()) {
                    line = reader.readLine();
                    if(line == null) {
                        interrupt();
                    } else {
                        System.out.println("OUT:" + line);
                        if(line.contains("BUILD SUCCESS")) {
                            success = true;
                            interrupt();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
