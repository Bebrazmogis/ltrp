package lt.ltrp.dao.impl;

import lt.ltrp.LtrpWorld;
import lt.ltrp.dao.LtrpWorldDao;

import java.io.*;
import java.util.Properties;

/**
 * @author Bebras
 *         2016.05.31.
 */
public class FileLtrpWorldImpl implements LtrpWorldDao {

    private File dataDir;

    public FileLtrpWorldImpl(File dataDir) {
        this.dataDir = dataDir;
    }

    @Override
    public void save(LtrpWorld ltrpWorld) {
        File file = new File(dataDir, "world");
        Properties properties = toProperties(ltrpWorld);
        try {
            properties.store(new FileWriter(file), "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(LtrpWorld ltrpWorld) {
        File file = new File(dataDir, "world");
        if(file.exists()) {
            Properties properties = new Properties();
            try {
                properties.load(new FileReader(file));
                ltrpWorld.setMoney(Integer.parseInt(properties.getProperty("money")));
                ltrpWorld.getTaxes().setBusinessTax(Integer.parseInt(properties.getProperty("tax_business")));
                ltrpWorld.getTaxes().setHouseTax(Integer.parseInt(properties.getProperty("tax_house")));
                ltrpWorld.getTaxes().setVehicleTax(Integer.parseInt(properties.getProperty("tax_vehicle")));
                ltrpWorld.getTaxes().setGarageTax(Integer.parseInt(properties.getProperty("tax_garage")));
                ltrpWorld.getTaxes().setVAT(Integer.parseInt(properties.getProperty("tax_vat")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Properties toProperties(LtrpWorld world) {
        Properties properties = new Properties();
        properties.put("money", world.getMoney());
        properties.put("tax_business", world.getTaxes().getBusinessTax());
        properties.put("tax_house", world.getTaxes().getHouseTax());
        properties.put("tax_vehicle", world.getTaxes().getVehicleTax());
        properties.put("tax_garage", world.getTaxes().getGarageTax());
        properties.put("tax_vat", world.getTaxes().getVAT());
        return properties;
    }

}
