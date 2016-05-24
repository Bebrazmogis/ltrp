package lt.ltrp.command;


import lt.ltrp.JobController;
import lt.ltrp.MechanicJobPlugin;
import lt.ltrp.colorpicker.ColorPicker;
import lt.ltrp.constant.Currency;
import lt.ltrp.data.Color;
import lt.ltrp.data.ColorPalettePlayerOffer;
import lt.ltrp.data.PlayerJobData;
import lt.ltrp.dialog.HullRepairMsgDialog;
import lt.ltrp.dialog.RemoveHydraulicsMsgDialog;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import lt.ltrp.session.*;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.constant.VehicleColor;
import net.gtaun.shoebill.constant.VehicleComponentSlot;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.constant.VehicleModelInfoType;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.VehicleDamage;
import net.gtaun.shoebill.object.VehicleParam;
import net.gtaun.util.event.EventManagerNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bebras
 *         2016.02.08.
 */
public class MechanicCommands {

    private static final Logger logger = LoggerFactory.getLogger(MechanicCommands.class);

    private EventManagerNode eventNode;
    private MechanicJobPlugin mechanicPlugin;


    public MechanicCommands(EventManagerNode node) {
        this.eventNode = node;
        this.mechanicPlugin = MechanicJobPlugin.get(MechanicJobPlugin.class);
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        logger.debug("beforeCheck " + cmd + " params" + params);
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = JobController.get().getJobData(player);
        if(jobData.getJob().equals(mechanicPlugin.getJob())) {

            return true;
        } else {
            player.sendErrorMessage("Ðià komandà gali naudoti tik mechanikai!");
        }
        return false;
    }


    @Command
    @CommandHelp("Leidþia kitam þaidëjui parodyti spalvø paletæ")
    public boolean colorPalette(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        } else if(player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage(target.getCharName() + " yra per toli.");
        } else if(target.containsOffer(ColorPalettePlayerOffer.class)) {
            player.sendErrorMessage(target.getCharName() + " jau kaþkas siûlo perþiûrëti spalvø paletæ.");
        } else if(player.equals(target)) {
            player.sendActionMessage("Susiranda spalvø paletæ ir þiûri á jà...");
            ColorPicker.create(player, eventNode, color -> {
                player.sendMessage(VehicleColor.getColorFromId(color), "Pasirinktos spalvos ID yra " + color);
            }).show();
        } else {
            target.sendMessage(Color.NEWS, "Mechanikas " + player.getCharName() + " jums siûlo perþvelgti spalvø paletæ. Naudokite /accept palette arba /decline palette");
            player.sendMessage("Pasiûlymas iðsiøstas, laukite atsakymo.");
            target.getOffers().add(new ColorPalettePlayerOffer(target, player, eventNode));
        }
        return true;
    }

    @Command
    @CommandHelp("Pradeda automobilio taisymà")
    public boolean repair(Player p, @CommandParameter(name = "Sritis kurià taisyti: [variklis/këbulas/viskas]")String action) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = player.isInAnyVehicle() ? LtrpVehicle.getByVehicle(player.getVehicle()) : LtrpVehicle.getClosest(player, 5f);
        if(vehicle == null) {
            player.sendErrorMessage("Jûs turite bûti transporto priemonëje arba ðalia jos!");
        } else if(vehicle.getState().getEngine() == VehicleParam.PARAM_ON) {
            player.sendErrorMessage("Prie transporto priemonës su uþkurtu varikliu dirbti negalite!");
        } else if(mechanicPlugin.isVehicleInSession(vehicle)) {
            player.sendErrorMessage("Kaþkas jau dirba prie ðio automobilio");
        } else if(mechanicPlugin.isPlayerInSession(player)) {
            player.sendErrorMessage("Jûs jau dirbate prie automobilio!");
        } else {
            VehicleDamage damage = vehicle.getDamage();
            final int enginePrice = Math.round(1000f - vehicle.getHealth());
            ListDialogItem engineItem = new ListDialogItem("Variklis", (i) -> {
                if(vehicle.getHealth() >= 1000f) {
                    player.sendErrorMessage("Automobilio varikliui viskas gerai!");
                } else {
                    MsgboxDialog.create(player, eventNode)
                            .caption(vehicle.getModelName() + " variklio remontas")
                            .buttonOk("Taip")
                            .buttonCancel("Ne")
                            .message("Automobilio variklio remontas." +
                                "\n\nAutomobilis: " + vehicle.getModelName() +
                                "\nRemonto kaina: " + enginePrice +
                                "\n\nAr norite pradëti remontà?")
                            .onClickOk(d -> {
                                mechanicPlugin.addRepairSession(new EngineRepairSession(eventNode, player, vehicle, (pl, veh, finished) -> {
                                    if(finished) {
                                        vehicle.setHealth(1000f);
                                        player.giveMoney(-enginePrice);
                                        player.sendMessage("Variklis sutaisytas, darbai baigti!");
                                    }
                                }));
                            })
                            .build()
                            .show();
                }
            });
            int hullPrice = 150 * Integer.bitCount(damage.getLights()) + 200 * Integer.bitCount(damage.getTires()) + 300 * Integer.bitCount(damage.getDoors());
            ListDialogItem hullItem = new ListDialogItem("Këbulas", i -> {
                if(damage.getDoors() == 0 && damage.getLights() == 0 && damage.getPanels() == 0 && damage.getTires() == 0) {
                    player.sendErrorMessage("Ðio automobilio këbului viskas gerai.");
                } else {
                    MsgboxDialog dialog = HullRepairMsgDialog.create(player, eventNode, vehicle, hullPrice);
                    dialog.setClickOkHandler(d -> {
                        mechanicPlugin.addRepairSession(new HullRepairSession(eventNode, player, vehicle, (pl, veh, finished) -> {
                            if(finished) {
                                vehicle.getDamage().set(0, 0, 0, 0);
                                pl.giveMoney(-hullPrice);
                                pl.sendMessage("Këbulas suremontuotas uþ $" + hullPrice + ".");
                            }
                        }));
                    });
                    dialog.show();
                }
            });

            ListDialogItem allItem = new ListDialogItem("Viskas", i -> {
                if(damage.getDoors() == 0 && damage.getLights() == 0 && damage.getTires() == 0 && damage.getPanels() == 0 && vehicle.getHealth() == 1000f) {
                    player.sendErrorMessage("Automobilis idealios bûklës, nëra kà jam taisyti.");
                } else {
                    MsgboxDialog.create(player, eventNode)
                            .caption("Pilnas " + vehicle.getModelName() + " remontas.")
                            .buttonOk("Taip")
                            .buttonCancel("Ne")
                            .message("Automobilis: " + vehicle.getModelName() +
                                    "\nVariklio remonto kaina: " + Currency.SYMBOL + enginePrice +
                                    "\nKëbulo remonto kaina: " + Currency.SYMBOL + hullPrice +
                                    "\nBendra kaina: " + Currency.SYMBOL + (hullPrice + enginePrice) +
                                    "\n\nAr norite pradëti remontà?")
                            .onClickOk(d -> {
                                mechanicPlugin.addRepairSession(new FullRepairSession(eventNode, player, vehicle, (pl, veh, finished) -> {
                                    if (finished) {
                                        vehicle.repair();
                                        pl.giveMoney(-enginePrice);
                                        pl.giveMoney(-hullPrice);
                                        pl.sendMessage("Automobilis visiðkai suremontuotas, tai kainavo " + Currency.SYMBOL + (enginePrice + hullPrice));
                                    }
                                }));
                            })
                            .build()
                            .show();
                }
            });

            if(action != null) {
                if(StringUtils.equalsIgnoreLtCharsAndCase(action, "variklis")) {
                    engineItem.onItemSelect();
                    return true;
                } else if(StringUtils.equalsIgnoreLtCharsAndCase(action, "kebulas")) {
                    hullItem.onItemSelect();
                    return true;
                } else if(StringUtils.equalsIgnoreLtCharsAndCase(action, "viskas")) {
                    allItem.onItemSelect();
                    return true;
                }
            }

            ListDialog.create(player, eventNode)
                    .item(engineItem)
                    .item(hullItem)
                    .item(allItem)
                    .caption("Tvarkymo darbai")
                    .buttonOk("Pasirinkti")
                    .buttonCancel("Iðeiti")
                    .build()
                    .show();

        }
        return true;
    }

    @Command
    @CommandHelp()
    public boolean addMods(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 5f);

        if(vehicle == null) {
            player.sendErrorMessage("Prie jûsø nëra jokios transporto priemonës!");
        } else if(vehicle.getState().getEngine() == VehicleParam.PARAM_ON) {
            player.sendErrorMessage("Prie transporto priemonës su uþkurtu varikliu dirbti negalite!");
        } else if(mechanicPlugin.isVehicleInSession(vehicle)) {
            player.sendErrorMessage("Kaþkas jau dirba prie ðio automobilio");
        } else if(mechanicPlugin.isPlayerInSession(player)) {
            player.sendErrorMessage("Jûs jau dirbate prie automobilio!");
        }else {
           ListDialogItem.ItemSelectHandler<Integer> newWheelHandler = (dialogItem, componentId) -> {
                MsgboxDialog.create(player, eventNode)
                        .caption("Ratai " + dialogItem.getItemText())
                        .message("Ratai: " + dialogItem.getItemText() +
                                "\nKaina: " + Currency.SYMBOL + mechanicPlugin.getJob().getWheelPrice() +
                                "\n\n Ar norite pradëti ratø montavimà?")
                        .buttonOk("Taip")
                        .buttonCancel("Ne")
                        .onClickOk(d -> {
                            if (player.getMoney() > mechanicPlugin.getJob().getWheelPrice()) {
                                mechanicPlugin.addRepairSession(new WheelChangeSession(eventNode, player, vehicle, (pl, veh, finished) -> {
                                    player.giveMoney(-mechanicPlugin.getJob().getWheelPrice());
                                    vehicle.getComponent().add(componentId);
                                    player.sendMessage("Ratai sëkmingai pakeisti.");
                                }));
                            } else {
                                player.sendErrorMessage("Jums neuþtenka pinigø!");
                            }
                        })
                        .build()
                        .show();
            };

            ListDialog modDialog = ListDialog.create(player, eventNode)
                    .caption("Automobilio modifikacijø keitimas")
                    .buttonOk("Pasirinkti")
                    .buttonCancel("Iðeiti")
                    .item("Paðalinti ratus", () -> vehicle.getComponent().get(VehicleComponentSlot.WHEELS) != 0, i -> {
                        mechanicPlugin.addRepairSession(new WheelRemovalSession(eventNode, player, vehicle, (pl, veh, finished) -> {
                            if (finished) {
                                vehicle.getComponent().remove(VehicleComponentSlot.WHEELS);
                                player.sendMessage("Lengvo lydinio ratai nuimti.");
                            }
                        }));
                    })
                    .item("Paðalinti hidraulinæ pakabà", () -> vehicle.getComponent().get(VehicleComponentSlot.HYDRAULICS) != 0, i -> {
                        MsgboxDialog dialog = RemoveHydraulicsMsgDialog.create(player, vehicle, eventNode, mechanicPlugin.getJob().getHydraulicRemovePrice());
                        dialog.setClickOkHandler(d -> {
                            if (player.getMoney() < mechanicPlugin.getJob().getHydraulicRemovePrice()) {
                                player.sendErrorMessage("Jums neuþtenka pinigø iðimti hidraulikai. Tai kainuoja " + Currency.SYMBOL + mechanicPlugin.getJob().getHydraulicRemovePrice());
                            } else {
                                mechanicPlugin.addRepairSession(new HydraulicsRemovalSession(eventNode, player, vehicle, (pl, veh, finished) -> {
                                    if (finished) {
                                        vehicle.getComponent().remove(VehicleComponentSlot.HYDRAULICS);
                                        player.giveMoney(-mechanicPlugin.getJob().getHydraulicRemovePrice());
                                        player.sendMessage("Hidraulika paðalinta sëkmingai.");
                                    }
                                }));
                            }
                        });
                        dialog.show();
                    })
                    .item("Ádiegti hidraulinæ pakabà", () -> vehicle.getComponent().get(VehicleComponentSlot.HYDRAULICS) == 0, i -> {
                        MsgboxDialog.create(player, eventNode)
                                .caption("Hidraulinë pakaba")
                                .message("Automobilis: " + vehicle.getModelName() +
                                    "\nKaina: " + Currency.SYMBOL + mechanicPlugin.getJob().getHydraulicsInstallPrice() +
                                    "\n\nAr norite pradëti darbus?")
                                .buttonOk("Taip")
                                .buttonCancel("Ne")
                                .onClickOk(d -> {
                                    mechanicPlugin.addRepairSession(new HydraulicInstallSession(eventNode, player, vehicle, (pl, veh, finished) -> {
                                        if(finished) {
                                            vehicle.getComponent().add(1087);
                                            player.giveMoney(-mechanicPlugin.getJob().getHydraulicsInstallPrice());
                                            player.sendMessage("Hidraulika sëkmingai ádiegta.");
                                        }
                                    }));
                                })
                                .build()
                                .show();
                    })
                    .build();
            MechanicJobPlugin.WHEEL_COMPONENTS.forEach((k, v) -> modDialog.addItem(v, k, newWheelHandler));
            modDialog.show();
        }
        return true;
    }

    @Command
    @CommandHelp("Pradeda automobilio daþymo procesà")
    public boolean repaint(Player pp, @CommandParameter(name = "Spalvos ID")Integer color) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        LtrpVehicle vehicle = player.isInAnyVehicle() ? LtrpVehicle.getByVehicle(player.getVehicle()) : LtrpVehicle.getClosest(player, 5f);
        if(color == null || VehicleColor.getColorFromId(color) == null) {
            player.sendErrorMessage("Tokios spalvos nëra!");
        } else if(vehicle == null) {
            player.sendErrorMessage("Jûs turite bûti transporto priemonëje arba ðalia jos!");
        } else if(vehicle.getState().getEngine() == VehicleParam.PARAM_ON) {
            player.sendErrorMessage("Prie transporto priemonës su uþkurtu varikliu dirbti negalite!");
        } else if(mechanicPlugin.isVehicleInSession(vehicle)) {
            player.sendErrorMessage("Kaþkas jau dirba prie ðio automobilio");
        } else if(mechanicPlugin.isPlayerInSession(player)) {
            player.sendErrorMessage("Jûs jau dirbate prie automobilio!");
        } else {
            Vector3D size = VehicleModel.getModelInfo(vehicle.getModelId(), VehicleModelInfoType.SIZE);
            final int price = Math.round((size.getX() + size.getY() + size.getZ()) * 10);
            MsgboxDialog.create(player, eventNode)
                    .caption("Dëmesio!")
                    .message("Ar tikrai norite perdaþyti automobilá?\n" +
                            "\nNaujos spalvos kodas: " + color +
                            "\nAutomobilio modelis: " + vehicle.getModelName() +
                            "\nPerdaþymo kain: " + price +
                            "\n\nPasirinkus \"Taip\" prasidës daþymas.")
                    .buttonOk("Taip")
                    .buttonCancel("Ne")
                    .onClickOk(d -> {
                        player.sendMessage(Color.NEWS, "Pradëjote daþymo darbus, jei pasitrauksite per toli nuo transporto priemonës daþymas bus atðauktas!");
                        final RepaintSession session =  new RepaintSession(player, vehicle, (p, v, sucess) -> {
                            if (sucess) {
                                vehicle.setColor(color, color);
                                player.giveMoney(-price);
                                player.sendMessage(VehicleColor.getColorFromId(color), "Daþymo darbai baigti uþ $" + price + "!");
                            }

                        }, eventNode);
                        mechanicPlugin.addRepairSession(session);
                    })
                    .build()
                    .show();
        }
        return true;
    }

}
