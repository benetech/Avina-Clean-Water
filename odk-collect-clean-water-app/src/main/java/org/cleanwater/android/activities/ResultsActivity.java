package org.cleanwater.android.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.cleanwater.android.R;
import org.javarosa.core.model.FormDef;
import org.javarosa.form.api.FormEntryCaption;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryModel;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.logic.FormController;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by animal@martus.org on 5/26/15.
 */
public class ResultsActivity extends Activity {

    private static final String PERSONALIZATION_GROUP_TAG = "/AVINA_proto_1/personalization_group";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<GroupColumn> groupColumns = createGroupColumnsFromForm();
        fillBarChart(groupColumns);
    }

    private ArrayList<GroupColumn> createGroupColumnsFromForm() {
        FormController formController = MainApplication.getInstance().getFormController();
        FormDef formDef = formController.getFormDef();

        FormEntryModel model = new FormEntryModel(formDef);
        FormEntryController formEntryController = new FormEntryController(model);
        ArrayList<GroupColumn> groupColumns = new ArrayList();
        int currentEvent;
        while ((currentEvent = formEntryController.stepToNextEvent()) != FormEntryController.EVENT_END_OF_FORM) {
            if (currentEvent == FormEntryController.EVENT_GROUP) {
                FormEntryCaption captionPrompt = formEntryController.getModel().getCaptionPrompt();
                if (shouldSkipGroup(captionPrompt))
                    continue;

                GroupColumn groupColumn = new GroupColumn(captionPrompt.getShortText());
                groupColumns.add(groupColumn);
                while ((formEntryController.stepToNextEvent()) == FormEntryController.EVENT_QUESTION) {
                    FormEntryPrompt formEntryPrompt = formEntryController.getModel().getQuestionPrompt();
                    groupColumn.put(formEntryPrompt.getQuestionText(), formEntryPrompt.getAnswerText());
                }

                formEntryController.stepToPreviousEvent();
            }
        }

        return groupColumns;
    }

    private boolean shouldSkipGroup(FormEntryCaption formEntryCaption) {
        String elementReference = formEntryCaption.getFormElement().getBind().getReference().toString();
        if (elementReference.equals(PERSONALIZATION_GROUP_TAG))
            return true;

        return false;
    }

    private void fillBarChart(ArrayList<GroupColumn> groupColumns) {
        setContentView(R.layout.results_activity);
        BarChart chart = (BarChart) findViewById(R.id.results_bar_chart);
        customizeXAxis(chart);
        customizeYAxis(chart);

        BarData lineData = createBarChartData(groupColumns);
        chart.setData(lineData);
        chart.invalidate();
    }

    private BarData createBarChartData(ArrayList<GroupColumn> groupColumns) {
        ArrayList<BarEntry> barEntries = new ArrayList();
        float totalPercentage = 0;
        for (int index = 0; index < groupColumns.size(); ++index) {
            GroupColumn groupColumn = groupColumns.get(index);
            float percentOfQuestionsWithAnswers = calculatePercentage(groupColumn);
            totalPercentage =+ percentOfQuestionsWithAnswers;
            BarEntry barEntry = new BarEntry(percentOfQuestionsWithAnswers, index);
            barEntries.add(barEntry);
        }

        totalPercentage = totalPercentage / groupColumns.size();
        BarEntry barEntry = new BarEntry(totalPercentage, 8);
        barEntries.add(barEntry);

        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColor(getResources().getColor(R.color.barchart_color));
        ArrayList<BarDataSet> barDataSets = new ArrayList();
        barDataSets.add(barDataSet);

        BarData barData = new BarData(getXAxisStaticNames(), barDataSets);
        barData.setValueFormatter(new IntegerPercentFormatter());

        return barData;
    }

    private float calculatePercentage(GroupColumn groupColumn) {
        float questionsWithAnswersCount = groupColumn.getQuestionsWithAnswersCount();
        float questionCount = groupColumn.getQuestionCount();
        if (questionsWithAnswersCount == 0)
            return 0;

        final int COUNT_EACH_ANSWER_TWICE = 2;
        return (questionsWithAnswersCount * COUNT_EACH_ANSWER_TWICE) / questionCount;
    }

    private void customizeYAxis(BarChart chart) {
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setTextSize(10f);
        yAxis.setAxisMaxValue(1.0f);
        yAxis.setTextColor(Color.BLACK);
    }

    private void customizeXAxis(BarChart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setSpaceBetweenLabels(1);
        xAxis.setLabelsToSkip(0);
        xAxis.setValues(Arrays.asList(getXAxisStaticNames()));
    }

    private String[] getXAxisStaticNames() {
        return new String[]{
                "Organización",
                "Administración",
                "Operación",
                "Saneamiento",
                "Educación",
                "Recurso Hídrico",
                "Residuous Sólidos",
                "Comunicación",
                "Total",
        };
    }
}
