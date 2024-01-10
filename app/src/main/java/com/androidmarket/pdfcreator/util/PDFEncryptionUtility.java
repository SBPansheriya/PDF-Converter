package com.androidmarket.pdfcreator.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import androidmarket.R;

import com.androidmarket.pdfcreator.db.DatabaseHelper;
import com.androidmarket.pdfcreator.interfaces.DataSetChanged;

import static com.androidmarket.pdfcreator.Constants.MASTER_PWD_STRING;
import static com.androidmarket.pdfcreator.Constants.appName;

public class PDFEncryptionUtility {

    private final Activity mContext;
    private final FileUtils mFileUtils;
    private final MaterialDialog mDialog;
    private String mPassword;
    private final SharedPreferences mSharedPrefs;

    public PDFEncryptionUtility(Activity context) {
        this.mContext = context;
        this.mFileUtils = new FileUtils(context);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mDialog = new MaterialDialog.Builder(mContext)
                .customView(R.layout.custom_dialog, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .build();
    }

    /**
     * Opens the password mDialog to set Password for an existing PDF file.
     *
     * @param filePath Path of file to be encrypted
     */
    public void setPassword(final String filePath, final DataSetChanged dataSetChanged) {
//        mDialog.setTitle(R.string.set_password);
//        final View mPositiveAction = mDialog.getActionButton(DialogAction.POSITIVE);
//        assert mDialog.getCustomView() != null;
//        EditText mPasswordInput = mDialog.getCustomView().findViewById(R.id.password);
//        mPasswordInput.addTextChangedListener(
//                new DefaultTextWatcher() {
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        mPositiveAction.setEnabled(s.toString().trim().length() > 0);
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable input) {
//                        if (StringUtils.getInstance().isEmpty(input))
//                            StringUtils.getInstance().
//                                    showSnackbar(mContext, R.string.snackbar_password_cannot_be_blank);
//                        else
//                            mPassword = input.toString();
//                    }
//                });
//        mDialog.show();
//        mPositiveAction.setEnabled(false);
//        mPositiveAction.setOnClickListener(v -> {
//            try {
//                String path = doEncryption(filePath, mPassword);
//                StringUtils.getInstance().getSnackbarwithAction(mContext, R.string.snackbar_pdfCreated)
//                        .setAction(R.string.snackbar_viewAction, v2 ->
//                                mFileUtils.openFile(path, FileUtils.FileType.e_PDF)).show();
//                if (dataSetChanged != null)
//                    dataSetChanged.updateDataset();
//            } catch (IOException | DocumentException e) {
//                e.printStackTrace();
//                StringUtils.getInstance().showSnackbar(mContext, R.string.cannot_add_password);
//            }
//            mDialog.dismiss();
//        });
        Dialog dialog = new Dialog(mContext);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.set_password_dialog);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        Button remove = dialog.findViewById(R.id.remove_dialog);
        EditText passwordInput = dialog.findViewById(R.id.password);

        passwordInput.addTextChangedListener(
                new DefaultTextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        ok.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable input) {
                        if (StringUtils.getInstance().isEmpty(input))
                            StringUtils.getInstance().
                                    showSnackbar(mContext, R.string.snackbar_password_cannot_be_blank);
                        else
                            mPassword = input.toString();
                    }
                });

        remove.setVisibility(View.GONE);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.getInstance().isEmpty(passwordInput.getText().toString())) {
                    StringUtils.getInstance().showSnackbar(mContext, R.string.snackbar_password_cannot_be_blank);
                } else {
                    try {
                        String path = doEncryption(filePath, mPassword);
                        StringUtils.getInstance().getSnackbarwithAction(mContext, R.string.snackbar_pdfCreated)
                                .setAction(R.string.snackbar_viewAction, v2 ->
                                        mFileUtils.openFile(path, FileUtils.FileType.e_PDF)).show();
                        if (dataSetChanged != null)
                            dataSetChanged.updateDataset();
                    } catch (IOException | DocumentException e) {
                        e.printStackTrace();
                        StringUtils.getInstance().showSnackbar(mContext, R.string.cannot_add_password);
                    }
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * Uses PDF Reader to set encryption in pdf file.
     *
     * @param path     - Path of pdf file to be encrypted
     * @param password - password to be encrypted with
     * @return string - path of output file
     */
    private String doEncryption(String path, String password) throws IOException, DocumentException {

        String masterpwd = mSharedPrefs.getString(MASTER_PWD_STRING, appName);
        String finalOutputFile = mFileUtils.getUniqueFileName(path.replace(mContext.getString(R.string.pdf_ext),
                mContext.getString(R.string.encrypted_file)));

        PdfReader reader = new PdfReader(path);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
        stamper.setEncryption(password.getBytes(), masterpwd.getBytes(),
                PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY, PdfWriter.ENCRYPTION_AES_128);
        stamper.close();
        reader.close();
        new DatabaseHelper(mContext).insertRecord(finalOutputFile, mContext.getString(R.string.encrypted));
        return finalOutputFile;
    }

    /**
     * Checks if PDf is encrypted
     *
     * @param file - path of PDF file
     * @return true, if PDF is encrypted, otherwise false
     */
    private boolean isPDFEncrypted(final String file) {
        PdfReader reader;
        String ownerPass = mContext.getString(R.string.app_name);
        try {
            reader = new PdfReader(file, ownerPass.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
        //Check if PDF is encrypted or not.
        if (!reader.isEncrypted()) {
            StringUtils.getInstance().showSnackbar(mContext, R.string.not_encrypted);
            return false;
        }
        return true;
    }

    /**
     * Uses PDF Reader to decrypt the PDF.
     *
     * @param file Path of pdf file to be decrypted
     */
    public void removePassword(final String file,
                               final DataSetChanged dataSetChanged) {

        if (!isPDFEncrypted(file))
            return;

        final String[] input_password = new String[1];
//        mDialog.setTitle(R.string.enter_password);
//        final View mPositiveAction = mDialog.getActionButton(DialogAction.POSITIVE);
//        final EditText mPasswordInput = Objects.requireNonNull(mDialog.getCustomView()).findViewById(R.id.password);
//        TextView text = mDialog.getCustomView().findViewById(R.id.enter_password);
//        text.setText(R.string.decrypt_message);
//        mPasswordInput.addTextChangedListener(
//                new DefaultTextWatcher() {
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        mPositiveAction.setEnabled(s.toString().trim().length() > 0);
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable input) {
//                        input_password[0] = input.toString();
//                    }
//                });
//        mDialog.show();
//        mPositiveAction.setEnabled(false);
//        mPositiveAction.setOnClickListener(v -> {
//
//            // check for password
//            // our master password & their user password
//            // their master password
//
//            if (!removePasswordUsingDefMasterPassword(file, dataSetChanged, input_password)) {
//                if (!removePasswordUsingInputMasterPassword(file, dataSetChanged, input_password)) {
//                    StringUtils.getInstance().showSnackbar(mContext, R.string.master_password_changed);
//                }
//            }
//            mDialog.dismiss();
//        });

        Dialog dialog = new Dialog(mContext);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.set_password_dialog);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        Button remove = dialog.findViewById(R.id.remove_dialog);
        TextView textView = dialog.findViewById(R.id.txt);
        EditText passwordInput = dialog.findViewById(R.id.password);

        textView.setText("Old password");
        passwordInput.setHint("Enter old password");

        passwordInput.addTextChangedListener(
                new DefaultTextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        ok.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable input) {
                        input_password[0] = input.toString();
                    }
                });

        remove.setVisibility(View.GONE);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String password = passwordInput.getText().toString();
                if (password.isEmpty()){
                    Toast.makeText(mContext, "Old password is not empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.dismiss();
                    Dialog dialogremove = new Dialog(mContext);
                    if (dialogremove.getWindow() != null) {
                        dialogremove.getWindow().setGravity(Gravity.CENTER);
                        dialogremove.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialogremove.setCancelable(false);
                    }
                    dialogremove.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    dialogremove.setContentView(R.layout.delete_dialog_watermark);
                    dialogremove.setCancelable(false);
                    dialogremove.show();

                    Button cancel = dialogremove.findViewById(R.id.canceldialog);
                    Button ok = dialogremove.findViewById(R.id.okdialog);
                    TextView txt = dialogremove.findViewById(R.id.txt);

                    txt.setText("Do you want to remove password ?");

                    ok.setText("Remove");

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            removePassword(file, dataSetChanged);
                            dialogremove.dismiss();
                        }
                    });

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!removePasswordUsingDefMasterPassword(file, dataSetChanged, input_password)) {
                                if (!removePasswordUsingInputMasterPassword(file, dataSetChanged, input_password)) {
                                    StringUtils.getInstance().showSnackbar(mContext, R.string.master_password_changed);
                                }
                            }
                            dialogremove.dismiss();
                        }
                    });
                }
            }
        });

    }

    /**
     * This function removes the password for encrypted files.
     *
     * @param file          - the path of the actual encrypted file.
     * @param inputPassword - the password of the encrypted file.
     * @return - output file path
     */
    public String removeDefPasswordForImages(final String file,
                                             final String[] inputPassword) {
        String finalOutputFile;
        try {
            String masterPwd = mSharedPrefs.getString(MASTER_PWD_STRING, appName);
            PdfReader reader = new PdfReader(file, masterPwd.getBytes());
            byte[] password;
            finalOutputFile = mFileUtils.getUniqueFileName
                    (file.replace(mContext.getResources().getString(R.string.pdf_ext),
                            mContext.getString(R.string.decrypted_file)));
            password = reader.computeUserPassword();
            byte[] input = inputPassword[0].getBytes();
            if (Arrays.equals(input, password)) {
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
                stamper.close();
                reader.close();
                return finalOutputFile;
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean removePasswordUsingDefMasterPassword(final String file,
                                                         final DataSetChanged dataSetChanged,
                                                         final String[] inputPassword) {
        String finalOutputFile;
        try {
            String masterPwd = mSharedPrefs.getString(MASTER_PWD_STRING, appName);
            PdfReader reader = new PdfReader(file, masterPwd.getBytes());
            byte[] password;
            finalOutputFile = mFileUtils.getUniqueFileName
                    (file.replace(mContext.getResources().getString(R.string.pdf_ext),
                            mContext.getString(R.string.decrypted_file)));
            password = reader.computeUserPassword();
            byte[] input = inputPassword[0].getBytes();
            if (Arrays.equals(input, password)) {
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
                stamper.close();
                reader.close();
                if (dataSetChanged != null)
                    dataSetChanged.updateDataset();
                new DatabaseHelper(mContext).insertRecord(finalOutputFile, mContext.getString(R.string.decrypted));
                final String filepath = finalOutputFile;
                StringUtils.getInstance().getSnackbarwithAction(mContext, R.string.snackbar_pdfCreated)
                        .setAction(R.string.snackbar_viewAction,
                                v2 -> mFileUtils.openFile(filepath, FileUtils.FileType.e_PDF)).show();
                return true;
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    private boolean removePasswordUsingInputMasterPassword(final String file,
                                                           final DataSetChanged dataSetChanged,
                                                           final String[] inputPassword) {
        String finalOutputFile;
        try {
            PdfReader reader = new PdfReader(file, inputPassword[0].getBytes());
            finalOutputFile = mFileUtils.getUniqueFileName(
                    file.replace(mContext.getResources().getString(R.string.pdf_ext),
                            mContext.getString(R.string.decrypted_file)));
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(finalOutputFile));
            stamper.close();
            reader.close();
            if (dataSetChanged != null)
                dataSetChanged.updateDataset();
            new DatabaseHelper(mContext).insertRecord(finalOutputFile, mContext.getString(R.string.decrypted));
            final String filepath = finalOutputFile;
            StringUtils.getInstance().getSnackbarwithAction(mContext, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction, v2 ->
                            mFileUtils.openFile(filepath, FileUtils.FileType.e_PDF)).show();
            return true;

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
