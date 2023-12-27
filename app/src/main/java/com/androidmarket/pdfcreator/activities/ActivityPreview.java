package com.androidmarket.pdfcreator.activities;

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
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.eftimoff.viewpagertransformers.DepthPageTransformer;

import java.util.ArrayList;

import androidmarket.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.androidmarket.pdfcreator.adapter.AdapterPreview;
import com.androidmarket.pdfcreator.adapter.AdapterPreviewImageOptions;
import com.androidmarket.pdfcreator.pdfModel.PreviewImageOptionItem;
import com.androidmarket.pdfcreator.Constants;
import com.androidmarket.pdfcreator.util.ImageSortUtils;
import com.androidmarket.pdfcreator.util.ThemeUtils;

import static com.androidmarket.pdfcreator.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static com.androidmarket.pdfcreator.Constants.IMAGE_SCALE_TYPE_STRETCH;
import static com.androidmarket.pdfcreator.Constants.PREVIEW_IMAGES;

public class ActivityPreview extends AppCompatActivity implements AdapterPreviewImageOptions.OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private ArrayList<String> mImagesArrayList;
    private static final int INTENT_REQUEST_REARRANGE_IMAGE = 1;
    private AdapterPreview mAdapterPreview;
    private ViewPager mViewPager;
    TextView textView;

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

//        textView = findViewById(R.id.heading);
//        int position = 0;
//
//        textView.setText(String.format(getResources().getString(R.string.showing_image),
//                position + 1, mImagesArrayList.size()));

        ButterKnife.bind(this);
        // Extract mImagesArrayList uri array from the intent
        Intent intent = getIntent();
        mImagesArrayList = intent.getStringArrayListExtra(PREVIEW_IMAGES);

        mViewPager = findViewById(R.id.viewpager);
        mAdapterPreview = new AdapterPreview(this, mImagesArrayList);
        mViewPager.setAdapter(mAdapterPreview);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        showOptions();
    }

    /**
     * Shows preview options at the bottom of activities
     */
    private void showOptions() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        AdapterPreviewImageOptions adapter = new AdapterPreviewImageOptions(this, getOptions(),
                getApplicationContext());
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * Returns a list of options for preview activities
     *
     * @return - list
     */
    private ArrayList<PreviewImageOptionItem> getOptions() {
        ArrayList<PreviewImageOptionItem> mOptions = new ArrayList<>();
        mOptions.add(new PreviewImageOptionItem(R.drawable.rearrange, getString(R.string.rearrange_text)));
        mOptions.add(new PreviewImageOptionItem(R.drawable.sortby, getString(R.string.sort)));
        return mOptions;
    }

    @Override
    public void onItemClick(int position) {
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

    /**
     * Shows a dialog to sort images
     */
    private void sortImages() {
//        new MaterialDialog.Builder(this)
//                .title(R.string.sort_by_title)
//                .items(R.array.sort_options_images)
//                .itemsCallback((dialog, itemView, position, text) -> {
//                    ImageSortUtils.getInstance().performSortOperation(position, mImagesArrayList);
//                    mAdapterPreview.setData(new ArrayList<>(mImagesArrayList));
//                    mViewPager.setAdapter(mAdapterPreview);
//                })
//                .negativeText(R.string.cancel)
//                .show();

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

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
                        // Handle the default case or show an error message
                        Toast.makeText(getApplicationContext(), "Invalid sort option", Toast.LENGTH_SHORT).show();
                        return;
                }

                // Perform sort operation based on the selected position
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

    /**
     * Sends the resultant uri back to calling activities
     */
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