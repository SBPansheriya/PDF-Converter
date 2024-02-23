package com.utillity.pdfgenerator.util;

import android.app.Activity;
import android.content.Context;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

import static com.utillity.pdfgenerator.Constants.pdfDirectory;

public class StringUtils {

    private StringUtils() {
    }

    private static class SingletonHolder {
        static final StringUtils INSTANCE = new StringUtils();
    }

    public static StringUtils getInstance() {
        return StringUtils.SingletonHolder.INSTANCE;
    }

    public boolean isEmpty(CharSequence s) {
        return s == null || s.toString().trim().equals("");
    }

    public boolean isNotEmpty(CharSequence s) {
        return s != null && !s.toString().trim().equals("");
    }

    public void showSnackbar(Activity context, int resID) {
        Toast.makeText(context, resID, Toast.LENGTH_SHORT).show();
    }

    public void showSnackbar(Activity context, String resID) {
        Toast.makeText(context, resID, Toast.LENGTH_SHORT).show();
    }

    public Snackbar getSnackbarwithAction(Activity context, int resID) {
        return Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG);
    }

    public String getDefaultStorageLocation(Context context) {
        File dir = context.getFilesDir();
        if (!dir.exists()) {
            boolean isDirectoryCreated = dir.mkdir();
            if (!isDirectoryCreated) {
                Log.e("Error", "Directory could not be created");
            }
        }
        return dir.getAbsolutePath() + pdfDirectory;
    }

    public int parseIntOrDefault(CharSequence text, int def) throws NumberFormatException {
        if (isEmpty(text))
            return def;
        else
            return Integer.parseInt(text.toString());
    }
}
