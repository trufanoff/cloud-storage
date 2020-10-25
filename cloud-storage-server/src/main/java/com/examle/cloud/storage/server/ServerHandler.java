package com.examle.cloud.storage.server;

import com.example.cloud.storage.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class ServerHandler extends ChannelInboundHandlerAdapter {


    private static final String CLOUD_STORAGE = "cloud-storage-server/cloud-storage/";
    private static String userFolder;
    private Path path;
    private Set<String> filesList;
    private boolean isAuth = false;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof AuthRequest) {
            AuthRequest authRequest = (AuthRequest) msg;
            DatabaseRequest.connect();
            if (DatabaseRequest.auth(authRequest.getUserName(), authRequest.getPassword())) {
                userFolder = authRequest.getUserName();
                path = Paths.get(CLOUD_STORAGE, userFolder);
                ctx.writeAndFlush(new AuthResponse(true));
            } else {
                ctx.writeAndFlush(new AuthResponse(false));
            }
        }

        if (msg instanceof FileMessage) {
            FileMessage fileMessage = (FileMessage) msg;
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            path = path.resolve(fileMessage.getFileName());
            Files.createFile(path);
            Files.write(path, fileMessage.getData());
        }

        if (msg instanceof FilesRequest) {
            FilesRequest filesRequest = new FilesRequest();
            filesRequest.setFilesList(getFiles());
            ctx.writeAndFlush(filesRequest);
        }

        if (msg instanceof DeleteFile) {
            DeleteFile file = (DeleteFile) msg;
            path = path.resolve(file.getFileName());
            Files.delete(path);
        }

        if (msg instanceof DownloadFile) {
            DownloadFile file = (DownloadFile) msg;
            path = path.resolve(file.getFileName());
            FileMessage fileMessage = new FileMessage(path);
            ctx.writeAndFlush(fileMessage);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();

    }

    public Set<String> getFiles() throws IOException {
        filesList = new HashSet<>();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                filesList.add(file.getFileName().toString());
                return FileVisitResult.CONTINUE;
            }
        });
        return filesList;
    }
}
