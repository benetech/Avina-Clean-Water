package org.cleanwater.android.activities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.IFormElement;
import org.javarosa.form.api.FormEntryCaption;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryModel;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.logic.FormController;
import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static android.widget.TableRow.*;

/**
 * Created by animal@martus.org on 5/26/15.
 */
public class ResultsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.results_activity);
        ArrayList<GroupColumn> groupColumns = createGroupColumnsFromForm();
        fillScoresDetailsTable(groupColumns);
        fillScoresSummaryTable(groupColumns);
        fillBarChart(groupColumns);
    }

    private ArrayList<GroupColumn> createGroupColumnsFromForm() {
        FormDef formDef = getFormDef();
        FormEntryModel model = new FormEntryModel(formDef);
        FormEntryController formEntryController = new FormEntryController(model);

        ArrayList<GroupColumn> groupColumns = new ArrayList();
        int currentEvent;
        while ((currentEvent = formEntryController.stepToNextEvent()) != FormEntryController.EVENT_END_OF_FORM) {
            if (currentEvent == FormEntryController.EVENT_GROUP) {

                FormEntryCaption formEntryCaption = formEntryController.getModel().getCaptionPrompt();
                String groupReference = formEntryCaption.getFormElement().getBind().getReference().toString();
                if (shouldSkipGroup(groupReference))
                    continue;

                GroupColumn groupColumn = new GroupColumn(groupReference, formEntryCaption.getShortText());
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

    private FormDef getFormDef() {
        FormController formController = MainApplication.getInstance().getFormController();
        return formController.getFormDef();
    }

    private boolean shouldSkipGroup(String groupReferenceName) {
        return getGroupReferencesToSkipAsList().contains(groupReferenceName);
    }

    private void fillScoresDetailsTable(ArrayList<GroupColumn> groupColumns) {
        ArrayList<TableRow> allRows = new ArrayList();
        allRows.add(createTableTitleHeaderRow(8));
        allRows.add(createScoresDetailsColumnHeadersRows());

        LinkedHashMap<String, String> groupReferenceToNameMap = getGroupReferences();
        Set<String> groupReferences = groupReferenceToNameMap.keySet();
        String[] groupReferencesAsArray = groupReferences.toArray(new String[0]);
        for (int index = 0; index < groupReferencesAsArray.length; ++index) {
            String groupReference = groupReferencesAsArray[index];
            TableRow tableRow = new TableRow(this);

            TextView indexCell = createStyledTextView();
            TextView variableCell = createStyledTextView();
            TextView numberOfScoresCell = createStyledTextView();
            TextView maxScoreCell = createStyledTextView();

            TextView risingCell = createStyledTextView();
            TextView moderateExpansionCell = createStyledTextView();
            TextView advancedExpansionCell = createStyledTextView();
            TextView consolidatedCell = createStyledTextView();

            indexCell.setText(Integer.toString(index + 1));
            variableCell.setText(groupReferenceToNameMap.get(groupReference));

            GroupColumn groupColumn = findGroupColumn(groupReference, groupColumns);
            if (groupColumn != null) {
                numberOfScoresCell.setText(Integer.toString(groupColumn.calculateScore()));
                maxScoreCell.setText(Integer.toString(groupColumn.getTotalPossibleScore()));
                int percentage = groupColumn.calculatePercentageAsRoundedInt();
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

            allRows.add(tableRow);
        }

        TableLayout table = (TableLayout) findViewById(R.id.scores_details_table);
        for (int index = 0; index < allRows.size(); ++index) {
            TableRow tableRow = allRows.get(index);
            table.addView(tableRow, index);
        }
    }

    private TextView createStyledTextView() {
        final TextView textView = new TextView(this);
        textView.setBackgroundResource(R.drawable.table_row);

        return textView;
    }

    private void configureCell(TextView risingCell, int percentage, AbstractSummaryCellValues summaryCellValues) {
        risingCell.setBackgroundColor(getResources().getColor(summaryCellValues.getColorResourceId()));
        risingCell.setGravity(Gravity.CENTER);
        risingCell.setText(Integer.toString(percentage));
    }

    private void fillScoresSummaryTable(ArrayList<GroupColumn> groupColumns) {
        ArrayList<TableRow> allRows = new ArrayList();
        allRows.add(createTableTitleHeaderRow(4));
        allRows.add(createTableColumnHeaderRow());

        LinkedHashMap<String, String> groupReferenceToNameMap = getGroupReferences();
        Set<String> groupReferences = groupReferenceToNameMap.keySet();
        float totalPercentages = 0;
        int totalScore = 0;
        for (String groupReference : groupReferences) {
            TableRow tableRow = new TableRow(this);

            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(layoutParams);

            TextView nameCell = createStyledTextView();
            TextView scoreCell = createStyledTextView();
            TextView percentCell = createStyledTextView();
            TextView stageCell = createStyledTextView();

            scoreCell.setGravity(Gravity.CENTER);
            percentCell.setGravity(Gravity.RIGHT);
            stageCell.setGravity(Gravity.CENTER);

            nameCell.setText(groupReferenceToNameMap.get(groupReference));
            scoreCell.setText(getString(R.string.non_applicable));
            percentCell.setText(getString(R.string.non_applicable));

            GroupColumn groupColumn = findGroupColumn(groupReference, groupColumns);
            if (groupColumn != null) {
                totalScore += groupColumn.calculateScore();
                scoreCell.setText(Integer.toString(groupColumn.calculateScore()));

                IntegerPercentFormatter formatter = new IntegerPercentFormatter();
                final float calculatedPercentage = groupColumn.calculatePercentageAsDecimal();
                totalPercentages += calculatedPercentage;
                String formattedPercentValue = formatter.getFormattedValue(calculatedPercentage);
                percentCell.setText(formattedPercentValue);

                final float percent = calculatedPercentage * 100;
                final String calculatedRoundedPercentageAsString = NumberFormat.getIntegerInstance().format(percent);
                final int calculatedRoundedPercentage = Integer.parseInt(calculatedRoundedPercentageAsString);
                System.out.println(calculatedRoundedPercentage + "     asstring " + calculatedRoundedPercentageAsString);
                final AbstractSummaryCellValues summaryCellValues = getSummaryCellValues(calculatedRoundedPercentage);

                System.out.println(getString(summaryCellValues.getLabelResourceId()) + "   color = " + summaryCellValues.getColorResourceId());
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
        totalScoreCell.setGravity(Gravity.CENTER);
        totalScoreCell.setTextColor(getResources().getColor(R.color.barchart_color));
        totalScoreCell.setText(Integer.toString(totalScore));
        totalsRow.addView(totalScoreCell);

        TextView totalPercentCell = createStyledTextView();
        totalPercentCell.setGravity(Gravity.RIGHT);
        totalPercentCell.setTextColor(getResources().getColor(R.color.barchart_color));
        float percentAsDecimal = totalPercentages / groupColumns.size();
        IntegerPercentFormatter formatter = new IntegerPercentFormatter();
        totalPercentCell.setText(formatter.getFormattedValue(percentAsDecimal));
        totalsRow.addView(totalPercentCell);

        TextView totalStageCell = createStyledTextView();
        totalStageCell.setGravity(Gravity.CENTER);
        final float calculatedPercentage = percentAsDecimal * 100;
        final int calculatedRoundedPercentage = Math.round(calculatedPercentage);
        final AbstractSummaryCellValues summaryCellValues = getSummaryCellValues(calculatedRoundedPercentage);
        totalStageCell.setBackgroundColor(getResources().getColor(summaryCellValues.getColorResourceId()));
        totalStageCell.setText(summaryCellValues.getLabelResourceId());
        totalsRow.addView(totalStageCell);

        allRows.add(totalsRow);

        TableLayout table = (TableLayout) findViewById(R.id.scores_summary_table);
        for (int index = 0; index < allRows.size(); ++index) {
            TableRow tableRow = allRows.get(index);
            table.addView(tableRow, index);
        }
    }

    private GroupColumn findGroupColumn(String groupReference, ArrayList<GroupColumn> groupColumns) {
        for (GroupColumn groupColumn : groupColumns) {
            if (groupReference.equals(groupColumn.getGroupReference()))
                return groupColumn;
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

    private TableLayout createColumnCellHeader(final int labelResourceId) {

        TableLayout linearLayout = new TableLayout(this);
        linearLayout.setStretchAllColumns(true);

        TextView textView1 = new TextView(this);
        textView1.setBackgroundResource(R.drawable.column_header_3_row_span_top_cell);
        textView1.setGravity(Gravity.CENTER);

        TextView headerDescriptionTextView = new TextView(this);
        headerDescriptionTextView.setBackgroundResource(R.drawable.column_header_3_row_span_center_cell);
        headerDescriptionTextView.setGravity(Gravity.CENTER);
        headerDescriptionTextView.setText(getString(labelResourceId));

        TextView textView2 = new TextView(this);
        textView2.setBackgroundResource(R.drawable.column_header_3_row_span_bottom_cell);
        textView2.setGravity(Gravity.CENTER);

        linearLayout.addView(textView1);
        linearLayout.addView(headerDescriptionTextView);
        linearLayout.addView(textView2);

        return linearLayout;
    }

    private LinearLayout createColumnHeaderCellWithMultiLabels(final int labelResourceId, final int colorResourceId, String percentColumnLabel) {
        LinearLayout columnHeaderContainer = new LinearLayout(this);
        columnHeaderContainer.setOrientation(LinearLayout.VERTICAL);

        TextView headerDescriptionTextView = createStyledTextView();
        headerDescriptionTextView.setGravity(Gravity.CENTER);
        headerDescriptionTextView.setText(getString(labelResourceId));
        headerDescriptionTextView.setBackgroundColor(getResources().getColor(colorResourceId));
        columnHeaderContainer.addView(headerDescriptionTextView);

        TextView percentRangeTextView = createStyledTextView();
        percentRangeTextView.setGravity(Gravity.CENTER);
        percentRangeTextView.setText(percentColumnLabel);
        percentRangeTextView.setTextColor(getResources().getColor(R.color.barchart_color));
        columnHeaderContainer.addView(percentRangeTextView);

        TextView scoreTextView = createStyledTextView();
        scoreTextView.setGravity(Gravity.CENTER);
        scoreTextView.setText(getString(R.string.scores_column_name));
        scoreTextView.setTextColor(getResources().getColor(R.color.barchart_color));
        columnHeaderContainer.addView(scoreTextView);

        return columnHeaderContainer;
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

    private TextView createBoldCenteredTextView(int labelResourceId) {
        TextView textView = createStyledTextView();
        textView.setGravity(Gravity.CENTER);
        textView.setText(getString(labelResourceId));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

        return textView;
    }

    private TableRow createTableRow() {
        return new TableRow(this);
    }

    private LinkedHashMap<String, String> getGroupReferences() {
        LinkedHashMap<String, String> groupNames = new LinkedHashMap();
        FormDef formDef = getFormDef();
        List<IFormElement> children = formDef.getChildren();
        for (IFormElement child : children) {
            String groupReference = child.getBind().getReference().toString();
            if (shouldSkipGroup(groupReference))
                continue;

            groupNames.put(groupReference, child.getLabelInnerText());
        }

        return groupNames;
    }

    private void fillBarChart(ArrayList<GroupColumn> groupColumns) {
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
            float percentOfQuestionsWithAnswers = groupColumn.calculatePercentageAsDecimal();
            totalPercentage += percentOfQuestionsWithAnswers;
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
}
