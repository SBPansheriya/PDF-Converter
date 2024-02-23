package com.utillity.pdfgenerator.util;

import android.app.Activity;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.widget.FrameLayout;

import com.utillity.pdfgenerator.Constants;
import com.dd.morphingbutton.MorphingButton;

import com.utillity.pdfgenerator.R;

import static com.utillity.pdfgenerator.Constants.THEME_BLACK;
import static com.utillity.pdfgenerator.Constants.THEME_DARK;
import static com.utillity.pdfgenerator.Constants.THEME_SYSTEM;
import static com.utillity.pdfgenerator.Constants.THEME_WHITE;

public class MorphButtonUtility {

    private final Activity mActivity;
    private boolean mDarkModeEnabled = false;

    public MorphButtonUtility(Activity activity) {
        mActivity = activity;
        checkDarkMode();
    }
    public int integer() {
        return mActivity.getResources().getInteger(R.integer.mb_animation);
    }

    private int dimen(@DimenRes int resId) {
        return (int) mActivity.getResources().getDimension(resId);
    }

    private int color(@ColorRes int resId) {
        return mActivity.getResources().getColor(resId);
    }

    private void checkDarkMode() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        String themeName = sharedPreferences.getString(Constants.DEFAULT_THEME_TEXT,
                Constants.DEFAULT_THEME);
        switch (themeName) {
            case THEME_WHITE:
                mDarkModeEnabled = false;
                break;
            case THEME_BLACK:
            case THEME_DARK:
                mDarkModeEnabled = true;
                break;
            case THEME_SYSTEM:
            default:
                mDarkModeEnabled = (mActivity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        }
    }

    public void morphToSquare(final MorphingButton btnMorph, int duration) {
        MorphingButton.Params square = defaultButton(duration);
        String text = btnMorph.getText().toString().isEmpty() ?
                mActivity.getString(R.string.create_pdf) :
                btnMorph.getText().toString();
        if (mDarkModeEnabled) {
            square.color(color(R.color.colorBlackAltLight));
            square.colorPressed(color(R.color.colorBlackAlt));
        } else {
            square.color(color(R.color.mb_blue));
            square.colorPressed(color(R.color.mb_blue_dark));
        }
        square.text(text);
        btnMorph.morph(square);
    }

    public void morphToGrey(final MorphingButton btnMorph, int duration) {
        MorphingButton.Params square = defaultButton(duration);
        square.color(color(R.color.mb_gray));
        square.colorPressed(color(R.color.mb_gray));
        square.text(btnMorph.getText().toString());
        btnMorph.morph(square);
    }

    private MorphingButton.Params defaultButton(int duration) {
        return MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(dimen(R.dimen.mb_corner_radius_2))
                .width(FrameLayout.LayoutParams.MATCH_PARENT)
                .height(FrameLayout.LayoutParams.WRAP_CONTENT);
    }
}
