package org.cleanwater.android.activities;

import org.cleanwater.android.R;

/**
 * Created by animal@martus.org on 5/29/15.
 */
public class RisingCellValues extends AbstractSummaryCellValues {

    @Override
    public int getPercentRatingLabelId() {
        return R.string.rising_percent_bounds_label;
    }

    @Override
    public int getLabelResourceId() {
        return R.string.rising_label;
    }

    @Override
    public int getColorResourceId() {
        return R.color.rising_color;
    }

    @Override
    public boolean isRisingCell() {
        return true;
    }
}
