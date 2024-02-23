package com.utillity.pdfgenerator.interfaces;

import android.graphics.Bitmap;

import java.util.List;

public interface OnPdfReorderedInterface {

    void onPdfReorderStarted();

    void onPdfReorderCompleted(List<Bitmap> bitmaps);

    void onPdfReorderFailed();
}
