package org.cleanwater.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import org.cleanwater.android.R;
import org.odk.collect.android.activities.FormEntryActivity;

/**
 * Created by animal@martus.org on 5/26/15.
 */
public class CleanWaterFormEntryActivity extends FormEntryActivity {

    public static final String CURRENT_FORM_URI_TAG = "CurrentFormUri";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected View inflateFormEntryEndView() {
        return View.inflate(this, R.layout.form_entry_end, null);
    }

    public void onSeeResults(View view) {
        Uri uri = getIntent().getData();
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra(CURRENT_FORM_URI_TAG, uri.toString());
        startActivity(intent);
    }
}
