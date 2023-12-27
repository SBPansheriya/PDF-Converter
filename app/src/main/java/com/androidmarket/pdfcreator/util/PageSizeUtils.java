package com.androidmarket.pdfcreator.util;

import static com.androidmarket.pdfcreator.Constants.DEFAULT_COMPRESSION;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.HashMap;

import androidmarket.R;

import com.androidmarket.pdfcreator.pdfPreferences.TextToPdfPreferences;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PageSizeUtils {

    private final Context mActivity;
    public static String mPageSize;
    private final String mDefaultPageSize;
    private final HashMap<Integer, Integer> mPageSizeToString;
    private final TextToPdfPreferences mPreferences;

    /**
     * Utils object to modify the page size
     *
     * @param mActivity - current context
     */
    public PageSizeUtils(Context mActivity) {
        this.mActivity = mActivity;
        mPreferences = new TextToPdfPreferences(mActivity);
        mDefaultPageSize = mPreferences.getPageSize();
        mPageSize = mPreferences.getPageSize();
        mPageSizeToString = new HashMap<>();
        mPageSizeToString.put(R.id.page_size_default, R.string.a4);
        mPageSizeToString.put(R.id.page_size_legal, R.string.legal);
        mPageSizeToString.put(R.id.page_size_executive, R.string.executive);
        mPageSizeToString.put(R.id.page_size_ledger, R.string.ledger);
        mPageSizeToString.put(R.id.page_size_tabloid, R.string.tabloid);
        mPageSizeToString.put(R.id.page_size_letter, R.string.letter);
    }

    /**
     * @param selectionId   - id of selected radio button
     * @param spinnerAValue - Value of A0 to A10 spinner
     * @param spinnerBValue - Value of B0 to B10 spinner
     * @return - Rectangle page size
     */
    private String getPageSize(int selectionId, String spinnerAValue, String spinnerBValue) {
        String stringPageSize;
        switch (selectionId) {
            case R.id.page_size_a0_a10:
                stringPageSize = spinnerAValue;
                mPageSize = stringPageSize.substring(0, stringPageSize.indexOf(" "));
                break;
            case R.id.page_size_b0_b10:
                stringPageSize = spinnerBValue;
                mPageSize = stringPageSize.substring(0, stringPageSize.indexOf(" "));
                break;
            default:
                mPageSize = mActivity.getString(mPageSizeToString.get(selectionId));

        }
        return mPageSize;
    }

    /**
     * Show a dialog to modify the page size
     *
     * @param saveValue - save the value in shared preferences
     * @return - dialog object
     */
    public MaterialDialog showPageSizeDialog(boolean saveValue) {
        MaterialDialog materialDialog = getPageSizeDialog(saveValue);

        View view = materialDialog.getCustomView();
        RadioGroup radioGroup = view.findViewById(R.id.radio_group_page_size);
        Spinner spinnerA = view.findViewById(R.id.spinner_page_size_a0_a10);
        Spinner spinnerB = view.findViewById(R.id.spinner_page_size_b0_b10);
        RadioButton radioButtonDefault = view.findViewById(R.id.page_size_default);
        radioButtonDefault.setText(String.format(mActivity.getString(R.string.default_page_size), mDefaultPageSize));

        if (saveValue)
            view.findViewById(R.id.set_as_default).setVisibility(View.GONE);

        if (mPageSize.equals(mDefaultPageSize)) {
            radioGroup.check(R.id.page_size_default);
        } else if (mPageSize.startsWith("A")) {
            radioGroup.check(R.id.page_size_a0_a10);
            spinnerA.setSelection(java.lang.Integer.parseInt(mPageSize.substring(1)));
        } else if (mPageSize.startsWith("B")) {
            radioGroup.check(R.id.page_size_b0_b10);
            spinnerB.setSelection(java.lang.Integer.parseInt(mPageSize.substring(1)));
        } else {
            Integer key = getKey(mPageSizeToString, mPageSize);
            if (key != null)
                radioGroup.check(key);
        }
        materialDialog.show();
        return materialDialog;
    }

    /**
     * Private show page size utils dialog
     *
     * @param saveValue - save the value in shared prefs
     * @return - dialog object
     */

//    public MaterialDialog createPageSizeDialog(boolean saveValue) {
//        View customView = LayoutInflater.from(mActivity).inflate(R.layout.set_page_size_dialog_layout, null);
//
//        RadioGroup radioGroupPageSize = customView.findViewById(R.id.radio_group_page_size);
//        Spinner spinnerPageSizeA = customView.findViewById(R.id.spinner_page_size_a0_a10);
//        Spinner spinnerPageSizeB = customView.findViewById(R.id.spinner_page_size_b0_b10);
//        CheckBox setAsDefaultCheckBox = customView.findViewById(R.id.cbSetDefault);
//        Button cancelButton = customView.findViewById(R.id.canceldialog);
//        Button okButton = customView.findViewById(R.id.okdialog);
//
//        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(mActivity)
//                .setView(customView);
//
//        MaterialDialog dialog = dialogBuilder.create();
//
//        cancelButton.setOnClickListener(v -> {
//            // Handle cancel button click
//            dialog.dismiss();
//        });
//
//        okButton.setOnClickListener(v -> {
//            // Handle ok button click
//            // You can access the selected radio button, spinners, and checkbox values here
//            dialog.dismiss();
//        });
//
//        return dialog;
//    }

//    @SuppressLint("ResourceType")
//    public MaterialDialog getPageSizeDialog(boolean saveValue) {
//        MaterialDialog.Builder builder = DialogUtils.getInstance().createCustomDialogWithoutContent((Activity) mActivity,0);
//
//        return builder.customView(R.layout.set_page_size_dialog_layout, true)
//                .negativeText(R.layout.positive_button_layout)
//                .positiveText("")
//                .onPositive((dialog1, which) -> {
//                    View view = LayoutInflater.from(dialog1.getContext())
//                            .inflate(R.layout.positive_button_layout, null);
//
//                    Button ok = view.findViewById(R.id.okdialog);
//                    RadioGroup radioGroup = dialog1.getCustomView().findViewById(R.id.radio_group_page_size);
//
//                    int selectedId = radioGroup.getCheckedRadioButtonId();
//                    Spinner spinnerA = dialog1.getCustomView().findViewById(R.id.spinner_page_size_a0_a10);
//                    Spinner spinnerB = dialog1.getCustomView().findViewById(R.id.spinner_page_size_b0_b10);
//                    mPageSize = getPageSize(selectedId, spinnerA.getSelectedItem().toString(),
//                            spinnerB.getSelectedItem().toString());
//                    CheckBox mSetAsDefault = dialog1.getCustomView().findViewById(R.id.set_as_default);
//                    if (saveValue || mSetAsDefault.isChecked()) {
//                        mPreferences.setPageSize(mPageSize);
//                    }
//                }).build();
//    }


    public MaterialDialog getPageSizeDialog(boolean saveValue) {
        MaterialDialog.Builder builder = DialogUtils.getInstance().createCustomDialogWithoutContent((Activity) mActivity, R.string.set_page_size_text);

        return builder.customView(R.layout.set_page_size_dialog, true)
                .onPositive((dialog1, which) -> {
                    View view = dialog1.getCustomView();
                    RadioGroup radioGroup = view.findViewById(R.id.radio_group_page_size);

                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    Spinner spinnerA = view.findViewById(R.id.spinner_page_size_a0_a10);
                    Spinner spinnerB = view.findViewById(R.id.spinner_page_size_b0_b10);
                    mPageSize = getPageSize(selectedId, spinnerA.getSelectedItem().toString(),
                            spinnerB.getSelectedItem().toString());
                    CheckBox mSetAsDefault = view.findViewById(R.id.set_as_default);
                    if (saveValue || mSetAsDefault.isChecked()) {
                        mPreferences.setPageSize(mPageSize);
                    }
                }).build();

//        Dialog dialog = new Dialog(mActivity.getApplicationContext());
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setGravity(Gravity.CENTER);
//            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//            dialog.setCancelable(false);
//        }
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        dialog.setContentView(R.layout.set_page_size_dialog_layout);
//        dialog.setCancelable(false);
//        dialog.show();
//
//        Button cancel = dialog.findViewById(R.id.canceldialog);
//        Button ok = dialog.findViewById(R.id.okdialog);
//        RadioGroup radioGroup = dialog.findViewById(R.id.radio_group_page_size);
//        Spinner spinnerA = dialog.findViewById(R.id.spinner_page_size_a0_a10);
//        Spinner spinnerB = dialog.findViewById(R.id.spinner_page_size_b0_b10);
//        RadioButton radioButtonDefault = dialog.findViewById(R.id.page_size_default);
//        radioButtonDefault.setText(String.format(mActivity.getString(R.string.default_page_size), mDefaultPageSize));
//
//        if (saveValue)
//            dialog.findViewById(R.id.set_as_default).setVisibility(View.GONE);
//
//        if (mPageSize.equals(mDefaultPageSize)) {
//            radioGroup.check(R.id.page_size_default);
//        } else if (mPageSize.startsWith("A")) {
//            radioGroup.check(R.id.page_size_a0_a10);
//            spinnerA.setSelection(java.lang.Integer.parseInt(mPageSize.substring(1)));
//        } else if (mPageSize.startsWith("B")) {
//            radioGroup.check(R.id.page_size_b0_b10);
//            spinnerB.setSelection(java.lang.Integer.parseInt(mPageSize.substring(1)));
//        } else {
//            Integer key = getKey(mPageSizeToString, mPageSize);
//            if (key != null)
//                radioGroup.check(key);
//        }
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int selectedId = radioGroup.getCheckedRadioButtonId();
//                Spinner spinnerA = view.findViewById(R.id.spinner_page_size_a0_a10);
//                Spinner spinnerB = view.findViewById(R.id.spinner_page_size_b0_b10);
//                mPageSize = getPageSize(selectedId, spinnerA.getSelectedItem().toString(),
//                        spinnerB.getSelectedItem().toString());
//                CheckBox mSetAsDefault = view.findViewById(R.id.set_as_default);
//                if (saveValue || mSetAsDefault.isChecked()) {
//                    mPreferences.setPageSize(mPageSize);
//                }
//                dialog.dismiss();
//            }
//        });
//        return null;
    }

    /**
     * Get key from the value
     *
     * @param map   - hash map
     * @param value - the value for which we want the key
     * @return - key value
     */
    private Integer getKey(HashMap<Integer, Integer> map, String value) {
        for (HashMap.Entry<Integer, Integer> entry : map.entrySet()) {
            if (value.equals(mActivity.getString(entry.getValue()))) {
                return entry.getKey();
            }
        }
        return null;
    }
}