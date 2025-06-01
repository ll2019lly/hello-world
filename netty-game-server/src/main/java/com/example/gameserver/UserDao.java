package com.example.gameserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/game_db";
    private static final String JDBC_USER = "game_user";
    private static final String JDBC_PASSWORD = "password";

    public static boolean authenticate(String username, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE username=? AND password=?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
