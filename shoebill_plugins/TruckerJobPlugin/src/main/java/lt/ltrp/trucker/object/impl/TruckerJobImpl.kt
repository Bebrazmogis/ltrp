package lt.ltrp.trucker.`object`.impl

import lt.ltrp.trucker.`object`.TruckerJob
import lt.ltrp.`object`.impl.AbstractContractJob
import net.gtaun.util.event.EventManager

/**
 * @author Bebras
 * 2016.06.19.
 */
class TruckerJobImpl(uuid: Int, eventManager: EventManager): AbstractContractJob(uuid, eventManager), TruckerJob {

}