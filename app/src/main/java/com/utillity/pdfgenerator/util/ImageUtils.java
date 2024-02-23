package com.utillity.pdfgenerator.util;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Environment;
import android.preference.PreferenceManager;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.utillity.pdfgenerator.Constants;
import com.itextpdf.text.Rectangle;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.FileOutputStream;

import com.utillity.pdfgenerator.R;

import static com.utillity.pdfgenerator.Constants.AUTHORITY_APP;
import static com.utillity.pdfgenerator.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static com.utillity.pdfgenerator.Constants.IMAGE_SCALE_TYPE_STRETCH;
import static com.utillity.pdfgenerator.Constants.pdfDirectory;
import static com.utillity.pdfgenerator.fragment.ImageToPdfFragment.lastSelected;

public class ImageUtils {

    public String mImageScaleType;
    int position;
    String imageScale;

    private static class SingletonHolder {
        static final ImageUtils INSTANCE = new ImageUtils();
    }

    public static ImageUtils getInstance() {
        return ImageUtils.SingletonHolder.INSTANCE;
    }

    static Rectangle calculateFitSize(float originalWidth, float originalHeight, Rectangle documentSize) {
        float widthChange = (originalWidth - documentSize.getWidth()) / originalWidth;
        float heightChange = (originalHeight - documentSize.getHeight()) / originalHeight;

        float changeFactor = Math.max(widthChange, heightChange);
        float newWidth = originalWidth - (originalWidth * changeFactor);
        float newHeight = originalHeight - (originalHeight * changeFactor);

        return new Rectangle(Math.abs((int) newWidth), Math.abs((int) newHeight));
    }

    public Bitmap getRoundBitmap(Bitmap bmp) {
        int width = bmp.getWidth(), height = bmp.getHeight();
        int radius = Math.min(width, height); // set the smallest edge as radius.
        Bitmap bitmap;

        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            bitmap = Bitmap.createScaledBitmap(bmp,
                    (int) (bmp.getWidth() / 1.0f),
                    (int) (bmp.getHeight() / 1.0f), false);
        } else {
            bitmap = bmp;
        }

        Bitmap output = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(radius / 2f + 0.7f, radius / 2f + 0.7f,
                radius / 2f + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public Bitmap getRoundBitmapFromPath(String path) {
        File file = new File(path);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

        bmOptions.inSampleSize = calculateInSampleSize(bmOptions);

        bmOptions.inJustDecodeBounds = false;
        Bitmap smallBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        if (smallBitmap == null) return null;

        return ImageUtils.getInstance().getRoundBitmap(smallBitmap);
    }

    private int calculateInSampleSize(BitmapFactory.Options options) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > 500 || width > 500) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= 500
                    && (halfWidth / inSampleSize) >= 500) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public void showImageScaleTypeDialog(Context context, Boolean saveValue,String click) {

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Dialog dialog1 = new Dialog(context);
        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setGravity(Gravity.CENTER);
            dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog1.setCancelable(false);
        }
        dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog1.setContentView(R.layout.set_image_scale_type_dialog);
        dialog1.setCancelable(false);
        dialog1.show();

        String savedImageScaleType = mSharedPreferences.getString(Constants.DEFAULT_IMAGE_SCALE_TYPE_TEXT,"");

        if (savedImageScaleType.equals("stretch_image")){
            position = 1;
        }
        else if (savedImageScaleType.equals("maintain_aspect_ratio")){
            position = 0;
        }
        Button cancel = dialog1.findViewById(R.id.canceldialog);
        Button ok = dialog1.findViewById(R.id.okdialog);
        RadioGroup radioGroup = dialog1.findViewById(R.id.scale_type);
        CheckBox mSetAsDefault = dialog1.findViewById(R.id.cbSetDefault);

        if (click.equals("setting")) {
            if (savedImageScaleType.equals(IMAGE_SCALE_TYPE_ASPECT_RATIO)) {
                radioGroup.check(R.id.aspect_ratio);
            } else if (savedImageScaleType.equals(IMAGE_SCALE_TYPE_STRETCH)) {
                radioGroup.check(R.id.stretch_image);
            }
        }

        if (click.equals("ImageToPdf")) {
            if (lastSelected != -1) {
                radioGroup.check(radioGroup.getChildAt(lastSelected).getId());
            } else {
                radioGroup.check(radioGroup.getChildAt(position).getId());
            }
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = dialog1.findViewById(selectedId);
                imageScale = radioButton.getText().toString();
                if (selectedId == R.id.aspect_ratio) {
                    lastSelected = 0;
                    mImageScaleType = IMAGE_SCALE_TYPE_ASPECT_RATIO;
                } else if (selectedId == R.id.stretch_image) {
                    lastSelected = 1;
                    mImageScaleType = IMAGE_SCALE_TYPE_STRETCH;
                }

                if (saveValue || mSetAsDefault.isChecked()) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(Constants.DEFAULT_IMAGE_SCALE_TYPE_TEXT, mImageScaleType);
                    editor.apply();
                }
                dialog1.dismiss();
            }
        });
        if (saveValue) {
            dialog1.findViewById(R.id.cbSetDefault).setVisibility(View.GONE);
        }
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, bmpOriginal.getConfig());
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static String saveImage(String filename, Bitmap finalBitmap) {

        if (finalBitmap == null || checkIfBitmapIsWhite(finalBitmap))
            return null;

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + pdfDirectory);
        String fileName = filename + ".png";

        File file = new File(myDir, fileName);
        if (file.exists())
            file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.v("saving", fileName);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return myDir + "/" + fileName;
    }

    public static void selectImages(Fragment frag, int requestCode) {
        Matisse.from(frag)
                .choose(MimeType.ofImage(), false)
                .countable(true)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, AUTHORITY_APP))
                .maxSelectable(1000)
                .imageEngine(new PicassoEngine())
                .forResult(requestCode);
    }

    private static boolean checkIfBitmapIsWhite(Bitmap bitmap) {
        if (bitmap == null)
            return true;
        Bitmap whiteBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(whiteBitmap);
        canvas.drawColor(Color.WHITE);
        return bitmap.sameAs(whiteBitmap);
    }
}
