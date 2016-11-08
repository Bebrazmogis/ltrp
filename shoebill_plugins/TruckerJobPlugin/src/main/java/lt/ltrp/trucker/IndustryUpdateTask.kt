package lt.ltrp.trucker

import lt.ltrp.trucker.controller.IndustryStockController
import net.gtaun.shoebill.Shoebill


/**
 * Created by Bebras on 2016-10-29.
 * This tasks calls [IndustryCommodity#produce] for all industry productions
 * It also updates their records and then updates industry labels
 *
 * Labels are updated from within the SAMP thread
 */
class IndustryUpdateTask: Runnable {

    override fun run() {
        IndustryContainer.industries.forEach {
            it.productions.forEach {
                if(it.canProduce()) {
                    it.produce()
                }
            }
            it.soldStock.plus(it.boughtStock).forEach {
                IndustryStockController.INSTANCE.update(it)
            }
        }
        // Update the labels from SAMP thread
        Shoebill.get().runOnSampThread {
            IndustryContainer.industries.forEach { it.updateLabel() }
        }
    }
}