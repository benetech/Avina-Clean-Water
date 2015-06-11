package org.cleanwater.android.activities;

import org.javarosa.core.model.Constants;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.IFormElement;
import org.javarosa.core.model.QuestionDef;
import org.javarosa.form.api.FormEntryCaption;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryModel;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.logic.FormController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by animal@martus.org on 6/8/15.
 */
public class FormDefParser {

    public ArrayList<RowData> getGroupReferences() {
        ArrayList<RowData> rowDataList = new ArrayList<>();
        FormDef formDef = getFormDef();

        List<IFormElement> children = formDef.getChildren();
        for (IFormElement child : children) {
            String groupReference = child.getBind().getReference().toString();
            if (shouldSkipGroup(groupReference))
                continue;

            RowData rowData = new RowData(groupReference, child.getLabelInnerText());
            int questionCountForGroup = countQuestions(child);
            rowData.setQuestionCount(questionCountForGroup);
            rowDataList.add(rowData);
        }

        FormEntryModel model = new FormEntryModel(formDef);
        FormEntryController formEntryController = new FormEntryController(model);
        for (RowData rowDat : rowDataList) {
            FormEntryCaption formEntryCaption = findFormEntryCaption(formEntryController, rowDat.getGroupReference());

            if (formEntryCaption == null)
                continue;

            RowData rowDataWithQuestionsAndAnswers = createGroupColumn(formEntryController, rowDat.getGroupReference(), formEntryCaption.getShortText());
            LinkedHashMap questionsToAnswersMap = rowDataWithQuestionsAndAnswers.getQuestionsToAnswerRowsMap();
            rowDat.putAll(questionsToAnswersMap);
        }

        return rowDataList;
    }

    private FormEntryCaption findFormEntryCaption(FormEntryController formEntryController, String referencesToMatch) {
        int currentEvent;
        formEntryController.jumpToIndex(FormIndex.createBeginningOfFormIndex());
        while ((currentEvent = formEntryController.stepToNextEvent()) != FormEntryController.EVENT_END_OF_FORM) {
            if (currentEvent == FormEntryController.EVENT_GROUP) {
                FormEntryCaption formEntryCaption = formEntryController.getModel().getCaptionPrompt();
                String groupReference = formEntryCaption.getFormElement().getBind().getReference().toString();
                if (referencesToMatch.equals(groupReference))
                    return formEntryCaption;

            }
        }

        return null;
    }

    private RowData createGroupColumn(FormEntryController formEntryController, String groupReference, String label) {
        RowData rowData = new RowData(groupReference, label);
        while ((formEntryController.stepToNextEvent()) == FormEntryController.EVENT_QUESTION) {
            FormEntryPrompt formEntryPrompt = formEntryController.getModel().getQuestionPrompt();
            if (shouldIncludeQuestionType(formEntryPrompt.getControlType()))
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

    private int countQuestions(IFormElement parent) {
        final List<IFormElement> children = parent.getChildren();
        if (children == null)
            return 0;

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

        return shouldIncludeQuestionType(question.getControlType());
    }

    private boolean shouldIncludeQuestionType(int controlType) {
        if (controlType == Constants.CONTROL_SELECT_ONE)
            return true;

        if (controlType == Constants.CONTROL_SELECT_MULTI)
            return true;

        return false;
    }

    private ArrayList<String> getGroupReferencesToSkipAsList() {
        String[] groupReferencesToSkip = new String[]{
                "/AVINA_Pedro/personalization_group",
                "/AVINA_Pedro/personalization_note",
                "/AVINA_Pedro/which_groups",
                "/AVINA_Pedro/photos_group",
        };
        
        return new ArrayList(Arrays.asList(groupReferencesToSkip));
    }

}
