package org.cleanwater.android.activities;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by animal@martus.org on 5/28/15.
 */
public class RowData {
    private String groupReference;
    private String groupName;
    private LinkedHashMap<String, String> questionsToAnswerRowsMap;
    private int questionCount;

    public RowData(String groupReferenceToUse, String groupNameToUse) {
        groupReference = groupReferenceToUse;
        groupName = groupNameToUse;
        questionsToAnswerRowsMap = new LinkedHashMap();
    }

    public void put(String question, String answer) {
        questionsToAnswerRowsMap.put(question, answer);
    }

    public String getGroupReference() {
        return groupReference;
    }

    public String getGroupName() {
        return groupName;
    }

    public float calculatePercentageAsDecimal() {
        float questionsWithAnswersCount = getQuestionsWithAnswersCount();
        float questionCount = getAnsweredQuestionCount();
        if (questionCount == 0)
            return 0;

        return questionsWithAnswersCount / questionCount;
    }

    public int getQuestionsWithAnswersCount() {
        Set<String> keys = questionsToAnswerRowsMap.keySet();
        int questionsWithAnswersCount = 0;
        for (String key : keys) {
            String answer = questionsToAnswerRowsMap.get(key);
            if (answer == null || answer.isEmpty())
                continue;
            else
                ++questionsWithAnswersCount;
        }

        return questionsWithAnswersCount;
    }

    private float calculatePercentage() {
        return calculatePercentageAsDecimal() * 100;
    }

    public int calculatePercentageAsRoundedInt() {
        return Math.round(calculatePercentage());
    }

    public int calculateScore() {
        return calculateScore(getQuestionsWithAnswersCount());
    }

    private int getAnsweredQuestionCount() {
        return questionsToAnswerRowsMap.size();
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getTotalQuestionCount() {
        return questionCount;
    }

    public int getMaxScore() {
        return calculateScore(getTotalQuestionCount());
    }

    private int calculateScore(final int questionCount) {
        return questionCount * 2;
    }
}
