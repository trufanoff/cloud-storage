package com.example.cloud.storage.client;

import com.example.cloud.storage.common.AuthResponse;
import com.example.cloud.storage.common.FileMessage;
import com.example.cloud.storage.common.FilesRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    private Pane topPanel;

    @FXML
    private Pane mainPanel;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private ListView<String> listViewLocal;

    @FXML
    private ListView<String> listViewCloud;

    private ObservableList listLocal = FXCollections.observableArrayList();
    private ObservableList listCloud = FXCollections.observableArrayList();

    public static final String LOCAL_STORAGE = "cloud-storage-client/local-storage";

    public void refreshLocal() throws IOException {
        Path localDir = Paths.get(LOCAL_STORAGE);
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
        mainPanel.setVisible(false);
        Client.startConnection();
    }

    public void sendFile() {
        String tempFile = listLocal.get(listViewLocal.getSelectionModel().getSelectedIndex()).toString();
        Client.sendFile(tempFile);
        refreshCloudFiles();
    }

    public void refreshCloudFiles() {
        listCloud.clear();
        try {
            Client.refreshCloudFiles();
            FilesRequest request = (FilesRequest) Client.readObject();
            listCloud.addAll(request.getFilesList());
            listViewCloud.setItems(listCloud);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteLocalFile() {
        try {
            Files.delete(Paths.get(LOCAL_STORAGE, listLocal.get(listViewLocal.getSelectionModel().getSelectedIndex()).toString()));
            refreshLocal();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteCloudFile() {
        Client.deleteFile(listCloud.get(listViewCloud.getSelectionModel().getSelectedIndex()).toString());
        refreshCloudFiles();
    }

    public void downloadCloudFile() {
        try {
            Client.downloadFile(listCloud.get(listViewCloud.getSelectionModel().getSelectedIndex()).toString());
            FileMessage fileMessage = (FileMessage) Client.readObject();
            Path path = Paths.get(LOCAL_STORAGE, fileMessage.getFileName());
            Files.createFile(path);
            Files.write(path, fileMessage.getData());
            refreshCloudFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userAuth() throws IOException, ClassNotFoundException {
        String login = username.getText().trim();
        String pass = password.getText().trim();
        if (!login.isEmpty() && !pass.isEmpty()) {
            Client.userAuth(login, pass);
            AuthResponse response = (AuthResponse) Client.readObject();
            if (response.isAuth()) {
                mainPanel.setVisible(true);
                topPanel.setVisible(false);
                refreshLocal();
                refreshCloudFiles();
            }
        }

    }
}



