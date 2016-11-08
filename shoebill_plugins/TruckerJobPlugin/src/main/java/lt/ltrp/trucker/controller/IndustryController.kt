package lt.ltrp.trucker.controller

import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import lt.ltrp.trucker.`object`.Industry
import lt.maze.injector.InjectorPlugin
import net.gtaun.shoebill.data.Location
import net.gtaun.shoebill.resource.ResourceManager

/**
 * Created by Bebras on 2016-10-30.
 * Methods for interacting with [Industry] objects
 *
 */
interface IndustryController {

    fun get(): Collection<Industry>
    fun getClosest(location: Location, maxDistance: Float): Industry?

    companion object {
        private val kodein by lazy {
            ResourceManager.get().getPlugin(InjectorPlugin::class.java).kodein
        }
        val INSTANCE by kodein?.lazy?.instance<IndustryController>()
    }
}