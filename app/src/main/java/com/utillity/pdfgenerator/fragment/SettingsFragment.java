package com.utillity.pdfgenerator.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.utillity.pdfgenerator.activities.HomeActivity;
import com.utillity.pdfgenerator.adapter.AdapterEnhancementOptions;
import com.itextpdf.text.Font;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.utillity.pdfgenerator.R;

import com.utillity.pdfgenerator.interfaces.OnItemClickListener;
import com.utillity.pdfgenerator.pdfModel.EnhancementOptionsEntity;
import com.utillity.pdfgenerator.Constants;
import com.utillity.pdfgenerator.util.ImageUtils;
import com.utillity.pdfgenerator.util.PageSizeUtils;
import com.utillity.pdfgenerator.util.SharedPreferencesUtil;
import com.utillity.pdfgenerator.util.StringUtils;
import com.utillity.pdfgenerator.util.ThemeUtils;

import static com.utillity.pdfgenerator.Constants.DEFAULT_COMPRESSION;
import static com.utillity.pdfgenerator.Constants.MASTER_PWD_STRING;
import static com.utillity.pdfgenerator.Constants.MODIFY_STORAGE_LOCATION_CODE;
import static com.utillity.pdfgenerator.Constants.STORAGE_LOCATION;
import static com.utillity.pdfgenerator.Constants.appName;
import static com.utillity.pdfgenerator.util.SettingsOptions.getEnhancementOptions;

public class SettingsFragment extends Fragment implements OnItemClickListener {

    @BindView(R.id.settings_list)
    RecyclerView mEnhancementOptionsRecycleView;

    private Activity mActivity;
    private SharedPreferences mSharedPreferences;

    public SettingsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, root);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        showSettingsOptions();

        ImageView ivBack = root.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HomeActivity.class));
                getActivity().finish();
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MODIFY_STORAGE_LOCATION_CODE) {
            if (data.getExtras() != null) {
                String folderLocation = data.getExtras().getString("data") + "/";
                mSharedPreferences.edit().putString(STORAGE_LOCATION, folderLocation).apply();
                StringUtils.getInstance().showSnackbar(mActivity, R.string.storage_location_modified);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showSettingsOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        mEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        ArrayList<EnhancementOptionsEntity> mEnhancementOptionsEntityArrayList = getEnhancementOptions(mActivity);
        AdapterEnhancementOptions adapter =
                new AdapterEnhancementOptions(this, mEnhancementOptionsEntityArrayList);
        mEnhancementOptionsRecycleView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                changeCompressImage();
                break;
            case 1:
                setPageSize();
                break;
            case 2:
                editFontSize();
                break;
            case 3:
                changeFontFamily();
                break;
            case 4:
                setTheme();
                break;
            case 5:
                ImageUtils.getInstance().showImageScaleTypeDialog(mActivity, true,"setting");
                break;
            case 6:
                changeMasterPassword();
                break;
            case 7:
                setShowPageNumber();
        }
    }
    private void changeMasterPassword() {

        Dialog dialog = new Dialog(getContext());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_change_master_pwd_layout);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        EditText et = dialog.findViewById(R.id.value);
        TextView tv = dialog.findViewById(R.id.content);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = et.getText().toString();
                if (!value.isEmpty())
                    mSharedPreferences.edit().putString(MASTER_PWD_STRING, value).apply();
                else
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                dialog.dismiss();
            }
        });
        tv.setText(String.format(mActivity.getString(R.string.current_master_pwd),
                mSharedPreferences.getString(MASTER_PWD_STRING, appName)));
    }
    private void changeCompressImage() {

        Dialog dialog = new Dialog(getContext());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.compress_image_dialog_layout);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        CheckBox cbSetDefault = dialog.findViewById(R.id.cbSetDefault);
        final EditText qualityInput = dialog.findViewById(R.id.quality);

        int imageCompression = mSharedPreferences.getInt(DEFAULT_COMPRESSION, 0);

        qualityInput.setText(""+imageCompression);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int check;
                try {
                    check = Integer.parseInt(String.valueOf(qualityInput.getText()));
                    if (check > 100 || check < 0) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                    } else {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putInt(DEFAULT_COMPRESSION, check);
                        editor.apply();
                        showSettingsOptions();
                    }
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                }
            }
        });

        cbSetDefault.setVisibility(View.GONE);
    }

    private void editFontSize() {
        int fontSize = mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT,
                Constants.DEFAULT_FONT_SIZE);
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
        EditText fontInput = dialog.findViewById(R.id.fontInput);
        TextView title = dialog.findViewById(R.id.txt);
        CheckBox checkBox = dialog.findViewById(R.id.cbSetFontDefault);

        title.setText(String.format("Font size (Default: %d)", fontSize));

        fontInput.setText("" + fontSize);

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
                    int check = Integer.parseInt(String.valueOf(fontInput.getText()));
                    if (check > 1000 || check < 0) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                    } else {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.font_size_changed);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putInt(Constants.DEFAULT_FONT_SIZE_TEXT, check);
                        editor.apply();
                        showSettingsOptions();
                    }
                } catch (NumberFormatException e) {
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                }
                dialog.dismiss();
            }
        });

        checkBox.setVisibility(View.GONE);
    }

    private void changeFontFamily() {
        String fontFamily = mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY);
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

        CheckBox checkBox = dialog.findViewById(R.id.set_default);

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
                RadioGroup radioGroup = dialog.findViewById(R.id.radio_group_font_family);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = dialog.findViewById(selectedId);
                String fontFamily1 = radioButton.getText().toString();
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(Constants.DEFAULT_FONT_FAMILY_TEXT, fontFamily1);
                editor.apply();
                showSettingsOptions();
                dialog.dismiss();
            }
        });
        RadioGroup radioGroup = dialog.findViewById(R.id.radio_group_font_family);
        RadioButton rb = (RadioButton) radioGroup.getChildAt(ordinal);
        rb.setChecked(true);
        checkBox.setVisibility(View.GONE);
    }

    private void setPageSize() {
        PageSizeUtils utils = new PageSizeUtils(mActivity);
        Dialog materialDialog = utils.showPageSizeDialog(true);
        materialDialog.setOnDismissListener(dialog -> showSettingsOptions());
    }

    private void setTheme() {
        Dialog dialog = new Dialog(mActivity);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_theme_default_layout);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        RadioGroup radioGroup = dialog.findViewById(R.id.radio_group_themes);

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
                String themeName = radioButton.getText().toString();
                ThemeUtils.getInstance().saveTheme(mActivity, themeName);
                mActivity.recreate();
                dialog.dismiss();
            }
        });
        RadioButton rb = (RadioButton) radioGroup.getChildAt(ThemeUtils.getInstance().getSelectedThemePosition(mActivity));
        rb.setChecked(true);
    }

    private void setShowPageNumber() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        int currChoseId = mSharedPreferences.getInt(Constants.PREF_PAGE_STYLE_ID, -1);

        Dialog dialog = new Dialog(getContext());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.add_pgnum_dialog_layout);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        Button remove = dialog.findViewById(R.id.remove_dialog);
        CheckBox cbDefault = dialog.findViewById(R.id.set_as_default);
        RadioButton rbOpt1 = dialog.findViewById(R.id.page_num_opt1);
        RadioButton rbOpt2 = dialog.findViewById(R.id.page_num_opt2);
        RadioButton rbOpt3 = dialog.findViewById(R.id.page_num_opt3);
        RadioGroup rg = dialog.findViewById(R.id.radioGroup);

        if (currChoseId > 0) {
            cbDefault.setChecked(true);
            rg.clearCheck();
            rg.check(currChoseId);
        }

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rg.clearCheck();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = rg.getCheckedRadioButtonId();
                String style = null;
                if (id == rbOpt1.getId()) {
                    style = Constants.PG_NUM_STYLE_PAGE_X_OF_N;
                } else if (id == rbOpt2.getId()) {
                    style = Constants.PG_NUM_STYLE_X_OF_N;
                } else if (id == rbOpt3.getId()) {
                    style = Constants.PG_NUM_STYLE_X;
                }
                if (cbDefault.isChecked()) {
                    SharedPreferencesUtil.getInstance().setDefaultPageNumStyle(editor, style, id);
                } else {
                    SharedPreferencesUtil.getInstance().clearDefaultPageNumStyle(editor);
                }
                dialog.dismiss();
            }
        });
    }
}
