package com.utillity.pdfgenerator.pdfPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

import com.utillity.pdfgenerator.Constants;

public class TextToPdfPreferences {

    private final SharedPreferences mSharedPreferences;

    public TextToPdfPreferences(@NonNull final Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getFontColor() {
        return mSharedPreferences.getInt(Constants.DEFAULT_FONT_COLOR_TEXT,
                Constants.DEFAULT_FONT_COLOR);
    }

    public void setFontColor(final int fontColor) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.DEFAULT_FONT_COLOR_TEXT, fontColor);
        editor.apply();
    }

    public int getPageColor() {
        return mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_TTP,
                Constants.DEFAULT_PAGE_COLOR);
    }

    public void setPageColor(final int pageColor) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.DEFAULT_PAGE_COLOR_TTP, pageColor);
        editor.apply();
    }

    public String getFontFamily() {
        return mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY);
    }

    public void setFontFamily(@NonNull final String fontFamily) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.DEFAULT_FONT_FAMILY_TEXT, fontFamily);
        editor.apply();
    }

    public int getFontSize() {
        return mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, Constants.DEFAULT_FONT_SIZE);
    }

    public void setFontSize(final int fontSize) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.DEFAULT_FONT_SIZE_TEXT, fontSize);
        editor.apply();
    }

    public String getPageSize() {
        return mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT, Constants.DEFAULT_PAGE_SIZE);
    }

    public void setPageSize(@NonNull final String pageSize) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.DEFAULT_PAGE_SIZE_TEXT, pageSize);
        editor.apply();
    }
}
