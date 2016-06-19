package lt.ltrp.trucker.`object`.impl

import lt.ltrp.AbstractContractJob
import lt.ltrp.trucker.`object`.TruckerJob
import net.gtaun.util.event.EventManager

/**
 * @author Bebras
 * 2016.06.19.
 */
class TruckerJobImpl(eventManager: EventManager): AbstractContractJob(eventManager), TruckerJob {
}