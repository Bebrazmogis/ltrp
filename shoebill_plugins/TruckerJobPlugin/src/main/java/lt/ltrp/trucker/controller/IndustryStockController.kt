package lt.ltrp.trucker.controller

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import lt.ltrp.trucker.data.IndustryStock
import lt.maze.injector.InjectorPlugin
import net.gtaun.shoebill.resource.ResourceManager

/**
 * Created by Bebras on 2016-10-29.
 */
interface IndustryStockController {

    fun update(stock: IndustryStock)

    companion object {
        private val kodein: Kodein? by lazy {
            ResourceManager.get().getPlugin(InjectorPlugin::class.java).kodein
        }

        val INSTANCE: IndustryStockController by kodein?.lazy?.instance<IndustryStockController>()
    }
}