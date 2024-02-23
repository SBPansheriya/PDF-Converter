package com.utillity.pdfgenerator.util;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.FileOutputStream;
import java.io.IOException;

import com.utillity.pdfgenerator.R;

import com.utillity.pdfgenerator.db.DatabaseHelper;
import com.utillity.pdfgenerator.interfaces.DataSetChanged;
import com.utillity.pdfgenerator.pdfModel.Watermark;

public class WatermarkUtils {

    private final Activity mContext;
    private Watermark mWatermark;
    private final FileUtils mFileUtils;

    public WatermarkUtils(Activity context) {
        mContext = context;
        mFileUtils = new FileUtils(context);
    }

    public void setWatermark(String path, final DataSetChanged dataSetChanged) {

        Dialog dialog1 = new Dialog(mContext);
        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setGravity(Gravity.CENTER);
            dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog1.setCancelable(false);
        }
        dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog1.setContentView(R.layout.add_water_mark_dialog_layout);
        dialog1.setCancelable(false);
        dialog1.show();

        Button cancel = dialog1.findViewById(R.id.canceldialog);
        Button ok = dialog1.findViewById(R.id.okdialog);
        Button remove = dialog1.findViewById(R.id.remove_dialog);
        final EditText watermarkTextInput = dialog1.findViewById(R.id.watermarkText);
        final EditText angleInput = dialog1.findViewById(R.id.watermarkAngle);
        final ColorPickerView colorPickerInput = dialog1.findViewById(R.id.watermarkColor);
        final EditText fontSizeInput = dialog1.findViewById(R.id.watermarkFontSize);
        final Spinner fontFamilyInput = dialog1.findViewById(R.id.watermarkFontFamily);
        final Spinner styleInput = dialog1.findViewById(R.id.watermarkStyle);

        this.mWatermark = new Watermark();

        remove.setVisibility(View.GONE);

        colorPickerInput.setAlphaSliderVisible(true);
        ArrayAdapter<Font.FontFamily> fontFamilyAdapter = new ArrayAdapter<>(mContext,
                R.layout.simple_spinner_item, Font.FontFamily.values());
        fontFamilyAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        fontFamilyInput.setAdapter(fontFamilyAdapter);

        ArrayAdapter<String> styleAdapter = new ArrayAdapter<>(mContext, R.layout.simple_spinner_item,
                mContext.getResources().getStringArray(R.array.fontStyles));
        styleAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        styleInput.setAdapter(styleAdapter);

        angleInput.setText("0");
        fontSizeInput.setText("50");

        watermarkTextInput.addTextChangedListener(
                new DefaultTextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable input) {
                        if (StringUtils.getInstance().isEmpty(input))
                            StringUtils.getInstance().
                                    showSnackbar(mContext, R.string.snackbar_watermark_cannot_be_blank);
                        else {
                            mWatermark.setWatermarkText(input.toString());
                        }
                    }
                });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.getInstance().isEmpty(watermarkTextInput.getText().toString())){
                    Toast.makeText(mContext, "Watermark text is not empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        mWatermark.setWatermarkText(watermarkTextInput.getText().toString());
                        mWatermark.setFontFamily(((Font.FontFamily) fontFamilyInput.getSelectedItem()));
                        mWatermark.setFontStyle(getStyleValueFromName(((String) styleInput.getSelectedItem())));

                        mWatermark.setRotationAngle(StringUtils.getInstance().parseIntOrDefault(angleInput.getText(), 0));
                        mWatermark.setTextSize(StringUtils.getInstance().parseIntOrDefault(fontSizeInput.getText(), 50));

                        int originalColor = colorPickerInput.getColor();

                        int red = Color.red(originalColor);
                        int green = Color.green(originalColor);
                        int blue = Color.blue(originalColor);

                        int alpha = (int) (Color.alpha(originalColor) * 0.2);

                        BaseColor adjustedBaseColor = new BaseColor(red, green, blue, alpha);

                        mWatermark.setTextColor(adjustedBaseColor);

                        String filePath = createWatermark(path);
                        dataSetChanged.updateDataset();
                        StringUtils.getInstance().getSnackbarwithAction(mContext, R.string.watermark_added).setAction(
                                R.string.snackbar_viewAction, v1 ->
                                        mFileUtils.openFile(filePath, FileUtils.FileType.e_PDF)).show();
                    } catch (IOException | DocumentException e) {
                        e.printStackTrace();
                        StringUtils.getInstance().showSnackbar(mContext, R.string.cannot_add_watermark);
                    }
                    dialog1.dismiss();
                }
            }
        });
    }

    private String createWatermark(String path) throws IOException, DocumentException {
        String finalOutputFile = mFileUtils.getUniqueFileName(path.replace(mContext.getString(R.string.pdf_ext),
                mContext.getString(R.string.watermarked_file)));

        PdfReader reader = new PdfReader(path);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
        Font font = new Font(this.mWatermark.getFontFamily(), this.mWatermark.getTextSize(),
                this.mWatermark.getFontStyle(), this.mWatermark.getTextColor());
        Phrase p = new Phrase(this.mWatermark.getWatermarkText(), font);

        PdfContentByte over;
        Rectangle pageSize;
        float x, y;
        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {

            // get page size and position
            pageSize = reader.getPageSizeWithRotation(i);
            x = (pageSize.getLeft() + pageSize.getRight()) / 2;
            y = (pageSize.getTop() + pageSize.getBottom()) / 2;
            over = stamper.getOverContent(i);

            ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, this.mWatermark.getRotationAngle());
        }

        stamper.close();
        reader.close();
        new DatabaseHelper(mContext).insertRecord(finalOutputFile, mContext.getString(R.string.watermarked));
        return finalOutputFile;
    }

    public static int getStyleValueFromName(String name) {
        switch (name) {
            case "BOLD":
                return Font.BOLD;
            case "ITALIC":
                return Font.ITALIC;
            case "UNDERLINE":
                return Font.UNDERLINE;
            case "STRIKETHRU":
                return Font.STRIKETHRU;
            case "BOLDITALIC":
                return Font.BOLDITALIC;
            default:
                return Font.NORMAL;
        }
    }

    public static String getStyleNameFromFont(int font) {
        switch (font) {
            case Font.BOLD:
                return "BOLD";
            case Font.ITALIC:
                return "ITALIC";
            case Font.UNDERLINE:
                return "UNDERLINE";
            case Font.STRIKETHRU:
                return "STRIKETHRU";
            case Font.BOLDITALIC:
                return "BOLDITALIC";
            default:
                return "NORMAL";
        }
    }

}
