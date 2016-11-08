package lt.ltrp.constant;

/**
 * @author Bebras
 *         2015.11.29.
 */
public enum ItemType {


    Wrench(1),
    Crowbar(2),
    Hammer(3),
    Flashlight(4),
    Tazer(5),
    Screwdriver(6),
    Radio(7),
    Dice(8),
    FishingRod(9),
    FishingBait(10),
    FishingBag(11),
    Lighter(12),
    Fueltank(13),
    Cigarettes(14),
    Phone(15),
    Clothing(16),
    MeleeWeapon(17),
    Suitcase(18),
    Mask(19),
    Weapon(20),
    Drink(21),
    WeedSeed(22),
    Syringe(23),
    Molotov(24),
    Newspaper(25),
    Amphetamine(26),
    Cocaine(27),
    Extazy(28),
    Heroin(29),
    MetaAmphetamine(30),
    Pcp(31),
    HouseAudio(32),
    Weed(33),
    DmvTheory(34),
    Prescription(35),
    Materials(36),
    Toolbox(37),
    Mp3Player(38),
    BoomBox(39),
    CarAudio(40)
    ;

    public int id;

    ItemType(int id) {
        this.id = id;

    }



    public static ItemType getById(int id) {
        for(ItemType type : ItemType.values()) {
            if(type.id == id) {
                return type;
            }
        }
        return null;
    }

}
