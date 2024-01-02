package com.androidmarket.pdfcreator.fragment.texttopdf;

import android.app.Activity;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidmarket.pdfcreator.util.ColorUtils;
import com.androidmarket.pdfcreator.util.StringUtils;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itextpdf.text.Font;

import androidmarket.R;

import com.androidmarket.pdfcreator.interfaces.Enhancer;
import com.androidmarket.pdfcreator.pdfModel.EnhancementOptionsEntity;
import com.androidmarket.pdfcreator.pdfModel.TextToPDFOptions;
import com.androidmarket.pdfcreator.pdfPreferences.TextToPdfPreferences;

/**
 * An {@link Enhancer} that lets you select the font family.
 */
public class FontFamilyEnhancer implements Enhancer {

    private final Activity mActivity;
    private EnhancementOptionsEntity mEnhancementOptionsEntity;
    private TextToPdfContract.View mView;
    private final TextToPdfPreferences mPreferences;
    private final TextToPDFOptions.Builder mBuilder;

    FontFamilyEnhancer(@NonNull final Activity activity,
                       @NonNull final TextToPdfContract.View view,
                       @NonNull final TextToPDFOptions.Builder builder) {
        mActivity = activity;
        mPreferences = new TextToPdfPreferences(activity);
        mBuilder = builder;
        mEnhancementOptionsEntity = new EnhancementOptionsEntity(
                mActivity, R.drawable.font_family,
                String.format(mActivity.getString(R.string.default_font_family_text),
                        mBuilder.getFontFamily().name()));
        mView = view;
    }

    /**
     * Shows dialog to change font size
     */
    @Override
    public void enhance() {
        String fontFamily = mPreferences.getFontFamily();
        int ordinal = Font.FontFamily.valueOf(fontFamily).ordinal();
//        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
//                .title(String.format(mActivity.getString(R.string.default_font_family_text), fontFamily))
//                .customView(R.layout.dialog_font_family, true)
//                .positiveText(R.string.ok)
//                .negativeText(R.string.cancel)
//                .onPositive((dialog, which) -> {
//                    View view = dialog.getCustomView();
//                    RadioGroup radioGroup = view.findViewById(R.id.radio_group_font_family);
//                    int selectedId = radioGroup.getCheckedRadioButtonId();
//                    RadioButton radioButton = view.findViewById(selectedId);
//                    String fontFamily1 = radioButton.getText().toString();
//                    mBuilder.setFontFamily(Font.FontFamily.valueOf(fontFamily1));
//                    final CheckBox cbSetDefault = view.findViewById(R.id.cbSetDefault);
//                    if (cbSetDefault.isChecked()) {
//                        mPreferences.setFontFamily(fontFamily1);
//                    }
//                    showFontFamily();
//                })
//                .build();
//        RadioGroup radioGroup = materialDialog.getCustomView().findViewById(R.id.radio_group_font_family);
//        RadioButton rb = (RadioButton) radioGroup.getChildAt(ordinal);
//        rb.setChecked(true);
//        materialDialog.show();
        Dialog dialog = new Dialog(mActivity);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_font_family_layout);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        TextView title = dialog.findViewById(R.id.txt);
        RadioGroup radioGroup = dialog.findViewById(R.id.radio_group_font_family);
        CheckBox cbSetDefault = dialog.findViewById(R.id.set_default);

        title.setText(String.format(mActivity.getString(R.string.default_font_family_text), fontFamily));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = dialog.findViewById(selectedId);
                String fontFamily1 = radioButton.getText().toString();
                mBuilder.setFontFamily(Font.FontFamily.valueOf(fontFamily1));
                if (cbSetDefault.isChecked()) {
                    mPreferences.setFontFamily(fontFamily1);
                }
                showFontFamily();
                dialog.dismiss();
            }
        });
        RadioButton rb = (RadioButton) radioGroup.getChildAt(ordinal);
        rb.setChecked(true);
    }


    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }

    /**
     * Displays font family in UI
     */
    private void showFontFamily() {
        mEnhancementOptionsEntity.setName(mActivity.getString(R.string.font_family_text)
                + mBuilder.getFontFamily().name());
        mView.updateView();
    }
}
