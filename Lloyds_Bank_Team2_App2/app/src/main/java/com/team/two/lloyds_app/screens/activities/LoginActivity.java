package com.team.two.lloyds_app.screens.activities;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.team.two.lloyds_app.database.DatabaseAdapter;
import com.team.two.lloyds_app.R;
import com.team.two.lloyds_app.Constants;


/**
 * Author: Josh Greenwood
 * Date: 23/03/2015
 * Purpose: A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    private static DatabaseAdapter dbadapter;

    // UI references.
    private ImageView logo;
    private AutoCompleteTextView mLoginView;
    private EditText mPasswordView;
    private CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbadapter = new DatabaseAdapter(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Set up the login form.
        logo = (ImageView) findViewById(R.id.login_logo);
        mLoginView = (AutoCompleteTextView) findViewById(R.id.login);
        mPasswordView = (EditText) findViewById(R.id.password);
        remember = (CheckBox) findViewById(R.id.remember_id);

        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        remember.setChecked(sharedPreferences.getBoolean("remember",false));
        mLoginView.setText(sharedPreferences.getString("userId", ""));
        logo.setImageResource(R.drawable.lloydsbanklogo);
    }
    public void login(View view){
        String userId = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();
        if (true){

            if (remember.isChecked()){
                SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId",userId);
                editor.putBoolean("remember",true);
                editor.apply();
            } else {
                SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId","");
                editor.putBoolean("remember",false);
                editor.apply();
            }

        //if (dbadapter.login(userId,password)){
            int databaseId = dbadapter.getId("123456789");
            //int databaseId = dbadapter.getId(userId);
            //Intent i = new Intent(this, MainScreen.class);
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("customerId", databaseId);
            startActivity(i);
            finish();
        }
    }
}
