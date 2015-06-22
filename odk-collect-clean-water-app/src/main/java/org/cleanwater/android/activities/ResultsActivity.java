package org.cleanwater.android.activities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.cleanwater.android.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by animal@martus.org on 5/26/15.
 */
public class ResultsActivity extends Activity {

    private FormDefParser formDefParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.results_activity);

        formDefParser = new FormDefParser();
        fillScoresDetailsTable();
        fillScoresSummaryTable();
        fillBarChart();
    }

    private void fillScoresDetailsTable() {
        ArrayList<RowData> rowDataList = getFormDefParser().getGroupReferences();
        ArrayList<TableRow> tableRows = new ArrayList();
        tableRows.add(createTableTitleHeaderRow(8));
        tableRows.add(createScoresDetailsColumnHeadersRows());

        for (int index = 0; index < rowDataList.size(); ++index) {
            RowData rowData = rowDataList.get(index);
            TableRow tableRow = createTableRow();

            TextView indexCell = createStyledTextView(getIndexColumnWeight());
            TextView variableCell = createStyledTextView(getVariableColumnWeight());
            TextView numberOfScoresCell = createStyledTextView();
            TextView maxScoreCell = createStyledTextView();
            TextView risingCell = createStyledTextView();
            TextView moderateExpansionCell = createStyledTextView();
            TextView advancedExpansionCell = createStyledTextView();
            TextView consolidatedCell = createStyledTextView();

            setGravityToCenter(indexCell);
            setGravityToCenter(numberOfScoresCell);
            setGravityToCenter(maxScoreCell);

            indexCell.setText(Integer.toString(index + 1));
            variableCell.setText(rowData.getGroupLabel());
            numberOfScoresCell.setText(Integer.toString(rowData.getQuestionCount()));
            maxScoreCell.setText(Integer.toString(rowData.getMaxScore()));

            if (rowData.hasQuestions()) {
                int percentage = rowData.calculatePercentageAsRoundedInt();
                AbstractSummaryCellValues summaryCellValues = AbstractSummaryCellValues.createSummaryCellValues(percentage);
                double score = rowData.calculateScore();
                if (summaryCellValues != null) {
                    if (summaryCellValues.isRisingCell())
                        configureCell(risingCell, score, summaryCellValues);
                    else if (summaryCellValues.isModerateExpansionCell())
                        configureCell(moderateExpansionCell, score, summaryCellValues);
                    else if (summaryCellValues.isAdvancedExpantionCell())
                        configureCell(advancedExpansionCell, score, summaryCellValues);
                    else if (summaryCellValues.isConsolidatedCell())
                        configureCell(consolidatedCell, score, summaryCellValues);
                }
            }

            tableRow.addView(indexCell);
            tableRow.addView(variableCell);
            tableRow.addView(numberOfScoresCell);
            tableRow.addView(maxScoreCell);
            tableRow.addView(risingCell);
            tableRow.addView(moderateExpansionCell);
            tableRow.addView(advancedExpansionCell);
            tableRow.addView(consolidatedCell);

            tableRows.add(tableRow);
        }

        fillTable(tableRows, R.id.scores_details_table);
    }

    private float getIndexColumnWeight() {
        return 0.25f;
    }

    private float getVariableColumnWeight() {
        return 1.5f;
    }

    private TextView createStyledTextView() {
        return createStyledTextView(1.0f);
    }

    private TextView createStyledTextView(float textViewWeight) {
        final TextView textView = new TextView(this);
        textView.setPadding(5, 5, 5, 5);
        final TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, textViewWeight);
        textView.setLayoutParams(params);
        textView.setBackgroundResource(R.drawable.table_row);

        return textView;
    }

    private void configureCell(TextView textView, double percentage, AbstractSummaryCellValues summaryCellValues) {
        setBackgroundToColorWithoutLoosingBorder(textView, summaryCellValues);
        setGravityToCenter(textView);
        textView.setText(Double.toString(percentage));
    }

    private void setBackgroundToColorWithoutLoosingBorder(TextView textView, AbstractSummaryCellValues summaryCellValues) {
        int colorResourceId = summaryCellValues.getColorResourceId();
        final int backgroundColor = getResources().getColor(colorResourceId);
        Drawable drawable = getResources().getDrawable(R.drawable.table_row);
        drawable.setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);
        textView.setBackground(drawable);
    }

    private void fillScoresSummaryTable() {
        ArrayList<RowData> rowDataList = getFormDefParser().getGroupReferences();
        ArrayList<TableRow> allRows = new ArrayList();
        allRows.add(createTableTitleHeaderRow(4));
        allRows.add(createTableColumnHeaderRow());

        double totalScore = 0;
        double totalMaxScore = 0;
        for (RowData rowData : rowDataList) {
            TableRow tableRow = createTableRow();

            TextView nameCell = createStyledTextView();
            TextView scoreCell = createStyledTextView();
            TextView percentCell = createStyledTextView();
            TextView stageCell = createStyledTextView();

            setGravityToCenter(scoreCell);
            setGravityToCenter(percentCell);
            setGravityToCenter(stageCell);

            nameCell.setText(rowData.getGroupLabel());
            scoreCell.setText(getString(R.string.non_applicable));
            percentCell.setText(getString(R.string.non_applicable));

            if (rowData.hasQuestions()) {
                totalScore += rowData.calculateScore();
                totalMaxScore += rowData.getMaxScore();
                scoreCell.setText(Double.toString(rowData.calculateScore()));

                IntegerPercentFormatter formatter = new IntegerPercentFormatter();
                final double calculatedPercentage = rowData.calculatePercentageAsDecimal();
                String formattedPercentValue = formatter.getFormattedValue((float) calculatedPercentage);
                percentCell.setText(formattedPercentValue);

                final double percent = calculatedPercentage * 100;
                final String calculatedRoundedPercentageAsString = NumberFormat.getIntegerInstance().format(percent);
                final int calculatedRoundedPercentage = Integer.parseInt(calculatedRoundedPercentageAsString);
                final AbstractSummaryCellValues summaryCellValues = AbstractSummaryCellValues.createSummaryCellValues(calculatedRoundedPercentage);

                setBackgroundToColorWithoutLoosingBorder(stageCell, summaryCellValues);
                stageCell.setText(summaryCellValues.getLabelResourceId());
            }

            tableRow.addView(nameCell);
            tableRow.addView(scoreCell);
            tableRow.addView(percentCell);
            tableRow.addView(stageCell);

            allRows.add(tableRow);
        }

        final TableRow totalsRow = createTableRow();
        TextView totalLabelCell = createStyledTextView();
        totalLabelCell.setTextColor(getResources().getColor(R.color.barchart_color));
        totalLabelCell.setText(getString(R.string.total_row_name));
        totalsRow.addView(totalLabelCell);

        TextView totalScoreCell = createStyledTextView();
        setGravityToCenter(totalScoreCell);
        totalScoreCell.setTextColor(getResources().getColor(R.color.barchart_color));
        totalScoreCell.setText(Double.toString(totalScore));
        totalsRow.addView(totalScoreCell);

        TextView totalPercentCell = createStyledTextView();
        totalPercentCell.setGravity(Gravity.RIGHT);
        totalPercentCell.setTextColor(getResources().getColor(R.color.barchart_color));
        IntegerPercentFormatter formatter = new IntegerPercentFormatter();
        double percentAsDecimal = totalScore / totalMaxScore;
        totalPercentCell.setText(formatter.getFormattedValue((float)percentAsDecimal));
        totalsRow.addView(totalPercentCell);

        TextView totalStageCell = createStyledTextView();
        setGravityToCenter(totalStageCell);
        final int calculatedRoundedPercentage = convertToRoundedPercent(percentAsDecimal);
        final AbstractSummaryCellValues summaryCellValues = AbstractSummaryCellValues.createSummaryCellValues(calculatedRoundedPercentage);
        setBackgroundToColorWithoutLoosingBorder(totalStageCell, summaryCellValues);
        totalStageCell.setText(summaryCellValues.getLabelResourceId());
        totalsRow.addView(totalStageCell);

        allRows.add(totalsRow);

        fillTable(allRows, R.id.scores_summary_table);
    }

    private int convertToRoundedPercent(double percentAsDecimal) {
        return (int) Math.round(percentAsDecimal * 100);
    }

    private void fillTable(ArrayList<TableRow> allRows, int tableResourceId) {
        TableLayout table = (TableLayout) findViewById(tableResourceId);
        for (int index = 0; index < allRows.size(); ++index) {
            TableRow tableRow = allRows.get(index);
            table.addView(tableRow, index);
        }
    }

    private TableRow createScoresDetailsColumnHeadersRows() {
        TableRow tableRow = createCenterAlignedTableRow();

        tableRow.addView(createBoldCenteredTextView(R.string.pound_label, getIndexColumnWeight()));
        tableRow.addView(createBoldCenteredTextView(R.string.variables_column_name, getVariableColumnWeight()));
        tableRow.addView(createBoldCenteredTextView(R.string.number_of_questions_label));
        tableRow.addView(createBoldCenteredTextView(R.string.max_number_of_points));
        tableRow.addView(createColumnHeaderCellWithMultiLabels(new RisingCellValues()));
        tableRow.addView(createColumnHeaderCellWithMultiLabels(new ModerateExpansionCellValues()));
        tableRow.addView(createColumnHeaderCellWithMultiLabels(new AdvancedExpansionCellValues()));
        tableRow.addView(createColumnHeaderCellWithMultiLabels(new ConsolidationCellValues()));

        return tableRow;
    }

    private TextView createColumnHeaderCellWithMultiLabels(AbstractSummaryCellValues summaryCellValues) {
        String label = getString(summaryCellValues.getLabelResourceId());
        label += "\n";
        label += getString(summaryCellValues.getPercentRatingLabelId());

        TextView headerDescriptionTextView = createHeaderTextView(label);
        setBackgroundToColorWithoutLoosingBorder(headerDescriptionTextView, summaryCellValues);

        return headerDescriptionTextView;
    }

    private TextView createHeaderTextView(String label) {
        TextView textView = createBoldCenteredTextView(label);
        textView.setBackgroundResource(R.drawable.table_row);

        return textView;
    }

    private TextView createBoldCenteredTextView(int labelResourceId) {
        return createBoldCenteredTextView(getString(labelResourceId), 1.0f);
    }

    private TextView createBoldCenteredTextView(String label) {
        return createBoldCenteredTextView(label, 1.0f);
    }

    private TextView createBoldCenteredTextView(int labelResourceId, float textViewWeight) {
        return createBoldCenteredTextView(getString(labelResourceId), textViewWeight);
    }

    private TextView createBoldCenteredTextView(String label, float textViewWeight) {
        TextView textView = createStyledTextView(textViewWeight);
        setGravityToCenter(textView);
        setToBold(textView);
        textView.setText(label);

        return textView;
    }

    private void setToBold(TextView textView) {
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
    }

    private TableRow createTableColumnHeaderRow() {
        TableRow tableRow = createCenterAlignedTableRow();
        tableRow.addView(createBoldCenteredTextView(R.string.variables_column_name));
        tableRow.addView(createBoldCenteredTextView(R.string.score_column_name));
        tableRow.addView(createBoldCenteredTextView(R.string.percent_column_name));
        tableRow.addView(createBoldCenteredTextView(R.string.stage_column_name));

        return tableRow;
    }

    private TableRow createTableTitleHeaderRow(final int columnSpanToUse) {
        TableRow tableRow = createCenterAlignedTableRow();
        final TextView textView = createBoldCenteredTextView(R.string.scores_column_name);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        params.span = columnSpanToUse;
        textView.setLayoutParams(params);
        tableRow.addView(textView);

        return tableRow;
    }

    private TableRow createCenterAlignedTableRow() {
        TableRow tableRow = createTableRow();
        tableRow.setGravity(Gravity.CENTER);

        return tableRow;
    }

    private TableRow createTableRow() {
        return new TableRow(this);
    }

    private void fillBarChart() {
        TextView chartTitleTextView = (TextView) findViewById(R.id.chartTitle);
        setToBold(chartTitleTextView);
        setGravityToCenter(chartTitleTextView);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        String chartTitle = getString(R.string.bar_chart_title);
        chartTitle += "\n";
        chartTitle += simpleDateFormat.format(Calendar.getInstance().getTime());
        chartTitleTextView.setText(chartTitle);

        ArrayList<RowData> rowDataList = getFormDefParser().getGroupReferences();
        BarChart barChart = (BarChart) findViewById(R.id.results_bar_chart);
        barChart.setTouchEnabled(false);
        barChart.setDescription(null);
        barChart.getLegend().setEnabled(false);

        customizeXAxis(barChart);
        customizeYAxis(barChart);

        BarData lineData = createBarChartData(rowDataList);
        barChart.setData(lineData);
        barChart.invalidate();
    }

    private BarData createBarChartData(ArrayList<RowData> rowDataList) {
        ArrayList<BarEntry> barEntries = new ArrayList();
        ArrayList<Integer> barColors = new ArrayList<>();
        double totalScore = 0;
        double totalMaxScore = 0;
        for (int index = 0; index < rowDataList.size(); ++index) {
            RowData rowData = rowDataList.get(index);

            double percentOfQuestionsWithAnswers = rowData.calculatePercentageAsDecimal();
            AbstractSummaryCellValues summaryCellValues = AbstractSummaryCellValues.createSummaryCellValues(rowData.calculatePercentageAsRoundedInt());
            barColors.add(getResources().getColor(summaryCellValues.getColorResourceId()));
            if (rowData.hasQuestions()) {
                totalScore += rowData.calculateScore();
                totalMaxScore += rowData.getMaxScore();
            }

            BarEntry barEntry = new BarEntry((float) percentOfQuestionsWithAnswers, index);
            barEntries.add(barEntry);
        }

        if (rowDataList.size() == 0)
            return new BarData(getXAxisStaticNames(), new ArrayList<BarDataSet>());

        double averagePercentage = totalScore / totalMaxScore;
        int roundedPercentage = convertToRoundedPercent(averagePercentage);
        AbstractSummaryCellValues summaryCellValues = AbstractSummaryCellValues.createSummaryCellValues(roundedPercentage);
        barColors.add(getResources().getColor(summaryCellValues.getColorResourceId()));

        BarEntry barEntry = new BarEntry((float) averagePercentage, rowDataList.size());
        barEntries.add(barEntry);

        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColors(barColors);
        ArrayList<BarDataSet> barDataSets = new ArrayList();
        barDataSets.add(barDataSet);

        BarData barData = new BarData(getXAxisStaticNames(), barDataSets);
        barData.setValueFormatter(new IntegerPercentFormatter());

        return barData;
    }

    private void customizeYAxis(BarChart chart) {
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawLabels(false);
        yAxis.setTextSize(10f);
        yAxis.setAxisMaxValue(1.0f);
        yAxis.setTextColor(Color.BLACK);
        yAxis.setDrawGridLines(false);
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
        return new String[] {
                getString(R.string.x_axis_label_organization),
                getString(R.string.x_axis_label_administration),
                getString(R.string.x_axis_label_operation),
                getString(R.string.x_axis_label_sanitation),
                getString(R.string.x_axis_label_education),
                getString(R.string.x_axis_label_water_resources),
                getString(R.string.x_axis_label_solid_residue),
                getString(R.string.x_axis_label_communication),
                getString(R.string.x_axis_label_total),
        };
    }

    private void setGravityToCenter(TextView textView) {
        textView.setGravity(Gravity.CENTER);
    }

    private FormDefParser getFormDefParser() {
        return formDefParser;
    }
}
