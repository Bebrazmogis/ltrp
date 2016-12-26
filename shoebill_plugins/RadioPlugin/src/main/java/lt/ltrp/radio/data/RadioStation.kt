package lt.ltrp.radio.data


/**
 * @author Bebras
 *         2016.02.15.
 */
class RadioStation(var uuid: Int, var url: String, var name: String) {

    companion object {
        internal var stations = mutableListOf<RadioStation>()

        fun get() = stations
    }

}