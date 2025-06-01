package com.example.gameserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class GameServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        String[] parts = msg.trim().split("\\s+");
        if (parts.length == 3 && "LOGIN".equalsIgnoreCase(parts[0])) {
            String username = parts[1];
            String password = parts[2];
            if (UserDao.authenticate(username, password)) {
                ctx.writeAndFlush("LOGIN SUCCESS\n");
            } else {
                ctx.writeAndFlush("LOGIN FAILED\n");
            }
        } else {
            ctx.writeAndFlush("UNKNOWN COMMAND\n");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
