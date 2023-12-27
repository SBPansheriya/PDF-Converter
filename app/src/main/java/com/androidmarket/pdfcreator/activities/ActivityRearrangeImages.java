package com.androidmarket.pdfcreator.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Objects;

import androidmarket.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.androidmarket.pdfcreator.adapter.AdapterRearrangeImages;
import com.androidmarket.pdfcreator.Constants;
import com.androidmarket.pdfcreator.util.DialogUtils;
import com.androidmarket.pdfcreator.util.ImageSortUtils;
import com.androidmarket.pdfcreator.util.ThemeUtils;

import static com.androidmarket.pdfcreator.Constants.CHOICE_REMOVE_IMAGE;
import static com.androidmarket.pdfcreator.Constants.DEFAULT_IMAGE_BORDER_TEXT;
import static com.androidmarket.pdfcreator.Constants.PREVIEW_IMAGES;

public class ActivityRearrangeImages extends AppCompatActivity implements AdapterRearrangeImages.OnClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private ArrayList<String> mImages;
    private AdapterRearrangeImages mAdapterRearrangeImages;
    private SharedPreferences mSharedPreferences;
    ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rearrange_images);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mImages = intent.getStringArrayListExtra(PREVIEW_IMAGES);
        initRecyclerView(mImages);
    }

    private void initRecyclerView(ArrayList<String> images) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        mAdapterRearrangeImages = new AdapterRearrangeImages(this, images, this);
        recyclerView.setAdapter(mAdapterRearrangeImages);
    }

    @Override
    public void onUpClick(int position) {
        mImages.add(position - 1, mImages.remove(position));
        mAdapterRearrangeImages.positionChanged(mImages);
    }

    @Override
    public void onDownClick(int position) {
        mImages.add(position + 1, mImages.remove(position));
        mAdapterRearrangeImages.positionChanged(mImages);

    }

    @Override
    public void onRemoveClick(int position) {
        if (mSharedPreferences.getBoolean(Constants.CHOICE_REMOVE_IMAGE, false)) {
            mImages.remove(position);
            mAdapterRearrangeImages.positionChanged(mImages);
        } else {
//            MaterialDialog.Builder builder = DialogUtils.getInstance().createWarningDialog(this,
//                    R.string.remove_image_message);
//            builder.checkBoxPrompt(getString(R.string.dont_show_again), false, null)
//                    .onPositive((dialog, which) -> {
//                        if (dialog.isPromptCheckBoxChecked()) {
//                            SharedPreferences.Editor editor = mSharedPreferences.edit();
//                            editor.putBoolean(CHOICE_REMOVE_IMAGE, true);
//                            editor.apply();
//                        }
//                        mImages.remove(position);
//                        mAdapterRearrangeImages.positionChanged(mImages);
//
//                    })
//                    .show();

            Dialog dialog = new Dialog(ActivityRearrangeImages.this);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.setCancelable(false);
            }
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.setContentView(R.layout.delete_dialog_layout);
            dialog.setCancelable(false);
            dialog.show();

            Button cancel = dialog.findViewById(R.id.canceldialog);
            Button ok = dialog.findViewById(R.id.okdialog);
            CheckBox cbSetDefault = dialog.findViewById(R.id.cbSetDefault);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            cbSetDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean(CHOICE_REMOVE_IMAGE, true);
                    editor.apply();
                }
            });

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mImages.remove(position);
                    mAdapterRearrangeImages.positionChanged(mImages);
                    dialog.dismiss();
                }
            });
        }
    }

    private void passUris() {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra(Constants.RESULT, mImages);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        passUris();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            passUris();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getStartIntent(Context context, ArrayList<String> uris) {
        Intent intent = new Intent(context, ActivityRearrangeImages.class);
        intent.putExtra(PREVIEW_IMAGES, uris);
        return intent;
    }

    private void sortImages() {
//        new MaterialDialog.Builder(this)
//                .title(R.string.sort_by_title)
//                .items(R.array.sort_options_images)
//                .itemsCallback((dialog, itemView, position, text) -> {
//                    ImageSortUtils.getInstance().performSortOperation(position, mImages);
//                    mAdapterRearrangeImages.positionChanged(mImages);
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

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Button cancel = dialog1.findViewById(R.id.canceldialog);
        RadioGroup radioGroup = dialog1.findViewById(R.id.scale_type);
        RadioButton button = dialog1.findViewById(R.id.newest_name_list);
        RadioButton button1 = dialog1.findViewById(R.id.oldest_name_list);
        RadioButton button2 = dialog1.findViewById(R.id.newest_date_list);
        RadioButton button3 = dialog1.findViewById(R.id.oldest_date_list);

        int checkedPosition = sharedPreferences.getInt("checked_position", (-1));

        switch (checkedPosition) {
            case 0:
                button.setChecked(true);
                break;
            case 1:
                button1.setChecked(true);
                break;
            case 2:
                button2.setChecked(true);
                break;
            case 3:
                button3.setChecked(true);
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

                editor.putInt("checked_position", position);
                editor.apply();

                // Perform sort operation based on the selected position
                ImageSortUtils.getInstance().performSortOperation(position, mImages);
                mAdapterRearrangeImages.positionChanged(mImages);

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

    @OnClick(R.id.sort)
    void sortImg() {
        sortImages();
    }
}

