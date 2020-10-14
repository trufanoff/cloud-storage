package com.example.cloud.storage.client.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ResourceBundle;

public class Controller implements Initializable {

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

    private ObservableList listLocal = FXCollections.observableArrayList();;

    private ObservableList listCloud = FXCollections.observableArrayList();


    public void refreshLocal() throws IOException {
        Path localDir = Paths.get("cloud-storage-client", "local-storage");
        if (!Files.exists(localDir)) {
            return;
        }
        listLocal.clear();
        Files.walkFileTree(localDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                listLocal.add(file.getFileName());
                return FileVisitResult.CONTINUE;
            }
        });
        listViewLocal.setItems(listLocal);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            refreshLocal();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



