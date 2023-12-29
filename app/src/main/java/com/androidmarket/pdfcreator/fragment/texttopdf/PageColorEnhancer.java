package com.androidmarket.pdfcreator.fragment.texttopdf;

import android.app.Activity;
import androidx.annotation.NonNull;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;

import androidmarket.R;
import com.androidmarket.pdfcreator.interfaces.Enhancer;
import com.androidmarket.pdfcreator.pdfModel.EnhancementOptionsEntity;
import com.androidmarket.pdfcreator.pdfModel.TextToPDFOptions;
import com.androidmarket.pdfcreator.pdfPreferences.TextToPdfPreferences;
import com.androidmarket.pdfcreator.util.ColorUtils;
import com.androidmarket.pdfcreator.util.StringUtils;

/**
 * An {@link Enhancer} that lets you select the page color.
 */
public class PageColorEnhancer implements Enhancer {

    private final Activity mActivity;
    private final EnhancementOptionsEntity mEnhancementOptionsEntity;
    private final TextToPdfPreferences mPreferences;
    private final TextToPDFOptions.Builder mBuilder;

    PageColorEnhancer(@NonNull final Activity activity,
                      @NonNull final TextToPDFOptions.Builder builder) {
        mActivity = activity;
        mPreferences = new TextToPdfPreferences(activity);
        mBuilder = builder;
        mEnhancementOptionsEntity = new EnhancementOptionsEntity(
                mActivity, R.drawable.page_color_texttopdf, R.string.page_color);
    }

    @Override
    public void enhance() {
//        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
//                .title(R.string.page_color)
//                .customView(R.layout.dialog_color_chooser, true)
//                .positiveText(R.string.ok)
//                .negativeText(R.string.cancel)
//                .onPositive((dialog, which) -> {
//                    View view = dialog.getCustomView();
//                    ColorPickerView colorPickerView = view.findViewById(R.id.color_picker);
//                    CheckBox defaultCheckbox = view.findViewById(R.id.set_default);
//                    final int pageColor = colorPickerView.getColor();
//                    final int fontColor = mPreferences.getFontColor();
//                    if (ColorUtils.getInstance().colorSimilarCheck(fontColor, pageColor)) {
//                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_color_too_close);
//                    }
//                    if (defaultCheckbox.isChecked()) {
//                        mPreferences.setPageColor(pageColor);
//                    }
//                    mBuilder.setPageColor(pageColor);
//                })
//                .build();
//        ColorPickerView colorPickerView = materialDialog.getCustomView().findViewById(R.id.color_picker);
//        colorPickerView.setColor(mBuilder.getPageColor());
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

        title.setText("Page color");

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
                final int pageColor = colorPickerView.getColor();
                final int fontColor = mPreferences.getFontColor();
                if (ColorUtils.getInstance().colorSimilarCheck(fontColor, pageColor)) {
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_color_too_close);
                }
                if (defaultCheckbox.isChecked()) {
                    mPreferences.setPageColor(pageColor);
                }
                mBuilder.setPageColor(pageColor);
                dialog.dismiss();
            }
        });
        colorPickerView.setColor(mBuilder.getPageColor());
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }
}
