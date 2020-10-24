package com.example.cloud.storage.common;

import java.util.Set;

public class FilesRequest extends AbstractMessage{

    private Set<String> filesList;

    public Set<String> getFilesList() {
        return filesList;
    }

    public void setFilesList(Set<String> filesList) {
        this.filesList = filesList;
    }


}
