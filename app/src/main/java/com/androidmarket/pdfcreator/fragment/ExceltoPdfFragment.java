package com.androidmarket.pdfcreator.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.androidmarket.pdfcreator.activities.HomeActivity;
import com.androidmarket.pdfcreator.adapter.AdapterEnhancementOptions;
import com.androidmarket.pdfcreator.adapter.AdapterMergeFiles;
import com.androidmarket.pdfcreator.util.AdsUtility;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;

import androidmarket.R;

import com.androidmarket.pdfcreator.db.DatabaseHelper;
import com.androidmarket.pdfcreator.interfaces.BottomSheetPopulate;
import com.androidmarket.pdfcreator.interfaces.OnItemClickListener;
import com.androidmarket.pdfcreator.interfaces.OnPDFCreatedInterface;
import com.androidmarket.pdfcreator.pdfModel.EnhancementOptionsEntity;
import com.androidmarket.pdfcreator.util.BottomSheetCallback;
import com.androidmarket.pdfcreator.util.BottomSheetUtils;
import com.androidmarket.pdfcreator.util.CommonCodeUtils;
import com.androidmarket.pdfcreator.Constants;
import com.androidmarket.pdfcreator.util.DefaultTextWatcher;
import com.androidmarket.pdfcreator.util.DialogUtils;
import com.androidmarket.pdfcreator.util.ExcelToPDFAsync;
import com.androidmarket.pdfcreator.util.FileUtils;
import com.androidmarket.pdfcreator.util.MergePdfEnhancementOptionsUtils;
import com.androidmarket.pdfcreator.util.MorphButtonUtility;
import com.androidmarket.pdfcreator.util.PermissionsUtils;
import com.androidmarket.pdfcreator.util.RealPathUtil;
import com.androidmarket.pdfcreator.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
//import static com.androidmarket.pdfcreator.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static com.androidmarket.pdfcreator.Constants.STORAGE_LOCATION;
//import static com.androidmarket.pdfcreator.Constants.WRITE_PERMISSIONS;

public class ExceltoPdfFragment extends Fragment implements AdapterMergeFiles.OnClickListener,
        OnPDFCreatedInterface, OnItemClickListener, BottomSheetPopulate {
    private Activity mActivity;
    private FileUtils mFileUtils;
    private Uri mExcelFileUri;
    private String mRealPath;
    private String mPath;
    private BottomSheetBehavior mSheetBehavior;

    @BindView(R.id.lottie_progress)
    LottieAnimationView mLottieProgress;
    @BindView(R.id.tv_excel_file_name_bottom)
    TextView mTextView;
    @BindView(R.id.open_pdf)
    CardView mOpenPdf;
    @BindView(R.id.create_excel_to_pdf)
    CardView mCreateExcelPdf;
    @BindView(R.id.enhancement_options_recycle_view)
    RecyclerView mEnhancementOptionsRecycleView;
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;

    private StringUtils mStringUtils;
    private SharedPreferences mSharedPreferences;
    private MorphButtonUtility mMorphButtonUtility;
    private BottomSheetUtils mBottomSheetUtils;
    private boolean mButtonClicked = false;
    private final int mFileSelectCode = 123;
    private MaterialDialog mMaterialDialog;
    private ArrayList<EnhancementOptionsEntity> mEnhancementOptionsEntityArrayList;
    private AdapterEnhancementOptions mAdapterEnhancementOptions;
    private boolean mPasswordProtected = false;
    private String mPassword;
    private static final int PICK_FILE_REQUEST_CODE = 123;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_excelto_pdf, container,
                false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);

        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        ButterKnife.bind(this, rootView);
        showEnhancementOptions();
        mCreateExcelPdf.setEnabled(false);

        ButterKnife.bind(this, rootView);
        mSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, isAdded()));
        mLottieProgress.setVisibility(View.VISIBLE);
        mBottomSheetUtils.populateBottomSheetWithExcelFiles(this);


        ImageView ivBack = rootView.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HomeActivity.class));
                getActivity().finish();
            }
        });

        return rootView;
    }

    private void showEnhancementOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        mEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        mEnhancementOptionsEntityArrayList = MergePdfEnhancementOptionsUtils.getInstance()
                .getEnhancementOptions(mActivity);
        mAdapterEnhancementOptions = new AdapterEnhancementOptions(this, mEnhancementOptionsEntityArrayList);
        mEnhancementOptionsRecycleView.setAdapter(mAdapterEnhancementOptions);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mFileUtils = new FileUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
        mStringUtils = StringUtils.getInstance();
    }

    @OnClick(R.id.select_excel_file)
    public void selectExcelFile() {
        if (!mButtonClicked) {
            Uri uri = Uri.parse(Environment.getRootDirectory() + "/");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setDataAndType(uri, "*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(
                        Intent.createChooser(intent, String.valueOf(R.string.select_file)),
                        mFileSelectCode);
            } catch (android.content.ActivityNotFoundException ex) {
                mStringUtils.showSnackbar(mActivity, R.string.install_file_manager);
            }
            mButtonClicked = true;
        }
    }

    /**
     * This function opens a dialog to enter the file name of
     * the converted file.
     */
    @OnClick(R.id.create_excel_to_pdf)
    public void openExcelToPdf() {
        openExcelToPdf_();
//        if (isStoragePermissionGranted()) {
//            openExcelToPdf_();
//        } else {
//            getRuntimePermissions();
//        }
    }

    private void openExcelToPdf_() {
//        new MaterialDialog.Builder(mActivity)
//                .title(R.string.creating_pdf)
//                .content(R.string.enter_file_name)
//                .input(getString(R.string.example), null, (dialog, input) -> {
//                    if (mStringUtils.isEmpty(input)) {
//                        mStringUtils.showSnackbar(mActivity, R.string.snackbar_name_not_blank);
//                    } else {
//                        final String inputName = input.toString();
//                        if (!mFileUtils.isFileExist(inputName + getString(R.string.pdf_ext))) {
//                            convertToPdf(inputName);
//                        } else {
//                            MaterialDialog.Builder builder = DialogUtils.getInstance().createOverwriteDialog(mActivity);
//                            builder.onPositive((dialog12, which) -> convertToPdf(inputName))
//                                    .onNegative((dialog1, which) -> openExcelToPdf())
//                                    .show();
//                        }
//                    }
//                })
//                .show();

        Dialog dialog = new Dialog(mActivity);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.create_pdf_dialog);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        EditText editText = dialog.findViewById(R.id.add_pdfName);

//        editText.setText(mRealPath);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String filename = editText.getText().toString();
                if (mStringUtils.isEmpty(filename)) {
                    mStringUtils.showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                } else {
                    final String inputName = filename;
                    if (!mFileUtils.isFileExist(inputName + getString(R.string.pdf_ext))) {
                        convertToPdf(inputName);
                    } else {
                        Dialog dialog = new Dialog(mActivity);
                        if (dialog.getWindow() != null) {
                            dialog.getWindow().setGravity(Gravity.CENTER);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialog.setCancelable(false);
                        }
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        dialog.setContentView(R.layout.name_override_dialog);
                        dialog.setCancelable(false);
                        dialog.show();

                        Button cancel = dialog.findViewById(R.id.canceldialog);
                        Button ok = dialog.findViewById(R.id.okdialog);

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openExcelToPdf();
                                dialog.dismiss();
                            }
                        });

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                convertToPdf(inputName);
                                dialog.dismiss();
                            }
                        });
                    }
                    dialog.dismiss();
                }
            }
        });
    }

    @OnClick(R.id.open_pdf)
    void openPdf() {
        mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mButtonClicked = false;
        if (requestCode == mFileSelectCode) {
            if (resultCode == RESULT_OK) {
                mExcelFileUri = data.getData();
                mRealPath = getFilePathFromUri(mExcelFileUri);
//                mRealPath = RealPathUtil.getInstance().getRealPath(getContext(), mExcelFileUri);
//                mRealPath =  mFileUtils.getFileName(mExcelFileUri);
                processUri();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
//            if (data != null) {
//                Uri uri = data.getData();
//
//                if (uri != null) {
//                    mRealPath = getPathFromUri(uri);
//                    processUri();
//                    Toast.makeText(getActivity(), "Selected file: " + mRealPath, Toast.LENGTH_SHORT).show();
//                    // Now you have the file path, and you can handle it as needed.
//                }
//            }
//        }
    }

    private String getFilePathFromUri(Uri uri) {
        String path = null;

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {OpenableColumns.DISPLAY_NAME};
            try (Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    path = cursor.getString(columnIndex);
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        } else {
            // For other URIs, attempt to copy the content to a temporary file
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    File tempFile = createTempFileFromStream(inputStream);
                    path = tempFile.getAbsolutePath();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return path;
    }

    private File createTempFileFromStream(InputStream inputStream) {
        try {
            File tempFile = new File(getActivity().getCacheDir(), "temp_file.xlsx");
            OutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4 * 1024]; // Adjust buffer size as needed

            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    private boolean isStoragePermissionGranted() {
//        if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 29) {
//            return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//        } else {
//            return true;
//        }
//    }
//
//    private void getRuntimePermissions() {
//        if (Build.VERSION.SDK_INT < 29) {
//            PermissionsUtils.getInstance().requestRuntimePermissions(this,
//                    WRITE_PERMISSIONS,
//                    REQUEST_CODE_FOR_WRITE_PERMISSION);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
//        PermissionsUtils.getInstance().handleRequestPermissionsResult(mActivity, grantResults,
//                requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION, this::openExcelToPdf_);
//    }

    private void processUri() {
        mStringUtils.showSnackbar(mActivity, getResources().getString(R.string.excel_selected));
        String fileName = mFileUtils.getFileName(mExcelFileUri);
        if (fileName != null && !fileName.endsWith(Constants.excelExtension) &&
                !fileName.endsWith(Constants.excelWorkbookExtension)) {
            mStringUtils.showSnackbar(mActivity, R.string.extension_not_supported);
            return;
        }

        fileName = getResources().getString(R.string.excel_selected)
                + fileName;
        mTextView.setText(fileName);
        mTextView.setVisibility(View.VISIBLE);
        mCreateExcelPdf.setEnabled(true);
        mOpenPdf.setVisibility(View.GONE);
    }

    /**
     * This function is required to convert the chosen excel file
     * to PDF.
     *
     * @param mFilename - output PDF name
     */

    private void convertToPdf(String mFilename) {
        String mStorePath = mSharedPreferences.getString(STORAGE_LOCATION,
                mStringUtils.getDefaultStorageLocation());

        mPath = mStorePath + mFilename + mActivity.getString(R.string.pdf_ext);
        new ExcelToPDFAsync(mRealPath, mPath, ExceltoPdfFragment.this, mPasswordProtected, mPassword);
    }

    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean success, String path) {
        if (mMaterialDialog != null && mMaterialDialog.isShowing())
            mMaterialDialog.dismiss();
        if (!success) {
            mStringUtils.showSnackbar(mActivity, R.string.error_pdf_not_created);
            mTextView.setVisibility(View.GONE);
            mCreateExcelPdf.setEnabled(false);
            mExcelFileUri = null;
            return;
        }
        mStringUtils.getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                .setAction(R.string.snackbar_viewAction,
                        v -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF))
                .show();
        new DatabaseHelper(mActivity).insertRecord(mPath, mActivity.getString(R.string.created));
        mTextView.setVisibility(View.GONE);
        mOpenPdf.setVisibility(View.VISIBLE);
        mExcelFileUri = null;
        mPasswordProtected = false;
        showEnhancementOptions();
    }

    @Override
    public void onItemClick(int position) {
        if (!mCreateExcelPdf.isEnabled()) {
            mStringUtils.showSnackbar(mActivity, R.string.no_excel_file);
            return;
        }
        if (position == 0) {
            setPassword();
        }
    }

    private void setPassword() {
//        MaterialDialog.Builder builder = DialogUtils.getInstance()
//                .createCustomDialogWithoutContent(mActivity, R.string.set_password);
//        final MaterialDialog dialog = builder
//                .customView(R.layout.custom_dialog, true)
//                .neutralText(R.string.remove_dialog)
//                .build();
//
//        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
//        final View neutralAction = dialog.getActionButton(DialogAction.NEUTRAL);
//        final EditText passwordInput = Objects.requireNonNull(dialog.getCustomView()).findViewById(R.id.password);
//        passwordInput.setText(mPassword);
//        passwordInput.addTextChangedListener(watcherImpl(positiveAction));
//        if (mStringUtils.isNotEmpty(mPassword)) {
//            neutralAction.setOnClickListener(v -> {
//                mPassword = null;
//                setPasswordIcon(R.drawable.password_protect_pdf);
//                mPasswordProtected = false;
//                dialog.dismiss();
//                mStringUtils.showSnackbar(mActivity, R.string.password_remove);
//            });
//        }
//        dialog.show();
//        positiveAction.setEnabled(false);

        Dialog dialog = new Dialog(mActivity);
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

        passwordinput.setText(mPassword);
        passwordinput.addTextChangedListener(
                new DefaultTextWatcher() {
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

        if (mStringUtils.isNotEmpty(mPassword)) {
            remove.setOnClickListener(v -> {
                mPassword = null;
                setPasswordIcon(R.drawable.password_protect_pdf);
                mPasswordProtected = false;
                dialog.dismiss();
                mStringUtils.showSnackbar(mActivity, R.string.password_remove);
            });
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = passwordinput.getText().toString();
                if (mStringUtils.isEmpty(input)) {
                    mStringUtils.showSnackbar(mActivity, R.string.snackbar_password_cannot_be_blank);
                } else {
                    mPassword = input;
                    mPasswordProtected = true;
                    setPasswordIcon(R.drawable.baseline_done_24);
                    dialog.dismiss();
                }
            }
        });
    }

    private DefaultTextWatcher watcherImpl(View positiveAction) {
        return new DefaultTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable input) {
                if (mStringUtils.isEmpty(input)) {
                    mStringUtils.showSnackbar(mActivity, R.string.snackbar_password_cannot_be_blank);
                } else {
                    mPassword = input.toString();
                    mPasswordProtected = true;
                    setPasswordIcon(R.drawable.baseline_done_24);
                }
            }
        };
    }

    private void setPasswordIcon(int drawable) {
        mEnhancementOptionsEntityArrayList.get(0).setImage(mActivity.getResources().getDrawable(drawable));
        mAdapterEnhancementOptions.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(String path) {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mExcelFileUri = Uri.parse("file://" + path);
        mRealPath = path;
        processUri();
    }

    @Override
    public void onPopulate(ArrayList<String> paths) {
        CommonCodeUtils.getInstance().populateUtil(mActivity, paths,
                this, mLayout, mLottieProgress, mRecyclerViewFiles);
    }

}
