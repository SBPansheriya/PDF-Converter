package com.androidmarket.pdfcreator.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

import androidmarket.R;

import com.androidmarket.pdfcreator.pdfModel.EnhancementOptionsEntity;
import com.androidmarket.pdfcreator.pdfModel.ImageToPDFOptions;

public class ImageEnhancementOptionsUtils {

    public ImageEnhancementOptionsUtils() {
    }

    /**
     * Singleton Implementation
     */
    private static class SingletonHolder {
        private static final ImageEnhancementOptionsUtils INSTANCE = new ImageEnhancementOptionsUtils();
    }

    public static ImageEnhancementOptionsUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public ArrayList<EnhancementOptionsEntity> getEnhancementOptions(Context context,
                                                                     ImageToPDFOptions pdfOptions) {
        ArrayList<EnhancementOptionsEntity> options = new ArrayList<>();
        int passwordIcon = R.drawable.password_protect_pdf;
        if (pdfOptions.isPasswordProtected())
            passwordIcon = R.drawable.baseline_done_24;

        options.add(new EnhancementOptionsEntity(
                context, passwordIcon, R.string.password_protect_pdf_text));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.edit_images, R.string.edit_images_text));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.image_compression,
                String.format(context.getResources().getString(R.string.compress_image),
                        pdfOptions.getQualityString())));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.filter_images, R.string.filter_images_Text));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.set_page_size, R.string.set_page_size_text));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.set_image_scale_type, R.string.image_scale_type));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.perview_pdf, R.string.preview_image_to_pdf));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.border_with,
                String.format(context.getResources().getString(R.string.border_dialog_title),
                        pdfOptions.getBorderWidth())));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.rearrange_image, R.string.rearrange_images));

//        Drawable iconGrayScale = context.getResources().getDrawable(R.drawable.create_grayscale_pdf);
//        iconGrayScale.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.create_grayscale_pdf, R.string.grayscale_images));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.add_margin, R.string.add_margins));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.show_page_numbers, R.string.show_pg_num));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.add_watermark, R.string.add_watermark));

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.page_color, R.string.page_color));

        return options;
    }
}
