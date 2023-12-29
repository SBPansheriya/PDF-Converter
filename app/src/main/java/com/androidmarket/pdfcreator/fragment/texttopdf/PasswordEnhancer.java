package com.androidmarket.pdfcreator.fragment.texttopdf;

import android.app.Activity;
import androidx.annotation.NonNull;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import androidmarket.R;
import com.androidmarket.pdfcreator.interfaces.Enhancer;
import com.androidmarket.pdfcreator.pdfModel.EnhancementOptionsEntity;
import com.androidmarket.pdfcreator.pdfModel.TextToPDFOptions;
import com.androidmarket.pdfcreator.util.DefaultTextWatcher;
import com.androidmarket.pdfcreator.util.DialogUtils;
import com.androidmarket.pdfcreator.util.StringUtils;

/**
 * An {@link Enhancer} that lets you add and remove passwords
 */
public class PasswordEnhancer implements Enhancer {

    private final Activity mActivity;
    private final EnhancementOptionsEntity mEnhancementOptionsEntity;
    private TextToPdfContract.View mView;
    private final TextToPDFOptions.Builder mBuilder;

    PasswordEnhancer(@NonNull final Activity activity,
                     @NonNull final TextToPdfContract.View view,
                     @NonNull final TextToPDFOptions.Builder builder) {
        mActivity = activity;
        mBuilder = builder;
        mBuilder.setPasswordProtected(false);
        mEnhancementOptionsEntity = new EnhancementOptionsEntity(
                mActivity, R.drawable.password_protect_pdf, R.string.set_password);
        mView = view;
    }

    @Override
    public void enhance() {
//        MaterialDialog.Builder builder = DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity,
//                R.string.set_password);
//        final MaterialDialog dialog = builder
//                .customView(R.layout.custom_dialog, true)
//                .neutralText(R.string.remove_dialog)
//                .build();
//
//        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
//        final View neutralAction = dialog.getActionButton(DialogAction.NEUTRAL);
//        final EditText passwordInput = dialog.getCustomView().findViewById(R.id.password);
//        passwordInput.setText(mBuilder.getPassword());
//        passwordInput.addTextChangedListener(
//                new DefaultTextWatcher() {
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        positiveAction.setEnabled(s.toString().trim().length() > 0);
//                    }
//                });
//
//        positiveAction.setOnClickListener(v -> {
//            if (StringUtils.getInstance().isEmpty(passwordInput.getText())) {
//                StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_password_cannot_be_blank);
//            } else {
//                mBuilder.setPassword(passwordInput.getText().toString());
//                mBuilder.setPasswordProtected(true);
//                onPasswordAdded();
//                dialog.dismiss();
//            }
//        });
//
//        if (StringUtils.getInstance().isNotEmpty(mBuilder.getPassword())) {
//            neutralAction.setOnClickListener(v -> {
//                mBuilder.setPassword(null);
//                onPasswordRemoved();
//                mBuilder.setPasswordProtected(false);
//                dialog.dismiss();
//                StringUtils.getInstance().showSnackbar(mActivity, R.string.password_remove);
//            });
//        }
//        dialog.show();
//        positiveAction.setEnabled(false);

        Dialog dialog1 = new Dialog(mActivity);
        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setGravity(Gravity.CENTER);
            dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog1.setCancelable(false);
        }
        dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog1.setContentView(R.layout.set_password_dialog);
        dialog1.setCancelable(false);
        dialog1.show();

        Button cancel = dialog1.findViewById(R.id.canceldialog);
        Button ok = dialog1.findViewById(R.id.okdialog);
        Button remove = dialog1.findViewById(R.id.remove_dialog);
        EditText passwordinput = dialog1.findViewById(R.id.password);

        passwordinput.setText(mBuilder.getPassword());
        passwordinput.addTextChangedListener(
                new DefaultTextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        ok.setEnabled(s.toString().trim().length() > 0);
                    }
                });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });

        if (StringUtils.getInstance().isNotEmpty(mBuilder.getPassword())) {
            remove.setOnClickListener(v -> {
                mBuilder.setPassword(null);
                onPasswordRemoved();
                mBuilder.setPasswordProtected(false);
                dialog1.dismiss();
                StringUtils.getInstance().showSnackbar(mActivity, R.string.password_remove);

            });
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.getInstance().isEmpty(passwordinput.getText())) {
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_password_cannot_be_blank);
                } else {
                    mBuilder.setPassword(passwordinput.getText().toString());
                    mBuilder.setPasswordProtected(true);
                    onPasswordAdded();
                    dialog1.dismiss();
                }
            }
        });
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }

    private void onPasswordAdded() {
        mEnhancementOptionsEntity
                .setImage(mActivity.getResources().getDrawable(R.drawable.baseline_done_24));
        mView.updateView();
    }

    private void onPasswordRemoved() {
        mEnhancementOptionsEntity
                .setImage(mActivity.getResources().getDrawable(R.drawable.password_protect_pdf));
        mView.updateView();
    }
}
