package org.cleanwater.android.activities;

import org.cleanwater.android.R;

/**
 * Created by animal@martus.org on 5/29/15.
 */
public class SummaryCellConsolidationValues extends AbstractSummaryCellValues {
    @Override
    public int getLabelResourceId() {
        return R.string.consolidation_label;
    }

    @Override
    public int getColorResourceId() {
        return R.color.consolidation_color;
    }

    @Override
    public boolean isConsolidatedCell() {
        return true;
    }
}
