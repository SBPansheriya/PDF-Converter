package com.utillity.pdfgenerator.interfaces;

import java.util.ArrayList;

public interface ExtractImagesListener {
    void extractionStarted();
    void updateView(int imageCount, ArrayList<String> outputFilePaths);
}