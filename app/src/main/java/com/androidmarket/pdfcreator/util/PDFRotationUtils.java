package com.androidmarket.pdfcreator.util;

import android.app.Activity;
import android.app.Dialog;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidmarket.pdfcreator.Constants;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.FileOutputStream;

import androidmarket.R;
import com.androidmarket.pdfcreator.db.DatabaseHelper;
import com.androidmarket.pdfcreator.interfaces.DataSetChanged;

public class PDFRotationUtils {

    private final Activity mContext;
    private final SparseIntArray mAngleRadioButton;
    private final FileUtils mFileUtils;

    public PDFRotationUtils(Activity context) {
        this.mContext = context;
        this.mFileUtils = new FileUtils(mContext);
        mAngleRadioButton = new SparseIntArray();
        mAngleRadioButton.put(R.id.deg90, 90);
        mAngleRadioButton.put(R.id.deg180, 180);
        mAngleRadioButton.put(R.id.deg270, 270);
    }

    /**
     * Show the dialog for angle of rotation of pdf pages
     *
     * @param sourceFilePath - path of file to be rotated
     */
    public void rotatePages(String sourceFilePath, final DataSetChanged dataSetChanged) {
//        MaterialDialog.Builder builder = DialogUtils.getInstance().createCustomDialogWithoutContent(mContext,
//                R.string.rotate_pages);
//        builder.customView(R.layout.dialog_rotate_pdf, true)
//                .onPositive((dialog, which) -> {
//                    final RadioGroup angleInput = dialog.getCustomView().findViewById(R.id.rotation_angle);
//                    int angle = mAngleRadioButton.get(angleInput.getCheckedRadioButtonId());
//                    String destFilePath = FileUtils.getFileDirectoryPath(sourceFilePath);
//                    String fileName = FileUtils.getFileName(sourceFilePath);
//                    destFilePath += String.format(mContext.getString(R.string.rotated_file_name),
//                            fileName.substring(0, fileName.lastIndexOf('.')),
//                            Integer.toString(angle),
//                            mContext.getString(R.string.pdf_ext));
//                    boolean result = rotatePDFPages(angle, sourceFilePath,
//                            destFilePath, dataSetChanged);
//                    if (result) {
//                        new DatabaseHelper(mContext).insertRecord(destFilePath,
//                                mContext.getString(R.string.rotated));
//                    }
//                })
//                .show();

        Dialog dialog = new Dialog(mContext);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_rotate_pdf_layout);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        RadioButton rbOpt1 = dialog.findViewById(R.id.deg90);
        RadioButton rbOpt2 = dialog.findViewById(R.id.deg180);
        RadioButton rbOpt3 = dialog.findViewById(R.id.deg270);
        RadioGroup angleInput = dialog.findViewById(R.id.rotation_angle);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int angle = mAngleRadioButton.get(angleInput.getCheckedRadioButtonId());
                String destFilePath = FileUtils.getFileDirectoryPath(sourceFilePath);
                String fileName = FileUtils.getFileName(sourceFilePath);
                destFilePath += String.format(mContext.getString(R.string.rotated_file_name),
                        fileName.substring(0, fileName.lastIndexOf('.')),
                        Integer.toString(angle),
                        mContext.getString(R.string.pdf_ext));
                boolean result = rotatePDFPages(angle, sourceFilePath,
                        destFilePath, dataSetChanged);
                if (result) {
                    new DatabaseHelper(mContext).insertRecord(destFilePath,
                            mContext.getString(R.string.rotated));
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * Rotates pages in pdf
     *
     * @param angle          rotation angle
     * @param sourceFilePath source file path
     * @param destFilePath   destination file path
     * @return true if no error else false
     */
    private boolean rotatePDFPages(int angle, String sourceFilePath, String destFilePath,
                                   final DataSetChanged dataSetChanged) {
        try {
            PdfReader reader = new PdfReader(sourceFilePath);
            int n = reader.getNumberOfPages();
            PdfDictionary page;
            PdfNumber rotate;
            for (int p = 1; p <= n; p++) {
                page = reader.getPageN(p);
                rotate = page.getAsNumber(PdfName.ROTATE);
                if (rotate == null)
                    page.put(PdfName.ROTATE, new PdfNumber(angle));
                else
                    page.put(PdfName.ROTATE, new PdfNumber((rotate.intValue() + angle) % 360));
            }
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(destFilePath));
            stamper.close();
            reader.close();
            StringUtils.getInstance().getSnackbarwithAction(mContext, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction, v ->
                            mFileUtils.openFile(destFilePath, FileUtils.FileType.e_PDF)).show();
            dataSetChanged.updateDataset();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            StringUtils.getInstance().showSnackbar(mContext, R.string.encrypted_pdf);
        }
        return false;
    }
}
