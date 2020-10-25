package com.examle.cloud.storage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class Server {
    static final int PORT = 8189;

    public void run() throws Exception {
        //пулы потоков
        //обработка входящих подключений
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //для всей сетевой работы, обработки
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //настройка работы сервера
            ServerBootstrap b = new ServerBootstrap();
            //указание пулов потоков для работы сервера
            b.group(bossGroup, workerGroup)
                    /* чтобы клиенты могли подкоючатся, аналог io ServerSocket
                    указание канала для поключения новых клиентов
                     */
                    .channel(NioServerSocketChannel.class)
                    //настройка конвеера для каждого поключившегося клиента
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(150 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),
                                    new ServerHandler()
                            );
                        }
                    });
            //запуск прослушивания порта для подключения клиентов
            ChannelFuture f = b.bind(PORT).sync();
            System.out.println("Server started");

            //ожидание завершения работы сервера
            f.channel().closeFuture().sync();
        } finally {
            //закрытие пулов потоков
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new Server().run();
    }

}
