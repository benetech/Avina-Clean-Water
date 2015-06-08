package org.cleanwater.android.activities;

import org.cleanwater.android.R;

/**
 * Created by animal@martus.org on 5/29/15.
 */
public class ConsolidationCellValues extends AbstractSummaryCellValues {

    @Override
    public int getPercentRatingLabelId() {
        return R.string.consolidated_percent_bound_label;
    }

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
