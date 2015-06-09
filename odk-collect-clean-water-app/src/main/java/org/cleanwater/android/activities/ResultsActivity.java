package org.cleanwater.android.activities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
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
import java.util.ArrayList;
import java.util.Arrays;

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
        ArrayList<RowData> rowDatas = getFormDefParser().createGroupColumnsFromForm();
        fillScoresDetailsTable(rowDatas);
        fillScoresSummaryTable(rowDatas);
        fillBarChart(rowDatas);
    }

    private void fillScoresDetailsTable(ArrayList<RowData> rowDatas) {
        ArrayList<TableRow> tableRows = new ArrayList();
        tableRows.add(createTableTitleHeaderRow(8));
        tableRows.add(createScoresDetailsColumnHeadersRows());

        ArrayList<RowData> groupReferenceToNameMap = getFormDefParser().getGroupReferences();
        for (int index = 0; index < groupReferenceToNameMap.size(); ++index) {
            RowData groupReference = groupReferenceToNameMap.get(index);
            TableRow tableRow = createTableRow();

            TextView indexCell = createStyledTextView();
            TextView variableCell = createStyledTextView();
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
            variableCell.setText(groupReference.getGroupName());
            numberOfScoresCell.setText(Integer.toString(groupReference.getTotalQuestionCount()));
            maxScoreCell.setText(Integer.toString(groupReference.getMaxScore()));

            RowData rowData = findRowData(groupReference.getGroupReference(), rowDatas);

            if (rowData != null) {
                final int totalQuestionCount = groupReference.getTotalQuestionCount();
                System.out.println(totalQuestionCount);
                rowData.setQuestionCount(totalQuestionCount);
                int percentage = rowData.calculatePercentageAsRoundedInt();
                AbstractSummaryCellValues summaryCellValues = AbstractSummaryCellValues.createSummaryCellValues(percentage);
                int score = rowData.calculateScore();
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

    private TextView createStyledTextView() {
        final TextView textView = new TextView(this);
        textView.setPadding(5, 5, 5, 5);
        final TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.0f);
        textView.setLayoutParams(params);
        textView.setBackgroundResource(R.drawable.table_row);

        return textView;
    }

    private void configureCell(TextView textView, int percentage, AbstractSummaryCellValues summaryCellValues) {
        textView.setBackgroundColor(getResources().getColor(summaryCellValues.getColorResourceId()));
        setGravityToCenter(textView);
        textView.setText(Integer.toString(percentage));
    }

    private void fillScoresSummaryTable(ArrayList<RowData> rowDatas) {
        ArrayList<TableRow> allRows = new ArrayList();
        allRows.add(createTableTitleHeaderRow(4));
        allRows.add(createTableColumnHeaderRow());

        ArrayList<RowData> groupReferenceToNameMap = getFormDefParser().getGroupReferences();
        float totalPercentages = 0;
        int totalScore = 0;
        for (RowData groupReference : groupReferenceToNameMap) {
            TableRow tableRow = createTableRow();

            TextView nameCell = createStyledTextView();
            TextView scoreCell = createStyledTextView();
            TextView percentCell = createStyledTextView();
            TextView stageCell = createStyledTextView();

            setGravityToCenter(scoreCell);
            setGravityToCenter(percentCell);
            setGravityToCenter(stageCell);

            nameCell.setText(groupReference.getGroupName());
            scoreCell.setText(getString(R.string.non_applicable));
            percentCell.setText(getString(R.string.non_applicable));

            RowData rowData = findRowData(groupReference.getGroupReference(), rowDatas);
            if (rowData != null) {
                totalScore += rowData.calculateScore();
                scoreCell.setText(Integer.toString(rowData.calculateScore()));

                IntegerPercentFormatter formatter = new IntegerPercentFormatter();
                final float calculatedPercentage = rowData.calculatePercentageAsDecimal();
                totalPercentages += calculatedPercentage;
                String formattedPercentValue = formatter.getFormattedValue(calculatedPercentage);
                percentCell.setText(formattedPercentValue);

                final float percent = calculatedPercentage * 100;
                final String calculatedRoundedPercentageAsString = NumberFormat.getIntegerInstance().format(percent);
                final int calculatedRoundedPercentage = Integer.parseInt(calculatedRoundedPercentageAsString);
                final AbstractSummaryCellValues summaryCellValues = AbstractSummaryCellValues.createSummaryCellValues(calculatedRoundedPercentage);

                stageCell.setBackgroundColor(getResources().getColor(summaryCellValues.getColorResourceId()));
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
        totalScoreCell.setText(Integer.toString(totalScore));
        totalsRow.addView(totalScoreCell);

        TextView totalPercentCell = createStyledTextView();
        totalPercentCell.setGravity(Gravity.RIGHT);
        totalPercentCell.setTextColor(getResources().getColor(R.color.barchart_color));
        float percentAsDecimal = totalPercentages / rowDatas.size();
        IntegerPercentFormatter formatter = new IntegerPercentFormatter();
        totalPercentCell.setText(formatter.getFormattedValue(percentAsDecimal));
        totalsRow.addView(totalPercentCell);

        TextView totalStageCell = createStyledTextView();
        setGravityToCenter(totalStageCell);
        final float calculatedPercentage = percentAsDecimal * 100;
        final int calculatedRoundedPercentage = Math.round(calculatedPercentage);
        final AbstractSummaryCellValues summaryCellValues = AbstractSummaryCellValues.createSummaryCellValues(calculatedRoundedPercentage);
        totalStageCell.setBackgroundColor(getResources().getColor(summaryCellValues.getColorResourceId()));
        totalStageCell.setText(summaryCellValues.getLabelResourceId());
        totalsRow.addView(totalStageCell);

        allRows.add(totalsRow);

        fillTable(allRows, R.id.scores_summary_table);
    }

    private void fillTable(ArrayList<TableRow> allRows, int tableResourceId) {
        TableLayout table = (TableLayout) findViewById(tableResourceId);
        for (int index = 0; index < allRows.size(); ++index) {
            TableRow tableRow = allRows.get(index);
            table.addView(tableRow, index);
        }
    }

    private RowData findRowData(String groupReference, ArrayList<RowData> rowDatas) {
        for (RowData rowData : rowDatas) {
            if (groupReference.equals(rowData.getGroupReference()))
                return rowData;
        }

        return null;
    }

    private TableRow createScoresDetailsColumnHeadersRows() {
        TableRow tableRow = createCenterAlignedTableRow();

        tableRow.addView(createBoldCenteredTextView(R.string.pound_label));
        tableRow.addView(createBoldCenteredTextView(R.string.variables_column_name));
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
        headerDescriptionTextView.setBackgroundColor(getResources().getColor(summaryCellValues.getColorResourceId()));

        return headerDescriptionTextView;
    }

    private TextView createHeaderTextView(String label) {
        TextView textView = createBoldCenteredTextView(label);
        textView.setBackgroundResource(R.drawable.table_row);

        return textView;
    }

    private TextView createBoldCenteredTextView(int labelResourceId) {
        return createBoldCenteredTextView(getString(labelResourceId));
    }

    private TextView createBoldCenteredTextView(String label) {
        TextView textView = createStyledTextView();
        setGravityToCenter(textView);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setText(label);

        return textView;
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

    private void fillBarChart(ArrayList<RowData> rowDatas) {
        BarChart chart = (BarChart) findViewById(R.id.results_bar_chart);
        customizeXAxis(chart);
        customizeYAxis(chart);

        BarData lineData = createBarChartData(rowDatas);
        chart.setData(lineData);
        chart.invalidate();
    }

    private BarData createBarChartData(ArrayList<RowData> rowDatas) {
        ArrayList<BarEntry> barEntries = new ArrayList();
        float totalPercentage = 0;
        for (int index = 0; index < rowDatas.size(); ++index) {
            RowData rowData = rowDatas.get(index);
            float percentOfQuestionsWithAnswers = rowData.calculatePercentageAsDecimal();
            totalPercentage += percentOfQuestionsWithAnswers;
            BarEntry barEntry = new BarEntry(percentOfQuestionsWithAnswers, index);
            barEntries.add(barEntry);
        }

        if (rowDatas.size() == 0)
            return new BarData(getXAxisStaticNames(), new ArrayList<BarDataSet>());

        totalPercentage = totalPercentage / rowDatas.size();
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
