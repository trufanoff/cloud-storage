package com.example.cloud.storage.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage{
    private String fileName;
    private byte[] data;

    public FileMessage(Path file) throws IOException {
        this.fileName = file.getFileName().toString();
        this.data = Files.readAllBytes(file);
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }
}
