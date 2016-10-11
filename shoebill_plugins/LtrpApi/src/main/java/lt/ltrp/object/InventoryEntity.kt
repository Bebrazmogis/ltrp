package lt.ltrp.`object`;

/**
 * @author Bebras
 *         2016.04.08.
 *
 *         Null allowed only because Inventory creation requires InventoryEntity instance
 */
 interface InventoryEntity: NamedEntity {

    var inventory: Inventory?
        get
        set

}
