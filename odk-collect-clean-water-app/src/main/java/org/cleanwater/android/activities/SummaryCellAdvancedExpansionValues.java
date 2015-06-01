package org.cleanwater.android.activities;

import org.cleanwater.android.R;

/**
 * Created by animal@martus.org on 5/29/15.
 */
public class SummaryCellAdvancedExpansionValues extends AbstractSummaryCellValues {
    @Override
    public int getLabelResourceId() {
        return R.string.advanced_expansion_label;
    }

    @Override
    public int getColorResourceId() {
        return R.color.advanced_expansion_color;
    }

    @Override
    public boolean isAdvancedExpantionCell() {
        return true;
    }
}
