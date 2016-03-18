package lt.ltrp.job;

import lt.ltrp.command.Commands;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.03.04.
 */
public class JobCommands extends Commands{

    public JobCommands() {

    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        return true;
    }


}
