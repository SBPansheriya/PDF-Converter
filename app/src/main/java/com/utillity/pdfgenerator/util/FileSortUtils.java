package com.utillity.pdfgenerator.util;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class FileSortUtils {

    public final int NAME_INDEX = 0;
    public final int DATE_INDEX = 1;
    public final int SIZE_INCREASING_ORDER_INDEX = 2;
    public final int SIZE_DECREASING_ORDER_INDEX = 3;

    private FileSortUtils(){}

    public void performSortOperation(int option, List<File> pdf) {
        switch (option) {
            case DATE_INDEX:
                sortFilesByDateNewestToOldest(pdf);
                break;
            case NAME_INDEX:
                sortByNameAlphabetical(pdf);
                break;
            case SIZE_INCREASING_ORDER_INDEX:
                sortFilesBySizeIncreasingOrder(pdf);
                break;
            case SIZE_DECREASING_ORDER_INDEX:
                sortFilesBySizeDecreasingOrder(pdf);
                break;
        }
    }

    private void sortByNameAlphabetical(List<File> filesList) {
        Collections.sort(filesList);
    }

    private void sortFilesByDateNewestToOldest(List<File> filesList) {
        Collections.sort(filesList, (file, file2) -> Long.compare(file2.lastModified(), file.lastModified()));
    }

    private void sortFilesBySizeIncreasingOrder(List<File> filesList) {
        Collections.sort(filesList, (file1, file2) -> Long.compare(file1.length(), file2.length()));
    }

    private void sortFilesBySizeDecreasingOrder(List<File> filesList) {
        Collections.sort(filesList, (file1, file2) -> Long.compare(file2.length(), file1.length()));
    }

    private static class SingletonHolder {
        static final FileSortUtils INSTANCE = new FileSortUtils();
    }

    public static FileSortUtils getInstance() {
        return FileSortUtils.SingletonHolder.INSTANCE;
    }
}
