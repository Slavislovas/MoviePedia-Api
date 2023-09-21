package com.api.MoviePedia.service;

import java.io.File;
import java.io.IOException;

public interface FileStorageService {
    String saveFile(byte[] fileContents, String fileName, String fileExtension) throws IOException;
    File retrieveFile(String path) throws IOException;
    byte[] retrieveFileContents(String path) throws IOException;
    String retrieveFileContentsAsBase64String(String path) throws IOException;
    Boolean deleteFileByName(String fileName, String fileExtension);
    void rewriteFileContents(String filePath, byte[] content) throws IOException;
    String renameFile(String filePath, String newFileName, String fileExtension) throws IOException;
    Boolean deleteFileByPath(String filePath);
}
