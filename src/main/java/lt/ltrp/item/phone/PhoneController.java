package lt.ltrp.item.phone;

import lt.ltrp.dao.PhoneDao;
import lt.ltrp.data.Color;
import lt.ltrp.item.ItemPhone;
import lt.ltrp.item.object.InventoryEntity;
import lt.ltrp.item.phone.event.PlayerAnswerPhoneEvent;
import lt.ltrp.item.phone.event.PlayerCallNumberEvent;
import lt.ltrp.item.phone.event.PlayerEndCallEvent;
import lt.ltrp.item.phone.event.PlayerSendSmsEvent;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.property.object.Property;
import lt.ltrp.vehicle.object.LtrpVehicle;
import net.gtaun.shoebill.event.player.PlayerTextEvent;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;

import java.util.*;

/**
 * @author Bebras
 *         2016.03.23.
 */
public class PhoneController {

    public static boolean isSpecialPhoneNumber(int phonenumber) {
        switch(phonenumber) {
            case 911:
            case 816:
            case 817:
            case 999:
                return true;
            default:
                return false;
        }
    }

    private EventManagerNode node;
    private Map<ItemPhone, Timer> callerTimers;
    private Map<LtrpPlayer, PhoneCall> ongoingCalls;

    public PhoneController(EventManager eventManager, PhoneDao phoneDao) {
        this.node = eventManager.createChildNode();
        this.callerTimers = new HashMap<>();
        this.ongoingCalls = new HashMap<>();

        // TODO special actions whhen tallking(phone in hand)

        this.node.registerHandler(PlayerCallNumberEvent.class, e -> {
            LtrpPlayer player = e.getPlayer();
            player.sendActionMessage("iðsitraukia telefonà ið kiðenës ir surenka numerá.");
            int number = e.getPhoneNumber();

            if (!isSpecialPhoneNumber(number)) {
                ItemPhone contactPhone = ItemPhone.get(number);
                if (contactPhone != null) {
                    if(!contactPhone.isBusy()) {
                        player.sendMessage("Numeris uþimtas, praðome pabandyti vëliau!");
                    } else {
                        ItemPhone callerPhone = e.getCallerPhone();
                        final PhoneCall phonecall = new PhoneCall(player, callerPhone, contactPhone);
                        contactPhone.setPhonecall(phonecall);
                        callerPhone.setPhonecall(phonecall);

                        Timer timer = Timer.create(2000, 4, new Timer.TimerCallback() {
                            @Override
                            public void onTick(int i) {
                                // Now we can start searching for its location
                                for (LtrpPlayer p : LtrpPlayer.get()) {
                                    if (p.getInventory().contains(contactPhone)) {
                                        p.sendStateMessage("kiðenëje skamba telefonas", 2.0f);
                                        p.sendMessage(Color.NEWS, "Jums skambina. Norëdami atsiliepti naudokite /pickup, skambuèiui atmesti /hangup");
                                        return;
                                    }
                                }

                                LtrpVehicle.get().stream()
                                        .filter(vehicle -> vehicle.getInventory()
                                                .contains(contactPhone)).forEach(vehicle -> {
                                    vehicle.sendActionMessage("Kaþkur automobilyje skamba telefonas...");
                                });

                                Property.get().stream()
                                        .filter(property -> property instanceof InventoryEntity && ((InventoryEntity) property).getInventory().contains(contactPhone))
                                                .forEach(property -> {
                                                    property.sendStateMessage("Kaþkur netoli, girdisi telefono skambëjimas");
                                                });
                            }

                            @Override
                            public void onStop() {
                                if (phonecall.getState() != PhoneCall.PhoneCallState.Talking) {
                                    contactPhone.setPhonecall(null);
                                    callerPhone.setPhonecall(null);
                                    player.sendMessage("Niekas neatsakë... Skambutis baigtas");
                                }
                                callerTimers.remove(callerPhone);
                                callerPhone.setBusy(false);
                            }
                        });
                        callerPhone.setBusy(true);
                        callerTimers.put(callerPhone, timer);
                        timer.start();
                    }
                    e.interrupt();
                }
            }
        });

        this.node.registerHandler(PlayerCallNumberEvent.class, HandlerPriority.BOTTOM, e -> {
            e.getPlayer().sendErrorMessage("Numeris nenaudojama arba telefonas ðiuo metu yra iðjungtas.");
        });

        this.node.registerHandler(PlayerSendSmsEvent.class, e -> {
            LtrpPlayer player = e.getPlayer();
            ItemPhone senderPhone = e.getSenderPhone();
            int number = e.getPhoneNumber();
            String text = e.getText();
            if(!isSpecialPhoneNumber(number)) {
                player.sendMessage(Color.SMS_SENT, "SMS:" + text);
                ItemPhone contactPhone = ItemPhone.get(number);
                if(contactPhone != null) {
                    // Now we can start searching for its location
                    for(LtrpPlayer p : LtrpPlayer.get()) {
                        if(p.getInventory().contains(contactPhone)) {
                            p.sendStateMessage("kiðenëje suskamba telefonas", 2.0f);
                            String senderName = contactPhone.getPhoneBook().contains(senderPhone.getPhonenumber()) ?
                                    contactPhone.getPhoneBook().getContactName(senderPhone.getPhonenumber()) :
                                    Integer.toString(senderPhone.getPhonenumber());
                            p.sendMessage(Color.SMS_RECEIVED, "SMS: " + text + ", siuntëjas: " + senderName);
                            p.playSound(1052);
                            phoneDao.addSms(new PhoneSms(senderPhone.getPhonenumber(), contactPhone.getPhonenumber(), new Date(), text));
                            return;
                        }
                    }

                    LtrpVehicle.get().stream()
                            .filter(vehicle -> vehicle.getInventory().contains(contactPhone))
                            .forEach(vehicle -> {
                                vehicle.sendActionMessage("Kaþkur automobilyje suskamba telefonas...");
                            });

                    Property.get()
                            .stream()
                            .filter(property -> property instanceof InventoryEntity && ((InventoryEntity) property).getInventory().contains(contactPhone))
                                    .forEach(property -> {
                                        property.sendStateMessage("Kaþkur netoli, suskamba telefonas");
                                    });
                }
            }
        });

        this.node.registerHandler(PlayerAnswerPhoneEvent.class, e -> {
            PhoneCall call = e.getPhone().getPhonecall();
            call.setState(PhoneCall.PhoneCallState.Talking);
            ongoingCalls.put(e.getPlayer(), call);
            ongoingCalls.put(call.getCaller(), call);
            call.setAnsweredBy(e.getPlayer());

            // The callee name as it appears in the callers phone book
            String contactName = call.getCallerPhone().getPhoneBook().getContactName(call.getRecipientPhone().getPhonenumber());
            // If the callee is not in callers phonebook, show the phone number
            if(contactName == null) {
                contactName = ""+call.getCallerPhone().getPhonenumber();
            }
            call.getCaller().sendMessage(contactName + " atsiliepë..");
            call.getCaller().sendMessage(Color.YELLOW, "Naudokite t kad galbëtumëte telefonu");
            e.getPlayer().sendMessage(Color.YELLOW, "Naudokite t kad galbëtumëte telefonu");
        });

        this.node.registerHandler(PlayerEndCallEvent.class, e -> {
            PhoneCall call = e.getCall();
            call.getRecipientPhone().setPhonecall(null);
            call.getCallerPhone().setPhonecall(null);
            call.setState(PhoneCall.PhoneCallState.Ended);
            e.getPlayer().sendMessage("Skambutis baigtas");
            call.getAnsweredBy().sendMessage(Color.LIGHTRED, "Jis/Ji padëjo telefono ragelá.");
            ongoingCalls.remove(e.getPlayer());
            ongoingCalls.remove(call.getAnsweredBy());
        });


        this.node.registerHandler(PlayerTextEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            PhoneCall call = ongoingCalls.get(player);
            if(call != null) {
                player.sendActionMessage("sako (telefonu): " + e.getText());
                phoneDao.logConversation(call.getCallerPhone().getPhonenumber(), call.getRecipientPhone().getPhonenumber(), e.getText());
                ItemPhone playerPhone;
                ItemPhone targetPhone;
                LtrpPlayer target;
                if(call.getCaller().equals(player)) {
                    playerPhone = call.getCallerPhone();
                    targetPhone = call.getRecipientPhone();

                    target = call.getAnsweredBy();
                } else {
                    playerPhone = call.getRecipientPhone();
                    targetPhone = call.getCallerPhone();
                    target = call.getCaller();
                }
                if(targetPhone.getPhoneBook().contains(playerPhone.getPhonenumber())) {
                    target.sendMessage(Color.LIGHTGREY, targetPhone.getPhoneBook().getContactName(playerPhone.getPhonenumber()) + " sako (telefonu): " + e.getText());
                } else {
                    target.sendMessage(Color.LIGHTGREY, targetPhone.getPhonenumber() + " sako (telefonu): " + e.getText());
                }
                e.interrupt();
            }
        });
    }

    public void destroy() {
        node.cancelAll();
        callerTimers.forEach((k,v) -> v.destroy());
        callerTimers.clear();

    }

}
