package com.api.MoviePedia.service.impl;

import com.api.MoviePedia.service.FileStorageService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    @Value("${file.upload-dir}")
    private String fileUploadDirectory;

    @Override
    public String saveFile(byte[] fileContents, String fileName, String fileExtension) throws IOException {
        Files.createDirectories(Paths.get(fileUploadDirectory));
        fileName = fileName + "." + fileExtension.replaceAll("\\.", "");
        Path imageFilePath = Paths.get(fileUploadDirectory, fileName);
        OutputStream outputStream = Files.newOutputStream(imageFilePath);
        outputStream.write(fileContents);
        outputStream.close();
        return imageFilePath.toString();
    }

    @Override
    public File retrieveFile(String path) throws IOException {
        if (!Files.exists(Paths.get(path))){
            throw new IOException("File: " + path + " does not exist");
        }
        return new File(path);
    }

    @Override
    public byte[] retrieveFileContents(String path) throws IOException {
        return FileUtils.readFileToByteArray(new File(path));
    }

    @Override
    public String retrieveFileContentsAsBase64String(String path) throws IOException {
        byte[] fileContents = retrieveFileContents(path);
        return Base64.getEncoder().encodeToString(fileContents);
    }

    @Override
    public Boolean deleteFileByName(String fileName, String fileExtension){
        try{
            fileName = fileName + "." + fileExtension.replaceAll("\\.", "");
            Path filePath = Paths.get(fileUploadDirectory, fileName);
            Files.delete(filePath);
            return true;
        } catch (IOException ioException){
            return false;
        }
    }

    @Override
    public void rewriteFileContents(String filePath, byte[] content) throws IOException {
        FileUtils.writeByteArrayToFile(retrieveFile(filePath), content);
    }

    @Override
    public String renameFile(String filePath, String newFileName, String fileExtension) throws IOException {
        newFileName = newFileName + "." + fileExtension.replaceAll("\\.", "");
        File fileWithNewName = new File(fileUploadDirectory, newFileName);
        File fileWithOldName = retrieveFile(filePath);
        fileWithOldName.renameTo(fileWithNewName);
        return fileWithNewName.getPath();
    }

    @Override
    public Boolean deleteFileByPath(String filePath) {
        try{
            Path path = Paths.get(filePath);
            Files.delete(path);
            return true;
        }catch (IOException exception){
            return false;
        }
    }
}
