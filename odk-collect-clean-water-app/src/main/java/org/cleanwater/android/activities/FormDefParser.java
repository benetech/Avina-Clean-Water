package org.cleanwater.android.activities;

import org.javarosa.core.model.Constants;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.IFormElement;
import org.javarosa.core.model.QuestionDef;
import org.javarosa.form.api.FormEntryCaption;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryModel;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.logic.FormController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by animal@martus.org on 6/8/15.
 */
public class FormDefParser {

    public ArrayList<RowData> createGroupColumnsFromForm() {
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

    public ArrayList<RowData> getGroupReferences() {
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

    private ArrayList<String> getGroupReferencesToSkipAsList() {
        return new ArrayList(Arrays.asList(getGroupReferencesToSkip()));
    }

    private String[] getGroupReferencesToSkip() {
        return new String[] {
                "/AVINA_proto_1/personalization_group",
                "/AVINA_proto_1/personalization_note",
                "/AVINA_proto_1/which_groups",
        };
    }
}
