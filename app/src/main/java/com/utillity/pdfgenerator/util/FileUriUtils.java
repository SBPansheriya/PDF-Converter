package com.utillity.pdfgenerator.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class FileUriUtils {

    private final String mEXTERNALSTORAGEDOC = "com.android.externalstorage.documents";
    private final String mISDOWNLOADDOC = "com.android.providers.downloads.documents";
    private final String mISMEDIADOC = "com.android.providers.media.documents";
    private final String mISGOOGLEPHOTODOC = "com.google.android.apps.photos.content";

    private FileUriUtils(){}

    private static class SingletonHolder {
        static final FileUriUtils INSTANCE = new FileUriUtils();
    }

    public static FileUriUtils getInstance() {
        return FileUriUtils.SingletonHolder.INSTANCE;
    }

    boolean isWhatsappImage(String uriAuthority) {
        return "com.whatsapp.provider.media".equals(uriAuthority);
    }

    private boolean checkURIAuthority(Uri uri, String toCheckWith) {
        return toCheckWith.equals(uri.getAuthority());
    }

    private boolean checkURI(Uri uri, String toCheckWith) {
        return uri != null && uri.getScheme() != null
                && uri.getScheme().equalsIgnoreCase(toCheckWith);
    }

    private boolean isDocumentUri(Context mContext, Uri uri) {
        boolean ret = false;
        if (mContext != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(mContext, uri);
        }
        return ret;
    }

    private String getURIForMediaDoc(ContentResolver mContentResolver, Uri uri) {
        String documentId = DocumentsContract.getDocumentId(uri);
        String[] idArr = documentId.split(":");
        if (idArr.length == 2) {
            String docType = idArr[0];
            String realDocId = idArr[1];

            Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            switch (docType) {
                case "image":
                    mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    break;
                case "video":
                    mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    break;
                case "audio":
                    mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    break;
            }
            String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

            return getImageRealPath(mContentResolver, mediaContentUri, whereClause);
        }
        return null;
    }

    private String getURIForDownloadDoc(ContentResolver mContentResolver, Uri uri) {
        String documentId = DocumentsContract.getDocumentId(uri);
        Uri downloadUri = Uri.parse("content://downloads/public_downloads");
        Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.parseLong(documentId));
        return getImageRealPath(mContentResolver, downloadUriAppendId, null);
    }

    private String getURIForExternalstorageDoc(Uri uri) {
        String documentId = DocumentsContract.getDocumentId(uri);
        String[] idArr = documentId.split(":");
        if (idArr.length == 2) {
            String type = idArr[0];
            String realDocId = idArr[1];
            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/" + realDocId;
            }
        }
        return null;
    }

    private String getUriForDocumentUri(ContentResolver mContentResolver, Uri uri) {
        if (checkURIAuthority(uri, mISMEDIADOC)) {
            return getURIForMediaDoc(mContentResolver, uri);
        } else if (checkURIAuthority(uri, mISDOWNLOADDOC)) {
            return getURIForDownloadDoc(mContentResolver, uri);
        } else if (checkURIAuthority(uri, mEXTERNALSTORAGEDOC)) {
            return getURIForExternalstorageDoc(uri);
        }
        return null;
    }

    String getUriRealPathAboveKitkat(Context mContext, Uri uri) {

        if (uri == null)
            return null;

        ContentResolver mContentResolver = mContext.getContentResolver();

        if (checkURI(uri, "content"))
            if (checkURIAuthority(uri, mISGOOGLEPHOTODOC))
                return uri.getLastPathSegment();
            else
                return getImageRealPath(mContentResolver, uri, null);

        if (checkURI(uri, "file"))
            return uri.getPath();

        if (isDocumentUri(mContext, uri))
            return getUriForDocumentUri(mContentResolver, uri);

        return null;
    }

    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause) {
        String ret = "";
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if (cursor != null) {
            boolean moveToFirst = cursor.moveToFirst();
            if (moveToFirst) {
                String columnName = MediaStore.Images.Media.DATA;

                int imageColumnIndex = cursor.getColumnIndex(columnName);
                if (imageColumnIndex == -1)
                    return ret;

                ret = cursor.getString(imageColumnIndex);
                cursor.close();
            }
        }
        return ret;
    }
}
