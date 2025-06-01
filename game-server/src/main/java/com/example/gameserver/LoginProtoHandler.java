package com.example.gameserver;

import com.example.gameserver.proto.LoginProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginProtoHandler extends SimpleChannelInboundHandler<LoginProto.LoginRequest> {
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
    protected void channelRead0(ChannelHandlerContext ctx, LoginProto.LoginRequest msg) {
        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean ok = authenticate(username, password);
        LoginProto.LoginResponse response = LoginProto.LoginResponse.newBuilder()
                .setSuccess(ok)
                .setMessage(ok ? "login ok" : "login failed")
                .build();
        ctx.writeAndFlush(response);
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
