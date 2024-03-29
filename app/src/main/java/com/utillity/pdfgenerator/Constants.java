package com.utillity.pdfgenerator;

import android.Manifest;
import android.graphics.Color;

public class Constants {

    public static final String DEFAULT_COMPRESSION = "DefaultCompression";
    public static final String SORTING_INDEX = "SORTING_INDEX";
    public static final String IMAGE_EDITOR_KEY = "first";
    public static final String DEFAULT_FONT_SIZE_TEXT = "DefaultFontSize";
    public static final int DEFAULT_FONT_SIZE = 11;
    public static final String PREVIEW_IMAGES = "preview_images";
    public static final String DATABASE_NAME = "ImagesToPdfDB.db";
    public static final String DEFAULT_FONT_FAMILY_TEXT = "DefaultFontFamily";
    public static final String DEFAULT_FONT_FAMILY = "TIMES_ROMAN";
    public static final String DEFAULT_FONT_COLOR_TEXT = "DefaultFontColor";
    public static final int DEFAULT_FONT_COLOR = -16777216;
    public static final String DEFAULT_PAGE_COLOR_TTP = "DefaultPageColorTTP";
    public static final String DEFAULT_PAGE_COLOR_ITP = "DefaultPageColorITP";
    public static final int DEFAULT_PAGE_COLOR = Color.WHITE;
    public static final String DEFAULT_THEME_TEXT = "DefaultTheme";
    public static final String DEFAULT_THEME = "System";
    public static final String DEFAULT_IMAGE_BORDER_TEXT = "Image_border_text";
    public static final String RESULT = "result";
    public static final String SAME_FILE = "SameFile";
    public static final String DEFAULT_PAGE_SIZE_TEXT = "DefaultPageSize";
    public static final String DEFAULT_PAGE_SIZE = "A4";
    public static final String CHOICE_REMOVE_IMAGE = "CHOICE_REMOVE_IMAGE";
    public static final int DEFAULT_QUALITY_VALUE = 30;
    public static final int DEFAULT_BORDER_WIDTH = 0;
    public static final String STORAGE_LOCATION = "storage_location";
    public static final String DEFAULT_IMAGE_SCALE_TYPE_TEXT = "image_scale_type";
    public static final String IMAGE_SCALE_TYPE_STRETCH = "stretch_image";
    public static final String IMAGE_SCALE_TYPE_ASPECT_RATIO = "maintain_aspect_ratio";
    public static final String PG_NUM_STYLE_PAGE_X_OF_N = "pg_num_style_page_x_of_n";
    public static final String PG_NUM_STYLE_X_OF_N = "pg_num_style_x_of_n";
    public static final String PG_NUM_STYLE_X = "pg_num_style_x";
    public static final String MASTER_PWD_STRING = "master_password";


    public static final String BUNDLE_DATA = "bundle_data";

    public static final String LAUNCH_COUNT = "launch_count";

    public static final String pdfDirectory = "/PDF Converter/";
    public static final String pdfExtension = ".pdf";
    public static final String appName = "PDF Converter";
    public static final String PATH_SEPERATOR = "/";
    public static final String textExtension = ".txt";
    public static final String excelExtension = ".xls";
    public static final String excelWorkbookExtension = ".xlsx";
    public static final String docExtension = ".doc";
    public static final String docxExtension = ".docx";
    public static final String tempDirectory = "temp";

    public static final String AUTHORITY_APP = "com.utillity.pdfgenerator.contentprovider";

    public static final String OPEN_SELECT_IMAGES = "open_select_images";

    public static final String THEME_WHITE = "White";
    public static final String THEME_BLACK = "Black";
    public static final String THEME_DARK = "Dark";
    public static final String THEME_SYSTEM = "System";

    public static final String PREF_PAGE_STYLE = "pref_page_number_style";
    public static final String PREF_PAGE_STYLE_ID = "pref_page_number_style_rb_id";

    public static final int REQUEST_CODE_FOR_READ_PERMISSION = 5;

    public static final String[] READ_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static final int MODIFY_STORAGE_LOCATION_CODE = 1;
    public static final int ROTATE_PAGES = 20;
    public static final int ADD_PASSWORD = 21;
    public static final int REMOVE_PASSWORD = 22;
    public static final int ADD_WATERMARK = 23;
    public static final String RECENT_PREF = "Recent";
}