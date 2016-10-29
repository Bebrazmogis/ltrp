package lt.maze.injector.resource

import com.github.salomonbrys.kodein.Kodein

/**
 * Created by Bebras on 2016-10-28.
 * If a plugin implements this interface, its bindings will be registered [lt.ltrp.ApiPlugin]
 *
 */
interface BindingPlugin {

    fun getKodeinModule(): Kodein.Module
}