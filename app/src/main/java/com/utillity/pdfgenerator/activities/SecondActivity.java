package com.utillity.pdfgenerator.activities;

import static com.utillity.pdfgenerator.Constants.ADD_WATERMARK;
import static com.utillity.pdfgenerator.Constants.BUNDLE_DATA;
import static com.utillity.pdfgenerator.activities.SplashActivity.SORT_PREFERENCE_KEY;
import static com.utillity.pdfgenerator.activities.SplashActivity.editorSortBy;
import static com.utillity.pdfgenerator.activities.SplashActivity.sharedPreferencesSortBy;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.utillity.pdfgenerator.fragment.ExceltoPdfFragment;
import com.utillity.pdfgenerator.fragment.HistoryFragment;
import com.utillity.pdfgenerator.fragment.ImageToPdfFragment;
import com.utillity.pdfgenerator.fragment.QrBarcodeScanFragment;
import com.utillity.pdfgenerator.fragment.SettingsFragment;
import com.utillity.pdfgenerator.fragment.ViewFilesFragment;
import com.utillity.pdfgenerator.fragment.texttopdf.TextToPdfFragment;

import com.utillity.pdfgenerator.R;

public class SecondActivity extends AppCompatActivity {

    public static int lastSelectedPageNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_second);
        getSupportActionBar().hide();

        lastSelectedPageNumber = -1;
        Intent intent = getIntent();
        String fragment = intent.getStringExtra("fragment");


        if (fragment.equals("imgToPdf")) {
            ImageToPdfFragment recentFragment = new ImageToPdfFragment();
            editorSortBy = sharedPreferencesSortBy.edit();
            editorSortBy.putInt(SORT_PREFERENCE_KEY,-1);
            editorSortBy.commit();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, recentFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (fragment.equals("textToPdf")) {
            TextToPdfFragment recentFragment = new TextToPdfFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, recentFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (fragment.equals("qrToPdf")) {
            QrBarcodeScanFragment recentFragment = new QrBarcodeScanFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, recentFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (fragment.equals("excelToPdf")) {
            ExceltoPdfFragment recentFragment = new ExceltoPdfFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, recentFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (fragment.equals("watermark")) {
            Fragment fragmentWatermark;
            Bundle bundle = new Bundle();
            fragmentWatermark = new ViewFilesFragment();
            bundle.putInt(BUNDLE_DATA, ADD_WATERMARK);
            fragmentWatermark.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, fragmentWatermark);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (fragment.equals("view")) {
            ViewFilesFragment recentFragment = new ViewFilesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, recentFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (fragment.equals("settings")) {
            SettingsFragment recentFragment = new SettingsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, recentFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (fragment.equals("history")) {
            HistoryFragment recentFragment = new HistoryFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, recentFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}