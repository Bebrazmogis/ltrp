package lt.ltrp.property.command;


import lt.ltrp.PlayerPlugin
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.property.PropertyPlugin
import lt.ltrp.property.`object`.Property
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.common.command.CommandHelp
import net.gtaun.shoebill.common.command.CommandParameter
import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.EventManager

/**
 * @author Bebras
 *         2015.12.05.
 */
class PropertyCommands(private val eventManager: EventManager) {

    private val playerPlugin: PlayerPlugin
    private val propertyPlugin: PropertyPlugin

    init {
        playerPlugin = ResourceManager.get().getPlugin(PlayerPlugin::class.java)!!
        propertyPlugin = ResourceManager.get().getPlugin(PropertyPlugin::class.java)!!
    }

/*
    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.Companion.get(p);
        if(player != null) {

            if(player.getProperty() != null || Property.getClosest(player.getLocation(), 10f) != null) {
                System.out.println("PropertyCommands :: beforeChcek. Cmd " + cmd + " returning true");
                return true;
            }
        }
        return false;
    }
*/


    @Command
    @CommandHelp("Leidþia raðyti þinutes bûnant prie durø, kurias matys viduje esantys þaidëjai")
    fun ds(pp: Player, @CommandParameter(name = "Tekstas") text: String): Boolean {
        playerPlugin.get(pp)
        val player: LtrpPlayer = playerPlugin.get(pp) ?: return false
        val property = Property.getClosest(player.location, 5f) ?: return false

        LtrpPlayer.Companion.get()
                .filter { property == Property.get(it) || Property.getClosest(it.location, 15f) != null }
                .forEach {
                    val inMsg = "${player.charName} ðaukia á duris: $text"
                    val outMsg = "{$player.charName} ðaukia pro duris: $text"
                    if (Property.get(it) != null)
                        it.sendMessage(Color.WHITE, inMsg)
                    else
                        it.sendMessage(Color.WHITE, outMsg)
                }
        return true
    }

    @Command
    @CommandHelp("Leidþia pasibelsti á namo/verslo/garaþo duris")
    fun knock(pp: Player): Boolean {
        val player = playerPlugin.get(pp) ?: return false
        val property = Property.getClosest(player.location, 5f) ?: return false

        LtrpPlayer.Companion.get()
                .filter { property == Property[it] || Property.getClosest(it.location, 15f) != null }
                .forEach {
                    if(property == Property.get(it))
                        property.sendActionMessage("Kaþkas beldþiasi á duris")
                    else
                        player.sendActionMessage("pasibeldþia á duris")
                }
        return true
    }
}

    // TODO cmd:furniture
