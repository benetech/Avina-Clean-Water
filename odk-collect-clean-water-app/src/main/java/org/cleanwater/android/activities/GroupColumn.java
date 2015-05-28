package org.cleanwater.android.activities;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by animal@martus.org on 5/28/15.
 */
public class GroupColumn {
    private String groupName;
    private LinkedHashMap<String, String> questionsToAnswerRowsMap;

    public GroupColumn(String groupNameToUse) {
        groupName = groupNameToUse;
        questionsToAnswerRowsMap = new LinkedHashMap();
    }

    public void put(String question, String answer) {
        questionsToAnswerRowsMap.put(question, answer);
    }

    public String getGroupName() {
        return groupName;
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

    public int getQuestionCount() {
        return questionsToAnswerRowsMap.size();
    }
}
