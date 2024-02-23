package com.utillity.pdfgenerator.util;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;

import com.utillity.pdfgenerator.interfaces.OnPDFCreatedInterface;

import com.aspose.cells.FileFormatType;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.PdfSecurityOptions;
import com.aspose.cells.Workbook;

public class ExcelToPDFAsync {
    private final OnPDFCreatedInterface mOnPDFCreatedInterface;
    private boolean mSuccess;
    private final String mPath;
    private final boolean mIsPasswordProtected;
    private final String mDestPath;
    Activity activity;
    Uri uriFile;
    private final String mPassword;

    @SuppressLint("StaticFieldLeak")
    public ExcelToPDFAsync(String parentPath, String destPath, OnPDFCreatedInterface onPDFCreated, boolean isPasswordProtected, String password, Activity mActivity, Uri mExcelFileUri) {
        mPath = parentPath;
        mDestPath = destPath;
        this.mOnPDFCreatedInterface = onPDFCreated;
        mIsPasswordProtected = isPasswordProtected;
        mPassword = password;
        activity = mActivity;
        uriFile = mExcelFileUri;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                mSuccess = true;
                mOnPDFCreatedInterface.onPDFCreationStarted();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Workbook workbook = new Workbook();

                    if (mIsPasswordProtected) {
                        PdfSaveOptions saveOption = new PdfSaveOptions();
                        saveOption.setSecurityOptions(new PdfSecurityOptions());
                        saveOption.getSecurityOptions().setUserPassword(mPassword);
                        saveOption.getSecurityOptions().setOwnerPassword(mPassword);
                        saveOption.getSecurityOptions().setExtractContentPermission(false);
                        saveOption.getSecurityOptions().setPrintPermission(false);
                        workbook.save(mDestPath, saveOption);
                    } else {
                        workbook.save(mDestPath, FileFormatType.PDF);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mSuccess = false;
                }
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                mOnPDFCreatedInterface.onPDFCreated(mSuccess, mDestPath);
            }
        }.execute();
    }
}