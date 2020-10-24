package com.examle.cloud.storage.server;

import com.example.cloud.storage.common.AuthRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.sql.*;

public class AuthHandler extends ChannelInboundHandlerAdapter {

    private Connection connection;
    private Statement statement;
    private static String userName;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        AuthRequest authRequest = (AuthRequest) msg;
        String query = "SELECT username FROM users WHERE username = '" + authRequest.getUserName() + "' AND password = '" + authRequest.getPassword() + "';";
        System.out.println(query);
        try{
            connect();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                userName = resultSet.getString("username");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            disconnect();
        }
        System.out.println(userName);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void connect() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:storage.db");
        statement = connection.createStatement();
    }

    private void disconnect() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
