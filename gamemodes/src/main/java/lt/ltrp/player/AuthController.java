package lt.ltrp.player;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.Util.Whirlpool;
import lt.ltrp.dao.PlayerDao;
import lt.ltrp.event.player.PlayerLogInEvent;
import net.gtaun.shoebill.common.dialog.InputDialog;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class AuthController {

    private static final int MAX_LOGIN_TRIES = 3;

    private int failedLoginAttempts;

    public AuthController(EventManager manager, LtrpPlayer player) {
        Logger.getLogger(AuthController.class.getSimpleName()).log(Level.INFO, "AuthController :: constructor");
        PlayerDao dao = LtrpGamemode.getDao().getPlayerDao();
        Logger.getLogger(AuthController.class.getSimpleName()).log(Level.INFO, "AuthController :: constructor dao got");

        player.setPassword(dao.getPassword(player));
        Logger.getLogger(AuthController.class.getSimpleName()).log(Level.INFO, "AuthController :: constructor player password set");

        // Check if player is banned

        if(player.getUserId() != LtrpPlayer.INVALID_USER_ID) {
            InputDialog.create(player, manager, true)
                    .caption("Prisijungimas")
                    .buttonOk("Jungtis")
                    .buttonCancel("Išeiti")
                    .message("{FFFFFF} Sveiki prisijungę į {cca267}Lithuanian role-play (ltrp.lt){FFFFFF} serverį, dabar galite prisijungti\n\n\n" +
                            "Vartotojas: {cca267}" + player.getName() + "\n{FFFFFF}  Skripto versija: {cca267} " + LtrpGamemode.Version +
                            " {FFFFFF}, atnaujintas: {cca267} " + LtrpGamemode.BuildDate + " " +
                            "\n\n\n{FFFFFF}Įveskite slaptažodį:")
                    .onClickCancel(dialog -> dialog.getPlayer().kick())
                    .onClickOk((dialog, password) -> {
                        if (player.getPassword().equals(Whirlpool.hash(password))) {
                            // He connected successfully
                            player.setLoggedIn(true);
                            manager.dispatchEvent(new PlayerLogInEvent(player, failedLoginAttempts));
                        } else {
                            failedLoginAttempts++;
                            if (failedLoginAttempts == MAX_LOGIN_TRIES)
                                dialog.getPlayer().kick();
                            else {
                                dialog.addLine("\n\n{AA0000}Slaptažodis neteisingas. Tai " + failedLoginAttempts + " bandymas iš " + MAX_LOGIN_TRIES);
                                dialog.show();
                            }

                        }
                    })
                    .build()
                    .show();
        }

    }




}
