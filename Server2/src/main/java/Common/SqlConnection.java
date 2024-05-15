package Common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnection {
    public static Connection conn;

    static {
        try {
            //1、注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //2.获取连接
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tavsystem?useSSL=false",Keys.MYSQLUSER, Keys.MYSQLPASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
