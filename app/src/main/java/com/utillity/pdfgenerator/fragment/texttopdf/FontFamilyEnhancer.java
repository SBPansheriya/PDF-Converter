package com.utillity.pdfgenerator.fragment.texttopdf;

import static com.utillity.pdfgenerator.fragment.texttopdf.TextToPdfFragment.lastSelectedFontFamily;

import android.app.Activity;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.itextpdf.text.Font;

import com.utillity.pdfgenerator.R;

import com.utillity.pdfgenerator.interfaces.Enhancer;
import com.utillity.pdfgenerator.pdfModel.EnhancementOptionsEntity;
import com.utillity.pdfgenerator.pdfModel.TextToPDFOptions;
import com.utillity.pdfgenerator.pdfPreferences.TextToPdfPreferences;

public class FontFamilyEnhancer implements Enhancer {

    private final Activity mActivity;
    private EnhancementOptionsEntity mEnhancementOptionsEntity;
    private TextToPdfContract.View mView;
    private final TextToPdfPreferences mPreferences;
    private final TextToPDFOptions.Builder mBuilder;
    String fontFamily1;

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

    @Override
    public void enhance() {
        String fontFamily = mPreferences.getFontFamily();
        int ordinal = Font.FontFamily.valueOf(fontFamily).ordinal();

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

        if (lastSelectedFontFamily != -1){
            radioGroup.check((radioGroup.getChildAt(lastSelectedFontFamily).getId()));
        } else {
            RadioButton rb = (RadioButton) radioGroup.getChildAt(ordinal);
            rb.setChecked(true);
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = dialog.findViewById(selectedId);
                fontFamily1 = radioButton.getText().toString();
                if (fontFamily1.equals("COURIER")) {
                    lastSelectedFontFamily = 0;
                } else if (fontFamily1.equals("HELVETICA")) {
                    lastSelectedFontFamily = 1;
                } else if (fontFamily1.equals("TIMES_ROMAN")) {
                    lastSelectedFontFamily = 2;
                } else if (fontFamily1.equals("SYMBOL")) {
                    lastSelectedFontFamily = 3;
                } else if (fontFamily1.equals("ZAPFDINGBATS")) {
                    lastSelectedFontFamily = 4;
                } else if (fontFamily1.equals("UNDEFINED")) {
                    lastSelectedFontFamily = 5;
                }
                mBuilder.setFontFamily(Font.FontFamily.valueOf(fontFamily1));
                if (cbSetDefault.isChecked()) {
                    mPreferences.setFontFamily(fontFamily1);
                }
                showFontFamily();
                dialog.dismiss();
            }
        });
    }


    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }

    private void showFontFamily() {
        mEnhancementOptionsEntity.setName(mActivity.getString(R.string.font_family_text)
                + mBuilder.getFontFamily().name());
        mView.updateView();
    }
}
