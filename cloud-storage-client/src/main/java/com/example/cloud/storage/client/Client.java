package com.example.cloud.storage.client;

import com.example.cloud.storage.common.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Client {
    private static ObjectEncoderOutputStream out;
    private static ObjectDecoderInputStream in;
    private static Socket socket;

    private static final String LOCAL_STORAGE = "cloud-storage-client/local-storage";
    private static Path path;

    public static void startConnection(){
        try {
            socket = new Socket("localhost",8189);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection(){
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendFile(String fileName){
        path = Paths.get(LOCAL_STORAGE,fileName);
        try {
            out.writeObject(new FileMessage(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object readObject() throws IOException, ClassNotFoundException {
        return in.readObject();
    }

    public static void refreshCloudFiles(){
        try {
            out.writeObject(new FilesRequest());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String fileName){
        try {
            out.writeObject(new DeleteFile(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadFile(String fileName){
        try {
            out.writeObject(new DownloadFile(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void userAuth(String userName, String password){
        try {
            out.writeObject(new AuthRequest(userName, password));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
