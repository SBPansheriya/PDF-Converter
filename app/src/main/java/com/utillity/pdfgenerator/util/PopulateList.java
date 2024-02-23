package com.utillity.pdfgenerator.util;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.utillity.pdfgenerator.adapter.AdapterViewFiles;
import com.utillity.pdfgenerator.interfaces.EmptyStateChangeListener;
import com.utillity.pdfgenerator.pdfModel.PDFFile;

public class PopulateList extends AsyncTask<Void, Void, Void> {

    private final int mCurrentSortingIndex;
    private final EmptyStateChangeListener mEmptyStateChangeListener;
    private final DirectoryUtils mDirectoryUtils;
    private final AdapterViewFiles mAdapter;
    private final Handler mHandler;
    @Nullable
    private final String mQuery;

    public PopulateList(AdapterViewFiles adapter,
                        EmptyStateChangeListener emptyStateChangeListener,
                        DirectoryUtils directoryUtils, int index, @Nullable String mQuery) {
        this.mAdapter = adapter;
        mCurrentSortingIndex = index;
        mEmptyStateChangeListener = emptyStateChangeListener;
        this.mQuery = mQuery;
        mHandler = new Handler(Looper.getMainLooper());
        mDirectoryUtils = directoryUtils;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        populateListView();
        return null;
    }

    private void populateListView() {
        ArrayList<File> pdfFiles;
        if (TextUtils.isEmpty(mQuery)) {
            pdfFiles = mDirectoryUtils.getPdfFromOtherDirectories();
        } else {
            pdfFiles = mDirectoryUtils.searchPDF(mQuery);
        }
        if (pdfFiles == null)
            mHandler.post(mEmptyStateChangeListener::showNoPermissionsView);
        else if (pdfFiles.size() == 0) {
            mHandler.post(mEmptyStateChangeListener::setEmptyStateVisible);
            mHandler.post(() -> mAdapter.setData(null));
        } else {
            mHandler.post(mEmptyStateChangeListener::setEmptyStateInvisible);
            FileSortUtils.getInstance().performSortOperation(FileSortUtils.getInstance().DATE_INDEX, pdfFiles);
            List<PDFFile> pdfFilesWithEncryptionStatus = getPdfFilesWithEncryptionStatus(pdfFiles);
            mHandler.post(mEmptyStateChangeListener::hideNoPermissionsView);
            mHandler.post(() -> mAdapter.setData(pdfFilesWithEncryptionStatus));
            mHandler.post(mEmptyStateChangeListener::filesPopulated);
        }
    }

    @WorkerThread
    private List<PDFFile> getPdfFilesWithEncryptionStatus(@NonNull List<File> files) {
        List<PDFFile> pdfFiles = new ArrayList<>(files.size());
        for (File file : files) {
            pdfFiles.add(new PDFFile(file, mAdapter.getPDFUtils().isPDFEncrypted(file.getPath())));
        }
        return pdfFiles;
    }
}