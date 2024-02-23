package com.utillity.pdfgenerator.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.utillity.pdfgenerator.adapter.AdapterImageFilters;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;

import java.io.File;
import java.util.ArrayList;

import com.utillity.pdfgenerator.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

import com.utillity.pdfgenerator.adapter.AdapterBrushItem;
import com.utillity.pdfgenerator.interfaces.OnFilterItemClickedListener;
import com.utillity.pdfgenerator.interfaces.OnItemClickListener;
import com.utillity.pdfgenerator.pdfModel.BrushItem;
import com.utillity.pdfgenerator.pdfModel.FilterItem;
import com.utillity.pdfgenerator.util.BrushUtils;
import com.utillity.pdfgenerator.util.ImageFilterUtils;
import com.utillity.pdfgenerator.util.StringUtils;
import com.utillity.pdfgenerator.util.ThemeUtils;

import static com.utillity.pdfgenerator.Constants.IMAGE_EDITOR_KEY;
import static com.utillity.pdfgenerator.Constants.RESULT;

public class ActivityImageEditor extends AppCompatActivity implements OnFilterItemClickedListener, OnItemClickListener {

    private ArrayList<String> mFilterUris = new ArrayList<>();
    private final ArrayList<String> mImagePaths = new ArrayList<>();
    private ArrayList<FilterItem> mFilterItems;
    private ArrayList<BrushItem> mBrushItems;
    AdapterImageFilters adapter;
    private int mDisplaySize;
    private int mCurrentImage;
    private String mFilterName;

    @BindView(R.id.nextimageButton)
    ImageView nextButton;
    @BindView(R.id.imagecount)
    TextView imageCount;
    @BindView(R.id.previousImageButton)
    ImageView previousButton;
    @BindView(R.id.doodleSeekBar)
    SeekBar doodleSeekBar;
    @BindView(R.id.photoEditorView)
    PhotoEditorView photoEditorView;
    @BindView(R.id.doodle_colors)
    RecyclerView brushColorsView;

    private boolean mClicked = true;
    private boolean mClickedFilter = false;
    private boolean mDoodleSelected = false;

    private PhotoEditor mPhotoEditor;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeUtils.getInstance().setThemeApp(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);
        ButterKnife.bind(this);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        initValues();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void initValues() {
        mFilterUris = getIntent().getStringArrayListExtra(IMAGE_EDITOR_KEY);
        mDisplaySize = mFilterUris.size();
        mFilterItems = ImageFilterUtils.getInstance().getFiltersList(this);
        mBrushItems = BrushUtils.getInstance().getBrushItems();
        mImagePaths.addAll(mFilterUris);

        photoEditorView.getSource().setImageBitmap(BitmapFactory.decodeFile(mFilterUris.get(0)));
        changeAndShowImageCount(0);

        initRecyclerView();

        mPhotoEditor = new PhotoEditor.Builder(this, photoEditorView).setPinchTextScalable(true).build();
        doodleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPhotoEditor.setBrushSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mPhotoEditor.setBrushSize(30);
        mPhotoEditor.setBrushDrawingMode(false);
    }

    @OnClick(R.id.nextimageButton)
    void nextImg() {
        if (mClicked) {
            changeAndShowImageCount((mCurrentImage + 1) % mDisplaySize);
        } else StringUtils.getInstance().showSnackbar(this, R.string.save_first);
    }

    @OnClick(R.id.previousImageButton)
    void previousImg() {
        if (mClicked) {
            changeAndShowImageCount((mCurrentImage - 1 % mDisplaySize));
        } else StringUtils.getInstance().showSnackbar(this, R.string.save_first);
    }

    private void changeAndShowImageCount(int count) {

        if (count < 0 || count >= mDisplaySize) return;

        mCurrentImage = count % mDisplaySize;
        photoEditorView.getSource().setImageBitmap(BitmapFactory.decodeFile(mImagePaths.get(mCurrentImage)));
        imageCount.setText(String.format(getString(R.string.showing_image_1_d_of_2_d), mCurrentImage + 1, mDisplaySize));
    }

    @OnClick(R.id.savecurrent)
    void saveC() {
        mClicked = true;
        if (mClickedFilter || mDoodleSelected) {
            saveCurrentImage();
            showHideBrushEffect(false);
            mClickedFilter = false;
            mDoodleSelected = false;
        }
    }

    @OnClick(R.id.resetCurrent)
    void resetCurrent() {
        mClicked = true;
        String originalPath = mFilterUris.get(mCurrentImage);
        mImagePaths.set(mCurrentImage, originalPath);
        photoEditorView.getSource().setImageBitmap(BitmapFactory.decodeFile(originalPath));
        mPhotoEditor.clearAllViews();
        mPhotoEditor.undo();
    }

    private void saveCurrentImage() {
        try {
            File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File dir = new File(sdCard.getAbsolutePath() + "/PDFfilter");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = String.format(getString(R.string.filter_file_name), String.valueOf(System.currentTimeMillis()), mFilterName);
            File outFile = new File(dir, fileName);
            String imagePath = outFile.getAbsolutePath();

            mPhotoEditor.saveAsFile(imagePath, new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(@NonNull String imagePath) {
                    mImagePaths.remove(mCurrentImage);
                    mImagePaths.add(mCurrentImage, imagePath);
                    photoEditorView.getSource().setImageBitmap(BitmapFactory.decodeFile(mImagePaths.get(mCurrentImage)));
                    Toast.makeText(getApplicationContext(), R.string.filter_saved, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), R.string.filter_not_saved, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdapterImageFilters(mFilterItems, this, this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        brushColorsView.setLayoutManager(layoutManager2);
        AdapterBrushItem adapterBrushItem = new AdapterBrushItem(this, this, mBrushItems);
        brushColorsView.setAdapter(adapterBrushItem);
    }

    @Override
    public void onItemClick(View view, int position) {
        adapter.setSelectedItemPosition(position);

        mClicked = position == 0;
        if (position == 1) {
            mPhotoEditor = new PhotoEditor.Builder(this, photoEditorView).setPinchTextScalable(true).build();
            if (doodleSeekBar.getVisibility() == View.GONE && brushColorsView.getVisibility() == View.GONE) {
                showHideBrushEffect(true);
            } else if (doodleSeekBar.getVisibility() == View.VISIBLE && brushColorsView.getVisibility() == View.VISIBLE) {
                showHideBrushEffect(false);
            }
        } else {
            applyFilter(mFilterItems.get(position).getFilter());
        }
    }

    private void showHideBrushEffect(boolean show) {
        mPhotoEditor.setBrushDrawingMode(show);
        doodleSeekBar.setVisibility(show ? View.VISIBLE : View.GONE);
        brushColorsView.setVisibility(show ? View.VISIBLE : View.GONE);
        mDoodleSelected = true;
    }

    private void applyFilter(PhotoFilter filterType) {
        try {
            mPhotoEditor = new PhotoEditor.Builder(this, photoEditorView).setPinchTextScalable(true).build();
            mPhotoEditor.setFilterEffect(filterType);
            mFilterName = filterType.name();
            mClickedFilter = filterType != PhotoFilter.NONE;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra(RESULT, mImagePaths);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onItemClick(int position) {
        int color = mBrushItems.get(position).getColor();
        if (position == mBrushItems.size() - 1) {

            Dialog dialog = new Dialog(ActivityImageEditor.this);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.setCancelable(false);
            }
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.setContentView(R.layout.color_pallete_dailog_layout);
            dialog.setCancelable(false);
            dialog.show();

            Button cancel = dialog.findViewById(R.id.canceldialog);
            Button ok = dialog.findViewById(R.id.okdialog);
            final ColorPickerView colorPickerInput = dialog.findViewById(R.id.color_pallete);

            colorPickerInput.setAlphaSliderVisible(true);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        doodleSeekBar.setBackgroundColor(colorPickerInput.getColor());
                        mPhotoEditor.setBrushColor(colorPickerInput.getColor());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });
        } else {
            doodleSeekBar.setBackgroundColor(this.getResources().getColor(color));
            mPhotoEditor.setBrushColor(this.getResources().getColor(color));
        }
    }

    public static Intent getStartIntent(Context context, ArrayList<String> uris) {
        Intent intent = new Intent(context, ActivityImageEditor.class);
        intent.putExtra(IMAGE_EDITOR_KEY, uris);
        return intent;
    }
}
