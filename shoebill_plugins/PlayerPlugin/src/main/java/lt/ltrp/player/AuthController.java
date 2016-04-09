package lt.ltrp.player;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.player.dao.PlayerDao;
import lt.ltrp.player.event.PlayerLogInEvent;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.util.Whirlpool;
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

    public AuthController(EventManager manager, LtrpPlayerImpl player) {
        Logger.getLogger(AuthController.class.getSimpleName()).log(Level.INFO, "AuthController :: constructor");
        PlayerDao dao = LtrpGamemode.getDao().getPlayerDao();
        Logger.getLogger(AuthController.class.getSimpleName()).log(Level.INFO, "AuthController :: constructor dao got");

        player.setPassword(dao.getPassword(player));
        Logger.getLogger(AuthController.class.getSimpleName()).log(Level.INFO, "AuthController :: constructor player password set");

        // Check if player is banned
        // i guess i didnt do that yet :O


        if(player.getUUID() != LtrpPlayer.INVALID_USER_ID) {
            InputDialog.create(player, manager, true)
                    .caption("Prisijungimas")
                    .buttonOk("Jungtis")
                    .buttonCancel("Iðeiti")
                    .message("{FFFFFF} Sveiki prisijungæ á {cca267}Lithuanian role-play (ltrp.lt){FFFFFF} serverá, dabar galite prisijungti\n\n\n" +
                            "Vartotojas: {cca267}" + player.getName() + "\n{FFFFFF}  Skripto versija: {cca267} " + LtrpGamemode.Version +
                            " {FFFFFF}, atnaujintas: {cca267} " + LtrpGamemode.BuildDate + " " +
                            "\n\n\n{FFFFFF}Áveskite slaptaþodá:")
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
                                dialog.addLine("\n\n{AA0000}Slaptaþodis neteisingas. Tai " + failedLoginAttempts + " bandymas ið " + MAX_LOGIN_TRIES);
                                dialog.show();
                            }

                        }
                    })
                    .build()
                    .show();
        } else {
            player.sendErrorMessage("Jûs neesate uþsreigstravæs. Tai padaryti galite tinklalapyje www.ltrp.lt");
            player.kick();
        }

    }




}
