package lt.ltrp.trucker.constant

/**
 * Created by Bebras on 2016-10-29.
 * The possible trucker cargo types
 */
enum class TruckerCargoType(val isCarryable: Boolean, val loadingTime: Int) {

    Box(true, 0 ),
    Liquid(false, 120),
    Bricks(false, 120),
    LooseMaterial(false, 120),
    Logs(false, 120),
    ValuableBox(true, 0),


}