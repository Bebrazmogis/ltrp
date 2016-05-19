package lt.ltrp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.04.16.
 */
public class MavenErrorReader extends Thread {

    private InputStream inputStream;
    private Collection<String> errors;

    public MavenErrorReader(InputStream inputStream) {
        this.inputStream = inputStream;
        this.errors = new ArrayList<>();
    }

    public Collection<String> getErrors() {
        return errors;
    }

    @Override
    public void run() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            while(!isInterrupted()) {
                if(reader.ready()) {
                    String error = reader.readLine();
                    if(error != null) {
                        System.out.println("ERR:" + error);
                        errors.add(error);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
