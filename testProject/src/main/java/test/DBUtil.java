package test;

import java.sql.*;

/**
 * @author yiting wang
 * @date 7/30/18 11:28
 * @email wang.yitin@husky.neu.edu
 */

public class DBUtil {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/vehicle_hx?characterEncoding=utf-8";
    private static final String USER = "root";
    private static final String PASSWORD = "WYTfwgz520";

    private static Connection conn = null;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static Connection getConnection() {
        return conn;
    }

    public static void main(String arg[]) throws Exception {

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select photoId from web_api_1-60");
        while (rs.next()) {
            System.out.println(rs.getString("photoId"));
        }
    }

}

