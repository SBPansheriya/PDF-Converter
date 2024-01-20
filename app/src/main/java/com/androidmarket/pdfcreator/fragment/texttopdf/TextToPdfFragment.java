package com.androidmarket.pdfcreator.fragment.texttopdf;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidmarket.pdfcreator.activities.HomeActivity;
import com.androidmarket.pdfcreator.adapter.AdapterEnhancementOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import androidmarket.R;

import com.androidmarket.pdfcreator.interfaces.Enhancer;
import com.androidmarket.pdfcreator.interfaces.OnItemClickListener;
import com.androidmarket.pdfcreator.interfaces.OnTextToPdfInterface;
import com.androidmarket.pdfcreator.pdfModel.EnhancementOptionsEntity;
import com.androidmarket.pdfcreator.pdfModel.TextToPDFOptions;
import com.androidmarket.pdfcreator.Constants;
import com.androidmarket.pdfcreator.util.AdsUtility;
import com.androidmarket.pdfcreator.util.DialogUtils;
import com.androidmarket.pdfcreator.util.DirectoryUtils;
import com.androidmarket.pdfcreator.util.FileUtils;
import com.androidmarket.pdfcreator.util.MorphButtonUtility;
import com.androidmarket.pdfcreator.util.PageSizeUtils;
import com.androidmarket.pdfcreator.util.PermissionsUtils;
import com.androidmarket.pdfcreator.util.StringUtils;
import com.androidmarket.pdfcreator.util.TextToPDFUtils;
import com.androidmarket.pdfcreator.util.TextToPdfAsync;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;

import static android.app.Activity.RESULT_OK;
//import static com.androidmarket.pdfcreator.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
//import static com.androidmarket.pdfcreator.Constants.WRITE_PERMISSIONS;

public class TextToPdfFragment extends Fragment implements OnItemClickListener,
        OnTextToPdfInterface, TextToPdfContract.View {

    private Activity mActivity;
    private FileUtils mFileUtils;
    private DirectoryUtils mDirectoryUtils;
    public static int lastSelectedFontFamily;
    private final int mFileSelectCode = 0;
    private Uri mTextFileUri = null;
    private String mFileExtension;
    private int mButtonClicked = 0;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    private MaterialDialog mMaterialDialog;
    private String mFileNameWithType = null;

    @BindView(R.id.enhancement_options_recycle_view_text)
    RecyclerView mTextEnhancementOptionsRecycleView;
    @BindView(R.id.selectFile)
    CardView mSelectFile;
    @BindView(R.id.createtextpdf)
    CardView mCreateTextPdf;

    private AdapterEnhancementOptions mTextAdapterEnhancementOptions;
    private MorphButtonUtility mMorphButtonUtility;
    private String mPath;
    private List<Enhancer> mEnhancerList;
    private TextToPDFOptions.Builder mBuilder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_text_to_pdf, container, false);

        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        ButterKnife.bind(this, rootView);

        lastSelectedFontFamily = -1;

        mBuilder = new TextToPDFOptions.Builder(mActivity);
        addEnhancements();
        showEnhancementOptions();
        mCreateTextPdf.setEnabled(false);


        FrameLayout nativeAdFrameOne = rootView.findViewById(R.id.nativeAdFrameLayout);
        AdsUtility.loadNativeAd(getActivity(), nativeAdFrameOne);


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

    private void addEnhancements() {
        mEnhancerList = new ArrayList<>();
        for (final Enhancers enhancer : Enhancers.values()) {
            mEnhancerList.add(enhancer.getEnhancer(mActivity, this, mBuilder));
        }
    }

    /**
     * Function to show the enhancement options.
     */
    private void showEnhancementOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        mTextEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        List<EnhancementOptionsEntity> optionsEntityist = new ArrayList<>();
        for (Enhancer enhancer : mEnhancerList) {
            optionsEntityist.add(enhancer.getEnhancementOptionsEntity());
        }
        mTextAdapterEnhancementOptions = new AdapterEnhancementOptions(this, optionsEntityist);
        mTextEnhancementOptionsRecycleView.setAdapter(mTextAdapterEnhancementOptions);
    }

    @Override
    public void onItemClick(int position) {
        final Enhancer enhancer = mEnhancerList.get(position);
        enhancer.enhance();
    }

    @OnClick(R.id.createtextpdf)
    public void openCreateTextPdf() {
//        new MaterialDialog.Builder(mActivity)
//                .title(R.string.creating_pdf)
//                .content(R.string.enter_file_name)
//                .input(getString(R.string.example), mFileNameWithType,
//                        (dialog, input) -> {
//                            if (StringUtils.getInstance().isEmpty(input)) {
//                                StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
//                            } else {
//                                final String inputName = input.toString();
//                                if (!mFileUtils.isFileExist(inputName + getString(R.string.pdf_ext))) {
//                                    createPdf(inputName);
//                                } else {
//                                    MaterialDialog.Builder builder = DialogUtils.getInstance()
//                                            .createOverwriteDialog(mActivity);
//                                    builder.onPositive((dialog12, which) -> createPdf(inputName))
//                                            .onNegative((dialog1, which) -> openCreateTextPdf())
//                                            .show();
//                                }
//                            }
//                        })
//                .show();

        Dialog dialog = new Dialog(getContext());
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
        EditText input = dialog.findViewById(R.id.add_pdfName);

        input.setText(mFileNameWithType);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputname  = input.getText().toString();
                if (StringUtils.getInstance().isEmpty(inputname)) {
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                } else {
                    final String inputName = inputname;
                    if (!mFileUtils.isFileExist(inputName + getString(R.string.pdf_ext))) {
                        createPdf(inputName);
                    } else {
                        Dialog dialog = new Dialog(getActivity());
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
                                openCreateTextPdf();
                                dialog.dismiss();
                            }
                        });

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createPdf(inputName);
                                dialog.dismiss();
                            }
                        });
                    }
                }
                lastSelectedFontFamily = -1;
                dialog.dismiss();
            }
        });
    }

    /**
     * function to create PDF
     *
     * @param mFilename name of file to be created.
     */
    private void createPdf(String mFilename) {
        mPath = mDirectoryUtils.getOrCreatePdfDirectory().getPath();
        mPath = mPath + "/" + mFilename + mActivity.getString(R.string.pdf_ext);
        TextToPDFOptions options = mBuilder.setFileName(mFilename)
                .setPageSize(PageSizeUtils.mPageSize)
                .setInFileUri(mTextFileUri)
                .build();
        TextToPDFUtils fileUtil = new TextToPDFUtils(mActivity);
        new TextToPdfAsync(fileUtil, options, mFileExtension,
                TextToPdfFragment.this).execute();
    }

    /**
     * Create a file picker to get text file.
     */
    @OnClick(R.id.selectFile)
    public void selectTextFile() {
//        if (isStoragePermissionGranted()) {
            selectFile();
//        } else {
//            getRuntimePermissions();
//        }
    }

    private void selectFile() {
        if (mButtonClicked == 0) {
            Uri uri = Uri.parse(Environment.getRootDirectory() + "/");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setDataAndType(uri, "*/*");
            String[] mimeTypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/msword", getString(R.string.text_type)};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(
                        Intent.createChooser(intent, String.valueOf(R.string.select_file)),
                        mFileSelectCode);
            } catch (android.content.ActivityNotFoundException ex) {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.install_file_manager);
            }
            mButtonClicked = 1;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mButtonClicked = 0;
        if (requestCode == mFileSelectCode) {
            if (resultCode == RESULT_OK) {
                mTextFileUri = data.getData();
                StringUtils.getInstance().showSnackbar(mActivity, R.string.text_file_selected);
                String fileName = mFileUtils.getFileName(mTextFileUri);
                if (fileName != null) {
                    if (fileName.endsWith(Constants.textExtension))
                        mFileExtension = Constants.textExtension;
                    else if (fileName.endsWith(Constants.docxExtension))
                        mFileExtension = Constants.docxExtension;
                    else if (fileName.endsWith(Constants.docExtension))
                        mFileExtension = Constants.docExtension;
                    else {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.extension_not_supported);
                        return;
                    }
                }
                mFileNameWithType = mFileUtils.stripExtension(fileName) + getString(R.string.pdf_suffix);
                mCreateTextPdf.setEnabled(true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        } else {
            mActivity = getActivity();
        }

        mFileUtils = new FileUtils(mActivity);
        mDirectoryUtils = new DirectoryUtils(mActivity);
    }

//    private boolean isStoragePermissionGranted() {
//        if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 29) {
//            return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//        } else {
//            return true;
//        }
//    }

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
//                requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION, this::selectFile);
//    }

    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean success) {
        if (mMaterialDialog != null && mMaterialDialog.isShowing())
            mMaterialDialog.dismiss();
        if (!success) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.error_pdf_not_created);
            mCreateTextPdf.setEnabled(false);
            mTextFileUri = null;
            mButtonClicked = 0;
            return;
        }
        StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                .setAction(R.string.snackbar_viewAction,
                        v -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF)).show();
        mCreateTextPdf.setEnabled(false);
        mTextFileUri = null;
        mButtonClicked = 0;
        mBuilder = new TextToPDFOptions.Builder(mActivity);
    }

    @Override
    public void updateView() {
        mTextAdapterEnhancementOptions.notifyDataSetChanged();
    }
}
