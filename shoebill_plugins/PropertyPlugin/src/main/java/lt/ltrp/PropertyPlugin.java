package lt.ltrp;

import lt.ltrp.command.PropertyAcceptCommands;
import lt.ltrp.command.PropertyCommands;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.Property;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class PropertyPlugin extends Plugin implements PropertyController {

    private static Logger logger;
    private EventManagerNode eventManagerNode;


    @Override
    protected void onEnable() throws Throwable {
        Instance.instance = this;
        logger = LoggerFactory.getLogger(PropertyController.class);

        eventManagerNode = getEventManager().createChildNode();
        PlayerCommandManager commandManager = new PlayerCommandManager(eventManagerNode);
        commandManager.registerCommands(new PropertyCommands(eventManagerNode));
        CommandGroup acceptGroup = new CommandGroup();
        acceptGroup.registerCommands(new PropertyAcceptCommands(eventManagerNode));
        commandManager.registerChildGroup(acceptGroup, "/accept");
        commandManager.installCommandHandler(HandlerPriority.NORMAL);

        logger.info(getDescription().getName() + " loaded");

    }

    @Override
    protected void onDisable() throws Throwable {
        eventManagerNode.cancelAll();
    }


    @Override
    public Collection<Property> getProperties() {
        Collection<Property> cl = new ArrayList<>();
        cl.addAll(HouseController.get().getHouses());
        cl.addAll(BusinessController.get().getBusinesses());
        cl.addAll(GarageController.get().getGarages());
        return cl;
    }

    @Override
    public Property get(LtrpPlayer ltrpPlayer) {
        Optional<Property> property = Property.get().stream().filter(h -> h.getExit() != null && h.getExit().distance(ltrpPlayer.getLocation()) < 200f).findFirst();
        return property.isPresent() ? property.get() : null;    }
}
