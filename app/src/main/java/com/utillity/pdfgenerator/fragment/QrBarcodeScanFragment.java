package com.utillity.pdfgenerator.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.utillity.pdfgenerator.activities.HomeActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.utillity.pdfgenerator.R;
import com.utillity.pdfgenerator.db.DatabaseHelper;
import com.utillity.pdfgenerator.interfaces.OnPDFCreatedInterface;
import com.utillity.pdfgenerator.pdfModel.ImageToPDFOptions;
import com.utillity.pdfgenerator.pdfModel.TextToPDFOptions;
import com.utillity.pdfgenerator.Constants;
import com.utillity.pdfgenerator.util.DialogUtils;
import com.utillity.pdfgenerator.util.FileUtils;
import com.utillity.pdfgenerator.util.PageSizeUtils;
import com.utillity.pdfgenerator.util.StringUtils;
import com.utillity.pdfgenerator.util.TextToPDFUtils;

import static com.utillity.pdfgenerator.Constants.DEFAULT_BORDER_WIDTH;
import static com.utillity.pdfgenerator.Constants.DEFAULT_COMPRESSION;
import static com.utillity.pdfgenerator.Constants.DEFAULT_IMAGE_BORDER_TEXT;
import static com.utillity.pdfgenerator.Constants.DEFAULT_PAGE_COLOR;
import static com.utillity.pdfgenerator.Constants.DEFAULT_PAGE_SIZE;

import static com.utillity.pdfgenerator.Constants.DEFAULT_PAGE_SIZE_TEXT;
import static com.utillity.pdfgenerator.Constants.DEFAULT_QUALITY_VALUE;

import static com.utillity.pdfgenerator.Constants.STORAGE_LOCATION;

public class QrBarcodeScanFragment extends Fragment implements View.OnClickListener, OnPDFCreatedInterface {
    private final String mTempFileName = "scan_result_temp.txt";

    private static final int REQUEST_CODE_FOR_QR_CODE = 1;
    private static final int REQUEST_CODE_FOR_BARCODE = 2;

    private SharedPreferences mSharedPreferences;
    private Activity mActivity;
    private MaterialDialog mMaterialDialog;
    private String mPath;
    private FileUtils mFileUtils;
    private Font.FontFamily mFontFamily;
    private int mFontColor;
    ImageView option_image,option_image1;

    @BindView(R.id.scan_qrcode)
    CardView scanQrcode;
    @BindView(R.id.scan_barcode)
    CardView scanBarcode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_qrcode_barcode, container, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);

        ButterKnife.bind(this, rootview);
        scanQrcode.setOnClickListener(this);
        scanBarcode.setOnClickListener(this);

        option_image = rootview.findViewById(R.id.option_image);
        option_image1 = rootview.findViewById(R.id.option_image1);

        option_image.setImageResource(R.drawable.scan_qr_code);
        option_image1.setImageResource(R.drawable.scan_barcode);

        mFontFamily = Font.FontFamily.valueOf(mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY));
        mFontColor = mSharedPreferences.getInt(Constants.DEFAULT_FONT_COLOR_TEXT,
                Constants.DEFAULT_FONT_COLOR);
        PageSizeUtils.mPageSize = mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT,
                Constants.DEFAULT_PAGE_SIZE);

        ImageView ivBack = rootview.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HomeActivity.class));
                getActivity().finish();
            }
        });
        return rootview;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result == null || result.getContents() == null)
            StringUtils.getInstance().showSnackbar(mActivity, R.string.scan_cancelled);
        else {
            Toast.makeText(mActivity, " " + result.getContents(), Toast.LENGTH_SHORT).show();

            File mDir = mActivity.getCacheDir();
            File mTempFile = new File(mDir.getPath() + "/" + mTempFileName);
            PrintWriter mWriter;
            try {
                mWriter = new PrintWriter(mTempFile);
                mWriter.print("");
                mWriter.append(result.getContents());
                mWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Uri uri = Uri.fromFile(mTempFile);
            resultToTextPdf(uri);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_qrcode:
                if (isCameraPermissionGranted()) {
                    openScanner(IntentIntegrator.QR_CODE_TYPES, R.string.scan_qrcode);
                } else {
                    requestCameraPermissionForQrCodeScan();
                }
                break;
            case R.id.scan_barcode:
                if (isCameraPermissionGranted()) {
                    openScanner(IntentIntegrator.ONE_D_CODE_TYPES, R.string.scan_barcode);
                } else {
                    requestCameraPermissionForBarCodeScan();
                }
                break;
        }
    }

    private void openScanner(Collection<String> scannerType, int promptId) {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(scannerType);
        integrator.setPrompt(mActivity.getString(promptId));
        integrator.setCameraId(0);
        integrator.initiateScan();
    }

    private void resultToTextPdf(Uri uri) {
        String ext = getString(R.string.pdf_ext);
        mFileUtils.openSaveDialog(null, ext, filename -> createPdf(filename, uri));
    }

    private void createPdf(String mFilename, Uri uri) {
        mPath = mSharedPreferences.getString(STORAGE_LOCATION,
                StringUtils.getInstance().getDefaultStorageLocation(getContext()));
        mPath = mPath + mFilename + mActivity.getString(R.string.pdf_ext);
        try {
            TextToPDFUtils fileUtil = new TextToPDFUtils(mActivity);
            int fontSize = mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, Constants.DEFAULT_FONT_SIZE);
            fileUtil.createPdfFromTextFile(new TextToPDFOptions(mFilename, PageSizeUtils.mPageSize, false,
                    "", uri, fontSize, mFontFamily, mFontColor, DEFAULT_PAGE_COLOR),
                    Constants.textExtension);
            final String finalMPath = mPath;
            StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction,
                            v -> mFileUtils.openFile(finalMPath, FileUtils.FileType.e_PDF)).show();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mFileUtils = new FileUtils(mActivity);
    }

    private void resetValues() {
        ImageToPDFOptions imageToPDFOptions = new ImageToPDFOptions();
        imageToPDFOptions.setBorderWidth(mSharedPreferences.getInt(DEFAULT_IMAGE_BORDER_TEXT,
                DEFAULT_BORDER_WIDTH));
        imageToPDFOptions.setQualityString(
                Integer.toString(mSharedPreferences.getInt(DEFAULT_COMPRESSION,
                        DEFAULT_QUALITY_VALUE)));
        imageToPDFOptions.setPageSize(mSharedPreferences.getString(DEFAULT_PAGE_SIZE_TEXT,
                DEFAULT_PAGE_SIZE));
        imageToPDFOptions.setPasswordProtected(false);
    }

    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean success, String path) {
        mMaterialDialog.dismiss();
        if (!success) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_folder_not_created);
            return;
        }
        new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
        StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                .setAction(R.string.snackbar_viewAction,
                        v -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF)).show();
        mPath = path;
        resetValues();
    }

    private boolean isCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermissionForQrCodeScan() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_FOR_QR_CODE);
    }

    private void requestCameraPermissionForBarCodeScan() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_FOR_BARCODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if ((requestCode == REQUEST_CODE_FOR_QR_CODE || requestCode == REQUEST_CODE_FOR_BARCODE && grantResults.length > 0)) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == REQUEST_CODE_FOR_QR_CODE) {
                    openScanner(IntentIntegrator.QR_CODE_TYPES, R.string.scan_qrcode);
                } else {
                    openScanner(IntentIntegrator.ONE_D_CODE_TYPES, R.string.scan_barcode);
                }
            } else {
                showPermissionDenyDialog(requestCode);
            }
        }
    }

    private void showPermissionDenyDialog(int requestCode) {
        String scanType, permissionType;
        if (requestCode == REQUEST_CODE_FOR_QR_CODE) {
            scanType = "QR-Code";
            permissionType = "Camera";
        } else if (requestCode == REQUEST_CODE_FOR_BARCODE) {
            scanType = "Bar-Code";
            permissionType = "Camera";
        }
        else {
            scanType = "unknown";
            permissionType = "unknown";
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Dialog dialog = new Dialog(mActivity);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.setCancelable(false);
            }
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.setContentView(R.layout.permission_denied_first_dialog);
            dialog.setCancelable(false);
            dialog.show();

            Button cancel = dialog.findViewById(R.id.canceldialog);
            Button ok = dialog.findViewById(R.id.okdialog);
            TextView textView = dialog.findViewById(R.id.filename);

            textView.setText(permissionType + " permission is needed to scan " + scanType);
            ok.setText("Re_try");

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (requestCode == REQUEST_CODE_FOR_QR_CODE) {
                        requestCameraPermissionForQrCodeScan();
                    } else if (requestCode == REQUEST_CODE_FOR_BARCODE) {
                        requestCameraPermissionForBarCodeScan();
                    }
                    dialog.dismiss();
                }
            });
        } else if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

            Dialog dialog = new Dialog(mActivity);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.setCancelable(false);
            }
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.setContentView(R.layout.permission_denied_first_dialog);
            dialog.setCancelable(false);
            dialog.show();

            Button cancel = dialog.findViewById(R.id.canceldialog);
            Button ok = dialog.findViewById(R.id.okdialog);
            TextView textView = dialog.findViewById(R.id.filename);

            textView.setText("You have chosen to never ask the permission again, but " + permissionType + " permission is needed to scan " + scanType);
            ok.setText(R.string.enable_from_settings);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
        }
    }
}