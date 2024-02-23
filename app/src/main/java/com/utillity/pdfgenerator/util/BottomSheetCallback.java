package com.utillity.pdfgenerator.util;

import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import android.view.View;
import android.widget.ImageView;


public class BottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {

    private final ImageView mUpArrow;
    private final boolean mIsAdded;

    public BottomSheetCallback(ImageView mUpArrow, boolean mIsFragmentAdded) {
        this.mUpArrow = mUpArrow;
        this.mIsAdded = mIsFragmentAdded;
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        if (mIsAdded) {
            animateBottomSheetArrow(slideOffset);
        }
    }
    private void animateBottomSheetArrow(float slideOffset) {
        if (slideOffset >= 0 && slideOffset <= 1) {
            mUpArrow.setRotation(slideOffset * -180);
        } else if (slideOffset >= -1 && slideOffset < 0) {
            mUpArrow.setRotation(slideOffset * 180);
        }
    }
}