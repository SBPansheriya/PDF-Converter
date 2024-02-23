package com.utillity.pdfgenerator.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.utillity.pdfgenerator.R;

import com.utillity.pdfgenerator.db.DatabaseHelper;
import com.utillity.pdfgenerator.util.lambda.Consumer;

import static com.utillity.pdfgenerator.Constants.AUTHORITY_APP;
import static com.utillity.pdfgenerator.Constants.PATH_SEPERATOR;
import static com.utillity.pdfgenerator.Constants.STORAGE_LOCATION;
import static com.utillity.pdfgenerator.Constants.pdfExtension;
import static com.utillity.pdfgenerator.activities.SecondActivity.lastSelectedPageNumber;
import static com.utillity.pdfgenerator.activities.SplashActivity.SORT_PREFERENCE_KEY;
import static com.utillity.pdfgenerator.activities.SplashActivity.editorSortBy;
import static com.utillity.pdfgenerator.activities.SplashActivity.sharedPreferencesSortBy;
import static com.utillity.pdfgenerator.fragment.ImageToPdfFragment.lastSelected;

public class FileUtils {

    private final Activity mContext;
    private final SharedPreferences mSharedPreferences;

    public FileUtils(Activity context) {
        this.mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public enum FileType {
        e_PDF,
        e_TXT
    }

    public void printFile(final File file) {
        final PrintDocumentAdapter mPrintDocumentAdapter = new PrintDocumentAdapterHelper(file);

        PrintManager printManager = (PrintManager) mContext
                .getSystemService(Context.PRINT_SERVICE);
        String jobName = mContext.getString(R.string.app_name) + " Document";
        if (printManager != null) {
            printManager.print(jobName, mPrintDocumentAdapter, null);
            new DatabaseHelper(mContext).insertRecord(file.getAbsolutePath(), mContext.getString(R.string.printed));
        }
    }

    public void shareFile(File file) {
        Uri uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file);
        ArrayList<Uri> uris = new ArrayList<>();
        uris.add(uri);
        shareFile(uris);
    }

    public void shareMultipleFiles(List<File> files) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (File file : files) {
            Uri uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file);
            uris.add(uri);
        }
        shareFile(uris);
    }

    private void shareFile(ArrayList<Uri> uris) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.i_have_attached_pdfs_to_this_message));
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType(mContext.getString(R.string.pdf_type));
        mContext.startActivity(Intent.createChooser(intent,
                mContext.getResources().getString(R.string.share_chooser)));
    }

    public void openFile(String path, FileType fileType) {
        if (path == null) {
            StringUtils.getInstance().showSnackbar(mContext, R.string.error_path_not_found);
            return;
        }
        openFileInternal(path, fileType == FileType.e_PDF ?
                mContext.getString(R.string.pdf_type) : mContext.getString(R.string.txt_type));
    }

    private void openFileInternal(String path, String dataType) {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {

            Uri uri = FileProvider.getUriForFile(
                    mContext,
                    "com.utillity.pdfgenerator.contentprovider",
                    file);

            target.setDataAndType(uri, dataType);
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            openIntent(Intent.createChooser(target, mContext.getString(R.string.open_file)));
        } catch (Exception e) {
            StringUtils.getInstance().showSnackbar(mContext, R.string.error_open_file);
        }
    }

    private int checkRepeat(String finalOutputFile, final List<File> mFile) {
        boolean flag = true;
        int append = 0;
        while (flag) {
            append++;
            String name = finalOutputFile.replace(mContext.getString(R.string.pdf_ext),
                    append + mContext.getString(R.string.pdf_ext));
            flag = mFile.contains(new File(name));
        }

        return append;
    }

    public String getUriRealPath(Uri uri) {
        if (uri == null || FileUriUtils.getInstance().isWhatsappImage(uri.getAuthority()))
            return null;

        return FileUriUtils.getInstance().getUriRealPathAboveKitkat(mContext, uri);
    }

    public boolean isFileExist(String mFileName) {
        String path = mSharedPreferences.getString(STORAGE_LOCATION,
                StringUtils.getInstance().getDefaultStorageLocation(mContext)) + mFileName;
        File file = new File(path);

        return file.exists();
    }

    public String getFileName(Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();

        if (scheme == null)
            return null;

        if (scheme.equals("file")) {
            return uri.getLastPathSegment();
        } else if (scheme.equals("content")) {
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);

            if (cursor != null) {
                if (cursor.getCount() != 0) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    cursor.moveToFirst();
                    fileName = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        }

        return fileName;
    }

    public static String getFileName(String path) {
        if (path == null)
            return null;

        int index = path.lastIndexOf(PATH_SEPERATOR);
        return index < path.length() ? path.substring(index + 1) : null;
    }

    public static String getFileNameWithoutExtension(String path) {
        if (path == null || path.lastIndexOf(PATH_SEPERATOR) == -1)
            return path;

        String filename = path.substring(path.lastIndexOf(PATH_SEPERATOR) + 1);
        filename = filename.replace(pdfExtension, "");

        return filename;
    }

    public static String getFileDirectoryPath(String path) {
        return path.substring(0, path.lastIndexOf(PATH_SEPERATOR) + 1);
    }

    public String getLastFileName(ArrayList<String> filesPath) {
        if (filesPath.size() == 0)
            return "";

        String lastSelectedFilePath = filesPath.get(filesPath.size() - 1);
        String nameWithoutExt = stripExtension(getFileNameWithoutExtension(lastSelectedFilePath));

        return nameWithoutExt + mContext.getString(R.string.pdf_suffix);
    }

    public String stripExtension(String fileNameWithExt) {
        if (fileNameWithExt == null) return null;

        int pos = fileNameWithExt.lastIndexOf(".");

        if (pos == -1) return fileNameWithExt;

        return fileNameWithExt.substring(0, pos);
    }

    public void openImage(String path) {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Uri uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file);
        target.setDataAndType(uri, "image/*");
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        openIntent(Intent.createChooser(target, mContext.getString(R.string.open_file)));
    }

    private void openIntent(Intent intent) {
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            StringUtils.getInstance().showSnackbar(mContext, R.string.snackbar_no_pdf_app);
        }
    }

    public Intent getFileChooser() {
        String folderPath = Environment.getExternalStorageDirectory() + "/";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Uri myUri = Uri.parse(folderPath);
        intent.setDataAndType(myUri, mContext.getString(R.string.pdf_type));

        return Intent.createChooser(intent, mContext.getString(R.string.merge_file_select));
    }

    String getUniqueFileName(String fileName) {
        String outputFileName = fileName;
        File file = new File(outputFileName);

        if (!isFileExist(file.getName()))
            return outputFileName;

        File parentFile = file.getParentFile();
        if (parentFile != null) {
            File[] listFiles = parentFile.listFiles();

            if (listFiles != null) {
                int append = checkRepeat(outputFileName, Arrays.asList(listFiles));
                outputFileName = outputFileName.replace(mContext.getString(R.string.pdf_ext),
                        append + mContext.getResources().getString(R.string.pdf_ext));
            }
        }
        return outputFileName;
    }

    public void openSaveDialog(String preFillName, String ext, Consumer<String> saveMethod) {

        Dialog dialog = new Dialog(mContext);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.create_pdf_dialog);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        EditText editText = dialog.findViewById(R.id.add_pdfName);

        editText.setText(preFillName);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String filename = editText.getText().toString();
                if (StringUtils.getInstance().isEmpty(filename)) {
                    StringUtils.getInstance().showSnackbar(mContext, R.string.snackbar_name_not_blank);
                    dialog.dismiss();
                } else {
                    if (!isFileExist(filename + ext)) {
                        saveMethod.accept(filename);
                        editorSortBy = sharedPreferencesSortBy.edit();
                        editorSortBy.putInt(SORT_PREFERENCE_KEY,-1);
                        editorSortBy.commit();
                    } else {
                        Dialog dialog = new Dialog(mContext);
                        if (dialog.getWindow() != null) {
                            dialog.getWindow().setGravity(Gravity.CENTER);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialog.setCancelable(false);
                        }
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        dialog.setContentView(R.layout.name_override_dialog);
                        dialog.setCancelable(false);
                        dialog.show();

                        Button cancel = dialog.findViewById(R.id.canceldialog);
                        Button ok = dialog.findViewById(R.id.okdialog);

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openSaveDialog(preFillName, ext, saveMethod);
                                dialog.dismiss();
                            }
                        });

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                saveMethod.accept(filename);
                                editorSortBy = sharedPreferencesSortBy.edit();
                                editorSortBy.putInt(SORT_PREFERENCE_KEY,-1);
                                editorSortBy.commit();
                                dialog.dismiss();
                            }
                        });
                    }
                    lastSelectedPageNumber = -1;
                    lastSelected = -1;
                    dialog.dismiss();
                }
            }
        });
    }
}