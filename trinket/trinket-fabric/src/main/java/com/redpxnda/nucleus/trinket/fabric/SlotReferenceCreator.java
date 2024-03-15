package com.redpxnda.nucleus.trinket.fabric;

import com.redpxnda.nucleus.trinket.curiotrinket.CommonSlotReference;
import dev.emi.trinkets.api.SlotReference;

public class SlotReferenceCreator {
    public static CommonSlotReference get(SlotReference slotReference){
        return new CommonSlotReference() {
            @Override
            public String getSlotGroupId() {
                return slotReference.inventory().getSlotType().getGroup();
            }

            @Override
            public boolean isTrinket() {
                return true;
            }

            @Override
            public String getSlotId() {
                return slotReference.inventory().getSlotType().getName();
            }

            @Override
            public int getSlotIndex() {
                return slotReference.index();
            }
        };
    }
}
