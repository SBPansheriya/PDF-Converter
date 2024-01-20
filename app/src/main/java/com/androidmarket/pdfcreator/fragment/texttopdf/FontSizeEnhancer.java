package com.androidmarket.pdfcreator.fragment.texttopdf;

import android.annotation.SuppressLint;
import android.app.Activity;
import androidx.annotation.NonNull;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import androidmarket.R;
import com.androidmarket.pdfcreator.interfaces.Enhancer;
import com.androidmarket.pdfcreator.pdfModel.EnhancementOptionsEntity;
import com.androidmarket.pdfcreator.pdfModel.TextToPDFOptions;
import com.androidmarket.pdfcreator.pdfPreferences.TextToPdfPreferences;
import com.androidmarket.pdfcreator.util.StringUtils;

/**
 * An {@link Enhancer} that lets you select font size.
 */
public class FontSizeEnhancer implements Enhancer {

    private final Activity mActivity;
    private EnhancementOptionsEntity mEnhancementOptionsEntity;
    private final TextToPdfContract.View mView;
    private final TextToPdfPreferences mPreferences;
    private final TextToPDFOptions.Builder mBuilder;

    FontSizeEnhancer(@NonNull final Activity activity,
                     @NonNull final TextToPdfContract.View view,
                     @NonNull final TextToPDFOptions.Builder builder) {
        mActivity = activity;
        mPreferences = new TextToPdfPreferences(activity);
        mBuilder = builder;
        mBuilder.setFontSizeTitle(
                String.format(mActivity.getString(R.string.edit_font_size),
                        mPreferences.getFontSize()));
        mEnhancementOptionsEntity = new EnhancementOptionsEntity(
                mActivity.getResources().getDrawable(R.drawable.font_size),
                mBuilder.getFontSizeTitle());
        mView = view;
    }

    /**
     * Function to take the font size of pdf as user input
     */
    @Override
    public void enhance() {
//        new MaterialDialog.Builder(mActivity)
//                .title(mBuilder.getFontSizeTitle())
//                .customView(R.layout.dialog_font_size, true)
//                .positiveText(R.string.ok)
//                .negativeText(R.string.cancel)
//                .onPositive((dialog, which) -> {
//                    final EditText fontInput = dialog.getCustomView().findViewById(R.id.fontInput);
//                    final CheckBox cbSetDefault = dialog.getCustomView().findViewById(R.id.cbSetFontDefault);
//                    try {
//                        int check = Integer.parseInt(String.valueOf(fontInput.getText()));
//                        if (check > 1000 || check < 0) {
//                            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
//                        } else {
//                            mBuilder.setFontSize(check);
//                            showFontSize();
//                            StringUtils.getInstance().showSnackbar(mActivity, R.string.font_size_changed);
//                            if (cbSetDefault.isChecked()) {
//                                mPreferences.setFontSize(mBuilder.getFontSize());
//                                mBuilder.setFontSizeTitle(String.format(mActivity.getString(R.string.edit_font_size),
//                                        mPreferences.getFontSize()));
//                            }
//                        }
//                    } catch (NumberFormatException e) {
//                        StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
//                    }
//                })
//                .show();

        Dialog dialog = new Dialog(mActivity);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_font_size_layout);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        CheckBox cbSetDefault = dialog.findViewById(R.id.cbSetFontDefault);
        EditText fontInput = dialog.findViewById(R.id.fontInput);
        TextView title = dialog.findViewById(R.id.txt);

        int fontSize = mBuilder.getFontSize();

        fontInput.setText(""+fontSize);

        title.setText(mBuilder.getFontSizeTitle());

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String fontGet = fontInput.getText().toString();
                    int check = Integer.parseInt(fontGet);
                    if (check > 1000 || check < 0) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                    } else {
                        mBuilder.setFontSize(check);
                        showFontSize();
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.font_size_changed);
                        if (cbSetDefault.isChecked()) {
                            mPreferences.setFontSize(mBuilder.getFontSize());
                            mBuilder.setFontSizeTitle(String.format(mActivity.getString(R.string.edit_font_size),
                                    mPreferences.getFontSize()));
                        }
                    }
                } catch (NumberFormatException e) {
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                }
                dialog.dismiss();
            }
        });
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }

    /**
     * Displays font size in UI
     */
    @SuppressLint("StringFormatInvalid")
    private void showFontSize() {
        mEnhancementOptionsEntity
                .setName(String.format(mActivity.getString(R.string.font_size),
                        String.valueOf(mBuilder.getFontSize())));
        mView.updateView();
    }
}
