package com.usyd.backend.utils;

public class FilenameUtils {

    public static String generateUniqueFilename(String originalFilename) {
        String baseName = FilenameUtils.getBaseName(originalFilename);
        String extension = FilenameUtils.getExtension(originalFilename);
        String uniqueName = baseName + "_" + System.nanoTime();
        return uniqueName + "." + extension;
    }
    private static String getBaseName(String filename) {
        if (filename == null) {
            return null;
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return filename;
        }
        return filename.substring(0, lastDotIndex);
    }

    private static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
