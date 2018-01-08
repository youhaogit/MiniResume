package com.youhao.miniresume;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.youhao.miniresume.model.Education;
import com.youhao.miniresume.util.DateUtils;

import java.util.Arrays;

/**
 * Created by YouHao.
 */

public abstract class EditBaseActivity<T> extends AppCompatActivity{

    private T data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        //add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = initializeData();
        if(data != null) {
            setupUIForEdit(data);
        } else {
            setupUIForCreate( );
        }

    }

    //add save button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.ic_save:
                //send edit education object back to main activity
                saveAndExit(data);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract int getLayoutId();

    protected abstract void setupUIForEdit(T data);

    protected abstract void setupUIForCreate( );

    protected abstract void saveAndExit(T data);

    protected abstract T initializeData();

}
