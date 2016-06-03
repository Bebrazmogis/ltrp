package lt.ltrp.dao.impl;

import lt.ltrp.dao.AdvertisementCenterDao;
import net.gtaun.shoebill.data.Location;

import java.io.*;

/**
 * @author Bebras
 *         2016.06.03.
 */
public class FileAdvertisementCenterDaoImpl implements AdvertisementCenterDao {

    private File root;

    public FileAdvertisementCenterDaoImpl(File rootDir) {
        this.root = rootDir;
    }

    private File dataFile() {
        return new File(root, "advertisementcenter");
    }

    @Override
    public Location get() {
        Location loc = null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dataFile()))) {
            loc = (Location)in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return loc;
    }

    @Override
    public void set(Location location) {
        int letterPrice = getLetterPrice();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dataFile()))) {
            out.writeObject(location);
            out.writeInt(letterPrice);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getLetterPrice() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dataFile()))) {
            in.readObject(); // ignore
            return in.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void setLetterPrice(int letterPrice) {
        Location location = get();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dataFile()))) {
            out.writeObject(location);
            out.writeInt(letterPrice);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
