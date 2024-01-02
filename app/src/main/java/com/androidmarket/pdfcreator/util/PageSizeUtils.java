package com.androidmarket.pdfcreator.util;

import static com.androidmarket.pdfcreator.util.WatermarkUtils.getStyleNameFromFont;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.HashMap;

import androidmarket.R;

import com.androidmarket.pdfcreator.pdfPreferences.TextToPdfPreferences;

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
//    public MaterialDialog showPageSizeDialog(boolean saveValue) {
//        MaterialDialog materialDialog = getPageSizeDialog(saveValue);
//
//        View view = materialDialog.getCustomView();
//        RadioGroup radioGroup = view.findViewById(R.id.radio_group_page_size);
//        Spinner spinnerA = view.findViewById(R.id.spinner_page_size_a0_a10);
//        Spinner spinnerB = view.findViewById(R.id.spinner_page_size_b0_b10);
//        RadioButton radioButtonDefault = view.findViewById(R.id.page_size_default);
//        radioButtonDefault.setText(String.format(mActivity.getString(R.string.default_page_size), mDefaultPageSize));
//
//        if (saveValue) view.findViewById(R.id.cbSetDefault).setVisibility(View.GONE);
//
//        ArrayAdapter<String> spinnerAAdapter = new ArrayAdapter<>(mActivity, R.layout.simple_spinner_item,
//                mActivity.getResources().getStringArray(R.array.array_page_sizes_a0_b10));
//        spinnerAAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//        spinnerA.setAdapter(spinnerAAdapter);
//
//        ArrayAdapter<String> spinnerBAdapter = new ArrayAdapter<>(mActivity, R.layout.simple_spinner_item,
//                mActivity.getResources().getStringArray(R.array.array_page_sizes_b0_b10));
//        spinnerBAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//        spinnerB.setAdapter(spinnerBAdapter);
//
//        if (mPageSize.equals(mDefaultPageSize)) {
//            radioGroup.check(R.id.page_size_default);
//        } else if (mPageSize.startsWith("A")) {
//            radioGroup.check(R.id.page_size_a0_a10);
////            spinnerA.setSelection(java.lang.Integer.parseInt(mPageSize.substring(1)));
//            spinnerA.setSelection(spinnerAAdapter.getPosition(mPageSize.substring(1)));
//        } else if (mPageSize.startsWith("B")) {
//            radioGroup.check(R.id.page_size_b0_b10);
////            spinnerB.setSelection(java.lang.Integer.parseInt(mPageSize.substring(1)));
//            spinnerA.setSelection(spinnerBAdapter.getPosition(mPageSize.substring(1)));
//        } else {
//            Integer key = getKey(mPageSizeToString, mPageSize);
//            if (key != null) radioGroup.check(key);
//        }
//        materialDialog.show();
//        return materialDialog;
//    }
//
//    /**
//     * Private show page size utils dialog
//     *
//     * @param saveValue - save the value in shared prefs
//     * @return - dialog object
//     */
//
//    public MaterialDialog getPageSizeDialog(boolean saveValue) {
////        View customView = LayoutInflater.from(mActivity).inflate(R.layout.set_page_size_dialog_layout, null);
//
////        Button btnOk = customView.findViewById(R.id.okdialog);
////        Button btnCancel = customView.findViewById(R.id.canceldialog);
////        RadioGroup radioGroup = customView.findViewById(R.id.radio_group_page_size);
////        Spinner spinnerA = customView.findViewById(R.id.spinner_page_size_a0_a10);
////        Spinner spinnerB = customView.findViewById(R.id.spinner_page_size_b0_b10);
////        CheckBox mSetAsDefault = customView.findViewById(R.id.cbSetDefault);
//
////        MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
////                .customView(customView, true)
////                .show();
//        MaterialDialog.Builder builder = new MaterialDialog.Builder(mActivity);
//        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(mActivity.LAYOUT_INFLATER_SERVICE);
//        View dialogView = inflater.inflate(R.layout.set_page_size_dialog_layout, null);
//        builder.customView(dialogView, false);
//
//        MaterialDialog myalertdialog = builder.build();
//
//        Button btnOk = dialogView.findViewById(R.id.okdialog);
//        Button btnCancel = dialogView.findViewById(R.id.canceldialog);
//        RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group_page_size);
//        Spinner spinnerA = dialogView.findViewById(R.id.spinner_page_size_a0_a10);
//        Spinner spinnerB = dialogView.findViewById(R.id.spinner_page_size_b0_b10);
//        CheckBox mSetAsDefault = dialogView.findViewById(R.id.cbSetDefault);
//
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                int selectedId = radioGroup.getCheckedRadioButtonId();
//                mPageSize = getPageSize(selectedId, spinnerA.getSelectedItem().toString(), spinnerB.getSelectedItem().toString());
//
//                if (saveValue || mSetAsDefault.isChecked()) {
//                    mPreferences.setPageSize(mPageSize);
//                }
//                myalertdialog.dismiss();
//            }
//        });
//
//
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                myalertdialog.dismiss();
//            }
//        });
//
//        return myalertdialog;
//    }

    public Dialog showPageSizeDialog(boolean saveValue) {
        Dialog dialog = getPageSizeDialog(saveValue);

        RadioGroup radioGroup = dialog.findViewById(R.id.radio_group_page_size);
        Spinner spinnerA = dialog.findViewById(R.id.spinner_page_size_a0_a10);
        Spinner spinnerB = dialog.findViewById(R.id.spinner_page_size_b0_b10);
        RadioButton radioButtonDefault = dialog.findViewById(R.id.page_size_default);
        radioButtonDefault.setText(String.format(mActivity.getString(R.string.default_page_size), mDefaultPageSize));

        if (saveValue) dialog.findViewById(R.id.cbSetDefault).setVisibility(View.VISIBLE);

        ArrayAdapter<String> spinnerAAdapter = new ArrayAdapter<>(mActivity, R.layout.simple_spinner_item,
                mActivity.getResources().getStringArray(R.array.array_page_sizes_a0_b10));
        spinnerAAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerA.setAdapter(spinnerAAdapter);

        ArrayAdapter<String> spinnerBAdapter = new ArrayAdapter<>(mActivity, R.layout.simple_spinner_item,
                mActivity.getResources().getStringArray(R.array.array_page_sizes_b0_b10));
        spinnerBAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerB.setAdapter(spinnerBAdapter);

        if (mPageSize.equals(mDefaultPageSize)) {
            radioGroup.check(R.id.page_size_default);
        } else if (mPageSize.startsWith("A")) {
            radioGroup.check(R.id.page_size_a0_a10);
//            spinnerA.setSelection(java.lang.Integer.parseInt(mPageSize.substring(1)));
            spinnerA.setSelection(spinnerAAdapter.getPosition(mPageSize.substring(1)));
        } else if (mPageSize.startsWith("B")) {
            radioGroup.check(R.id.page_size_b0_b10);
//            spinnerB.setSelection(java.lang.Integer.parseInt(mPageSize.substring(1)));
            spinnerA.setSelection(spinnerBAdapter.getPosition(mPageSize.substring(1)));
        } else {
            Integer key = getKey(mPageSizeToString, mPageSize);
            if (key != null) radioGroup.check(key);
        }
        dialog.show();
        return dialog;
    }

    public Dialog getPageSizeDialog(boolean saveValue) {
        Dialog dialog = new Dialog(mActivity);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.set_page_size_dialog_layout);
        dialog.setCancelable(false);
        dialog.show();

        Button btnOk = dialog.findViewById(R.id.okdialog);
        Button btnCancel = dialog.findViewById(R.id.canceldialog);
        RadioGroup radioGroup = dialog.findViewById(R.id.radio_group_page_size);
        Spinner spinnerA = dialog.findViewById(R.id.spinner_page_size_a0_a10);
        Spinner spinnerB = dialog.findViewById(R.id.spinner_page_size_b0_b10);
        CheckBox mSetAsDefault = dialog.findViewById(R.id.cbSetDefault);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                mPageSize = getPageSize(selectedId, spinnerA.getSelectedItem().toString(), spinnerB.getSelectedItem().toString());

                if (saveValue || mSetAsDefault.isChecked()) {
                    mPreferences.setPageSize(mPageSize);
                }
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

//    public MaterialDialog getPageSizeDialog(boolean saveValue) {
//
//        MaterialDialog.Builder builder = DialogUtils.getInstance().createCustomDialogWithoutContent((Activity) mActivity, R.string.set_page_size_text);
//
//        return builder.customView(R.layout.set_page_size_dialog, true)
//                .onPositive((dialog1, which) -> {
//                    View view = dialog1.getCustomView();
//                    RadioGroup radioGroup = view.findViewById(R.id.radio_group_page_size);
//
//                    int selectedId = radioGroup.getCheckedRadioButtonId();
//                    Spinner spinnerA = view.findViewById(R.id.spinner_page_size_a0_a10);
//                    Spinner spinnerB = view.findViewById(R.id.spinner_page_size_b0_b10);
//                    mPageSize = getPageSize(selectedId, spinnerA.getSelectedItem().toString(),
//                            spinnerB.getSelectedItem().toString());
//                    CheckBox mSetAsDefault = view.findViewById(R.id.set_as_default);
//                    if (saveValue || mSetAsDefault.isChecked()) {
//                        mPreferences.setPageSize(mPageSize);
//                    }
//                }).build();
//    }

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