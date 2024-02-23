package com.utillity.pdfgenerator.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.utillity.pdfgenerator.CustomAdapter;
import com.google.android.material.snackbar.Snackbar;

import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.balysv.materialripple.MaterialRippleLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.utillity.pdfgenerator.R;

import com.utillity.pdfgenerator.db.DatabaseHelper;
import com.utillity.pdfgenerator.interfaces.DataSetChanged;
import com.utillity.pdfgenerator.interfaces.EmptyStateChangeListener;
import com.utillity.pdfgenerator.interfaces.ItemSelectedListener;
import com.utillity.pdfgenerator.pdfModel.PDFFile;
import com.utillity.pdfgenerator.util.DialogUtils;
import com.utillity.pdfgenerator.util.DirectoryUtils;
import com.utillity.pdfgenerator.util.FileInfoUtils;
import com.utillity.pdfgenerator.util.FileSortUtils;
import com.utillity.pdfgenerator.util.FileUtils;
import com.utillity.pdfgenerator.util.PDFEncryptionUtility;
import com.utillity.pdfgenerator.util.PDFRotationUtils;
import com.utillity.pdfgenerator.util.PDFUtils;
import com.utillity.pdfgenerator.util.PopulateList;
import com.utillity.pdfgenerator.util.StringUtils;
import com.utillity.pdfgenerator.util.WatermarkUtils;

import static com.utillity.pdfgenerator.Constants.SORTING_INDEX;
import static com.utillity.pdfgenerator.util.FileInfoUtils.getFormattedDate;

public class AdapterViewFiles extends RecyclerView.Adapter<AdapterViewFiles.ViewFilesHolder>
        implements DataSetChanged, EmptyStateChangeListener {

    private final Activity mActivity;
    private final EmptyStateChangeListener mEmptyStateChangeListener;
    private final ItemSelectedListener mItemSelectedListener;
    private final ArrayList<Integer> mSelectedFiles;
    private final FileUtils mFileUtils;
    private final PDFUtils mPDFUtils;
    private final PDFRotationUtils mPDFRotationUtils;
    private final WatermarkUtils mWatermarkUtils;
    private final PDFEncryptionUtility mPDFEncryptionUtils;
    private final DatabaseHelper mDatabaseHelper;
    private final SharedPreferences mSharedPreferences;

    private List<PDFFile> mFileList;
    int mCurrentSortingIndex;

    public AdapterViewFiles(Activity activity,
                            List<PDFFile> feedItems,
                            EmptyStateChangeListener emptyStateChangeListener,
                            ItemSelectedListener itemSelectedListener, int mCurrentSortingIndex ){
        this.mActivity = activity;
        this.mEmptyStateChangeListener = emptyStateChangeListener;
        this.mItemSelectedListener = itemSelectedListener;
        this.mFileList = feedItems;
        mSelectedFiles = new ArrayList<>();
        mFileUtils = new FileUtils(activity);
        mPDFUtils = new PDFUtils(activity);
        mPDFRotationUtils = new PDFRotationUtils(activity);
        mPDFEncryptionUtils = new PDFEncryptionUtility(activity);
        this.mCurrentSortingIndex = mCurrentSortingIndex;
        mWatermarkUtils = new WatermarkUtils(activity);
        mDatabaseHelper = new DatabaseHelper(mActivity);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
    }

    @NonNull
    @Override
    public ViewFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new ViewFilesHolder(itemView, mItemSelectedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewFilesHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final PDFFile pdfFile = mFileList.get(position);

        holder.fileName.setText(pdfFile.getPdfFile().getName());
        holder.fileSize.setText(FileInfoUtils.getFormattedSize(pdfFile.getPdfFile()));
        holder.fileDate.setText(getFormattedDate(pdfFile.getPdfFile()));
        holder.checkBox.setChecked(mSelectedFiles.contains(position));
        holder.encryptionImage.setVisibility(pdfFile.isEncrypted() ? View.VISIBLE : View.GONE);
        holder.ripple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(mActivity);
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.setContentView(R.layout.pdf_action_dialog_layout);
                dialog.show();

                ListView itemsListView = dialog.findViewById(R.id.listView);

                String[] items = mActivity.getResources().getStringArray(R.array.items);
                CustomAdapter adapter = new CustomAdapter(mActivity, Arrays.asList(items));
                itemsListView.setAdapter(adapter);


                itemsListView.setOnItemClickListener((parent, view1, position1, id) -> {
                    performOperation(position1, position, pdfFile.getPdfFile());
                    dialog.dismiss();
                });
                notifyDataSetChanged();
            }
        });
    }

    private void performOperation(int index, int position, File file) {
        switch (index) {
            case 0: //Open
                mFileUtils.openFile(file.getPath(), FileUtils.FileType.e_PDF);
                break;

            case 1: //delete
                deleteFile(position);
                break;

            case 2: //rename
                onRenameFileClick(position);
                break;

            case 3: //Print
                mFileUtils.printFile(file);
                break;

            case 4: //Email
                mFileUtils.shareFile(file);
                break;

            case 5: //Details
                mPDFUtils.showDetails(file);
                break;

            case 6://Password Set
                mPDFEncryptionUtils.setPassword(file.getPath(), AdapterViewFiles.this);

                break;

            case 7://Password Remove
                mPDFEncryptionUtils.removePassword(file.getPath(), AdapterViewFiles.this);
                break;

            case 8://Rotate Pages
                mPDFRotationUtils.rotatePages(file.getPath(), AdapterViewFiles.this);
                break;

            case 9: // Add Watermark
                mWatermarkUtils.setWatermark(file.getPath(), AdapterViewFiles.this);
                break;
        }
    }

    public void checkAll() {
        mSelectedFiles.clear();
        for (int i = 0; i < mFileList.size(); i++)
            mSelectedFiles.add(i);
        notifyDataSetChanged();
    }

    public void unCheckAll() {
        mSelectedFiles.clear();
        notifyDataSetChanged();
        updateActionBarTitle();
    }

    private void updateActionBarTitle() {
        mActivity.setTitle(R.string.app_name);
    }

    public ArrayList<String> getSelectedFilePath() {
        ArrayList<String> filePathList = new ArrayList<>();
        for (int position : mSelectedFiles) {
            if (mFileList.size() > position)
                filePathList.add(mFileList.get(position).getPdfFile().getPath());
        }
        return filePathList;
    }

    @Override
    public int getItemCount() {
        return mFileList == null ? 0 : mFileList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<PDFFile> pdfFiles) {
        mFileList = pdfFiles;
        notifyDataSetChanged();
    }

    public PDFUtils getPDFUtils() {
        return mPDFUtils;
    }

    public boolean areItemsSelected() {
        return mSelectedFiles.size() > 0;
    }

    public void deleteFile() {
        deleteFiles(mSelectedFiles);
    }

    private void deleteFile(int position) {
        if (position < 0 || position >= mFileList.size())
            return;

        ArrayList<Integer> files = new ArrayList<>();
        files.add(position);
        deleteFiles(files);
    }

    private void deleteFiles(ArrayList<Integer> files) {

        int messageAlert, messageSnackbar;
        if (files.size() > 1) {
            messageAlert = R.string.delete_alert_selected;
            messageSnackbar = R.string.snackbar_files_deleted;
        } else {
            messageAlert = R.string.delete_alert_singular;
            messageSnackbar = R.string.snackbar_file_deleted;
        }
        Dialog dialog = new Dialog(mActivity);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.delete_dialog_watermark);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> filePath = new ArrayList<>();

                for (int position : files) {
                    if (position >= mFileList.size())
                        continue;
                    filePath.add(mFileList.get(position).getPdfFile().getPath());
                    mFileList.remove(position);
                }

                mSelectedFiles.clear();
                files.clear();
                updateActionBarTitle();
                notifyDataSetChanged();

                if (mFileList.size() == 0)
                    mEmptyStateChangeListener.setEmptyStateVisible();

                AtomicInteger undoClicked = new AtomicInteger();
                StringUtils.getInstance().getSnackbarwithAction(mActivity, messageSnackbar)
                        .setAction(R.string.snackbar_undoAction, v -> {
                            if (mFileList.size() == 0) {
                                mEmptyStateChangeListener.setEmptyStateInvisible();
                            }
                            updateDataset();
                            undoClicked.set(1);
                        }).addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                if (undoClicked.get() == 0) {
                                    for (String path : filePath) {
                                        File fdelete = new File(path);
                                        mDatabaseHelper.insertRecord(fdelete.getAbsolutePath(),
                                                mActivity.getString(R.string.deleted));
                                        if (fdelete.exists() && !fdelete.delete())
                                            StringUtils.getInstance().showSnackbar(mActivity,
                                                    R.string.snackbar_file_not_deleted);
                                    }
                                }
                            }
                        }).show();
                dialog.dismiss();
            }
        });
    }

    /**
     * Opens file sharer for selected files
     */
    public void shareFiles() {
        ArrayList<File> files = new ArrayList<>();
        for (int position : mSelectedFiles) {
            if (mFileList.size() > position)
                files.add(mFileList.get(position).getPdfFile());
        }
        mFileUtils.shareMultipleFiles(files);
    }

    /**
     * Renames the selected file
     *
     * @param position - position of file to be renamed
     */
    private void onRenameFileClick(final int position) {

        Dialog dialog = new Dialog(mActivity);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.create_pdf_dialog);
        dialog.setCancelable(false);
        dialog.show();

        Button cancel = dialog.findViewById(R.id.canceldialog);
        Button ok = dialog.findViewById(R.id.okdialog);
        TextView textView = dialog.findViewById(R.id.txt1);
        EditText editText = dialog.findViewById(R.id.add_pdfName);

        textView.setText("Change file name");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = editText.getText().toString();
                if (input == null || input.trim().isEmpty())
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                else {
                    if (!mFileUtils.isFileExist(input + mActivity.getString(R.string.pdf_ext))) {
                        renameFile(position, input);
                    } else {
                        MaterialDialog.Builder builder = DialogUtils.getInstance().createOverwriteDialog(mActivity);
                        builder.onPositive((dialog2, which) -> renameFile(position, input))
                                .onNegative((dialog1, which) -> onRenameFileClick(position))
                                .show();
                        Dialog dialog = new Dialog(mActivity);
                        if (dialog.getWindow() != null) {
                            dialog.getWindow().setGravity(Gravity.CENTER);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialog.setCancelable(false);
                        }
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        dialog.setContentView(R.layout.name_override_dialog);
                        dialog.setCancelable(false);
                        dialog.show();

                        Button cancel = dialog.findViewById(R.id.canceldialog);
                        Button ok = dialog.findViewById(R.id.okdialog);

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onRenameFileClick(position);
                                dialog.dismiss();
                            }
                        });

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                renameFile(position, input);
                                dialog.dismiss();
                            }
                        });
                    }
                }
                dialog.dismiss();
            }
        });
    }

    private void renameFile(int position, String newName) {
        PDFFile pdfFile = mFileList.get(position);
        File oldfile = pdfFile.getPdfFile();
        String oldPath = oldfile.getPath();
        String newfilename = oldPath.substring(0, oldPath.lastIndexOf('/'))
                + "/" + newName + mActivity.getString(R.string.pdf_ext);
        File newfile = new File(newfilename);
        if (oldfile.renameTo(newfile)) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_file_renamed);
            pdfFile.setPdfFile(newfile);
            notifyDataSetChanged();
            mDatabaseHelper.insertRecord(newfilename, mActivity.getString(R.string.renamed));
        } else
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_file_not_renamed);
    }

    @Override
    public void updateDataset() {
        int index = mSharedPreferences.getInt(SORTING_INDEX, FileSortUtils.getInstance().NAME_INDEX);
        new PopulateList(this, this,
                new DirectoryUtils(mActivity), index, null).execute();
    }

    @Override
    public void setEmptyStateVisible() {

    }

    @Override
    public void setEmptyStateInvisible() {

    }

    @Override
    public void showNoPermissionsView() {

    }

    @Override
    public void hideNoPermissionsView() {

    }

    @Override
    public void filesPopulated() {

    }

    class ViewFilesHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fileRipple)
        MaterialRippleLayout ripple;
        @BindView(R.id.fileName)
        TextView fileName;
        @BindView(R.id.checkbox)
        CheckBox checkBox;
        @BindView(R.id.fileDate)
        TextView fileDate;
        @BindView(R.id.fileSize)
        TextView fileSize;
        @BindView(R.id.encryptionImage)
        ImageView encryptionImage;

        ViewFilesHolder(View itemView, ItemSelectedListener itemSelectedListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!mSelectedFiles.contains(getAdapterPosition())) {
                        mSelectedFiles.add(getAdapterPosition());
                        itemSelectedListener.isSelected(true, mSelectedFiles.size());
                    }
                } else
                    mSelectedFiles.remove(Integer.valueOf(getAdapterPosition()));
                itemSelectedListener.isSelected(false, mSelectedFiles.size());
            });
        }
    }
}