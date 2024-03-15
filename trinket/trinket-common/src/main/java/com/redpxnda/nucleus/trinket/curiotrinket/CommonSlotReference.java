package com.redpxnda.nucleus.trinket.curiotrinket;

/**
 * Common SlotReference for Trinkets and Curious
 */
public interface CommonSlotReference {
    /**
     * for Trinket(fabric) this will return the base Slot Id (leg/feet etc)
     * for Curious(forge) this will return "curious" since there is no group equivalent
     * @return either "curious" or parent Slot ID
     */
    String getSlotGroupId();

    /**
     * @return will be true for Trinkets(fabric) and false for Curious(forge)
     */
    boolean isTrinket();

    /**
     * The main SlotId of the slot
     * @return
     */
    String getSlotId();

    /**
     * Slot Index
     * @return
     */
    int getSlotIndex();
}
