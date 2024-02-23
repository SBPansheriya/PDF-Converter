package com.utillity.pdfgenerator.util;

import android.os.AsyncTask;

import java.util.ArrayList;

import com.utillity.pdfgenerator.interfaces.BottomSheetPopulate;

class PopulateBottomSheetList extends AsyncTask<Void, Void, ArrayList<String>> {

    private final BottomSheetPopulate mOnLoadListener;
    private final DirectoryUtils mDirectoryUtils;

    PopulateBottomSheetList(BottomSheetPopulate listener,
                                   DirectoryUtils directoryUtils) {
        mOnLoadListener = listener;
        mDirectoryUtils = directoryUtils;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        return mDirectoryUtils.getAllPDFsOnDevice();
    }

    @Override
    protected void onPostExecute(ArrayList<String> paths) {
        super.onPostExecute(paths);
        mOnLoadListener.onPopulate(paths);
    }

}