package com.utillity.pdfgenerator.util;

import android.app.Activity;

import com.utillity.pdfgenerator.adapter.AdapterMergeFiles;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import com.airbnb.lottie.LottieAnimationView;

import com.utillity.pdfgenerator.pdfModel.HomePageItem;

import java.util.ArrayList;
import java.util.Map;


public class CommonCodeUtils {

    Map<Integer, HomePageItem> mFragmentPositionMap;

    public void populateUtil(Activity mActivity, ArrayList<String> paths,
                             AdapterMergeFiles.OnClickListener onClickListener,
                             RelativeLayout layout, LottieAnimationView animationView,
                             RecyclerView recyclerView) {

        if (paths == null || paths.size() == 0) {
            layout.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            AdapterMergeFiles adapterMergeFiles = new AdapterMergeFiles(mActivity,
                    paths, false, onClickListener);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapterMergeFiles);
            recyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
        }
        animationView.setVisibility(View.GONE);
    }

    private static class SingletonHolder {
        static final CommonCodeUtils INSTANCE = new CommonCodeUtils();
    }

    public static CommonCodeUtils getInstance() {
        return CommonCodeUtils.SingletonHolder.INSTANCE;
    }
}
