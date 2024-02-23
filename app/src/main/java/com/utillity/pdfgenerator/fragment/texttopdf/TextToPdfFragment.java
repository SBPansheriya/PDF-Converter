package com.utillity.pdfgenerator.fragment.texttopdf;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.utillity.pdfgenerator.activities.HomeActivity;
import com.utillity.pdfgenerator.adapter.AdapterEnhancementOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.utillity.pdfgenerator.R;

import com.utillity.pdfgenerator.interfaces.Enhancer;
import com.utillity.pdfgenerator.interfaces.OnItemClickListener;
import com.utillity.pdfgenerator.interfaces.OnTextToPdfInterface;
import com.utillity.pdfgenerator.pdfModel.EnhancementOptionsEntity;
import com.utillity.pdfgenerator.pdfModel.TextToPDFOptions;
import com.utillity.pdfgenerator.Constants;
import com.utillity.pdfgenerator.util.DialogUtils;
import com.utillity.pdfgenerator.util.DirectoryUtils;
import com.utillity.pdfgenerator.util.FileUtils;
import com.utillity.pdfgenerator.util.MorphButtonUtility;
import com.utillity.pdfgenerator.util.PageSizeUtils;
import com.utillity.pdfgenerator.util.StringUtils;
import com.utillity.pdfgenerator.util.TextToPDFUtils;
import com.utillity.pdfgenerator.util.TextToPdfAsync;

import static android.app.Activity.RESULT_OK;

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

    @OnClick(R.id.selectFile)
    public void selectTextFile() {
            selectFile();
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
