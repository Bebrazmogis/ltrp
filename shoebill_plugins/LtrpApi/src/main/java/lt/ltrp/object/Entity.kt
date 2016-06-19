package lt.ltrp.`object`

/**
 * @author Bebras
 *         2016.03.23.
 */
interface Entity {

    var UUID: Int
        get
        set

    companion  object {
        val INVALID_ID: Int = 0
    }
}
