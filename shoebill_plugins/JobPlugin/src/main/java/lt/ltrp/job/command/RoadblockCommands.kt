package lt.ltrp.command

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.command.Commands
import lt.ltrp.job.`object`.Job
import lt.ltrp.job.data.Roadblock
import lt.ltrp.job.dialog.RoadblockListDialog
import lt.ltrp.job.modelpreview.RoadblockModelPreview
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.common.command.BeforeCheck
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.common.command.CommandHelp
import net.gtaun.shoebill.common.command.CommandParameter
import net.gtaun.shoebill.data.Vector3D
import net.gtaun.util.event.EventManager

/**
 * @author Bebras
 *         2015.12.30.
 */
class RoadblockCommands(private val job: Job, private val eventManager: EventManager): Commands() {

    @BeforeCheck
    fun beforeCheck(p: Player, cmd: String, params: String): Boolean {
        val player = LtrpPlayer.get(p)
        if(player.isAdmin || player.jobData?.job == job) {
            return true
        } else {
            return false
        }
    }

    @Command
    @CommandHelp("Pastato uþtvarà, galimi veiksmai create, destroy, edit, manage")
    fun roadblock(p: Player, @CommandParameter(name = "Veiksmas")param: String): Boolean {
        val player = LtrpPlayer.get(p);
        if(player.vehicle != null) {
            player.sendErrorMessage("Uþtvaros pastatyti negalite bûdami transporto priemonëje.")
        } else {
            if(param.equals("create", true)) {
                RoadblockModelPreview.create(player, eventManager, { preview, model ->
                    val location = player.getLocation();
                    location.setX(location.getX() + (3f * Math.sin(-Math.toRadians(location.getAngle().toDouble())).toFloat()))
                    location.setY(location.getY() + (3f * Math.cos(-Math.toRadians(location.getAngle().toDouble())).toFloat()))
                    val roadblock = Roadblock(model, location, Vector3D())
                    roadblock.`object`.edit(player)
                })
            } else if(param.equals("destroy", true)) {
                val location = player.location;
                val roadBlock = Roadblock.get().minBy { it.location.distance(location) }
                if(roadBlock == null) {
                    player.sendErrorMessage("Prie jûsø nëra jokios kelio uþtvaros!")
                } else {
                    roadBlock.destroy()
                    player.sendMessage("Uþtvaras paðalintas.")
                }
            } else if(param.equals("edit", true)) {
                val location = player.getLocation();
                val roadBlock = Roadblock.get().minBy { it.location.distance(location) }
                if(roadBlock == null) {
                    player.sendErrorMessage("Prie jûsø nëra jokios kelio uþtvaros!")
                } else {
                    roadBlock.`object`.edit(player)
                }
            } else if(param.equals("manage", true)) {
                if(player.isAdmin) {
                    RoadblockListDialog(player, eventManager)
                            .show()
                }
            }
            return true
        }
        return false
    }

}
