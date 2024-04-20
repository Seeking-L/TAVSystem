package Mapper;

import java.sql.*;
import Common.SqlConnection;

/**
 * refer to https://blog.csdn.net/m0_37761437/article/details/110468944
 */

public class LoginMapper {

    /**
     * @param userName user's name
     * @param password user's password
     * @return userId
     */
    public static int login(String userName,String password){
        try{
            Statement statement= SqlConnection.conn.createStatement();
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

    /**
     * after user login, server update the user's IP and port with its Id.
     * @return true if update successful.
     */
    public static boolean updateIpAndPort(int userId,String IP,int port){
        try {
            Statement statement=SqlConnection.conn.createStatement();
            if(1==statement.executeUpdate(
                    "update user " +
                            "set user_ip=INET_ATON('"+IP+"')," +
                            "port="+port+
                            "where user_id="+userId
            )){
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
