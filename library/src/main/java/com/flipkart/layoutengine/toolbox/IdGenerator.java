package com.flipkart.layoutengine.toolbox;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulates the R class. Useful to given unique ID for use in {@link android.view.View#setId(int)} method.
 * An ID which doesnt conflict with aapt's ID is ensured. Please ensure that all dynamic ID call go through this class to ensure uniqueness with other dynamic IDs.
 */
public class IdGenerator {
    private static IdGenerator ourInstance = new IdGenerator();

    public static IdGenerator getInstance() {
        return ourInstance;
    }

    private final HashMap<String,Integer> idMap = new HashMap<String, Integer>();
    private final AtomicInteger sNextGeneratedId = new AtomicInteger(1);


    private IdGenerator() {
    }

    /**
     * Generates and returns a unique id, for the given key.
     * If key exists, returns old value.
     * Ensure that all
     * @param idKey
     * @return a unique ID integer for use with {@link android.view.View#setId(int)}.
     */
    public synchronized int getUnique(String idKey)
    {
        Integer existingId = idMap.get(idKey);
        if(existingId == null)
        {
            int newId = generateViewId();
            idMap.put(idKey,newId);
            existingId = newId;
        }
        return existingId;
    }

    /**
     * Taken from Android View Source code API 17+
     *
     * Generate a value suitable for use.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    private int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}
