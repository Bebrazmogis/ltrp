package lt.ltrp.job.policeman.dialog;

import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.PlayerAttach;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.03.05.
 */
public class PoliceGearListDialog  {

    private static final Collection<PoliceGear> GEAR = new ArrayList<>();
    {
        GEAR.add(new PoliceGear(18636, PlayerAttachBone.HEAD, "Policijos kepurë 1"));
        GEAR.add(new PoliceGear(19099, PlayerAttachBone.HEAD, "Policijos kepurë 2"));
        GEAR.add(new PoliceGear(19100, PlayerAttachBone.HEAD, "Policijos kepurë 3"));
        GEAR.add(new PoliceGear(19138, PlayerAttachBone.HEAD, "Policijos akiniai"));
        GEAR.add(new PoliceGear(19141, PlayerAttachBone.HEAD, "Policijos kepurë 4"));
        GEAR.add(new PoliceGear(19200, PlayerAttachBone.HEAD, "Policijos kepurë 5"));
        GEAR.add(new PoliceGear(18637, PlayerAttachBone.HAND_LEFT, "Skydas"));
    }

    public static ListDialog create(LtrpPlayer player, EventManager eventManager) {
        Collection<ListDialogItem> items = new ArrayList<>();
        for(PoliceGear gear : GEAR) {
            items.add(new ListDialogItem(gear, String.format("{FFFFFF}%s[%s]{FFFFFF}", gear.name, player.getAttach().getSlotByBone(gear.bone).isUsed() ? "{FF0000}-" : "{00FF00}+"),
                    (d,o) -> {
                        PlayerAttach attach = player.getAttach();
                        for(PlayerAttach.PlayerAttachSlot slot : attach.getSlots()) {
                            if(slot != null) {
                                if(!slot.isUsed())
                                    slot.set(gear.bone, gear.modelId, new Vector3D(), new Vector3D(), new Vector3D(), 0, 0);
                                else if(slot.getModelId() == gear.modelId)
                                    slot.remove();
                            }
                        }
            }));
        }
        return ListDialog.create(player, eventManager)
                .caption("Spec. policijos apranga")
                .buttonOk("Pasirinkti")
                .items(items)
                .buttonCancel("Uþdaryti")
                .build();
    }

    private class PoliceGear {
        int modelId;
        PlayerAttachBone bone;
        String name;

        public PoliceGear(int modelId, PlayerAttachBone bone, String name) {
            this.modelId = modelId;
            this.bone = bone;
            this.name = name;
        }
    }

}
