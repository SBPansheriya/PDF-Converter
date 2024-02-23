package com.utillity.pdfgenerator.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.utillity.pdfgenerator.Constants;
import com.itextpdf.text.Font;

import java.util.ArrayList;

import com.utillity.pdfgenerator.R;
import com.utillity.pdfgenerator.pdfModel.EnhancementOptionsEntity;

import static com.utillity.pdfgenerator.Constants.DEFAULT_COMPRESSION;

public class SettingsOptions {

    public static ArrayList<EnhancementOptionsEntity> getEnhancementOptions(Context context) {
        ArrayList<EnhancementOptionsEntity> options = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.image_compression,
                String.format(context.getString(R.string.image_compression_value_default),
                        sharedPreferences.getInt(DEFAULT_COMPRESSION, 30))));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.set_page_size,
                String.format(context.getString(R.string.page_size_value_def),
                        sharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT,
                                Constants.DEFAULT_PAGE_SIZE))));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.font_size,
                String.format(context.getString(R.string.font_size_value_def),
                        sharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT,
                                Constants.DEFAULT_FONT_SIZE))));

        Font.FontFamily fontFamily = Font.FontFamily.valueOf(
                sharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                        Constants.DEFAULT_FONT_FAMILY));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.font_family,
                String.format(context.getString(R.string.font_family_value_def),
                        fontFamily.name())));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.theme_default_value,
                String.format(context.getString(R.string.theme_value_def),
                        sharedPreferences.getString(Constants.DEFAULT_THEME_TEXT,
                                Constants.DEFAULT_THEME))));

        options.add(new EnhancementOptionsEntity(context,
                R.drawable.set_image_scale_type, R.string.image_scale_type));

        options.add(new EnhancementOptionsEntity(context,
                R.drawable.password_protect_pdf, R.string.change_master_pwd));

        options.add(new EnhancementOptionsEntity(context,
                R.drawable.show_page_numbers, R.string.show_pg_num));

        return options;
    }

}
