package com.androidmarket.pdfcreator.util;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.Toast;

import com.androidmarket.pdfcreator.fragment.ExceltoPdfFragment;
import com.androidmarket.pdfcreator.interfaces.OnPDFCreatedInterface;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.PdfSecurityOptions;
import com.aspose.cells.Workbook;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

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
//                try {
//                    final Workbook workbook = new Workbook(mDestPath);
//                    if (mIsPasswordProtected) {
//                        PdfSaveOptions saveOption = new PdfSaveOptions();
//                        saveOption.setSecurityOptions(new PdfSecurityOptions());
//                        saveOption.getSecurityOptions().setUserPassword(mPassword);
//                        saveOption.getSecurityOptions().setOwnerPassword(mPassword);
//                        saveOption.getSecurityOptions().setExtractContentPermission(false);
//                        saveOption.getSecurityOptions().setPrintPermission(false);
//                        workbook.save(mDestPath, saveOption);
//                    } else {
//                        workbook.save(mDestPath, FileFormatType.PDF);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    mSuccess = false;
//                }
//                try {
//                    FileOutputStream fos = new FileOutputStream(new File(mDestPath));
//                    com.itextpdf.text.Document document = new com.itextpdf.text.Document();
//                    com.itextpdf.text.pdf.PdfWriter.getInstance(document, fos);
//                    document.open();
//
//                    Paragraph paragraph = new Paragraph("Hello, this is a sample PDF document.");
//                    document.add(paragraph);
//
//                    PdfPTable table = new PdfPTable(3); // 3 columns
//                    table.addCell("Column 1");
//                    table.addCell("Column 2");
//                    table.addCell("Column 3");
//                    document.add(table);
//
//                    document.close();
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (DocumentException e) {
//                    throw new RuntimeException(e);
//                }
                try {
                    // Create PDF
                    FileOutputStream pdfFile = new FileOutputStream(new File(mDestPath));
                    Document document = new Document();
                    document.open();

                    document.close();
                    pdfFile.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                mOnPDFCreatedInterface.onPDFCreated(mSuccess, mDestPath);
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
