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
import org.javarosa.core.model.Constants;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.IFormElement;
import org.javarosa.core.model.QuestionDef;
import org.javarosa.form.api.FormEntryCaption;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryModel;
import org.javarosa.form.api.FormEntryPrompt;

import org.odk.collect.android.logic.FormController;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by animal@martus.org on 5/26/15.
 */
public class ResultsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.results_activity);
        ArrayList<RowData> rowDatas = createGroupColumnsFromForm();
        fillScoresDetailsTable(rowDatas);
        fillScoresSummaryTable(rowDatas);
        fillBarChart(rowDatas);
    }

    private ArrayList<RowData> createGroupColumnsFromForm() {
        FormDef formDef = getFormDef();
        FormEntryModel model = new FormEntryModel(formDef);
        FormEntryController formEntryController = new FormEntryController(model);

        ArrayList<RowData> rowDatas = new ArrayList();
        int currentEvent;
        while ((currentEvent = formEntryController.stepToNextEvent()) != FormEntryController.EVENT_END_OF_FORM) {
            if (currentEvent == FormEntryController.EVENT_GROUP) {

                FormEntryCaption formEntryCaption = formEntryController.getModel().getCaptionPrompt();
                String groupReference = formEntryCaption.getFormElement().getBind().getReference().toString();
                if (shouldSkipGroup(groupReference))
                    continue;

                RowData rowData = createGroupColumn(formEntryController, groupReference, formEntryCaption.getShortText());
                rowDatas.add(rowData);
                formEntryController.stepToPreviousEvent();
            }
        }

        return rowDatas;
    }

    private RowData createGroupColumn(FormEntryController formEntryController, String groupReference, String label) {
        RowData rowData = new RowData(groupReference, label);
        while ((formEntryController.stepToNextEvent()) == FormEntryController.EVENT_QUESTION) {
            FormEntryPrompt formEntryPrompt = formEntryController.getModel().getQuestionPrompt();
            rowData.put(formEntryPrompt.getQuestionText(), formEntryPrompt.getAnswerText());
        }
        return rowData;
    }

    private FormDef getFormDef() {
        FormController formController = MainApplication.getInstance().getFormController();
        return formController.getFormDef();
    }

    private boolean shouldSkipGroup(String groupReferenceName) {
        return getGroupReferencesToSkipAsList().contains(groupReferenceName);
    }

    private void fillScoresDetailsTable(ArrayList<RowData> rowDatas) {
        ArrayList<TableRow> tableRows = new ArrayList();
        tableRows.add(createTableTitleHeaderRow(8));
        tableRows.add(createScoresDetailsColumnHeadersRows());

        ArrayList<RowData> groupReferenceToNameMap = getGroupReferences();
        for (int index = 0; index < groupReferenceToNameMap.size(); ++index) {
            RowData groupReference = groupReferenceToNameMap.get(index);
            TableRow tableRow = new TableRow(this);

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
                int percentage = rowData.calculateScore();
                AbstractSummaryCellValues summaryCellValues = getSummaryCellValues(percentage);
                if (summaryCellValues != null) {
                    if (summaryCellValues.isRisingCell())
                        configureCell(risingCell, percentage, summaryCellValues);
                    else if (summaryCellValues.isModerateExpansionCell())
                        configureCell(moderateExpansionCell, percentage, summaryCellValues);
                    else if (summaryCellValues.isAdvancedExpantionCell())
                        configureCell(advancedExpansionCell, percentage, summaryCellValues);
                    else if (summaryCellValues.isConsolidatedCell())
                        configureCell(consolidatedCell, percentage, summaryCellValues);
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

        ArrayList<RowData> groupReferenceToNameMap = getGroupReferences();
        float totalPercentages = 0;
        int totalScore = 0;
        for (RowData groupReference : groupReferenceToNameMap) {
            TableRow tableRow = new TableRow(this);

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
                final AbstractSummaryCellValues summaryCellValues = getSummaryCellValues(calculatedRoundedPercentage);

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
        final AbstractSummaryCellValues summaryCellValues = getSummaryCellValues(calculatedRoundedPercentage);
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

        tableRow.addView(createColumnCellHeader(R.string.pound_label));
        tableRow.addView(createColumnCellHeader(R.string.variables_column_name));
        tableRow.addView(createColumnCellHeader(R.string.number_of_questions_label));
        tableRow.addView(createColumnCellHeader(R.string.max_number_of_points));
        tableRow.addView(createColumnHeaderCellWithMultiLabels(R.string.rising_label, R.color.rising_color, "Percent \n0-30%"));
        tableRow.addView(createColumnHeaderCellWithMultiLabels(R.string.moderate_expansion_label, R.color.moderate_expansion_color, "Percent \n31-55%"));
        tableRow.addView(createColumnHeaderCellWithMultiLabels(R.string.advanced_expansion_label, R.color.advanced_expansion_color, "Percent \n56-80%"));
        tableRow.addView(createColumnHeaderCellWithMultiLabels(R.string.consolidation_label, R.color.consolidation_color, "Percent \n81-100%"));

        return tableRow;
    }

    private TextView createColumnCellHeader(final int labelResourceId) {

        return createBoldCenteredTextView(labelResourceId);
    }

    private TextView createColumnHeaderCellWithMultiLabels(final int labelResourceId, final int colorResourceId, String percentColumnLabel) {

        final String label = getString(labelResourceId) + "\n" + percentColumnLabel;
        TextView headerDescriptionTextView = createHeaderTextView(label);
        headerDescriptionTextView.setBackgroundColor(getResources().getColor(colorResourceId));

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

    private ArrayList<RowData> getGroupReferences() {
        ArrayList<RowData> rowDatas = new ArrayList<>();
        FormDef formDef = getFormDef();
        List<IFormElement> children = formDef.getChildren();
        for (IFormElement child : children) {
            String groupReference = child.getBind().getReference().toString();
            if (shouldSkipGroup(groupReference))
                continue;

            RowData rowData = new RowData(groupReference, child.getLabelInnerText());
            int questionCountForGroup = countQuestions(child);
            rowData.setQuestionCount(questionCountForGroup);
            rowDatas.add(rowData);
        }

        return rowDatas;
    }

    private int countQuestions(IFormElement parent) {
        final List<IFormElement> children = parent.getChildren();
        int questionCount = 0;
        for (IFormElement child : children) {
            if (child instanceof QuestionDef) {
                QuestionDef question = (QuestionDef) child;
                if (isQuestion(question))
                    ++questionCount;
            }
        }

        return questionCount;
    }

    private boolean isQuestion(QuestionDef question) {
        if (question.getControlType() == Constants.DATATYPE_UNSUPPORTED)
            return false;

        if (question.getControlType() == Constants.DATATYPE_NULL)
            return false;

        if (question.getControlType() == Constants.DATATYPE_TEXT)
            return false;

        return true;
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

    private ArrayList<String> getGroupReferencesToSkipAsList() {
        return new ArrayList(Arrays.asList(getGroupReferencesToSkip()));
    }

    private AbstractSummaryCellValues getSummaryCellValues(int percent) {
        if (percent >= 0 && percent <= 30)
            return new SummaryCellRisingValues();

        if (percent >= 31 && percent <= 55)
            return new SummaryCellModerateExpansionValues();

        if (percent >= 56 && percent <= 80)
            return new SummaryCellAdvancedExpansionValues();

        if (percent >= 81 && percent <= 100)
            return new SummaryCellConsolidationValues();

        return null;
    }

    private String[] getGroupReferencesToSkip() {
        return new String[] {
                "/AVINA_proto_1/personalization_group",
                "/AVINA_proto_1/personalization_note",
                "/AVINA_proto_1/which_groups",
        };
    }

    private String[] getXAxisStaticNames() {
        return new String[] {
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

    private void setGravityToCenter(TextView textView) {
        textView.setGravity(Gravity.CENTER);
    }
}
