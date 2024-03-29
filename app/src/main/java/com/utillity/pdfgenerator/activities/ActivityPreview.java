package com.utillity.pdfgenerator.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eftimoff.viewpagertransformers.DepthPageTransformer;

import java.util.ArrayList;

import com.utillity.pdfgenerator.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.utillity.pdfgenerator.adapter.AdapterPreview;
import com.utillity.pdfgenerator.adapter.AdapterPreviewImageOptions;
import com.utillity.pdfgenerator.pdfModel.PreviewImageOptionItem;
import com.utillity.pdfgenerator.Constants;
import com.utillity.pdfgenerator.util.ImageSortUtils;
import com.utillity.pdfgenerator.util.ThemeUtils;
import com.google.android.material.tabs.TabLayout;

import static com.utillity.pdfgenerator.Constants.PREVIEW_IMAGES;
import static com.utillity.pdfgenerator.activities.SplashActivity.SORT_PREFERENCE_KEY;

public class ActivityPreview extends AppCompatActivity implements AdapterPreviewImageOptions.OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private ArrayList<String> mImagesArrayList;
    private static final int INTENT_REQUEST_REARRANGE_IMAGE = 1;
    private AdapterPreview mAdapterPreview;
    private ViewPager mViewPager;
    ImageView back;
    AdapterPreviewImageOptions adapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        getSupportActionBar().hide();

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ButterKnife.bind(this);
        Intent intent = getIntent();
        mImagesArrayList = intent.getStringArrayListExtra(PREVIEW_IMAGES);

        mViewPager = findViewById(R.id.viewpager);
        mAdapterPreview = new AdapterPreview(this, mImagesArrayList);
        mViewPager.setAdapter(mAdapterPreview);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        TabLayout tabLayout = findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);

        for (int i = 0; i < mImagesArrayList.size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setText(String.format(getResources().getString(R.string.showing_image_1_d_of_2_d),
                        i + 1, mImagesArrayList.size()));
            }
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        showOptions();
    }
    private void showOptions() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new AdapterPreviewImageOptions(this, getOptions(),
                getApplicationContext());
        mRecyclerView.setAdapter(adapter);
    }
    private ArrayList<PreviewImageOptionItem> getOptions() {
        ArrayList<PreviewImageOptionItem> mOptions = new ArrayList<>();
        mOptions.add(new PreviewImageOptionItem(R.drawable.rearrange, getString(R.string.rearrange_text)));
        mOptions.add(new PreviewImageOptionItem(R.drawable.sortby, getString(R.string.sort)));
        return mOptions;
    }

    @Override
    public void onItemClick(int position) {

        adapter.setSelectedItemPosition(position);

        switch (position) {
            case 0:
                startActivityForResult(ActivityRearrangeImages.getStartIntent(this, mImagesArrayList),
                        INTENT_REQUEST_REARRANGE_IMAGE);
                break;
            case 1:
                sortImages();
                break;
        }
    }

    private void sortImages() {
        Dialog dialog1 = new Dialog(this);
        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setGravity(Gravity.CENTER);
            dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog1.setCancelable(false);
        }
        dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog1.setContentView(R.layout.sort_image_dialog);
        dialog1.setCancelable(false);
        dialog1.show();

        Button cancel = dialog1.findViewById(R.id.canceldialog);
        RadioGroup radioGroup = dialog1.findViewById(R.id.scale_type);

        sharedPreferences = getSharedPreferences(SplashActivity.PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        int checkedPosition = sharedPreferences.getInt(SORT_PREFERENCE_KEY, -1);

        switch (checkedPosition) {
            case 0:
                radioGroup.check(R.id.newest_name_list);
                break;
            case 1:
                radioGroup.check(R.id.oldest_name_list);
                break;
            case 2:
                radioGroup.check(R.id.newest_date_list);
                break;
            case 3:
                radioGroup.check(R.id.oldest_date_list);
                break;
            default:
                radioGroup.clearCheck();
                break;
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int position;
                switch (checkedId) {
                    case R.id.newest_name_list:
                        position = 0;
                        break;
                    case R.id.oldest_name_list:
                        position = 1;
                        break;
                    case R.id.newest_date_list:
                        position = 2;
                        break;
                    case R.id.oldest_date_list:
                        position = 3;
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Invalid sort option", Toast.LENGTH_SHORT).show();
                        return;
                }

                editor.putInt(SORT_PREFERENCE_KEY, position).apply();

                ImageSortUtils.getInstance().performSortOperation(position, mImagesArrayList);
                mAdapterPreview.setData(new ArrayList<>(mImagesArrayList));
                mViewPager.setAdapter(mAdapterPreview);
                dialog1.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });
    }

    private void passUris() {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra(Constants.RESULT, mImagesArrayList);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == INTENT_REQUEST_REARRANGE_IMAGE) {
            try {
                mImagesArrayList = data.getStringArrayListExtra(Constants.RESULT);
                mAdapterPreview.setData(mImagesArrayList);
                mViewPager.setAdapter(mAdapterPreview);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        passUris();
    }

    public static Intent getStartIntent(Context context, ArrayList<String> uris) {
        Intent intent = new Intent(context, ActivityPreview.class);
        intent.putExtra(PREVIEW_IMAGES, uris);
        return intent;
    }
}