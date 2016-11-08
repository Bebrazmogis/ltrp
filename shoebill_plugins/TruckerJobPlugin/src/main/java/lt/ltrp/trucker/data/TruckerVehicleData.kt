package lt.ltrp.trucker.data

import lt.ltrp.trucker.constant.TruckerCargoType
import net.gtaun.shoebill.data.Vector3D

/**
 * Created by Bebras on 2016-10-29.
 * This class holds trucker vehicle data
 *
 */
class TruckerVehicleData(val cargoType: TruckerCargoType,
                         val limit: Short,
                         private val offsets: Array<Vector3D>?,
                         private val rotationOffsets: Array<Vector3D>?,
                         private val cargoVisible: Array<Boolean>) {

    constructor(cargoType: TruckerCargoType,
                limit: Short,
                offsets: Array<Vector3D>,
                cargoVisible: Array<Boolean>): this(cargoType, limit, offsets, null, cargoVisible) {

    }

    constructor(cargoType: TruckerCargoType, limit: Short): this(cargoType, limit, null, null, Array(limit.toInt(), { false } )) {

    }


    /**
     * Gets the position [Vector3D] at the specified index
     * If there are less offsets then the limit, index will be truncated
     *
     * For example if there are 2 vectors of offsets and the limit is 16, when index is 0-7(inclusive) will return 0
     * and when index 8-15) it will return 1st vector
     *
     * @param index
     *
     * @return a position vector or null if it doesn't exist
     */
    fun getPositionOffset(index: Int): Vector3D? {
        if(offsets != null && index >= 0) {
            if(offsets.size < limit) {
                return getPositionOffset(limit / index)
            }
            return offsets[ index ]
        }
        return null
    }

    /**
     * Gets the rotation [Vector3D]. Refer to [getPositionOffset] docs
     */
    fun getRotationOffset(index: Int): Vector3D? {
        if(rotationOffsets != null && index >= 0) {
            if(limit > rotationOffsets.size) {
                return getRotationOffset(limit / index)
            }
            return rotationOffsets[ index ]
        }
        return null
    }

    fun isVisible(index: Int): Boolean {
        if(index >= 0 && index < cargoVisible.size) {
            return cargoVisible[ index ]
        }
        return false
    }


}