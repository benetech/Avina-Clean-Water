package org.cleanwater.android.activities;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by animal@martus.org on 5/28/15.
 */
public class RowData {
    private String groupReference;
    private String groupLabel;
    private LinkedHashMap<String, Double> questionToAnswerRowsMap;
    private int questionCount;

    public RowData(String groupReferenceToUse, String groupLabelToUse) {
        groupReference = groupReferenceToUse;
        groupLabel = groupLabelToUse;
        questionToAnswerRowsMap = new LinkedHashMap();
    }

    public void put(String question, Double answer) {
        questionToAnswerRowsMap.put(question, answer);
    }

    public void putAll(LinkedHashMap questionsToAnswersMap) {
        getQuestionToAnswerRowsMap().putAll(questionsToAnswersMap);
    }

    public String getGroupReference() {
        return groupReference;
    }

    public String getGroupLabel() {
        return groupLabel;
    }

    public double calculatePercentageAsDecimal() {
        double questionsWithAnswersCount = calculateScore();
        float questionCount = getMaxScore();
        if (questionCount == 0)
            return 0;

        return questionsWithAnswersCount / questionCount;
    }

    public double calculateScore() {
        Set<String> keys = questionToAnswerRowsMap.keySet();
        double questionsWithAnswersCount = 0;
        for (String key : keys) {
            Double answer = questionToAnswerRowsMap.get(key);
            questionsWithAnswersCount += answer;
        }

        return questionsWithAnswersCount;
    }

    private double calculatePercentage() {
        return calculatePercentageAsDecimal() * 100;
    }

    public int calculatePercentageAsRoundedInt() {
        return (int) Math.round(calculatePercentage());
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public int getMaxScore() {
        return getQuestionCount() * 2;
    }

    public LinkedHashMap<String, Double> getQuestionToAnswerRowsMap() {
        return questionToAnswerRowsMap;
    }

    public boolean hasQuestions() {
        return !getQuestionToAnswerRowsMap().isEmpty();
    }
}
