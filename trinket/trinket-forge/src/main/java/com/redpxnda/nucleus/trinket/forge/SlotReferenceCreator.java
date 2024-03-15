package com.redpxnda.nucleus.trinket.forge;

import com.redpxnda.nucleus.trinket.curiotrinket.CommonSlotReference;
import top.theillusivec4.curios.api.SlotContext;

public class SlotReferenceCreator {
    public static CommonSlotReference get(SlotContext context){
        return new CommonSlotReference() {
            @Override
            public String getSlotGroupId() {
                return "curious";
            }

            @Override
            public boolean isTrinket() {
                return false;
            }

            @Override
            public String getSlotId() {
                return context.identifier();
            }

            @Override
            public int getSlotIndex() {
                return context.index();
            }
        };
    }
}
