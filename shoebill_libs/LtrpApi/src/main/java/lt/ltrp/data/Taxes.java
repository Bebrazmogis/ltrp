package lt.ltrp.data;

/**
 * @author Bebras
 *         2016.04.16.
 */
public class Taxes {

    private int houseTax;
    private int businessTax;
    private int garageTax;
    private int vehicleTax;
    private int VAT;

    public Taxes(int houseTax, int businessTax, int garageTax, int vehicleTax, int vat) {
        this.houseTax = houseTax;
        this.businessTax = businessTax;
        this.garageTax = garageTax;
        this.vehicleTax = vehicleTax;
        this.VAT = vat;
    }

    public int getHouseTax() {
        return houseTax;
    }

    public void setHouseTax(int houseTax) {
        this.houseTax = houseTax;
    }

    public int getBusinessTax() {
        return businessTax;
    }

    public void setBusinessTax(int businessTax) {
        this.businessTax = businessTax;
    }

    public int getGarageTax() {
        return garageTax;
    }

    public void setGarageTax(int garageTax) {
        this.garageTax = garageTax;
    }

    public int getVehicleTax() {
        return vehicleTax;
    }

    public void setVehicleTax(int vehicleTax) {
        this.vehicleTax = vehicleTax;
    }

    public int getVAT() {
        return VAT;
    }

    public void setVAT(int VAT) {
        this.VAT = VAT;
    }

    public int getVAT(int amount) {
        return amount / 100 * VAT;
    }
}