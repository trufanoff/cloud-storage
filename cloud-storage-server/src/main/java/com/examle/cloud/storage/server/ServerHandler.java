package com.examle.cloud.storage.server;

import com.example.cloud.storage.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LogManager.getLogger(ServerHandler.class);
    private static final String CLOUD_STORAGE = "cloud-storage-server/cloud-storage/";
    private static String userFolder;
    private Path path;
    private Set<String> filesList;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof AuthRequest) {
            AuthRequest authRequest = (AuthRequest) msg;
            DatabaseRequest.connect();
            if (DatabaseRequest.auth(authRequest.getUserName(), authRequest.getPassword())) {
                userFolder = authRequest.getUserName();
                path = Paths.get(CLOUD_STORAGE, userFolder);
                ctx.writeAndFlush(new AuthResponse(true));
                logger.info(String.format("User %s was authorized", authRequest.getUserName()));
            } else {
                ctx.writeAndFlush(new AuthResponse(false));
            }
        }

        if (msg instanceof FileMessage) {
            FileMessage fileMessage = (FileMessage) msg;
            path = Paths.get(CLOUD_STORAGE, userFolder);
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            path = path.resolve(fileMessage.getFileName());
            Files.createFile(path);
            Files.write(path, fileMessage.getData());
            logger.info(String.format("File %s received", path.getFileName()));
        }

        if (msg instanceof FilesRequest) {
            FilesRequest filesRequest = new FilesRequest();
            filesRequest.setFilesList(getFiles());
            ctx.writeAndFlush(filesRequest);
        }

        if (msg instanceof DeleteFile) {
            DeleteFile file = (DeleteFile) msg;
            Files.delete(Paths.get(CLOUD_STORAGE, userFolder, file.getFileName()));
            logger.info(String.format("File %s deleted from cloud-storage", file.getFileName()));
        }

        if (msg instanceof DownloadFile) {
            DownloadFile file = (DownloadFile) msg;
            path = Paths.get(CLOUD_STORAGE, userFolder, file.getFileName());
            FileMessage fileMessage = new FileMessage(path);
            ctx.writeAndFlush(fileMessage);
            logger.info(String.format("File %s downloaded into local-storage", file.getFileName()));
        }

    }

    public Set<String> getFiles() throws IOException {
        filesList = new HashSet<>();
        Files.walkFileTree(Paths.get(CLOUD_STORAGE, userFolder), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                filesList.add(file.getFileName().toString());
                return FileVisitResult.CONTINUE;
            }
        });
        return filesList;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
