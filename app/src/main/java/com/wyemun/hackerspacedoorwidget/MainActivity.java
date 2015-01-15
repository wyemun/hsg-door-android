package com.wyemun.hackerspacedoorwidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.wyemun.hackerspacedoorwidget.configs.AppConfig;


public class MainActivity extends ActionBarActivity {

    EditText mPinField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPinField = (EditText) findViewById(R.id.et_pin);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String doorPin = sp.getString(AppConfig.PREF_PIN, null);

        if(doorPin != null)
            mPinField.setText(doorPin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        if(toolbar!= null) {
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case  R.id.action_settings:
                return true;

            case android.R.id.home:
                this.finish();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void onClickSavePIN(View v) {
        //validate first
        String pin = mPinField.getText().toString();

        if(pin != null && pin.length() >= 6) {

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(AppConfig.PREF_PIN, pin);
            editor.commit();

            Toast.makeText(this, R.string.toast_pin_saved, Toast.LENGTH_SHORT).show();

            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            return;
        }

        Toast.makeText(this, R.string.toast_pin_error, Toast.LENGTH_SHORT).show();
    }
}
