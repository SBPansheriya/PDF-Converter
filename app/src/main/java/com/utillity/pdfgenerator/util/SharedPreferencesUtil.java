package com.utillity.pdfgenerator.util;

import android.content.SharedPreferences;

import com.utillity.pdfgenerator.Constants;

public class SharedPreferencesUtil {

    private SharedPreferencesUtil() { }

    private static class SingletonHolder {
        static final SharedPreferencesUtil INSTANCE = new SharedPreferencesUtil();
    }

    public static SharedPreferencesUtil getInstance() {
        return SharedPreferencesUtil.SingletonHolder.INSTANCE;
    }

    public void setDefaultPageNumStyle(SharedPreferences.Editor editor, String pageNumStyle, int id) {
        editor.putString(Constants.PREF_PAGE_STYLE, pageNumStyle);
        editor.putInt(Constants.PREF_PAGE_STYLE_ID, id);
        editor.apply();
    }

    public void clearDefaultPageNumStyle(SharedPreferences.Editor editor) {
        setDefaultPageNumStyle(editor, null, -1);
    }

}
