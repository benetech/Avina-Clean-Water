package org.cleanwater.android.tests;

import junit.framework.TestCase;

import org.cleanwater.android.activities.RowData;

/**
 * Created by animal@martus.org on 6/9/15.
 */
public class TestRowData extends TestCase {

    public void testBasics() {
        verifyBasics(0, 0, 0, false);
        verifyBasics(0, 1, 0, false);
        verifyBasics(33, 3, 1, true);
        verifyBasics(50, 2, 1, true);
        verifyBasics(100, 1, 1, true);
    }

    private void verifyBasics(int expectedPercentage, int numberOfQuestions, int numberOfQuestionsWithAnswers, boolean expectedHasQuestionsWithAnswers) {
        RowData rowData = new RowData("/some/random/groupReference", "Test Group");
        fillUpRowData(rowData, numberOfQuestions, numberOfQuestionsWithAnswers);
        int percentage = rowData.calculatePercentageAsRoundedInt();
        assertEquals("Incorrect percentage?", expectedPercentage, percentage);

        assertEquals("Incorrect number of questions with answers?", numberOfQuestionsWithAnswers, rowData.getQuestionsWithAnswersCount());
        assertEquals("Incorrect score?", numberOfQuestionsWithAnswers * 2, rowData.calculateScore());

        rowData.setQuestionCount(numberOfQuestions);
        assertEquals("Incorrect max score?", numberOfQuestions * 2, rowData.getMaxScore());

        assertEquals("Incorrect return value for hasQuestions with answers", expectedHasQuestionsWithAnswers, rowData.hasQuestionsWithAnswers());
    }

    private void fillUpRowData(RowData rowData, int numberOfQuestions, int numberOfQuestionsWithAnswers) {
        for (int index = 0; index < numberOfQuestions; ++index) {
            if (index < numberOfQuestionsWithAnswers)
                rowData.put("question #" + index, "answer #"  +index);
            else
                rowData.put("question #" + index, "");
        }
    }
}
