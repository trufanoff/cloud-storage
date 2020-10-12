package com.example.cloud.storage.client.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Controller {

    @FXML
    private Pane topPanel;

    @FXML
    private Button btnSignIn;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @FXML
    private Pane mainPanel;

    @FXML
    private ListView<String> listViewLocal;

    @FXML
    private Button btnSendLocal;

    @FXML
    private Button btnDeleteLocal;

    @FXML
    private Button btnRefreshLocal;

    @FXML
    private ListView<String> listViewCloud;

    @FXML
    private Button btnDownloadCloud;

    @FXML
    private Button btnDeleteCloud;

    @FXML
    private Button btnRefreshCloud;

    private ObservableList listLocal;

    private ObservableList listCloud = FXCollections.observableArrayList();


    public void refreshLocal() throws IOException {
        Path localDir = Paths.get("cloud-storage-client", "local-storage");
        if (!Files.exists(localDir)) {
            return;
        }
        listLocal = FXCollections.observableArrayList();
        Files.walkFileTree(localDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                listLocal.add(file.getFileName());
                return FileVisitResult.CONTINUE;
            }
        });
        listViewLocal.setItems(listLocal);
        topPanel.setVisible(false);
    }
}



