package org.cleanwater.android.tests;

import junit.framework.TestCase;

import org.cleanwater.android.activities.RowData;

/**
 * Created by animal@martus.org on 6/9/15.
 */
public class TestRowData extends TestCase {

    public void testBasics() {
        verifyBasics(0,   0.0, 0, new double[]{}, false);
        verifyBasics(0,   0.0, 1, new double[]{0, }, false);
        verifyBasics(17,  1.0, 3, new double[]{0, 1, }, true);
        verifyBasics(38,  1.5, 2, new double[]{1, 0.5, }, true);
        verifyBasics(100, 2.0, 1, new double[]{2, }, true);
    }

    private void verifyBasics(int expectedPercentage, double expectedScore,  int numberOfQuestions, double[] numberOfQuestionsWithAnswers, boolean expectedHasQuestionsWithAnswers) {
        RowData rowData = new RowData("/some/random/groupReference", "Test Group");
        rowData.setQuestionCount(numberOfQuestions);
        fillUpRowData(rowData, numberOfQuestions, numberOfQuestionsWithAnswers);
        assertEquals("Incorrect has questions response?", expectedHasQuestionsWithAnswers, rowData.hasQuestions());
        assertEquals("Incorrect percentage?", expectedPercentage, rowData.calculatePercentageAsRoundedInt());
        assertEquals("Incorrect number of questions with answers?", expectedScore, rowData.calculateScore());
        assertEquals("Incorrect max score?", numberOfQuestions * 2, rowData.getMaxScore());
    }

    private void fillUpRowData(RowData rowData, int numberOfQuestions, double[] numberOfQuestionsWithAnswers) {
        for (int index = 0; index < numberOfQuestions; ++index) {
            if (index < numberOfQuestionsWithAnswers.length)
                rowData.put("question #" + index, numberOfQuestionsWithAnswers[index]);
            else
                rowData.put("question #" + index, 0.0);
        }
    }
}
