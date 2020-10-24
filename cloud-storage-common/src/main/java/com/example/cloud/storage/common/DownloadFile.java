package com.example.cloud.storage.common;

public class DownloadFile extends AbstractMessage{
    private String fileName;

    public DownloadFile(String fileName){
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
