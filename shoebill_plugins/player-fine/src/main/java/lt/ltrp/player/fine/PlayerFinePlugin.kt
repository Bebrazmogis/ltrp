package lt.ltrp.player.fine

import lt.ltrp.DatabasePlugin
import lt.ltrp.player.fine.dao.impl.MySqlPlayerFineDaoImpl
import lt.ltrp.resource.DependentPlugin

/**
 * Created by Bebras on 2016-10-28.
 * This plugin provides the implementation for player fine dao and controller
 * Other than that, it does nothing
 * If needed, some sort of caching for PlayerFine objects can be added
 */
class PlayerFinePlugin: DependentPlugin() {

    private lateinit var controller: PlayerFineControllerImpl
    private lateinit var dao: MySqlPlayerFineDaoImpl

    init {
        addDependency(DatabasePlugin::class)
    }

    override fun onEnable() {
        super.onEnable()
    }


    override fun onDependenciesLoaded() {
        dao = MySqlPlayerFineDaoImpl(DatabasePlugin.get(DatabasePlugin::class.java).dataSource)
        controller = PlayerFineControllerImpl(dao)
    }

    override fun onDisable() {
        super.onDisable()
    }
}