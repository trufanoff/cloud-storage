package com.example.cloud.storage.client.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Controller {

    @FXML
    private ListView<String> listViewLocal;

    private ObservableList listLocal = FXCollections.observableArrayList();

    @FXML
    private ListView<String> listViewCloud;

    private ObservableList listCloud = FXCollections.observableArrayList();

    @FXML
    private Button btnSendLocal;

    @FXML
    private Button btnDeleteLocal;

    @FXML
    private Button btnRefreshLocal;

    @FXML
    private Button btnDownloadCloud;

    @FXML
    private Button btnDeleteCloud;

    @FXML
    private Button btnRefreshCloud;

    public void refreshLocal() throws IOException {
        Path localDir = Paths.get("cloud-storage-client","local-storage");
        if(!Files.exists(localDir)){
            return;
        }

        Files.walkFileTree(localDir, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                listLocal.add(file.getFileName());
                return FileVisitResult.CONTINUE;
            }
        });
        listViewLocal.setItems(listLocal);

    }
}
