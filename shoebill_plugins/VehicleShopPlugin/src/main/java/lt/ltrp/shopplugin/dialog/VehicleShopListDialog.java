package lt.ltrp.shopplugin.dialog;

import lt.ltrp.shopplugin.ShopVehicle;
import lt.ltrp.shopplugin.VehicleShop;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.TabListDialog;
import net.gtaun.shoebill.common.dialog.TabListDialogItem;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.15.
 */
public class VehicleShopListDialog extends TabListDialog {

    private static final int ITEMS_PER_PAGE = 15;

    private VehicleShop vehicleShop;
    private int maxPage;
    private int currentPage;

    private TabListDialogItem nextPageItem;
    private TabListDialogItem prevPageItem;

    private VehicleShopSelectVehicleHandler selectVehicleHandler;

    public VehicleShopListDialog(Player player, EventManager eventManager, VehicleShop shop) {
        super(player, eventManager);
        this.vehicleShop = shop;
        this.setCaption(shop.getName());
        this.setHeader(0, "Modelis");
        this.setHeader(1, "Kaina");
        this.maxPage = shop.getVehicles().length / ITEMS_PER_PAGE;

        this.nextPageItem = new TabListDialogItem();
        nextPageItem.setItemText("Toliau ---->");
        nextPageItem.setSelectHandler(i -> {
            if(currentPage < maxPage)
                show(currentPage+1);
        });

        this.prevPageItem = new TabListDialogItem();
        prevPageItem.setItemText("<---- Atgal");
        prevPageItem.setSelectHandler(i -> {
            if(currentPage > 0)
                show(currentPage - 1);
        });
    }

    public void setSelectVehicleHandler(VehicleShopSelectVehicleHandler selectVehicleHandler) {
        this.selectVehicleHandler = selectVehicleHandler;
    }

    @Override
    public void onClickOk(ListDialogItem item) {
        if(selectVehicleHandler != null && item.getData() != null) {
            selectVehicleHandler.onSelectShopVehicle(this, (ShopVehicle)item.getData());
        }
    }


    @Override
    public void show() {
        show(0);
    }

    public void show(int page) {
        this.currentPage = page;

        items.clear();
        if(page > 0)
            items.add(prevPageItem);

        ShopVehicle[] vehicles = vehicleShop.getVehicles();
        for(int i = currentPage * ITEMS_PER_PAGE; i < (currentPage+1) * ITEMS_PER_PAGE && i < vehicles.length; i++) {
            TabListDialogItem item = new TabListDialogItem();
            item.addColumn(0, new ListDialogItem(VehicleModel.getName(vehicles[i].getModelId())));
            item.addColumn(1, new ListDialogItem(Integer.toString(vehicles[i].getPrice())));
            item.setData(vehicles[i]);
            items.add(item);
        }

        if(page < maxPage)
            items.add(nextPageItem);
        super.show();
    }

    @FunctionalInterface
    public interface VehicleShopSelectVehicleHandler {
        void onSelectShopVehicle(VehicleShopListDialog dialog, ShopVehicle vehicle);
    }
}
