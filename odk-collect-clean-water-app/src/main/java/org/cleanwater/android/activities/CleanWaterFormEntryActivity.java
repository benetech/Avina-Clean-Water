package org.cleanwater.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.cleanwater.android.R;
import org.odk.collect.android.activities.FormEntryActivity;

/**
 * Created by animal@martus.org on 5/26/15.
 */
public class CleanWaterFormEntryActivity extends FormEntryActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected View inflateFormEntryEndView() {
        return View.inflate(this, R.layout.form_entry_end, null);
    }

    public void onSeeResults(View view) {
        Intent intent = new Intent(this, ResultsActivity.class);
        startActivity(intent);
    }
}
