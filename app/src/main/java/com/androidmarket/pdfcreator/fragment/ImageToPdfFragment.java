package com.androidmarket.pdfcreator.fragment;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.provider.Settings;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.androidmarket.pdfcreator.activities.ActivityImageEditor;
import com.androidmarket.pdfcreator.activities.ActivityPreview;
import com.androidmarket.pdfcreator.activities.HomeActivity;
import com.androidmarket.pdfcreator.util.AdsUtility;
import com.canhub.cropper.CropImage;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
//import com.theartofdev.edmodo.cropper.CropImage;
import com.zhihu.matisse.Matisse;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import androidmarket.R;

import com.androidmarket.pdfcreator.activities.ActivityCropImage;
import com.androidmarket.pdfcreator.activities.ActivityRearrangeImages;
import com.androidmarket.pdfcreator.adapter.AdapterEnhancementOptions;
import com.androidmarket.pdfcreator.db.DatabaseHelper;
import com.androidmarket.pdfcreator.interfaces.OnItemClickListener;
import com.androidmarket.pdfcreator.interfaces.OnPDFCreatedInterface;
import com.androidmarket.pdfcreator.pdfModel.EnhancementOptionsEntity;
import com.androidmarket.pdfcreator.pdfModel.ImageToPDFOptions;
import com.androidmarket.pdfcreator.pdfModel.Watermark;
import com.androidmarket.pdfcreator.Constants;
import com.androidmarket.pdfcreator.util.CreatePdf;
import com.androidmarket.pdfcreator.util.DefaultTextWatcher;
import com.androidmarket.pdfcreator.util.DialogUtils;
import com.androidmarket.pdfcreator.util.FileUtils;
import com.androidmarket.pdfcreator.util.ImageEnhancementOptionsUtils;
import com.androidmarket.pdfcreator.util.ImageUtils;
import com.androidmarket.pdfcreator.util.MorphButtonUtility;
import com.androidmarket.pdfcreator.util.PageSizeUtils;
import com.androidmarket.pdfcreator.util.PermissionsUtils;
import com.androidmarket.pdfcreator.util.SharedPreferencesUtil;
import com.androidmarket.pdfcreator.util.StringUtils;

import static com.androidmarket.pdfcreator.Constants.DEFAULT_BORDER_WIDTH;
import static com.androidmarket.pdfcreator.Constants.DEFAULT_COMPRESSION;
import static com.androidmarket.pdfcreator.Constants.DEFAULT_IMAGE_BORDER_TEXT;
import static com.androidmarket.pdfcreator.Constants.DEFAULT_IMAGE_SCALE_TYPE_TEXT;
import static com.androidmarket.pdfcreator.Constants.DEFAULT_PAGE_COLOR;
import static com.androidmarket.pdfcreator.Constants.DEFAULT_PAGE_SIZE;
import static com.androidmarket.pdfcreator.Constants.DEFAULT_PAGE_SIZE_TEXT;
import static com.androidmarket.pdfcreator.Constants.DEFAULT_QUALITY_VALUE;
import static com.androidmarket.pdfcreator.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static com.androidmarket.pdfcreator.Constants.MASTER_PWD_STRING;
import static com.androidmarket.pdfcreator.Constants.OPEN_SELECT_IMAGES;
import static com.androidmarket.pdfcreator.Constants.READ_PERMISSIONS;
import static com.androidmarket.pdfcreator.Constants.REQUEST_CODE_FOR_READ_PERMISSION;
//import static com.androidmarket.pdfcreator.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static com.androidmarket.pdfcreator.Constants.RESULT;
import static com.androidmarket.pdfcreator.Constants.STORAGE_LOCATION;
//import static com.androidmarket.pdfcreator.Constants.WRITE_PERMISSIONS;
import static com.androidmarket.pdfcreator.Constants.appName;
import static com.androidmarket.pdfcreator.activities.SecondActivity.lastSelectedPageNumber;
import static com.androidmarket.pdfcreator.util.WatermarkUtils.getStyleNameFromFont;
import static com.androidmarket.pdfcreator.util.WatermarkUtils.getStyleValueFromName;

/**
 * ImageToPdfFragment fragment to start with creating PDF
 */
public class ImageToPdfFragment extends Fragment implements OnItemClickListener, OnPDFCreatedInterface {

    private static final int INTENT_REQUEST_APPLY_FILTER = 10;
    private static final int INTENT_REQUEST_PREVIEW_IMAGE = 11;
    private static final int INTENT_REQUEST_REARRANGE_IMAGE = 12;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int REQUEST_CODE_SECOND_ACTIVITY = 15;
    private static final int PERMISSIONS_REQUEST_ID = 123;
    public static int lastSelected;

    @BindView(R.id.pdfCreate)
    CardView mCreatePdf;
    @BindView(R.id.pdfOpen)
    CardView mOpenPdf;
    @BindView(R.id.enhancement_options_recycle_view)
    RecyclerView mEnhancementOptionsRecycleView;
    @BindView(R.id.tvNoOfImages)
    TextView mNoOfImages;

    private MorphButtonUtility mMorphButtonUtility;
    private Activity mActivity;
    public static ArrayList<String> mImagesUri = new ArrayList<>();
    private static final ArrayList<String> mUnarrangedImagesUri = new ArrayList<>();
    private String mPath;
    private SharedPreferences mSharedPreferences;
    private FileUtils mFileUtils;
    private PageSizeUtils mPageSizeUtils;
    private int mPageColor;
    private boolean mIsButtonAlreadyClicked = false;
    private ImageToPDFOptions mPdfOptions;
    private MaterialDialog mMaterialDialog;
    private String mHomePath;
    private int mMarginTop = 50;
    private int mMarginBottom = 38;
    private int mMarginLeft = 50;
    private int mMarginRight = 38;
    private String mPageNumStyle;
    private int mChoseId;
    String[] permissions;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_images_to_pdf, container, false);
        ButterKnife.bind(this, root);

        // Initialize variables
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mPageSizeUtils = new PageSizeUtils(mActivity);
        mPageColor = mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_ITP, DEFAULT_PAGE_COLOR);
        mHomePath = mSharedPreferences.getString(STORAGE_LOCATION, StringUtils.getInstance().getDefaultStorageLocation());

        lastSelected = -1;
        // Get default values & show enhancement options
        resetValues();

        FrameLayout nativeAdFrameOne = root.findViewById(R.id.nativeAdFrameLayout);
        AdsUtility.loadNativeAd(getActivity(), nativeAdFrameOne);


        // Check for the images received
        checkForImagesInBundle();

        if (mImagesUri.size() > 0) {
            mNoOfImages.setText(String.format(mActivity.getResources().getString(R.string.images_selected), mImagesUri.size()));
            mNoOfImages.setVisibility(View.VISIBLE);
//            mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
            mCreatePdf.setEnabled(true);
        } else {
            mNoOfImages.setVisibility(View.GONE);
//            mMorphButtonUtility.morphToGrey(mCreatePdf, mMorphButtonUtility.integer());
        }

        ImageView ivBack = root.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HomeActivity.class));
                getActivity().finish();
            }
        });

//        launchSomeActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            if (result.getResultCode() == RESULT_OK) {
//                Intent data = result.getData();
//                if (data != null) {
//
//                }
//            }
//        });
        return root;
    }

    /**
     * Adds images (if any) received in the bundle
     */
    private void checkForImagesInBundle() {
        Bundle bundle = getArguments();
        if (bundle == null) return;
        if (bundle.getBoolean(OPEN_SELECT_IMAGES)) startAddingImages();
        ArrayList<Parcelable> uris = bundle.getParcelableArrayList(getString(R.string.bundleKey));
        if (uris == null) return;
        for (Parcelable p : uris) {
            String uriRealPath = mFileUtils.getUriRealPath((Uri) p);
            if (uriRealPath == null) {
                Toast.makeText(getActivity(), R.string.whatsappToast, Toast.LENGTH_SHORT).show();
            } else {
                mImagesUri.add(uriRealPath);
            }
        }
    }

    /**
     * Shows enhancement options
     */
    private void showEnhancementOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        mEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        ImageEnhancementOptionsUtils imageEnhancementOptionsUtilsInstance = ImageEnhancementOptionsUtils.getInstance();
        ArrayList<EnhancementOptionsEntity> list = imageEnhancementOptionsUtilsInstance.getEnhancementOptions(mActivity, mPdfOptions);
        AdapterEnhancementOptions adapter = new AdapterEnhancementOptions(this, list);
        mEnhancementOptionsRecycleView.setAdapter(adapter);
    }

    /**
     * Adding Images to PDF
     */
    @OnClick(R.id.addImages)
    void startAddingImages() {
        checkPermissions();
//        if (!mIsButtonAlreadyClicked) {
//            if (isStoragePermissionGranted()) {
//                selectImages();
//                mIsButtonAlreadyClicked = true;
//            } else {
//                getRuntimePermissions();
//            }
//        }
    }

    /**
     * Create Pdf of selected images
     */
    @OnClick(R.id.pdfCreate)
    void pdfCreateClicked() {
        createPdf(false);
    }

    /**
     * Opens the dialog to select a save name
     */
    private void createPdf(boolean isGrayScale) {
        String preFillName = mFileUtils.getLastFileName(mImagesUri);
        String ext = getString(R.string.pdf_ext);
        mFileUtils.openSaveDialog(preFillName, ext, filename -> save(isGrayScale, filename));
    }

    /**
     * Saves the PDF
     *
     * @param isGrayScale if the images should be converted to grayscale before
     * @param filename    the filename to save to
     */
    private void save(boolean isGrayScale, String filename) {
        mPdfOptions.setImagesUri(mImagesUri);
        mPdfOptions.setPageSize(PageSizeUtils.mPageSize);
        mPdfOptions.setImageScaleType(ImageUtils.getInstance().mImageScaleType);
        mPdfOptions.setPageNumStyle(mPageNumStyle);
        mPdfOptions.setMasterPwd(mSharedPreferences.getString(MASTER_PWD_STRING, appName));
        mPdfOptions.setPageColor(mPageColor);
        mPdfOptions.setOutFileName(filename);
        if (isGrayScale) saveImagesInGrayScale();
        new CreatePdf(mPdfOptions, mHomePath, ImageToPdfFragment.this).execute();
    }

    @OnClick(R.id.pdfOpen)
    void openPdf() {
        mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF);
    }

    private boolean isStoragePermissionGranted() {
//        if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 29) {
//            return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//        } else
        if (Build.VERSION.SDK_INT >= 29) {
            return ContextCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else return true;
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            selectImages();
        } else {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
                permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            else if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            else if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
                permissions = new String[]{Manifest.permission.CAMERA};
            requestPermissionLauncher.launch(permissions);
        }
    }

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    selectImages();
                } else {
                    showPermissionDenyDialog(getActivity(), 123);
                }
            });

    private void showPermissionDenyDialog(Activity activity, int requestCode) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA)) {

            Dialog dialog = new Dialog(activity);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.setCancelable(false);
            }
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.setContentView(R.layout.permission_denied_first_dialog);
            dialog.setCancelable(false);
            dialog.show();

            Button cancel = dialog.findViewById(R.id.canceldialog);
            Button ok = dialog.findViewById(R.id.okdialog);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkPermissions();
                    dialog.dismiss();
                }
            });
        }
//        else if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA) && ActivityCompat.shouldShowRequestPermissionRationale(activity,READ_EXTERNAL_STORAGE)) {
//
//            Dialog dialog = new Dialog(activity);
//            if (dialog.getWindow() != null) {
//                dialog.getWindow().setGravity(Gravity.CENTER);
//                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                dialog.setCancelable(false);
//            }
//            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            dialog.setContentView(R.layout.permission_denied_first_dialog);
//            dialog.setCancelable(false);
//            dialog.show();
//
//            Button cancel = dialog.findViewById(R.id.canceldialog);
//            Button ok = dialog.findViewById(R.id.okdialog);
//
//            cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog.dismiss();
//                }
//            });
//
//            ok.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    checkPermissions();
//                    dialog.dismiss();
//                }
//            });
//        }
        else if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, READ_EXTERNAL_STORAGE) || !ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA)) {

            Dialog dialog = new Dialog(activity);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.setCancelable(false);
            }
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.setContentView(R.layout.permission_denied_first_dialog);
            dialog.setCancelable(false);
            dialog.show();

            Button cancel = dialog.findViewById(R.id.canceldialog);
            Button ok = dialog.findViewById(R.id.okdialog);
            TextView textView = dialog.findViewById(R.id.filename);

            textView.setText(R.string.storage_permission);
            ok.setText(R.string.enable_from_settings);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivityForResult(intent, requestCode);
                    dialog.dismiss();
                }
            });
        }
    }

    /**
     * Called after user is asked to grant permissions
     *
     * @param requestCode  REQUEST Code for opening permissions
     * @param permissions  permissions asked to user
     * @param grantResults bool array indicating if permission is granted
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (Build.VERSION.SDK_INT >= 29) {
//            PermissionsUtils.getInstance().handleRequestPermissionsResult(mActivity, grantResults, requestCode, REQUEST_CODE_FOR_READ_PERMISSION, this::selectImages);
//        }
////        else {
////            PermissionsUtils.getInstance().handleRequestPermissionsResult(mActivity, grantResults, requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION, this::selectImages);
////        }
//    }

    /**
     * Called after Matisse Activity is called
     *
     * @param requestCode REQUEST Code for opening Matisse Activity
     * @param resultCode  result code of the process
     * @param data        Data of the image selected
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {
            checkPermissions();
        }

        mIsButtonAlreadyClicked = false;
        if (resultCode != Activity.RESULT_OK || data == null) return;

        switch (requestCode) {

            case INTENT_REQUEST_GET_IMAGES:
                mImagesUri.clear();
                mUnarrangedImagesUri.clear();
                mImagesUri.addAll(Matisse.obtainPathResult(data));
                mUnarrangedImagesUri.addAll(mImagesUri);
                if (mImagesUri.size() > 0) {
                    mNoOfImages.setText(String.format(mActivity.getResources().getString(R.string.images_selected), mImagesUri.size()));
                    mNoOfImages.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), R.string.snackbar_images_added, Toast.LENGTH_SHORT).show();

                    mCreatePdf.setEnabled(true);
//                    mCreatePdf.unblockTouch();
                }
//                mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
                mOpenPdf.setVisibility(View.GONE);
                break;

//            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
//                HashMap<Integer, Uri> croppedImageUris = (HashMap) data.getSerializableExtra(CropImage.CROP_IMAGE_EXTRA_RESULT);
//
//                for (int i = 0; i < mImagesUri.size(); i++) {
//                    if (croppedImageUris.get(i) != null) {
//                        mImagesUri.set(i, croppedImageUris.get(i).getPath());
//                        Toast.makeText(getActivity(), R.string.snackbar_imagecropped, Toast.LENGTH_SHORT).show();
//                    }
//                }
//                break;

            case REQUEST_CODE_SECOND_ACTIVITY:
                List<String> uriStrings = data.getStringArrayListExtra(Constants.RESULT);

//                String croppedImagePath = data.getStringExtra(Constants.RESULT);

                for (int i = 0; i < mImagesUri.size(); i++) {
                    if (uriStrings != null) {
                        if (uriStrings.size() >= i) {
                            mImagesUri.set(i, uriStrings.get(i));
                            Toast.makeText(getActivity(), R.string.snackbar_imagecropped, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;

            case INTENT_REQUEST_APPLY_FILTER:
                mImagesUri.clear();
                ArrayList<String> mFilterUris = data.getStringArrayListExtra(RESULT);
                int size = mFilterUris.size() - 1;
                for (int k = 0; k <= size; k++)
                    mImagesUri.add(mFilterUris.get(k));
                break;

            case INTENT_REQUEST_PREVIEW_IMAGE:
                mImagesUri = data.getStringArrayListExtra(RESULT);
                if (mImagesUri.size() > 0) {
                    mNoOfImages.setText(String.format(mActivity.getResources().getString(R.string.images_selected), mImagesUri.size()));
                } else {
                    mNoOfImages.setVisibility(View.GONE);
//                    mMorphButtonUtility.morphToGrey(mCreatePdf, mMorphButtonUtility.integer());
                    mCreatePdf.setEnabled(false);
                }
                break;

            case INTENT_REQUEST_REARRANGE_IMAGE:
                mImagesUri = data.getStringArrayListExtra(RESULT);
                if (!mUnarrangedImagesUri.equals(mImagesUri) && mImagesUri.size() > 0) {
                    mNoOfImages.setText(String.format(mActivity.getResources().getString(R.string.images_selected), mImagesUri.size()));
                    Toast.makeText(getActivity(), R.string.images_rearranged, Toast.LENGTH_SHORT).show();

                    mUnarrangedImagesUri.clear();
                    mUnarrangedImagesUri.addAll(mImagesUri);
                }
                if (mImagesUri.size() == 0) {
                    mNoOfImages.setVisibility(View.GONE);
//                    mMorphButtonUtility.morphToGrey(mCreatePdf, mMorphButtonUtility.integer());
                    mCreatePdf.setEnabled(false);
                }
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        if (mImagesUri.size() == 0) {
            Toast.makeText(getActivity(), R.string.snackbar_no_images, Toast.LENGTH_SHORT).show();
//            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_no_images);
            return;
        }
        switch (position) {
            case 0:
                passwordProtectPDF();
                break;
            case 1:
                cropImage();
                break;
            case 2:
                compressImage();
                break;
            case 3:
                startActivityForResult(ActivityImageEditor.getStartIntent(mActivity, mImagesUri), INTENT_REQUEST_APPLY_FILTER);
                break;
            case 4:
                mPageSizeUtils.showPageSizeDialog(false);
                break;
            case 5:
                ImageUtils.getInstance().showImageScaleTypeDialog(mActivity, false, "ImageToPdf");
                break;
            case 6:
                startActivityForResult(ActivityPreview.getStartIntent(mActivity, mImagesUri), INTENT_REQUEST_PREVIEW_IMAGE);
                break;
            case 7:
                addBorder();
                break;
            case 8:
                startActivityForResult(ActivityRearrangeImages.getStartIntent(mActivity, mImagesUri), INTENT_REQUEST_REARRANGE_IMAGE);
                break;
            case 9:
                addMargins();
                break;
            case 10:
                addPageNumbers();
                break;
            case 11:
                addWatermark();
                break;
            case 12:
                setPageColor();
                break;
            case 13:
                createPdf(true);
                break;
        }
    }

    /**
     * Saves Images with gray scale filter
     */
    private void saveImagesInGrayScale() {
        ArrayList<String> tempImageUri = new ArrayList<>();
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/PDFfilter");
            dir.mkdirs();

            int size = mImagesUri.size();
            for (int i = 0; i < size; i++) {
                String fileName = String.format(getString(R.string.filter_file_name), String.valueOf(System.currentTimeMillis()), i + "_grayscale");
                File outFile = new File(dir, fileName);

                File f = new File(mImagesUri.get(i));
                FileInputStream fis = new FileInputStream(f);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                Bitmap grayScaleBitmap = ImageUtils.getInstance().toGrayscale(bitmap);

                outFile.createNewFile();
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile), 1024 * 8);
                grayScaleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.close(); // Includes flushing the stream and closing the FileOutputStream
                tempImageUri.add(outFile.getAbsolutePath());
            }
            mImagesUri.clear();
            mImagesUri.addAll(tempImageUri);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private void addBorder() {
//        DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity, R.string.border)
//                .customView(R.layout.dialog_border_image, true)
//                .onPositive((dialog1, which) -> {
//                    View view = dialog1.getCustomView();
//                    final EditText input = view.findViewById(R.id.border_width);
//                    int value = 0;
//                    try {
//                        value = Integer.parseInt(String.valueOf(input.getText()));
//                        if (value > 200 || value < 0) {
//                            Toast.makeText(getActivity(), R.string.invalid_entry, Toast.LENGTH_SHORT).show();
//                        } else {
//                            mPdfOptions.setBorderWidth(value);
//                            showEnhancementOptions();
//                        }
//                    } catch (NumberFormatException e) {
//                        Toast.makeText(getActivity(), R.string.invalid_entry, Toast.LENGTH_SHORT).show();
//                    }
//                    final CheckBox cbSetDefault = view.findViewById(R.id.cbSetDefault);
//                    if (cbSetDefault.isChecked()) {
//                        SharedPreferences.Editor editor = mSharedPreferences.edit();
//                        editor.putInt(DEFAULT_IMAGE_BORDER_TEXT, value);
//                        editor.apply();
//                    }
//                }).build().show();
        Dialog dialog = new Dialog(getContext());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.border_width_dialog_layout);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        final EditText input = dialog.findViewById(R.id.number);
        CheckBox cbSetDefault = dialog.findViewById(R.id.cbSetDefault);

        int borderWidth = mPdfOptions.getBorderWidth();

        if (borderWidth == 0) {
            input.setHint(R.string.enter_border_width_units);
        }
        else {
            input.setText(""+borderWidth);
        }


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = 0;
                try {
                    value = Integer.parseInt(String.valueOf(input.getText()));
                    if (value > 200 || value < 0) {
                        Toast.makeText(getActivity(), R.string.invalid_entry, Toast.LENGTH_SHORT).show();
                    } else {
                        mPdfOptions.setBorderWidth(value);
                        showEnhancementOptions();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), R.string.invalid_entry, Toast.LENGTH_SHORT).show();
                }
                if (cbSetDefault.isChecked()) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putInt(DEFAULT_IMAGE_BORDER_TEXT, value);
                    editor.apply();
                }
                dialog.dismiss();
            }
        });
    }

    private void compressImage() {
//        DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity, R.string.compression_image_edit)
//                .customView(R.layout.compress_image_dialog, true)
//                .onPositive((dialog1, which) -> {
//                    final EditText qualityInput = dialog1.getCustomView().findViewById(R.id.quality);
//                    final CheckBox cbSetDefault = dialog1.getCustomView().findViewById(R.id.cbSetDefault);
//                    int check;
//                    try {
//                        check = Integer.parseInt(String.valueOf(qualityInput.getText()));
//                        if (check > 100 || check < 0) {
//                            Toast.makeText(getActivity(), R.string.invalid_entry, Toast.LENGTH_SHORT).show();
//                        } else {
//                            mPdfOptions.setQualityString(String.valueOf(check));
//                            if (cbSetDefault.isChecked()) {
//                                SharedPreferences.Editor editor = mSharedPreferences.edit();
//                                editor.putInt(DEFAULT_COMPRESSION, check);
//                                editor.apply();
//                            }
//                            showEnhancementOptions();
//                        }
//                    } catch (NumberFormatException e) {
//                        Toast.makeText(getActivity(), R.string.invalid_entry, Toast.LENGTH_SHORT).show();
//                    }
//                }).show();

        Dialog dialog = new Dialog(getContext());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.compress_image_dialog_layout);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        final EditText qualityInput = dialog.findViewById(R.id.quality);
        final CheckBox cbSetDefault = dialog.findViewById(R.id.cbSetDefault);

        String text = mPdfOptions.getQualityString();

        qualityInput.setText(text);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int check;
                try {
                    check = Integer.parseInt(String.valueOf(qualityInput.getText()));
                    if (check > 100 || check < 0) {
                        Toast.makeText(getActivity(), R.string.invalid_entry, Toast.LENGTH_SHORT).show();
                    } else {
                        mPdfOptions.setQualityString(String.valueOf(check));
                        if (cbSetDefault.isChecked()) {
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putInt(DEFAULT_COMPRESSION, check);
                            editor.apply();
                        }
                        showEnhancementOptions();
                    }
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), R.string.invalid_entry, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }

    private void passwordProtectPDF() {
//        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
//                .title(R.string.set_password)
//                .customView(R.layout.custom_dialog, true)
//                .positiveText(android.R.string.ok)
//                .negativeText(android.R.string.cancel)
//                .neutralText(R.string.remove_dialog)
//                .build();
//
//        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
//        final View neutralAction = dialog.getActionButton(DialogAction.NEUTRAL);
//        final EditText passwordInput = dialog.getCustomView().findViewById(R.id.password);
//        passwordInput.setText(mPdfOptions.getPassword());
//        passwordInput.addTextChangedListener(
//                new DefaultTextWatcher() {
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        positiveAction.setEnabled(s.toString().trim().length() > 0);
//                    }
//                });
//
//        positiveAction.setOnClickListener(v -> {
//            if (StringUtils.getInstance().isEmpty(passwordInput.getText())) {
//                Toast.makeText(getActivity(), R.string.snackbar_password_cannot_be_blank, Toast.LENGTH_SHORT).show();
//            } else {
//                mPdfOptions.setPassword(passwordInput.getText().toString());
//                mPdfOptions.setPasswordProtected(true);
//                showEnhancementOptions();
//                dialog.dismiss();
//            }
//        });
//
//        if (StringUtils.getInstance().isNotEmpty(mPdfOptions.getPassword())) {
//            neutralAction.setOnClickListener(v -> {
//                mPdfOptions.setPassword(null);
//                mPdfOptions.setPasswordProtected(false);
//                showEnhancementOptions();
//                dialog.dismiss();
//                Toast.makeText(getActivity(), R.string.password_remove, Toast.LENGTH_SHORT).show();
//
//            });
//        }
//        dialog.show();
//        positiveAction.setEnabled(false);

        Dialog dialog = new Dialog(getContext());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.set_password_dialog);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        Button remove = dialog.findViewById(R.id.remove_dialog);
        EditText passwordinput = dialog.findViewById(R.id.password);

        passwordinput.setText(mPdfOptions.getPassword());
        passwordinput.addTextChangedListener(new DefaultTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ok.setEnabled(s.toString().trim().length() > 0);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if (StringUtils.getInstance().isNotEmpty(mPdfOptions.getPassword())) {
            remove.setOnClickListener(v -> {
                mPdfOptions.setPassword(null);
                mPdfOptions.setPasswordProtected(false);
                showEnhancementOptions();
                dialog.dismiss();
                Toast.makeText(getActivity(), R.string.password_remove, Toast.LENGTH_SHORT).show();

            });
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.getInstance().isEmpty(passwordinput.getText())) {
                    Toast.makeText(getActivity(), R.string.snackbar_password_cannot_be_blank, Toast.LENGTH_SHORT).show();
                } else {
                    mPdfOptions.setPassword(passwordinput.getText().toString());
                    mPdfOptions.setPasswordProtected(true);
                    showEnhancementOptions();
                    dialog.dismiss();
                }
            }
        });
    }

    private void addWatermark() {
//        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
//                .title(R.string.add_watermark)
//                .customView(R.layout.add_water_mark_dialog_layout, true)
//                .positiveText(android.R.string.ok)
//                .negativeText(android.R.string.cancel)
//                .neutralText(R.string.remove_dialog)
//                .build();
//
//        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
//        final View neutralAction = dialog.getActionButton(DialogAction.NEUTRAL);
//
//        final Watermark watermark = new Watermark();
//
//        final EditText watermarkTextInput = dialog.getCustomView().findViewById(R.id.watermarkText);
//        final EditText angleInput = dialog.getCustomView().findViewById(R.id.watermarkAngle);
//        final ColorPickerView colorPickerInput = dialog.getCustomView().findViewById(R.id.watermarkColor);
//        final EditText fontSizeInput = dialog.getCustomView().findViewById(R.id.watermarkFontSize);
//        final Spinner fontFamilyInput = dialog.getCustomView().findViewById(R.id.watermarkFontFamily);
//        final Spinner styleInput = dialog.getCustomView().findViewById(R.id.watermarkStyle);
//
//        ArrayAdapter<Font.FontFamily> fontFamilyAdapter = new ArrayAdapter<>(mActivity,
//                android.R.layout.simple_spinner_dropdown_item, Font.FontFamily.values());
//        fontFamilyInput.setAdapter(fontFamilyAdapter);
//
//        ArrayAdapter<String> styleAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_dropdown_item,
//                mActivity.getResources().getStringArray(R.array.fontStyles));
//        styleInput.setAdapter(styleAdapter);
//
//
//        if (mPdfOptions.isWatermarkAdded()) {
//            watermarkTextInput.setText(mPdfOptions.getWatermark().getWatermarkText());
//            angleInput.setText(String.valueOf(mPdfOptions.getWatermark().getRotationAngle()));
//            fontSizeInput.setText(String.valueOf(mPdfOptions.getWatermark().getTextSize()));
//            BaseColor color = this.mPdfOptions.getWatermark().getTextColor();
//            //color.getRGB() returns an ARGB color
//            colorPickerInput.setColor(color.getRGB());
//
//            fontFamilyInput.setSelection(fontFamilyAdapter.getPosition(mPdfOptions.getWatermark().getFontFamily()));
//            styleInput.setSelection(styleAdapter.getPosition(
//                    getStyleNameFromFont(mPdfOptions.getWatermark().getFontStyle())));
//        } else {
//            angleInput.setText("0");
//            fontSizeInput.setText("50");
//        }
//        watermarkTextInput.addTextChangedListener(
//                new DefaultTextWatcher() {
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        positiveAction.setEnabled(s.toString().trim().length() > 0);
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable input) {
//                        if (StringUtils.getInstance().isEmpty(input)) {
//                            Toast.makeText(getActivity(), R.string.snackbar_watermark_cannot_be_blank, Toast.LENGTH_SHORT).show();
//
//
//                        } else {
//                            watermark.setWatermarkText(input.toString());
//                            showEnhancementOptions();
//                        }
//                    }
//                });
//
//        neutralAction.setEnabled(this.mPdfOptions.isWatermarkAdded());
//        positiveAction.setEnabled(this.mPdfOptions.isWatermarkAdded());
//
//        neutralAction.setOnClickListener(v -> {
//            mPdfOptions.setWatermarkAdded(false);
//            showEnhancementOptions();
//            dialog.dismiss();
//            Toast.makeText(getActivity(), R.string.watermark_remove, Toast.LENGTH_SHORT).show();
//
//        });
//
//        positiveAction.setOnClickListener(v -> {
//            watermark.setWatermarkText(watermarkTextInput.getText().toString());
//            watermark.setFontFamily(((Font.FontFamily) fontFamilyInput.getSelectedItem()));
//            watermark.setFontStyle(getStyleValueFromName(((String) styleInput.getSelectedItem())));
//
//            watermark.setRotationAngle(StringUtils.getInstance().parseIntOrDefault(angleInput.getText(), 0));
//
//            watermark.setTextSize(StringUtils.getInstance().parseIntOrDefault(fontSizeInput.getText(), 50));
//
//            watermark.setTextColor((new BaseColor(
//                    Color.red(colorPickerInput.getColor()),
//                    Color.green(colorPickerInput.getColor()),
//                    Color.blue(colorPickerInput.getColor()),
//                    Color.alpha(colorPickerInput.getColor())
//            )));
//            mPdfOptions.setWatermark(watermark);
//            mPdfOptions.setWatermarkAdded(true);
//            showEnhancementOptions();
//            dialog.dismiss();
//            Toast.makeText(getActivity(), R.string.watermark_added, Toast.LENGTH_SHORT).show();
//
//        });
//
//        dialog.show();

        Dialog dialog1 = new Dialog(getContext());
        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setGravity(Gravity.CENTER);
            dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog1.setCancelable(false);
        }
        dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog1.setContentView(R.layout.add_water_mark_dialog_layout);
        dialog1.setCancelable(false);
        dialog1.show();

        Button cancel = dialog1.findViewById(R.id.canceldialog);
        Button ok = dialog1.findViewById(R.id.okdialog);
        Button remove = dialog1.findViewById(R.id.remove_dialog);
        final EditText watermarkTextInput = dialog1.findViewById(R.id.watermarkText);
        final EditText angleInput = dialog1.findViewById(R.id.watermarkAngle);
        final ColorPickerView colorPickerInput = dialog1.findViewById(R.id.watermarkColor);
        final EditText fontSizeInput = dialog1.findViewById(R.id.watermarkFontSize);
        final Spinner fontFamilyInput = dialog1.findViewById(R.id.watermarkFontFamily);
        final Spinner styleInput = dialog1.findViewById(R.id.watermarkStyle);

        final Watermark watermark = new Watermark();

        colorPickerInput.setAlphaSliderVisible(true);
        ArrayAdapter<Font.FontFamily> fontFamilyAdapter = new ArrayAdapter<>(mActivity, R.layout.simple_spinner_item, Font.FontFamily.values());
        fontFamilyAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        fontFamilyInput.setAdapter(fontFamilyAdapter);

        ArrayAdapter<String> styleAdapter = new ArrayAdapter<>(mActivity, R.layout.simple_spinner_item, mActivity.getResources().getStringArray(R.array.fontStyles));
        styleAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        styleInput.setAdapter(styleAdapter);

        if (mPdfOptions.isWatermarkAdded()) {
            watermarkTextInput.setText(mPdfOptions.getWatermark().getWatermarkText());
            angleInput.setText(String.valueOf(mPdfOptions.getWatermark().getRotationAngle()));
            fontSizeInput.setText(String.valueOf(mPdfOptions.getWatermark().getTextSize()));
            BaseColor color = this.mPdfOptions.getWatermark().getTextColor();
            //color.getRGB() returns an ARGB color
            colorPickerInput.setColor(color.getRGB());

            fontFamilyInput.setSelection(fontFamilyAdapter.getPosition(mPdfOptions.getWatermark().getFontFamily()));
            styleInput.setSelection(styleAdapter.getPosition(getStyleNameFromFont(mPdfOptions.getWatermark().getFontStyle())));
        } else {
            angleInput.setText("0");
            fontSizeInput.setText("50");
        }

        watermarkTextInput.addTextChangedListener(new DefaultTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                ok.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable input) {
                if (StringUtils.getInstance().isEmpty(input)) {
                    Toast.makeText(getActivity(), R.string.snackbar_watermark_cannot_be_blank, Toast.LENGTH_SHORT).show();
                } else {
                    watermark.setWatermarkText(input.toString());
                    showEnhancementOptions();
                }
            }
        });

        remove.setEnabled(this.mPdfOptions.isWatermarkAdded());
//        ok.setEnabled(this.mPdfOptions.isWatermarkAdded());

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPdfOptions.setWatermarkAdded(false);
                showEnhancementOptions();
                dialog1.dismiss();
                Toast.makeText(getActivity(), R.string.watermark_remove, Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.getInstance().isEmpty(watermarkTextInput.getText().toString())) {
                    Toast.makeText(mActivity, "Watermark text is not empty", Toast.LENGTH_SHORT).show();
                } else {
                    watermark.setWatermarkText(watermarkTextInput.getText().toString());
                    watermark.setFontFamily(((Font.FontFamily) fontFamilyInput.getSelectedItem()));
                    watermark.setFontStyle(getStyleValueFromName(((String) styleInput.getSelectedItem())));

                    watermark.setRotationAngle(StringUtils.getInstance().parseIntOrDefault(angleInput.getText(), 0));

                    watermark.setTextSize(StringUtils.getInstance().parseIntOrDefault(fontSizeInput.getText(), 50));

                    int originalColor = colorPickerInput.getColor();

                    int red = Color.red(originalColor);
                    int green = Color.green(originalColor);
                    int blue = Color.blue(originalColor);

                    int alpha = (int) (Color.alpha(originalColor) * 0.2);

                    BaseColor adjustedBaseColor = new BaseColor(red, green, blue, alpha);

                    watermark.setTextColor(adjustedBaseColor);

//                    watermark.setTextColor((new BaseColor(Color.red(colorPickerInput.getColor()), Color.green(colorPickerInput.getColor()), Color.blue(colorPickerInput.getColor()), Color.alpha(colorPickerInput.getColor()))));
                    mPdfOptions.setWatermark(watermark);
                    mPdfOptions.setWatermarkAdded(true);
                    showEnhancementOptions();
                    Toast.makeText(getActivity(), R.string.watermark_added, Toast.LENGTH_SHORT).show();
                    dialog1.dismiss();
                }
            }
        });
    }

    private void setPageColor() {
//        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
//                .title(R.string.page_color)
//                .customView(R.layout.dialog_color_chooser, true)
//                .positiveText(R.string.ok)
//                .negativeText(R.string.cancel)
//                .onPositive((dialog, which) -> {
//                    View view = dialog.getCustomView();
//                    ColorPickerView colorPickerView = view.findViewById(R.id.color_picker);
//                    CheckBox defaultCheckbox = view.findViewById(R.id.set_default);
//                    mPageColor = colorPickerView.getColor();
//                    if (defaultCheckbox.isChecked()) {
//                        SharedPreferences.Editor editor = mSharedPreferences.edit();
//                        editor.putInt(Constants.DEFAULT_PAGE_COLOR_ITP, mPageColor);
//                        editor.apply();
//                    }
//                })
//                .build();
//        ColorPickerView colorPickerView = materialDialog.getCustomView().findViewById(R.id.color_picker);
//        colorPickerView.setColor(mPageColor);
//        materialDialog.show();

        Dialog dialog = new Dialog(getContext());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_color_chooser_layout);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        ColorPickerView colorPickerView = dialog.findViewById(R.id.watermarkColor);
        CheckBox defaultCheckbox = dialog.findViewById(R.id.set_default);
        TextView title = dialog.findViewById(R.id.txt);

        title.setText("Page color");

        colorPickerView.setAlphaSliderVisible(true);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPageColor = colorPickerView.getColor();
                if (defaultCheckbox.isChecked()) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putInt(Constants.DEFAULT_PAGE_COLOR_ITP, mPageColor);
                    editor.apply();
                }
                dialog.dismiss();
            }
        });
        colorPickerView.setColor(mPageColor);
    }

    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean success, String path) {
        if (mMaterialDialog != null && mMaterialDialog.isShowing()) mMaterialDialog.dismiss();

        if (!success) {
            Toast.makeText(getActivity(), R.string.snackbar_folder_not_created, Toast.LENGTH_SHORT).show();

            return;
        }
        new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
        Toast.makeText(getActivity(), R.string.snackbar_pdfCreated, Toast.LENGTH_SHORT).show();

        mOpenPdf.setVisibility(View.VISIBLE);
//        mMorphButtonUtility.morphToSuccess(mCreatePdf);
//        mCreatePdf.blockTouch();
        mPath = path;
        resetValues();
    }

    private void cropImage() {
        Intent intent = new Intent(mActivity, ActivityCropImage.class);
        startActivityForResult(intent, REQUEST_CODE_SECOND_ACTIVITY);
//        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void getRuntimePermissions() {
//        if (Build.VERSION.SDK_INT < 29) {
//            PermissionsUtils.getInstance().requestRuntimePermissions(this, WRITE_PERMISSIONS, REQUEST_CODE_FOR_WRITE_PERMISSION);
//        } else
        if (Build.VERSION.SDK_INT >= 29) {
            PermissionsUtils.getInstance().requestRuntimePermissions(this, READ_PERMISSIONS, REQUEST_CODE_FOR_READ_PERMISSION);
        }
    }

    /**
     * Opens Matisse activities to select Images
     */
    private void selectImages() {
        ImageUtils.selectImages(this, INTENT_REQUEST_GET_IMAGES);
    }

    /**
     * Resets pdf creation related values & show enhancement options
     */
    private void resetValues() {
        mPdfOptions = new ImageToPDFOptions();
        mPdfOptions.setBorderWidth(mSharedPreferences.getInt(DEFAULT_IMAGE_BORDER_TEXT, DEFAULT_BORDER_WIDTH));
        mPdfOptions.setQualityString(Integer.toString(mSharedPreferences.getInt(DEFAULT_COMPRESSION, DEFAULT_QUALITY_VALUE)));
        mPdfOptions.setPageSize(mSharedPreferences.getString(DEFAULT_PAGE_SIZE_TEXT, DEFAULT_PAGE_SIZE));
        mPdfOptions.setPasswordProtected(false);
        mPdfOptions.setWatermarkAdded(false);
        mImagesUri.clear();
        showEnhancementOptions();
        mNoOfImages.setVisibility(View.GONE);
        ImageUtils.getInstance().mImageScaleType = mSharedPreferences.getString(DEFAULT_IMAGE_SCALE_TYPE_TEXT, IMAGE_SCALE_TYPE_ASPECT_RATIO);
        mPdfOptions.setMargins(0, 0, 0, 0);
        mPageNumStyle = mSharedPreferences.getString(Constants.PREF_PAGE_STYLE, null);
        mPageColor = mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_ITP, DEFAULT_PAGE_COLOR);
    }

    private void addMargins() {
//        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
//                .title(R.string.add_margins)
//                .customView(R.layout.add_margins_dialog, false)
//                .positiveText(R.string.ok)
//                .negativeText(R.string.cancel)
//                .onPositive(((dialog, which) -> {
//                    View view = dialog.getCustomView();
//                    EditText top = view.findViewById(R.id.topMarginEditText);
//                    EditText bottom = view.findViewById(R.id.bottomMarginEditText);
//                    EditText right = view.findViewById(R.id.rightMarginEditText);
//                    EditText left = view.findViewById(R.id.leftMarginEditText);
//
//                    mMarginTop = StringUtils.getInstance().parseIntOrDefault(top.getText(), 0);
//                    mMarginBottom = StringUtils.getInstance().parseIntOrDefault(bottom.getText(), 0);
//                    mMarginRight = StringUtils.getInstance().parseIntOrDefault(right.getText(), 0);
//                    mMarginLeft = StringUtils.getInstance().parseIntOrDefault(left.getText(), 0);
//
//                    mPdfOptions.setMargins(mMarginTop, mMarginBottom, mMarginRight, mMarginLeft);
//                })).build();
//        materialDialog.show();

        Dialog dialog = new Dialog(getContext());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.add_margin_dialog_layout);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        EditText top = dialog.findViewById(R.id.topMarginEditText);
        EditText bottom = dialog.findViewById(R.id.bottomMarginEditText);
        EditText right = dialog.findViewById(R.id.rightMarginEditText);
        EditText left = dialog.findViewById(R.id.leftMarginEditText);

        int marginTop = mPdfOptions.getMarginTop();
        int marginBottom = mPdfOptions.getMarginBottom();
        int marginRight = mPdfOptions.getMarginRight();
        int marginLeft = mPdfOptions.getMarginLeft();

        TextView[] textViews = {top, bottom, right, left};
        int[] margins = {marginTop, marginBottom, marginRight, marginLeft};

        for (int i = 0; i < textViews.length; i++) {
            if (margins[i] == 0) {
                textViews[i].setText("");
            } else {
                textViews[i].setText(String.valueOf(margins[i]));
            }
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMarginTop = StringUtils.getInstance().parseIntOrDefault(top.getText(), 0);
                mMarginBottom = StringUtils.getInstance().parseIntOrDefault(bottom.getText(), 0);
                mMarginRight = StringUtils.getInstance().parseIntOrDefault(right.getText(), 0);
                mMarginLeft = StringUtils.getInstance().parseIntOrDefault(left.getText(), 0);
                mPdfOptions.setMargins(mMarginTop, mMarginBottom, mMarginRight, mMarginLeft);
                dialog.dismiss();
            }
        });
    }

    private void addPageNumbers() {

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        mPageNumStyle = mSharedPreferences.getString(Constants.PREF_PAGE_STYLE, null);
        mChoseId = mSharedPreferences.getInt(Constants.PREF_PAGE_STYLE_ID, -1);

//        if (mPageNumStyle.equals()){
//
//        }
//        RelativeLayout dialogLayout = (RelativeLayout) getLayoutInflater()
//                .inflate(R.layout.add_pgnum_dialog, null);
//
//        RadioButton rbOpt1 = dialogLayout.findViewById(R.id.page_num_opt1);
//        RadioButton rbOpt2 = dialogLayout.findViewById(R.id.page_num_opt2);
//        RadioButton rbOpt3 = dialogLayout.findViewById(R.id.page_num_opt3);
//        RadioGroup rg = dialogLayout.findViewById(R.id.radioGroup);
//        CheckBox cbDefault = dialogLayout.findViewById(R.id.set_as_default);
//
//        if (mChoseId > 0) {
//            cbDefault.setChecked(true);
//            rg.clearCheck();
//            rg.check(mChoseId);
//        }
//
//        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
//                .title(R.string.choose_page_number_style)
//                .customView(dialogLayout, false)
//                .positiveText(R.string.ok)
//                .negativeText(R.string.cancel)
//                .neutralText(R.string.remove_dialog)
//                .onPositive(((dialog, which) -> {
//
//                    int checkedRadioButtonId = rg.getCheckedRadioButtonId();
//                    mChoseId = checkedRadioButtonId;
//                    if (checkedRadioButtonId == rbOpt1.getId()) {
//                        mPageNumStyle = Constants.PG_NUM_STYLE_PAGE_X_OF_N;
//                    } else if (checkedRadioButtonId == rbOpt2.getId()) {
//                        mPageNumStyle = Constants.PG_NUM_STYLE_X_OF_N;
//                    } else if (checkedRadioButtonId == rbOpt3.getId()) {
//                        mPageNumStyle = Constants.PG_NUM_STYLE_X;
//                    }
//                    if (cbDefault.isChecked()) {
//                        SharedPreferencesUtil.getInstance().setDefaultPageNumStyle(editor, mPageNumStyle, mChoseId);
//                    } else {
//                        SharedPreferencesUtil.getInstance().clearDefaultPageNumStyle(editor);
//                    }
//                }))
//                .onNeutral((((dialog, which) -> mPageNumStyle = null)))
//                .build();
//        materialDialog.show();
        Dialog dialog = new Dialog(getContext());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.add_pgnum_dialog_layout);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        Button remove = dialog.findViewById(R.id.remove_dialog);
        CheckBox cbDefault = dialog.findViewById(R.id.set_as_default);
        RadioButton rbOpt1 = dialog.findViewById(R.id.page_num_opt1);
        RadioButton rbOpt2 = dialog.findViewById(R.id.page_num_opt2);
        RadioButton rbOpt3 = dialog.findViewById(R.id.page_num_opt3);
        RadioGroup rg = dialog.findViewById(R.id.radioGroup);

        if (mChoseId > 0) {
            cbDefault.setChecked(true);
            rg.clearCheck();
            rg.check(mChoseId);
        }

        if (lastSelectedPageNumber != -1) {
            rg.check(rg.getChildAt(lastSelectedPageNumber).getId());
        } else {
            rg.check(mChoseId);
        }

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rg.clearCheck();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkedRadioButtonId = rg.getCheckedRadioButtonId();
                mChoseId = checkedRadioButtonId;
                if (checkedRadioButtonId == rbOpt1.getId()) {
                    lastSelectedPageNumber = 0;
                    mPageNumStyle = Constants.PG_NUM_STYLE_PAGE_X_OF_N;
                } else if (checkedRadioButtonId == rbOpt2.getId()) {
                    lastSelectedPageNumber = 1;
                    mPageNumStyle = Constants.PG_NUM_STYLE_X_OF_N;
                } else if (checkedRadioButtonId == rbOpt3.getId()) {
                    lastSelectedPageNumber = 2;
                    mPageNumStyle = Constants.PG_NUM_STYLE_X;
                }
                if (cbDefault.isChecked()) {
                    SharedPreferencesUtil.getInstance().setDefaultPageNumStyle(editor, mPageNumStyle, mChoseId);
                } else {
                    SharedPreferencesUtil.getInstance().clearDefaultPageNumStyle(editor);
                }
                dialog.dismiss();
            }
        });
    }
}
