package com.androidmarket.pdfcreator.util;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.androidmarket.pdfcreator.interfaces.OnPDFCreatedInterface;

import com.aspose.cells.FileFormatType;
import com.aspose.cells.PdfCompliance;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.PdfSecurityOptions;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ExcelToPDFAsync {
    private final OnPDFCreatedInterface mOnPDFCreatedInterface;
    private boolean mSuccess;
    private final String mPath;
    private final boolean mIsPasswordProtected;
    private final String mDestPath;
    Activity activity;
    Uri uriFile;
    private final String mPassword;

    /**
     * This public constructor is responsible for initializing the path of actual file,
     * the destination path and the onPDFCreatedInterface instance.
     *
     * @param parentPath    is the path of the actual excel file to be converted.
     * @param destPath      is the path of the destination pdf file.
     * @param onPDFCreated  is the onPDFCreatedInterface instance.
     * @param mActivity
     * @param mExcelFileUri
     */
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
//                try {
////                    Base64.InputStream inputStream = (Base64.InputStream) activity.getAssets().open(mPath);
////                    Workbook workbook = new XSSFWorkbook(inputStream);
//
//                    File pdfFile = new File(Environment.getExternalStorageDirectory(), "output.pdf");
//                    Document document = new Document();
//                    PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
//                    document.open();
//
//                    FileInputStream fis = new FileInputStream(mPath);
//                    Workbook workbook1 = new XSSFWorkbook(fis);
//
//                    Sheet sheet = workbook1.getSheetAt(0);
//
////                    com.itextpdf.text.pdf.PdfDocument pdf = new PdfDocument();
////                    com.itextpdf.text.Document document = new Document();
////                    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(mDestPath));
//
//                    for (Row row : sheet) {
//                        for (Cell cell : row) {
//                            String cellValue = cell.getStringCellValue();
//                            document.add(new Paragraph(cellValue));
//                        }
//                    }
//                    document.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    mSuccess = false;
//                }
//                return null;
//            }
                try {
//                    FileInputStream input_document = new FileInputStream(new File(mPath));
//                    HSSFWorkbook my_xls_workbook = new HSSFWorkbook(input_document);
//                    HSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
//                    Iterator<Row> rowIterator = my_worksheet.iterator();
//                    Document iText_xls_2_pdf = new Document();
//                    PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(mDestPath));
//                    iText_xls_2_pdf.open();
//
//                    PdfPTable my_table = new PdfPTable(2);
//                    PdfPCell table_cell;
//                    while(rowIterator.hasNext()) {
//                        Row row = rowIterator.next();
//                        Iterator<Cell> cellIterator = row.cellIterator();
//                        while(cellIterator.hasNext()) {
//                            Cell cell = cellIterator.next();
//                            switch(cell.getCellType()) {
//
//                                case Cell.CELL_TYPE_STRING:
//                                    table_cell=new PdfPCell(new Phrase(cell.getStringCellValue()));
//                                    my_table.addCell(table_cell);
//                                    break;
//                            }
//                        }
//                    }
//                    iText_xls_2_pdf.add(my_table);
//                    iText_xls_2_pdf.close();
//                    input_document.close();
//                    File file = new File(mPath);
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
//                    Workbook workbook = new Workbook();
//                    FileInputStream inputStream = new FileInputStream(mPath);
//                    Workbook workbook = new Workbook(mPath);
//
//                    PdfSaveOptions options = new PdfSaveOptions();
//                    options.setCompliance(PdfCompliance.PDF_A_1_A);
//                    workbook.save(mDestPath, FileFormatType.PDF);
//                    File pdfFile1 = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), mPath);
//                    FileOutputStream pdfFile = new FileOutputStream(pdfFile1);
//                    Document document = new Document();
//                    PdfWriter.getInstance(document, pdfFile);
//                    document.open();
//
//                    for (int i = 0; i < workbook.getWorksheets().getCount(); i++) {
//                        Worksheet worksheet = workbook.getWorksheets().get(i);
//                        for (int row = 0; row <= worksheet.getCells().getMaxDataRow(); row++) {
//                            for (int col = 0; col <= worksheet.getCells().getMaxDataColumn(); col++) {
//                                Cell cell = (Cell) worksheet.getCells().get(row, col);
//                                document.add(new Paragraph(cell.getStringCellValue()));
//                            }
//                        }
//                    }
//                    document.close();
//                    pdfFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    mSuccess = false;
                }
                return null;
            }
////                try {
////                    Document document = new Document();
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                        PdfWriter.getInstance(document, Files.newOutputStream(Paths.get(mDestPath)));
////                    }
////                    document.open();
////
////                    FileInputStream inputStream = new FileInputStream(mPath);
////                    Workbook workbook = new Workbook(inputStream);
////                    int read;
////                    byte[] bytes = new byte[1024];
////
////                    while ((read = inputStream.read(bytes)) != -1) {
////                        document.add(new Paragraph(new String(bytes, 0, read)));
////
////                    }
////                    workbook.save(mDestPath,FileFormatType.PDF);
////                    document.close();
////                    inputStream.close();
////
////                    Toast.makeText(activity, "PDF created successfully", Toast.LENGTH_SHORT).show();
//                    FileOutputStream pdfFile = new FileOutputStream(new File(mDestPath));
//                    Document document = new Document();
//                    document.open();
//
//                    document.close();
//                    pdfFile.close();
//
//                    return mDestPath;
//                } catch (Exception e) {
//                    mSuccess = false;
//                    return e.getMessage();
//                }
//            }
            @Override
            protected void onPostExecute(String result) {
                mOnPDFCreatedInterface.onPDFCreated(mSuccess, mDestPath);
            }
        }.execute();
    }
//    }
//            @Override
//            protected void onPreExecute() {
//                mSuccess = true;
//                mOnPDFCreatedInterface.onPDFCreationStarted();
//            }
//
//            @Override
//            protected String doInBackground(Void... voids) {
//                try {
//                    Workbook workbook = new Workbook(mPath);
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
//                return null;
//            }
//            @Override
//            protected void onPostExecute(String result) {
//                mOnPDFCreatedInterface.onPDFCreated(mSuccess, mPath);
//            }
//        }.execute();
//    }
}