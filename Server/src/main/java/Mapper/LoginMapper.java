package Mapper;

import java.sql.*;

import Common.Keys;
import com.mysql.cj.jdbc.Driver;
import com.mysql.jdbc.*;

/**
 * refer to https://blog.csdn.net/m0_37761437/article/details/110468944    ——————
 */

public class LoginMapper {
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


    /**
     * @param userName user's name
     * @param password user's password
     * @return userId
     */
    public int login(String userName,String password){
        try{
            Statement statement=conn.createStatement();
            ResultSet resultSet=statement.executeQuery(
                    "select user_id from user where user_name='"
                            + userName
                            +"'and password='"+password+"'");
            if(resultSet.next()){
                return resultSet.getInt("user_Id");
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
