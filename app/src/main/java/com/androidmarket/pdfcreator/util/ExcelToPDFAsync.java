package com.androidmarket.pdfcreator.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.aspose.cells.FileFormatType;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.PdfSecurityOptions;
import com.aspose.cells.Workbook;

import com.androidmarket.pdfcreator.interfaces.OnPDFCreatedInterface;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExcelToPDFAsync {
    private final OnPDFCreatedInterface mOnPDFCreatedInterface;
    private boolean mSuccess;
    private final String mPath;
    private final boolean mIsPasswordProtected;
    private final String mDestPath;
    private final String mPassword;

    /**
     * This public constructor is responsible for initializing the path of actual file,
     * the destination path and the onPDFCreatedInterface instance.
     *
     * @param parentPath   is the path of the actual excel file to be converted.
     * @param destPath     is the path of the destination pdf file.
     * @param onPDFCreated is the onPDFCreatedInterface instance.
     */
    @SuppressLint("StaticFieldLeak")
    public ExcelToPDFAsync(String parentPath, String destPath,
                           OnPDFCreatedInterface onPDFCreated, boolean isPasswordProtected, String password) {
        mPath = parentPath;
        mDestPath = destPath;
        this.mOnPDFCreatedInterface = onPDFCreated;
        mIsPasswordProtected = isPasswordProtected;
        mPassword = password;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                mSuccess = true;
                mOnPDFCreatedInterface.onPDFCreationStarted();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    final Workbook workbook = new Workbook(mPath);
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
                mOnPDFCreatedInterface.onPDFCreated(mSuccess, mPath);
            }
        }.execute();
    }
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        mSuccess = true;
//        mOnPDFCreatedInterface.onPDFCreationStarted();
//    }
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//        Executor executor = Executors.newSingleThreadExecutor();
//        executor.execute(() -> {
//            try {
//                final Workbook workbook = new Workbook(mPath);
//                if (mIsPasswordProtected) {
//                    PdfSaveOptions saveOption = new PdfSaveOptions();
//                    saveOption.setSecurityOptions(new PdfSecurityOptions());
//                    saveOption.getSecurityOptions().setUserPassword(mPassword);
//                    saveOption.getSecurityOptions().setOwnerPassword(mPassword);
//                    saveOption.getSecurityOptions().setExtractContentPermission(false);
//                    saveOption.getSecurityOptions().setPrintPermission(false);
//                    workbook.save(mDestPath, saveOption);
//                } else {
//                    workbook.save(mDestPath, FileFormatType.PDF);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                mSuccess = false;
//            }
//        });
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(Void aVoid) {
//        super.onPostExecute(aVoid);
//        mOnPDFCreatedInterface.onPDFCreated(mSuccess, mPath);
//    }
}
