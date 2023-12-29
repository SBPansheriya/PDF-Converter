package com.androidmarket.pdfcreator.fragment.texttopdf;

import android.app.Activity;
import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidmarket.pdfcreator.Constants;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;

import androidmarket.R;
import com.androidmarket.pdfcreator.interfaces.Enhancer;
import com.androidmarket.pdfcreator.pdfModel.EnhancementOptionsEntity;
import com.androidmarket.pdfcreator.pdfModel.TextToPDFOptions;
import com.androidmarket.pdfcreator.pdfPreferences.TextToPdfPreferences;
import com.androidmarket.pdfcreator.util.ColorUtils;
import com.androidmarket.pdfcreator.util.StringUtils;

/**
 * An {@link Enhancer} that lets you select font colors.
 */
public class FontColorEnhancer implements Enhancer {

    private final Activity mActivity;
    private final EnhancementOptionsEntity mEnhancementOptionsEntity;
    private final TextToPdfPreferences mPreferences;
    private final TextToPDFOptions.Builder mBuilder;

    FontColorEnhancer(@NonNull final Activity activity,
                      @NonNull final TextToPDFOptions.Builder builder) {
        mActivity = activity;
        mPreferences = new TextToPdfPreferences(activity);
        mBuilder = builder;
        mEnhancementOptionsEntity =  new EnhancementOptionsEntity(
                mActivity, R.drawable.font_color, R.string.font_color);
    }

    @Override
    public void enhance() {
//        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
//                .title(R.string.font_color)
//                .customView(R.layout.dialog_color_chooser, true)
//                .positiveText(R.string.ok)
//                .negativeText(R.string.cancel)
//                .onPositive((dialog, which) -> {
//                    View view = dialog.getCustomView();
//                    ColorPickerView colorPickerView = view.findViewById(R.id.color_picker);
//                    CheckBox defaultCheckbox = view.findViewById(R.id.set_default);
//                    final int fontcolor = colorPickerView.getColor();
//                    final int pageColor = mPreferences.getPageColor();
//                    if (ColorUtils.getInstance().colorSimilarCheck(fontcolor, pageColor)) {
//                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_color_too_close);
//                    }
//                    if (defaultCheckbox.isChecked()) {
//                        mPreferences.setFontColor(fontcolor);
//                    }
//                    mBuilder.setFontColor(fontcolor);
//                })
//                .build();
//        ColorPickerView colorPickerView = materialDialog.getCustomView().findViewById(R.id.color_picker);
//        colorPickerView.setColor(mBuilder.getFontColor());
//        materialDialog.show();

        Dialog dialog = new Dialog(mActivity);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_color_chooser_layout);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        ColorPickerView colorPickerView = dialog.findViewById(R.id.watermarkColor);
        CheckBox defaultCheckbox = dialog.findViewById(R.id.set_default);
        TextView title = dialog.findViewById(R.id.txt);

        title.setText("Font color");

        colorPickerView.setAlphaSliderVisible(true);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int fontcolor = colorPickerView.getColor();
                final int pageColor = mPreferences.getPageColor();
                if (ColorUtils.getInstance().colorSimilarCheck(fontcolor, pageColor)) {
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_color_too_close);
                }
                if (defaultCheckbox.isChecked()) {
                    mPreferences.setFontColor(fontcolor);
                }
                mBuilder.setFontColor(fontcolor);
                dialog.dismiss();
            }
        });
        colorPickerView.setColor(mBuilder.getFontColor());
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }
}
