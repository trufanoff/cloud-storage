package com.examle.cloud.storage.server;

import com.example.cloud.storage.common.DeleteFile;
import com.example.cloud.storage.common.DownloadFile;
import com.example.cloud.storage.common.FileMessage;
import com.example.cloud.storage.common.FilesRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class ServerHandler extends ChannelInboundHandlerAdapter {


    public static final String CLOUD_STORAGE = "cloud-storage-server/cloud-storage";
    private Path path;
    private Set<String> filesList;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FileMessage){
            FileMessage fileMessage = (FileMessage)msg;
            path = Paths.get(CLOUD_STORAGE,fileMessage.getFileName());
            Files.createFile(path);
            Files.write(path,fileMessage.getData());
        }

        if(msg instanceof FilesRequest){
            FilesRequest filesRequest = new FilesRequest();
            filesRequest.setFilesList(getFiles());
            ctx.writeAndFlush(filesRequest);
        }

        if(msg instanceof DeleteFile){
            DeleteFile file = (DeleteFile) msg;
            Files.delete(Paths.get(CLOUD_STORAGE,file.getFileName()));
        }

        if(msg instanceof DownloadFile){
            DownloadFile file = (DownloadFile) msg;
            FileMessage fileMessage = new FileMessage(Paths.get(CLOUD_STORAGE, file.getFileName()));
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
        Files.walkFileTree(Paths.get(CLOUD_STORAGE), new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                filesList.add(file.getFileName().toString());
                return FileVisitResult.CONTINUE;
            }
        });
        return filesList;
    }
}
