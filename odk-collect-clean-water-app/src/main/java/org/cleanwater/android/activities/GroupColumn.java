package org.cleanwater.android.activities;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by animal@martus.org on 5/28/15.
 */
public class GroupColumn {
    private String groupReference;
    private String groupName;
    private LinkedHashMap<String, String> questionsToAnswerRowsMap;

    public GroupColumn(String groupReferenceToUse, String groupNameToUse) {
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
        float questionCount = getQuestionCount();
        if (questionsWithAnswersCount == 0)
            return 0;

        return questionsWithAnswersCount / questionCount;
    }

    public float calculatePercentage() {
        return calculatePercentageAsDecimal() * 100;
    }

    public int calculatePercentageAsRoundedInt() {
        return Math.round(calculatePercentage());
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

    public int calculateScore() {
        return getQuestionsWithAnswersCount() * 2;
    }

    public int getQuestionCount() {
        return questionsToAnswerRowsMap.size();
    }

    public int getTotalPossibleScore() {
        return getQuestionCount() * 2;
    }
}
