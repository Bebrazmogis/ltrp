package lt.ltrp;

import lt.ltrp.dao.DrugAddictionDao;
import lt.ltrp.data.PlayerAddiction;
import lt.ltrp.data.PlayerDrugs;
import lt.ltrp.event.PaydayEvent;
import lt.ltrp.event.player.PlayerDataLoadEvent;
import lt.ltrp.event.player.PlayerUseDrugsEvent;
import lt.ltrp.object.DetoxEffect;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.WeedItem;
import lt.ltrp.object.drug.DrugItem;
import lt.ltrp.object.impl.*;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * @author Bebras
 *         2016.04.05.
 */
public class DrugController implements Destroyable {

    private EventManagerNode managerNode;
    private Timer addictionTimer;
    private Timer drugEffectTimer;
    private boolean isDestroyed;
    private Map<LtrpPlayer, Queue<DetoxEffect>> playerDrugEffectQueue;

    public DrugController(EventManager eventManager, DrugAddictionDao drugAddictionDao) {
        this.managerNode = eventManager.createChildNode();
        this.playerDrugEffectQueue = new HashMap<>();

        this.managerNode.registerHandler(PlayerDataLoadEvent.class, e -> {
            PlayerDrugs drugs = drugAddictionDao.get(e.getPlayer());
            if(drugs != null)
                e.getPlayer().setDrugs(drugs);
        });

        this.managerNode.registerHandler(PaydayEvent.class, e -> {
            new Thread(() -> {
                LtrpPlayer.get().stream()
                        // Loop through all drug addicts
                        .filter(p -> p.getDrugs().getAddictionCount() > 0)
                        .forEach(p -> {
                            PlayerDrugs drugs = p.getDrugs();
                            // Loop through all the types of drugs the user is taking
                            drugs.getTypes().forEach(c -> {
                                PlayerAddiction addiction = drugs.getAddiction(c);
                                // Time has passed, his addiction level goes down
                                addiction.setLevel(addiction.getLevel() - 1);
                                // If the level is 0, he's no longer an addict
                                if(addiction.getLevel() == 0)
                                    drugAddictionDao.remove(addiction);
                                else
                                    drugAddictionDao.update(addiction);
                            });
                        });
            }).start();
        });

        this.managerNode.registerHandler(PlayerUseDrugsEvent.class, e -> {
            PlayerDrugs drugs = e.getPlayer().getDrugs();
            Class<? extends DrugItem> type = e.getDrugType();
            PlayerAddiction addiction = drugs.getAddiction(type);
            // First use
            if(addiction.getLevel() == 1) {
                drugAddictionDao.insert(addiction);
            } else {
                drugAddictionDao.update(addiction);
            }
        });


        // This timer merely adds detox effects to a queue
        // A threaded, java.util.Timer could be here instead although the Queue is not thread safe
        this.addictionTimer = Timer.create(5*60*1000, (i) -> {
            LtrpPlayer.get().stream()
                    .filter(p -> p.getDrugs().getAddictionCount() > 0)
                    .forEach(p -> {
                        PlayerDrugs drugs = p.getDrugs();
                        Queue<DetoxEffect> effectQueue;
                        if(playerDrugEffectQueue.containsKey(p))
                            effectQueue = playerDrugEffectQueue.get(p);
                        else {
                            effectQueue = new ArrayDeque<>();
                            playerDrugEffectQueue.put(p, effectQueue);
                        }
                        int level;
                        if ((level = drugs.getAddictionLevel(EctazyItem.class)) > 0) {
                            if (level > 20) {
                                effectQueue.offer(new FadeEffect(p, 3));
                            } else if (level > 15) {
                                effectQueue.offer(new FallAnimEffect(p));
                            } else if (level > 10) {
                                effectQueue.offer(new HealthReduceEffect(p, 5));
                            }
                        }
                        if ((level = drugs.getAddictionLevel(AmphetamineItem.class)) > 0) {
                            if (level > 15) {
                                effectQueue.offer(new FadeEffect(p, 5 + level / 5));
                            } else if (level > 10) {
                                effectQueue.offer(new FallAnimEffect(p));
                            } else if (level > 5) {
                                effectQueue.offer(new HealthReduceEffect(p, 5));
                            }
                        }
                        if(drugs.getAddictionLevel(WeedItem.class) > 0) {
                            effectQueue.offer(new HealthReduceEffect(p, 5));
                        }

                        if ((level = drugs.getAddictionLevel(HeroinItem.class)) > 0) {
                            if (level > 12) {
                                effectQueue.offer(new FadeEffect(p, 5 + level / 5));
                            } else if (level > 7) {
                                effectQueue.offer(new FallAnimEffect(p));
                            } else if (level > 2) {
                                effectQueue.offer(new HealthReduceEffect(p, 5));
                            }
                        }
                    });
        });
        this.addictionTimer.start();

        // Basically this delays the effects so that not all of them would happen at once
        drugEffectTimer = Timer.create(4000, i -> {
            playerDrugEffectQueue.keySet().forEach(p -> {
                Queue<DetoxEffect> effects = playerDrugEffectQueue.get(p);
                if(effects != null) {
                    effects.poll().start();
                }
            });
        });
        this.drugEffectTimer.start();
    }


    @Override
    public void destroy() {
        isDestroyed = true;
        managerNode.cancelAll();
        if(addictionTimer != null)
            addictionTimer.destroy();
        if(drugEffectTimer != null)
            drugEffectTimer.destroy();
        playerDrugEffectQueue.values().stream().forEach(c -> c.forEach(DetoxEffect::destroy));
        playerDrugEffectQueue.clear();
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }
}
