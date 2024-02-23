package com.utillity.pdfgenerator.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import android.util.Log;

import com.utillity.pdfgenerator.Constants;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.utillity.pdfgenerator.interfaces.OnPDFCreatedInterface;
import com.utillity.pdfgenerator.pdfModel.ImageToPDFOptions;
import com.utillity.pdfgenerator.pdfModel.Watermark;

import static com.utillity.pdfgenerator.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static com.utillity.pdfgenerator.Constants.pdfExtension;

public class CreatePdf extends AsyncTask<String, String, String> {

    private final String mFileName;
    private final String mPassword;
    private final String mQualityString;
    private final ArrayList<String> mImagesUri;
    private final int mBorderWidth;
    private final OnPDFCreatedInterface mOnPDFCreatedInterface;
    private boolean mSuccess;
    private String mPath;
    private final String mPageSize;
    private final boolean mPasswordProtected;
    private final Boolean mWatermarkAdded;
    private final Watermark mWatermark;
    private final int mMarginTop;
    private final int mMarginBottom;
    private final int mMarginRight;
    private final int mMarginLeft;
    private final String mImageScaleType;
    private final String mPageNumStyle;
    private final String mMasterPwd;
    private final int mPageColor;

    public CreatePdf(ImageToPDFOptions mImageToPDFOptions, String parentPath,
                     OnPDFCreatedInterface onPDFCreated) {
        this.mImagesUri = mImageToPDFOptions.getImagesUri();
        this.mFileName = mImageToPDFOptions.getOutFileName();
        this.mPassword = mImageToPDFOptions.getPassword();
        this.mQualityString = mImageToPDFOptions.getQualityString();
        this.mOnPDFCreatedInterface = onPDFCreated;
        this.mPageSize = mImageToPDFOptions.getPageSize();
        this.mPasswordProtected = mImageToPDFOptions.isPasswordProtected();
        this.mBorderWidth = mImageToPDFOptions.getBorderWidth();
        this.mWatermarkAdded = mImageToPDFOptions.isWatermarkAdded();
        this.mWatermark = mImageToPDFOptions.getWatermark();
        this.mMarginTop = mImageToPDFOptions.getMarginTop();
        this.mMarginBottom = mImageToPDFOptions.getMarginBottom();
        this.mMarginRight = mImageToPDFOptions.getMarginRight();
        this.mMarginLeft = mImageToPDFOptions.getMarginLeft();
        this.mImageScaleType = mImageToPDFOptions.getImageScaleType();
        this.mPageNumStyle = mImageToPDFOptions.getPageNumStyle();
        this.mMasterPwd = mImageToPDFOptions.getMasterPwd();
        this.mPageColor = mImageToPDFOptions.getPageColor();
        mPath = parentPath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mSuccess = true;
        mOnPDFCreatedInterface.onPDFCreationStarted();
    }

    private void setFilePath() {
        File folder = new File(mPath);
        if (!folder.exists())
            folder.mkdir();
        mPath = mPath + mFileName + pdfExtension;
    }

    @Override
    protected String doInBackground(String... params) {

        setFilePath();

        Log.v("stage 1", "store the pdf in sd card");

        Rectangle pageSize = new Rectangle(PageSize.getRectangle(mPageSize));
        pageSize.setBackgroundColor(getBaseColor(mPageColor));
        Document document = new Document(pageSize,
                mMarginLeft, mMarginRight, mMarginTop, mMarginBottom);

        Log.v("stage 2", "Document Created");
        document.setMargins(mMarginLeft, mMarginRight, mMarginTop, mMarginBottom);
        Rectangle documentRect = document.getPageSize();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, Files.newOutputStream(Paths.get(mPath)));

            Log.v("Stage 3", "Pdf writer");

            if (mPasswordProtected) {
                writer.setEncryption(mPassword.getBytes(), mMasterPwd.getBytes(),
                        PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY,
                        PdfWriter.ENCRYPTION_AES_128);

                Log.v("Stage 3.1", "Set Encryption");
            }

            if (mWatermarkAdded) {
                WatermarkPageEvent watermarkPageEvent = new WatermarkPageEvent();
                watermarkPageEvent.setWatermark(mWatermark);
                writer.setPageEvent(watermarkPageEvent);
            }

            document.open();

            Log.v("Stage 4", "Document opened");

            for (int i = 0; i < mImagesUri.size(); i++) {
                int quality;
                quality = 30;
                if (StringUtils.getInstance().isNotEmpty(mQualityString)) {
                    quality = Integer.parseInt(mQualityString);
                }
                Image image = Image.getInstance(mImagesUri.get(i));
                double qualityMod = quality * 0.09;
                image.setCompressionLevel((int) qualityMod);
                image.setBorder(Rectangle.BOX);
                image.setBorderWidth(mBorderWidth);

                Log.v("Stage 5", "Image compressed " + qualityMod);

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(mImagesUri.get(i), bmOptions);

                Log.v("Stage 6", "Image path adding");

                float pageWidth = document.getPageSize().getWidth() - (mMarginLeft + mMarginRight);
                float pageHeight = document.getPageSize().getHeight() - (mMarginBottom + mMarginTop);
                if (mImageScaleType.equals(IMAGE_SCALE_TYPE_ASPECT_RATIO))
                    image.scaleToFit(pageWidth, pageHeight);
                else
                    image.scaleAbsolute(pageWidth, pageHeight);

                image.setAbsolutePosition(
                        (documentRect.getWidth() - image.getScaledWidth()) / 2,
                        (documentRect.getHeight() - image.getScaledHeight()) / 2);

                Log.v("Stage 7", "Image Alignments");
                addPageNumber(documentRect, writer);
                document.add(image);

                document.newPage();
            }

            Log.v("Stage 8", "Image adding");

            document.close();

            Log.v("Stage 7", "Document Closed" + mPath);

            Log.v("Stage 8", "Record inserted in database");

        } catch (Exception e) {
            e.printStackTrace();
            mSuccess = false;
        }
        return null;
    }

    private void addPageNumber(Rectangle documentRect, PdfWriter writer) {
        if (mPageNumStyle != null) {

            float fixedTextSize = 20f;

            float calculatedTextSize = fixedTextSize * (documentRect.getWidth() / 595f);

            if (mPageSize.equals("Letter")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(110)),
                        documentRect.getBottom(10), 0);
            } else if (mPageSize.equals("Legal")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(120)),
                        documentRect.getBottom(20), 0);
            } else if (mPageSize.equals("Executive")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(100)),
                        documentRect.getBottom(10), 0);
            } else if (mPageSize.equals("Ledger")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(200)),
                        documentRect.getBottom(20), 0);
            } else if (mPageSize.equals("Tabloid")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(140)),
                        documentRect.getBottom(20), 0);
            } else if (mPageSize.equals("A0")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(440)),
                        documentRect.getBottom(30), 0);
            } else if (mPageSize.equals("A1")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(370)),
                        documentRect.getBottom(20), 0);
            } else if (mPageSize.equals("A2")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(330)),
                        documentRect.getBottom(20), 0);
            } else if (mPageSize.equals("A3") ) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(160)),
                        documentRect.getBottom(20), 0);
            } else if (mPageSize.equals("A5")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(80)),
                        documentRect.getBottom(20), 0);
            } else if (mPageSize.equals("A6")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                        getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(60)),
                        documentRect.getBottom(10), 0);
            } else if (mPageSize.equals("A7")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(40)),
                        documentRect.getBottom(10), 0);
            }  else if (mPageSize.equals("A8")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                        getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(25)),
                        documentRect.getBottom(5), 0);
            } else if (mPageSize.equals("A9")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                        getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(20)),
                        documentRect.getBottom(3), 0);
            } else if (mPageSize.equals("A10")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                        getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(15)),
                        documentRect.getBottom(2), 0);
            } else if (mPageSize.equals("B0")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                        getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(580)),
                        documentRect.getBottom(50), 0);
            } else if (mPageSize.equals("B1")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                        getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(370)),
                        documentRect.getBottom(40), 0);
            } else if (mPageSize.equals("B2")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                        getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(290)),
                        documentRect.getBottom(30), 0);
            } else if (mPageSize.equals("B3") ) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                        getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(190)),
                        documentRect.getBottom(20), 0);
            } else if (mPageSize.equals("B4") ) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                        getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(135)),
                        documentRect.getBottom(20), 0);
            } else if (mPageSize.equals("B5")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                        getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(100)),
                        documentRect.getBottom(20), 0);
            } else if (mPageSize.equals("B6")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                        getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(65)),
                        documentRect.getBottom(10), 0);
            } else if (mPageSize.equals("B7")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                        getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(50)),
                        documentRect.getBottom(5), 0);
            } else if (mPageSize.equals("B8")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                        getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(35)),
                        documentRect.getBottom(5), 0);
            } else if (mPageSize.equals("B9")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                        getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(25)),
                        documentRect.getBottom(5), 0);
            } else if (mPageSize.equals("B10")) {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                        getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(15)),
                        documentRect.getBottom(3), 0);
            } else {
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_BOTTOM | Element.ALIGN_LEFT,
                getPhrase(writer, mPageNumStyle, mImagesUri.size(), calculatedTextSize),
                        (documentRect.getRight(120)),
                        documentRect.getBottom(20), 0);
            }
        }
}

    @NonNull
    private Phrase getPhrase(PdfWriter writer, String pageNumStyle, int size, float fixedTextSize) {
        Phrase phrase;

        Font fixedFont = new Font(Font.FontFamily.TIMES_ROMAN, fixedTextSize);

        switch (pageNumStyle) {
            case Constants.PG_NUM_STYLE_PAGE_X_OF_N:
                phrase = new Phrase(String.format("Page %d of %d", writer.getPageNumber(), size), fixedFont);
                break;
            case Constants.PG_NUM_STYLE_X_OF_N:
                phrase = new Phrase(String.format("%d of %d", writer.getPageNumber(), size), fixedFont);
                break;
            default:
                phrase = new Phrase(String.format("%d", writer.getPageNumber()), fixedFont);
                break;
        }
        return phrase;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mOnPDFCreatedInterface.onPDFCreated(mSuccess, mPath);
    }

    private BaseColor getBaseColor(int color) {
        return new BaseColor(
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }
}


