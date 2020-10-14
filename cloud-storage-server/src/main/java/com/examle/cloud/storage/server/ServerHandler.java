package com.examle.cloud.storage.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    public enum State {
        IDLE, NAME_LENGTH, NAME, FILE_LENGTH, FILE
    }

    private int nextLength;
    private long fileLength;
    //    private long receivedFileLength;
    private State currentState = State.IDLE;
    private Path filePath;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;

        while (buf.readableBytes() > 0) {
            System.out.println("buf count: "+buf.readableBytes());
            if (currentState == State.IDLE) {
                byte readed = buf.readByte();
                System.out.println("Signal byte: "+readed);
                if (readed == (byte) 25) {
                    currentState = State.NAME_LENGTH;
                    System.out.println("STATE: IDLE");
                } else {
                    System.out.println("ERROR: Invalid first byte - " + readed);
                }
            }
            System.out.println("buf count: "+buf.readableBytes());
            if (currentState == State.NAME_LENGTH) {
                if (buf.readableBytes() >= 4) {
                    System.out.println("STATE: NAME LENGTH");
                    nextLength = buf.readInt();
                    currentState = State.NAME;
                }
            }
            System.out.println("buf count: "+buf.readableBytes());
            if (currentState == State.NAME) {
                if (buf.readableBytes() >= nextLength) {
                    byte[] fileName = new byte[nextLength];
                    buf.readBytes(fileName);
                    filePath = Paths.get("cloud-storage-server", "cloud-storage", new String(fileName));
                    System.out.println("STATE: NAME - " + new String(fileName, "UTF-8"));
                    Files.createFile(filePath);
                    currentState = State.FILE_LENGTH;
                }
            }
            System.out.println("buf count: "+buf.readableBytes());
            if (currentState == State.FILE_LENGTH) {
                if (buf.readableBytes() >= 8) {
                    fileLength = buf.readLong();
                    System.out.println("STATE: FILE LENGTH - " + fileLength);
                    currentState = State.FILE;
                }
            }
            System.out.println("buf count: "+buf.readableBytes());
            if (currentState == State.FILE) {
                while (buf.readableBytes() > 0) {
                    byte[] data = new byte[(int) fileLength];
                    buf.readBytes(data);
                    Files.write(filePath, data);
                }
                currentState = State.IDLE;
            }
        }
        if(buf.readableBytes()==0){
            System.out.println("release\n");
            buf.clear();
            buf.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
