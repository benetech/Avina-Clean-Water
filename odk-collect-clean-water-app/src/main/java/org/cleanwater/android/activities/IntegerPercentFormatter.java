package org.cleanwater.android.activities;

import com.github.mikephil.charting.utils.PercentFormatter;

import java.text.NumberFormat;

/**
 * Created by animal@martus.org on 5/28/15.
 */
public class IntegerPercentFormatter extends PercentFormatter {

    private NumberFormat numberFormat;

    public IntegerPercentFormatter() {
        numberFormat = NumberFormat.getPercentInstance();
    }

    @Override
    public String getFormattedValue(float value) {
        return numberFormat.format(value);
    }
}
