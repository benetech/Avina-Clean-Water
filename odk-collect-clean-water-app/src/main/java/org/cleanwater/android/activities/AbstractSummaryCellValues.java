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

    public static AbstractSummaryCellValues createSummaryCellValues(int percent) {
        if (percent >= 0 && percent <= 30)
            return new SummaryCellRisingValues();

        if (percent >= 31 && percent <= 55)
            return new SummaryCellModerateExpansionValues();

        if (percent >= 56 && percent <= 80)
            return new SummaryCellAdvancedExpansionValues();

        if (percent >= 81 && percent <= 100)
            return new SummaryCellConsolidationValues();

        return null;
    }
}
