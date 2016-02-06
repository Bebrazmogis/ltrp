package lt.ltrp.dialogmenu;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.data.Color;
import lt.ltrp.player.JailData;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerCrime;
import lt.ltrp.vehicle.VehicleCrime;
import net.gtaun.shoebill.common.dialog.*;
import net.gtaun.util.event.EventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.12.
 */
public class PoliceDatabaseMenu extends PlayerDialogMenu {

    private static final Logger logger = LoggerFactory.getLogger(PoliceDatabaseMenu.class);

    private boolean shown;

    public PoliceDatabaseMenu(LtrpPlayer player, EventManager manager) {
        super(player, manager);
    }

    @Override
    public void show() {
        LtrpPlayer player = getPlayer();
        EventManager eventManager = getEventManager();
        shown = true;
        ListDialog.create(getPlayer(), eventManager)
                .caption("{1797cd}" + player.getJob().getName() + " duomenø bazë. {FFFFFF}Vartotojas " + player.getName())
                .buttonOk("Pasirinkti")
                .buttonCancel("Iðeiti")
                .onClickCancel(dialog -> {
                    getDialogMenuCloseHandler().onDialogMenuClose(getPlayer());
                    shown = false;
                })
                .item("Surasti asmená", item -> {
                    InputDialog.create(player, eventManager)
                            .caption(item.getCurrentDialog().getCaption() + ". Paieðka:")
                            .message("Paieðka duomenø bazëje pagal vardà ir/ar pavardæ" +
                                    "\n\nÁveskite vardà, pavardæ arba abu." +
                                    "\nBus ieðkoma 15 panaðiausiø asmenø" +
                                    "\n\nVardui ir pavardei atskirti naudokite simbolá \" \"(tarpà) arba \"_\"" +
                                    "\nÐiø simboliø neradus tekstas bus ieðkoma pateikto teksto tiek varde, tiek pavardëje.")
                            .buttonOk("Ieðkoti")
                            .buttonCancel("Atgal")
                            .onClickCancel(AbstractDialog::showParentDialog)
                            .onClickOk((nameDialog, text) -> {
                                if (text != null && !text.isEmpty()) {
                                    text = text.replaceAll(" ", "_");
                                    List<UserData> users = getByUsername(text);
                                    // If we found any users
                                    if (users.size() != 0) {
                                        ListDialog userList = ListDialog.create(player, eventManager).build();
                                        userList.setCaption(nameDialog.getCaption());
                                        userList.setButtonOk("Perþiûrëti");
                                        userList.setButtonCancel("Atgal");
                                        userList.setClickCancelHandler(AbstractDialog::showParentDialog);
                                        for (UserData data : users) {
                                            ListDialogItem dialogitem = new ListDialogItem();
                                            dialogitem.setData(item);
                                            dialogitem.setItemText(data.username);
                                            userList.addItem(dialogitem);
                                        }
                                        userList.setClickOkHandler((userListDialog, selectedUser) -> {
                                            UserData userData = (UserData) selectedUser.getData();
                                            showUserData(userData, userListDialog);
                                        });
                                        userList.show();
                                    } else {
                                        nameDialog.addLine("{AA1111}Paieðka \"" + text + "\" nesëkminga. Nerasta nei vieno þmogaus.\n");
                                        nameDialog.show();
                                    }
                                } else
                                    nameDialog.show();
                            })
                            .build()
                            .show();
                })
                .item("Ieðkoti tr. priemonës ((Numeris))", dialog -> {
                    throw new NotImplementedException();
                })
                .item("Paieðkomø sàraðas", item -> {
                    List<UserData> wantedUsers = getWanted();
                    if(wantedUsers.size() != 0) {
                        ListDialog wantedUserDialog = ListDialog.create(player, eventManager).build();
                        wantedUserDialog.setCaption(item.getCurrentDialog().getCaption() + ". Ieðkomi asmenys.");
                        wantedUserDialog.setButtonOk("Periûrëti");
                        wantedUserDialog.setButtonCancel("Atgal");
                        wantedUserDialog.setClickCancelHandler(AbstractDialog::showParentDialog);
                        for(UserData user : wantedUsers) {
                            ListDialogItem dialogItem = new ListDialogItem();
                            dialogItem.setData(user);
                            dialogItem.setItemText(String.format("%s (%d)", user.username, user.wantedLevel));
                            wantedUserDialog.addItem(dialogItem);
                        }
                        wantedUserDialog.setClickOkHandler((wantedUserListDialog, selectedWantedUser) -> {
                            showUserData((UserData)selectedWantedUser.getData(), wantedUserDialog);
                        });
                        wantedUserDialog.show();
                    } else {
                        MsgboxDialog.create(player, eventManager)
                                .caption(item.getCurrentDialog().getCaption())
                                .buttonOk("Atgal")
                                .message("{00BB00}Paieðkomø asmenø nëra!")
                                .onClickOk(AbstractDialog::showParentDialog)
                                .build()
                                .show();
                    }
                })
                .item("Kalëjimo duomenø bazë", item -> {
                    Map<String, JailData> prisoners = getPrisoners();
                    if(prisoners.size() != 0) {
                        TabListDialog prisonerListDialog = TabListDialog.create(player,eventManager).build();
                        prisonerListDialog.setCaption(item.getCurrentDialog().getCaption() + ". Kaliniø sàraðas");
                        prisonerListDialog.setHeader(1, "Vardas");
                        prisonerListDialog.setHeader(2, "Likæs laikas(min)");
                        prisonerListDialog.setHeader(3, "Uþdarë");
                        prisonerListDialog.setButtonOk("Gerai");
                        prisonerListDialog.setButtonCancel("Atgal");
                        prisonerListDialog.setClickCancelHandler(AbstractDialog::showParentDialog);
                        prisonerListDialog.setClickOkHandler((d, i) -> {
                            d.showParentDialog();
                        });
                        for(String prisonerName : prisoners.keySet()) {
                            JailData data = prisoners.get(prisonerName);
                            TabListDialogItem prisonerDialogItem = new TabListDialogItem();
                            prisonerDialogItem.setItemText(String.format("%s\t%d\t%s",
                                    prisonerName, data.getTime() / 60, data.getJailer()));
                        }
                        prisonerListDialog.show();
                    }
                })
                .item("Pridëti prie paieðkomø asmenø", item -> {
                    InputDialog.create(player, eventManager)
                            .caption(item.getCurrentDialog().getCaption() + ". Naujo asmens praneðimas")
                            .buttonOk("Tæsti")
                            .buttonCancel("Atgal")
                            .message("Áveskite átariamojo vardà ir pavardæ" +
                                    "Pavyzdys: Vardenis_Pavardenis")
                            .onClickOk((suspectDialog, suspectName) -> {
                                if(suspectName != null && !suspectName.isEmpty()) {
                                    InputDialog.create(player, eventManager)
                                            .caption(suspectDialog.getCaption() + "(2)")
                                            .buttonOk("Baigti")
                                            .buttonOk("Cancel")
                                            .onClickCancel(AbstractDialog::showParentDialog)
                                            .message("Áveskite átarimus kuriuos norite pareikðti " + suspectName)
                                            .onClickOk((suspectDialog2, suspectReason) -> {
                                                if(suspectReason != null && !suspectReason.isEmpty()) {
                                                    LtrpPlayer suspect = LtrpPlayer.get(suspectName);
                                                    if(suspect != null) {
                                                        player.getJob().sendMessage(Color.POLICE, String.format("[LSPD] Asmuo %s gavo áskaita nuo pareigûno: %s, áskaita: %s", suspectName, player.getName(), suspectReason));
                                                        suspect.sendMessage(Color.POLICE, String.format("[LSPD] Policininkas %s áraðë jums áskaità , esate kaltinamas %s, tai yra %d jûsø áskaita.", player.getName(), suspectReason, suspect.getWantedLevel()));
                                                    }
                                                    LtrpGamemode.getDao().getPlayerDao().insertCrime(new PlayerCrime(suspectName, suspectReason, player.getName(), 0));
                                                    updateWantedLevel(suspectName);
                                                    item.getCurrentDialog().show();
                                                } else {
                                                    player.sendErrorMessage("Netinkama prieþastis");
                                                    suspectDialog2.show();
                                                }
                                            })
                                            .build()
                                            .show();
                                } else {
                                    player.sendErrorMessage("Netinkamas átariamojo vardas");
                                    suspectDialog.show();
                                }
                            })
                            .build().show();
                })
                .item("Paskelbti tr. priemonæ paieðkomà", item -> {
                    InputDialog.create(player, eventManager)
                            .caption(item.getCurrentDialog().getCaption() + ". Naujos transporto priemonës praneðimas")
                            .buttonOk("Tæsti")
                            .buttonCancel("Atgal")
                            .message("Áveskite transporto priemonës valstybiná numerá." +
                                    "Formatas: ABC-000")
                            .onClickOk((suspectDialog, suspectPlate) -> {
                                if(suspectPlate != null && !suspectPlate.isEmpty()) {
                                    //suspectPlate = suspectPlate.replaceAll(" ", "-");
                                    InputDialog.create(player, eventManager)
                                            .caption(suspectDialog.getCaption() + "(2)")
                                            .buttonOk("Baigti")
                                            .buttonOk("Cancel")
                                            .onClickCancel(AbstractDialog::showParentDialog)
                                            .message("Áveskite transporto priemonës " + suspectPlate + " paieðkos prieþastá")
                                            .onClickOk((suspectDialog2, suspectReason) -> {
                                                if(suspectReason != null && !suspectReason.isEmpty()) {
                                                    player.getJob().sendMessage(Color.POLICE, String.format("[LSPD] Tr. priemonë, kurios valstybiniai numeriai %s buvo átrauka pareigûno %s á áskaita.", suspectPlate, player.getName()));
                                                    player.getJob().sendMessage(Color.POLICE, "[LSPD] Nurodyta áskaitos prieþastis: " +  suspectReason);
                                                    item.getCurrentDialog().show();
                                                    LtrpGamemode.getDao().getVehicleDao().insertCrime(new VehicleCrime(suspectPlate, player.getCharName(), suspectReason, 0));
                                                } else {
                                                    player.sendErrorMessage("Netinkama prieþastis");
                                                    suspectDialog2.show();
                                                }
                                            })
                                            .build()
                                            .show();
                                } else {
                                    player.sendErrorMessage("Neáraðë tr. priemonës numeriø.");
                                    suspectDialog.show();
                                }
                            })
                            .build()
                            .show();

                })
                .item("Paieðkomø tr. priemoniø sàraðas", item -> {
                    throw new NotImplementedException();
                })
                .item("Areðtuotu tr. priemoniø sàraðas", item -> {
                    throw new NotImplementedException();
                })
                .item("Iðkvietimø registras", item -> {
                    throw new NotImplementedException();
                })
                .build()
                .show();

    }


    private void showUserData(UserData user, AbstractDialog parentDialog) {
        MsgboxDialog.create(getPlayer(), getEventManager())
                .parentDialog(parentDialog)
                .caption(parentDialog.getCaption() + ". " + user.username + " perþiûra.")
                .buttonOk("Atgal")
                .message("\t" + user.username +
                        "\n\n\nAmþius: " + user.age +
                        "\nÁskaitø skaièius: " + user.wantedLevel +
                        "\nLytis: " + (user.sex == 1 ? "Vyras" : "Moteris") +
                        "\nKilmë: " + user.origin)
                .onClickOk(AbstractDialog::showParentDialog)
                .build()
                .show();
    }



    private List<UserData> getByUsername(String searchKeyword) {
        ArrayList<UserData> users = new ArrayList<>();
        try (
                Connection connection = LtrpGamemode.getDao().getConnection();
                PreparedStatement stmt = connection.prepareStatement("SELECT id, username, sex, age, origin, wanted_level FROM players WHERE username LIKE(?) LIMIT 15")
            ) {
            stmt.setString(1, "%" + searchKeyword + "%");
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                UserData data = new UserData();
                data.id = result.getInt("id");
                data.username = result.getString("username");
                data.sex = result.getInt("sex");
                data.origin = result.getString("origin");
                data.wantedLevel = result.getInt("wanted_level");
                users.add(data);
            }
        } catch(SQLException e) {
            logger.error("Error executing user search. Message:" + e.getMessage());
        }
        return users;
    }

    private void updateWantedLevel(String username) {
        try (
                Connection con = LtrpGamemode.getDao().getConnection();
                PreparedStatement stmt = con.prepareStatement("UPDATE players SET wanted_level = wanted_level + 1 WHERE username = ?");
                ) {
            stmt.setString(1, username);
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private List<UserData> getWanted() {
        ArrayList<UserData> users = new ArrayList<>();
        try (
                Connection con = LtrpGamemode.getDao().getConnection();
                Statement stmt = con.createStatement();
                ) {
            ResultSet result = stmt.executeQuery("SELECT id, username, sex, age, origin, wanted_level FROM players WHERE wanted_level > 0 ORDER BY wanted_level DESC");
            while(result.next()) {
                UserData data = new UserData();
                data.id = result.getInt("id");
                data.username = result.getString("username");
                data.sex = result.getInt("sex");
                data.origin = result.getString("origin");
                data.wantedLevel = result.getInt("wanted_level");
                users.add(data);
            }
        } catch(SQLException e) {
            logger.error("Error executing wanted user search. Message:" + e.getMessage());
        }
        return users;
    }

    private Map<String, JailData> getPrisoners() {
        Map<String, JailData> prisoners = new HashMap<>();
        try (
                Connection con = LtrpGamemode.getDao().getConnection();
                PreparedStatement stmt = con.prepareStatement("SELECT player_jailtime.*, players.username FROM player_jailtime LEFT JOIN players ON players.id = player_jailtime.player_id WHERE `type` = ? ORDER BY jail_date DESC");
                ) {
            stmt.setString(1, JailData.JailType.InCharacter.name());
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                String username = result.getString("username");
                JailData data = new JailData(result.getInt("id"), null, JailData.JailType.InCharacter, result.getInt("remaining_time"), result.getString("jailer_name"));
                prisoners.put(username, data);
            }
        } catch(SQLException e) {
            logger.error("Error executing prisoner search. Message:" + e.getMessage());
        }
        return prisoners;
    }

    @Override
    public boolean isShown() {
        return shown;
    }


    private class UserData {
        private int id, age, wantedLevel, sex;
        private String username, origin;

        public UserData() {

        }
    }

}
