package com.example.cloud.storage.client;

import com.example.cloud.storage.common.FileMessage;
import com.example.cloud.storage.common.FilesRequest;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ResourceBundle;


public class Controller implements Initializable {

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
        try {
            refreshLocal();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Client.startConnection();
    }

    public void sendFile() throws IOException {
        String tempFile = listLocal.get(listViewLocal.getSelectionModel().getSelectedIndex()).toString();
        Client.sendFile(tempFile);
    }

    //
    public void refreshCloudFiles(ActionEvent actionEvent) {
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

    public void deleteLocalFile(){
        try {
            Files.delete(Paths.get(LOCAL_STORAGE, listLocal.get(listViewLocal.getSelectionModel().getSelectedIndex()).toString()));
            refreshLocal();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteCloudFile(){
        Client.deleteFile(listCloud.get(listViewCloud.getSelectionModel().getSelectedIndex()).toString());
    }

    public void downloadCloudFile(){
        try {
            Client.downloadFile(listCloud.get(listViewCloud.getSelectionModel().getSelectedIndex()).toString());
            FileMessage fileMessage = (FileMessage)Client.readObject();
            Path path = Paths.get(LOCAL_STORAGE,fileMessage.getFileName());
            Files.createFile(path);
            Files.write(path,fileMessage.getData());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void userAuth(){
        String login = username.getText().trim();
        String pass = password.getText().trim();
        if(!login.isEmpty() && !pass.isEmpty()){
            System.out.println(login+"|"+pass);
            Client.userAuth(login, pass);
        }
    }
}



