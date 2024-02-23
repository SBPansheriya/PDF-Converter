package com.utillity.pdfgenerator.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.utillity.pdfgenerator.R;
import com.utillity.pdfgenerator.db.DatabaseHelper;

import static com.utillity.pdfgenerator.Constants.STORAGE_LOCATION;
import static com.utillity.pdfgenerator.Constants.pdfExtension;

public class SplitPDFUtils {

    private static final int NO_ERROR = 0;
    private static final int ERROR_PAGE_NUMBER = 1;
    private static final int ERROR_PAGE_RANGE = 2;
    private static final int ERROR_INVALID_INPUT = 3;

    private final Activity mContext;
    private final SharedPreferences mSharedPreferences;

    public SplitPDFUtils(Activity context) {
        this.mContext = context;
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
    }

    public ArrayList<String> splitPDFByConfig(String path, String splitDetail) {
        String splitConfig = splitDetail.replaceAll("\\s+", "");
        ArrayList<String> outputPaths = new ArrayList<>();
        String delims = "[,]";
        String[] ranges = splitConfig.split(delims);
        Log.v("Ranges", Arrays.toString(ranges));

        if (path == null || !isInputValid(path, ranges))
            return outputPaths;

        try {
            String folderPath = mSharedPreferences.getString(STORAGE_LOCATION,
                    StringUtils.getInstance().getDefaultStorageLocation(mContext));
            PdfReader reader = new PdfReader(path);
            PdfCopy copy;
            Document document;
            for (String range : ranges) {
                int startPage;
                int endPage;

                String fileName = folderPath + FileUtils.getFileName(path);

                if (reader.getNumberOfPages() > 1) {
                    if (!range.contains("-")) {
                        startPage = Integer.parseInt(range);
                        document = new Document();
                        fileName = fileName.replace(pdfExtension,
                                "_" + startPage + pdfExtension);
                        copy = new PdfCopy(document, new FileOutputStream(fileName));

                        document.open();
                        copy.addPage(copy.getImportedPage(reader, startPage));
                        document.close();
                        outputPaths.add(fileName);
                        new DatabaseHelper(mContext).insertRecord(fileName,
                                mContext.getString(R.string.created));
                    } else {

                        startPage = Integer.parseInt(range.substring(0, range.indexOf("-")));
                        endPage = Integer.parseInt(range.substring(range.indexOf("-") + 1));
                        if (reader.getNumberOfPages() == endPage - startPage + 1) {
                            StringUtils.getInstance().showSnackbar(mContext, R.string.split_range_alert);
                        } else {
                            document = new Document();
                            fileName = fileName.replace(pdfExtension,
                                    "_" + startPage + "-" + endPage + pdfExtension);
                            copy = new PdfCopy(document, new FileOutputStream(fileName));
                            document.open();
                            for (int page = startPage; page <= endPage; page++) {
                                copy.addPage(copy.getImportedPage(reader, page));
                            }
                            document.close();

                            new DatabaseHelper(mContext).insertRecord(fileName,
                                    mContext.getString(R.string.created));
                            outputPaths.add(fileName);
                        }
                    }
                } else {
                    StringUtils.getInstance().showSnackbar(mContext, R.string.split_one_page_pdf_alert);
                }
            }
        } catch (IOException | DocumentException | IllegalArgumentException e) {
            e.printStackTrace();
            StringUtils.getInstance().showSnackbar(mContext, R.string.file_access_error);
        }
        return outputPaths;
    }

    private boolean isInputValid(String path, String[] ranges) {
        try {
            PdfReader reader = new PdfReader(path);
            int numOfPages = reader.getNumberOfPages();
            int result = checkRangeValidity(numOfPages, ranges);
            switch (result) {
                case ERROR_PAGE_NUMBER:
                    StringUtils.getInstance().showSnackbar(mContext, R.string.error_page_number);
                    break;
                case ERROR_PAGE_RANGE:
                    StringUtils.getInstance().showSnackbar(mContext, R.string.error_page_range);
                    break;
                case ERROR_INVALID_INPUT:
                    StringUtils.getInstance().showSnackbar(mContext, R.string.error_invalid_input);
                    break;
                default:
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int checkRangeValidity(int numOfPages, String[] ranges) {
        int startPage, endPage;
        int returnValue = NO_ERROR;

        if (ranges.length == 0)
            returnValue = ERROR_INVALID_INPUT;
        else {
            for (String range : ranges) {
                if (!range.contains("-")) {
                    try {
                        startPage = Integer.parseInt(range);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        returnValue = ERROR_INVALID_INPUT;
                        break;
                    }
                    if (startPage > numOfPages || startPage == 0) {
                        returnValue = ERROR_PAGE_NUMBER;
                        break;
                    }
                } else {
                    try {
                        startPage = Integer.parseInt(range.substring(0, range.indexOf("-")));
                        endPage = Integer.parseInt(range.substring(range.indexOf("-") + 1));
                    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        returnValue = ERROR_INVALID_INPUT;
                        break;
                    }
                    if (startPage > numOfPages || endPage > numOfPages || startPage == 0 || endPage == 0) {
                        returnValue = ERROR_PAGE_NUMBER;
                        break;
                    } else if (startPage >= endPage) {
                        returnValue = ERROR_PAGE_RANGE;
                        break;
                    }
                }
            }
        }
        return returnValue;
    }
}

