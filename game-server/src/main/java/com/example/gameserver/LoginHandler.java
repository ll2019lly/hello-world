package com.example.gameserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginHandler extends ChannelInboundHandlerAdapter {
    private Connection connection;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        String url = "jdbc:mysql://localhost:3306/game";
        String user = "gameuser";
        String pass = "gamepassword";
        connection = DriverManager.getConnection(url, user, pass);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        if (message.startsWith("login ")) {
            String[] parts = message.split("\\s+");
            if (parts.length == 3) {
                String username = parts[1];
                String password = parts[2];
                if (authenticate(username, password)) {
                    ctx.writeAndFlush("login ok\n");
                } else {
                    ctx.writeAndFlush("login failed\n");
                }
            } else {
                ctx.writeAndFlush("login failed\n");
            }
        } else {
            ctx.writeAndFlush("unknown command\n");
        }
    }

    private boolean authenticate(String username, String password) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM users WHERE username=? AND password=?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
