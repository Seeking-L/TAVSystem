package Mapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginMapper {
    public static Connection conn;

    static {
        try {
            //1、注册驱动
            Class.forName("com.mysql.jdbc.Driver");
            //2.获取连接
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tavsystem?useSSL=false","root","571428");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
