package org.cleanwater.android.activities;

import android.app.Activity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

import org.cleanwater.android.R;

import java.util.ArrayList;

/**
 * Created by animal@martus.org on 5/26/15.
 */
public class ResultsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.results_activity);
        LineChart chart = (LineChart) findViewById(R.id.results_chart);
        chart.invalidate();
        LineData lineData = new LineData(new ArrayList());
        chart.setData(lineData);

        System.out.println("HERERERER");
    }
}
