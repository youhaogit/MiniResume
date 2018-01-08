package com.youhao.miniresume;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.youhao.miniresume.model.Education;
import com.youhao.miniresume.util.DateUtils;

import java.util.Arrays;

/**
 * Created by YouHao.
 */

public class EditEducationActivity extends EditBaseActivity<Education> {

    public static final String KEY_EDUCATION = "education";
    public static final String KEY_EDUCATION_ID = "education_id";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_education_edit;
    }

    @Override
    protected void setupUIForEdit(final Education data) {
        ((EditText) findViewById(R.id.education_edit_school)).setText(data.school);
        ((EditText) findViewById(R.id.education_edit_major)).setText(data.major);
        ((EditText) findViewById(R.id.education_edit_start_date)).setText(DateUtils.dateToString(data.startDate));
        ((EditText) findViewById(R.id.education_edit_end_date)).setText(DateUtils.dateToString(data.endDate));
        ((EditText) findViewById(R.id.education_edit_courses)).setText(TextUtils.join("\n", data.courses));

        findViewById(R.id.education_edit_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(KEY_EDUCATION_ID, data.id);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    protected void setupUIForCreate( ) {
        findViewById(R.id.education_edit_delete).setVisibility(View.INVISIBLE);
    }

    @Override
    protected void saveAndExit(Education data) {
        if(data == null) {
            data = new Education();
        }

        data.school = ((EditText) findViewById(R.id.education_edit_school)).getText().toString();
        data.major = ((EditText) findViewById(R.id.education_edit_major)).getText().toString();
        data.startDate = DateUtils.stringToDate(
                ((TextView) findViewById(R.id.education_edit_start_date)).getText().toString());
        data.endDate = DateUtils.stringToDate(
                ((TextView) findViewById(R.id.education_edit_end_date)).getText().toString());
        data.courses = Arrays.asList(TextUtils.split(
                ((EditText) findViewById(R.id.education_edit_courses)).getText().toString(), "\n"));

        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_EDUCATION, data);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


    @Override
    protected Education initializeData() {
        return getIntent().getParcelableExtra(KEY_EDUCATION);
    }
}
