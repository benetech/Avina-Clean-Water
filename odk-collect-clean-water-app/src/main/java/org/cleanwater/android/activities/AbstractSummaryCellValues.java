package org.cleanwater.android.activities;

import android.graphics.Color;

/**
 * Created by animal@martus.org on 5/29/15.
 */
abstract public class AbstractSummaryCellValues {
    abstract public int getLabelResourceId();
    abstract public int getColorResourceId();

    public boolean isModerateExpansionCell() {
        return false;
    }

    public boolean isAdvancedExpantionCell() {
        return false;
    }

    public boolean isRisingCell() {
        return false;
    }

    public boolean isConsolidatedCell() {
        return false;
    }
}
