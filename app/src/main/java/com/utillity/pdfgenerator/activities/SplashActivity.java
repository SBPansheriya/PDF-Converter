package com.utillity.pdfgenerator.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.utillity.pdfgenerator.R;

public class SplashActivity extends AppCompatActivity {

    ImageView continueBtn;
    public static SharedPreferences sharedPreferencesSortBy;
    public static SharedPreferences.Editor editorSortBy;
    public static String PREFS_NAME = "MyPrefsFile";
    public static String SORT_PREFERENCE_KEY = "sort_preference";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        init();

        sharedPreferencesSortBy = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editorSortBy = sharedPreferencesSortBy.edit();

        editorSortBy.putInt(SORT_PREFERENCE_KEY,-1);
        editorSortBy.commit();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        });

    }

    public void init(){
        continueBtn = findViewById(R.id.continueBtn);
    }
}