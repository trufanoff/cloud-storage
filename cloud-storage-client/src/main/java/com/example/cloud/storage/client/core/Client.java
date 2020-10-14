package com.example.cloud.storage.client.core;

import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {
    public static void main(String[] args) {
        Path file = Paths.get("cloud-storage-client","local-storage","file1.txt");
        try (Socket socket = new Socket("localhost", 8080)) {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            //сигнальный байт
            out.write(25);

            //name length
            byte[] filenameBytes = file.getFileName().toString().getBytes();
            out.writeInt(filenameBytes.length);

            //name
            out.write(filenameBytes);

            //file length
            out.writeLong(Files.size(file));

            //file
            byte[] fileData = Files.readAllBytes(file);
            out.write(fileData);

            //close
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

