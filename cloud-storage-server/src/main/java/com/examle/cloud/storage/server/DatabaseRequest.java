package com.examle.cloud.storage.server;

import com.example.cloud.storage.common.AuthRequest;
import io.netty.channel.ChannelHandlerContext;

import java.sql.*;

public class DatabaseRequest {

    private static Connection connection;
    private static Statement statement;

    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:storage.db");
        statement = connection.createStatement();
    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static boolean auth(String username, String password){
        String query = "SELECT username FROM users WHERE username = '" + username + "' AND password = '" + password + "' LIMIT 1;";
        try {
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                if(username.equals(resultSet.getString("username"))){
                    return true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

}
