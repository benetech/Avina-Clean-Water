package org.cleanwater.android.activities;

import org.cleanwater.android.R;

/**
 * Created by animal@martus.org on 5/29/15.
 */
public class SummaryCellModerateExpansionValues extends AbstractSummaryCellValues {
    @Override
    public int getLabelResourceId() {
        return R.string.moderate_expansion_label;
    }

    @Override
    public int getColorResourceId() {
        return R.color.moderate_expansion_color;
    }

    @Override
    public boolean isModerateExpansionCell() {
        return true;
    }
}