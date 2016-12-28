package lt.ltrp.job.`object`

import lt.ltrp.`object`.DestroyableEntity
import net.gtaun.shoebill.data.Vector3D

/**
 * Created by Bebras on 2016-10-24.
 * Defines available methods for interacting with job gate objects
 */
interface JobGate: DestroyableEntity {

    val modelId: Int
    val job: Job
    val rank: JobRank
    val openPosition: Vector3D
    val closedPosition: Vector3D
    val openRotation: Vector3D
    val closedRotation: Vector3D
    var isOpen:Boolean
    var isDefaultOpen:Boolean
    val speed: Float

    fun open()
    fun close()
    fun isMoving(): Boolean


}