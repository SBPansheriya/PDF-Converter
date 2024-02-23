package com.utillity.pdfgenerator.util;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import com.utillity.pdfgenerator.R;

import static com.utillity.pdfgenerator.Constants.ADD_PASSWORD;
import static com.utillity.pdfgenerator.Constants.ADD_WATERMARK;
import static com.utillity.pdfgenerator.Constants.REMOVE_PASSWORD;
import static com.utillity.pdfgenerator.Constants.ROTATE_PAGES;

public class DialogUtils {

    private DialogUtils() {
    }

    private static class SingletonHolder {
        static final DialogUtils INSTANCE = new DialogUtils();
    }

    public static DialogUtils getInstance() {
        return DialogUtils.SingletonHolder.INSTANCE;
    }

    public MaterialDialog.Builder createWarningDialog(Activity activity,
                                                      int content) {
        return new MaterialDialog.Builder(activity)
                .title(R.string.warning)
                .content(content)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    public MaterialDialog.Builder createOverwriteDialog(Activity activity) {
        return new MaterialDialog.Builder(activity)
                .title(R.string.warning)
                .content(R.string.overwrite_message)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    public MaterialDialog.Builder createCustomDialog(Activity activity,
                                                     int title, int content) {
        return new MaterialDialog.Builder(activity)
                .title(title)
                .content(content)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    public MaterialDialog.Builder createCustomDialogWithoutContent(Activity activity,
                                                                   int title) {
        return new MaterialDialog.Builder(activity)
                .title(title)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    public MaterialDialog createAnimationDialog(Activity activity) {
        return new MaterialDialog.Builder(activity)
                .customView(R.layout.lottie_anim_dialog, false)
                .build();
    }

    public MaterialDialog createCustomAnimationDialog(Activity activity, String title) {
        View view = LayoutInflater.from(activity).inflate(R.layout.lottie_anim_dialog, null);

        TextView dialogTitle = view.findViewById(R.id.textView);
        dialogTitle.setText(title);

        return new MaterialDialog.Builder(activity)
                .customView(view, false)
                .build();
    }

    public void showFilesInfoDialog(Activity activity, int dialogId) {
        int stringId = R.string.viewfiles_rotatepages;
        switch (dialogId) {
            case ROTATE_PAGES:
                stringId = R.string.viewfiles_rotatepages;
                break;
            case REMOVE_PASSWORD:
                stringId = R.string.viewfiles_removepassword;
                break;
            case ADD_PASSWORD:
                stringId = R.string.viewfiles_addpassword;
                break;
            case ADD_WATERMARK:
                stringId = R.string.viewfiles_addwatermark;
                break;
        }

        Dialog dialog = new Dialog(activity);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.pdf_converter_dialog_layout);
        dialog.setCancelable(false);
        dialog.show();

        Button ok = dialog.findViewById(R.id.okdialog);
        TextView textView = dialog.findViewById(R.id.txt);

        textView.setText(stringId);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}