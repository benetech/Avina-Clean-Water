package org.cleanwater.android.activities;

import org.cleanwater.android.R;

/**
 * Created by animal@martus.org on 5/29/15.
 */
public class SummaryCellRisingValues extends AbstractSummaryCellValues {
    @Override
    public int getLabelResourceId() {
        return R.string.rising_label;
    }

    @Override
    public int getColorResourceId() {
        return R.color.rising_color;
    }
}
