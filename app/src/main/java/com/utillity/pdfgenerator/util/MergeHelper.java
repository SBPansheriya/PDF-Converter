package com.utillity.pdfgenerator.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.afollestad.materialdialogs.MaterialDialog;

import com.utillity.pdfgenerator.R;

import com.utillity.pdfgenerator.adapter.AdapterViewFiles;
import com.utillity.pdfgenerator.db.DatabaseHelper;
import com.utillity.pdfgenerator.interfaces.MergeFilesListener;

import static com.utillity.pdfgenerator.Constants.MASTER_PWD_STRING;
import static com.utillity.pdfgenerator.Constants.STORAGE_LOCATION;
import static com.utillity.pdfgenerator.Constants.appName;

public class MergeHelper implements MergeFilesListener {
    private MaterialDialog mMaterialDialog;
    private final Activity mActivity;
    private final FileUtils mFileUtils;
    private final boolean mPasswordProtected = false;
    private String mPassword;
    private final String mHomePath;
    private final Context mContext;
    private final AdapterViewFiles mAdapterViewFiles;
    private final SharedPreferences mSharedPrefs;

    public MergeHelper(Activity activity, AdapterViewFiles adapterViewFiles) {
        mActivity = activity;
        mFileUtils = new FileUtils(mActivity);
        mHomePath = PreferenceManager.getDefaultSharedPreferences(mActivity)
                .getString(STORAGE_LOCATION,
                        StringUtils.getInstance().getDefaultStorageLocation(activity));
        mContext = mActivity;
        mAdapterViewFiles = adapterViewFiles;
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
    }

    public void mergeFiles() {
        String[] pdfpaths = mAdapterViewFiles.getSelectedFilePath().toArray(new String[0]);
        String masterpwd = mSharedPrefs.getString(MASTER_PWD_STRING, appName);
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(mContext.getResources().getString(R.string.example), null, (dialog, input) -> {
                    if (StringUtils.getInstance().isEmpty(input)) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                    } else {
                        if (!mFileUtils.isFileExist(input + mContext.getResources().getString(R.string.pdf_ext))) {
                            new MergePdf(input.toString(), mHomePath, mPasswordProtected,
                                    mPassword, this, masterpwd).execute(pdfpaths);
                        } else {
                            MaterialDialog.Builder builder = DialogUtils.getInstance().createOverwriteDialog(mActivity);
                            builder.onPositive((dialog12, which) -> new MergePdf(input.toString(),
                                    mHomePath, mPasswordProtected, mPassword,
                                    this, masterpwd).execute(pdfpaths))
                                    .onNegative((dialog1, which) -> mergeFiles()).show();
                        }
                    }
                })
                .show();
    }
    @Override
    public void resetValues(boolean isPDFMerged, String path) {
        mMaterialDialog.dismiss();
        if (isPDFMerged) {
            StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.pdf_merged)
                    .setAction(R.string.snackbar_viewAction, v ->
                            mFileUtils.openFile(path, FileUtils.FileType.e_PDF)).show();
            new DatabaseHelper(mActivity).insertRecord(path,
                    mActivity.getString(R.string.created));
        } else
            StringUtils.getInstance().showSnackbar(mActivity, R.string.file_access_error);
        mAdapterViewFiles.updateDataset();
    }

    @Override
    public void mergeStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }
}
