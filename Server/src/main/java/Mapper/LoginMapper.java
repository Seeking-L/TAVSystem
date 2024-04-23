package Mapper;

import java.sql.*;
import java.util.LinkedList;

import Common.SqlConnection;
import Entity.User;

/**
 * refer to https://blog.csdn.net/m0_37761437/article/details/110468944
 */

public class LoginMapper {

    /**
     * @param userName user's name
     * @param password user's password
     * @return userId (return 0 if failed)
     */
    public static User login(String userName,String password){
        try{
            Statement statement= SqlConnection.conn.createStatement();
            ResultSet resultSet=statement.executeQuery(
                    "select user_id,user_name,INET_NTOA(user_ip),port" +
                            " from user where user_name='"
                            + userName
                            +"'and password='"+password+"'");
            if(resultSet.next()){
                return new User(
                        resultSet.getString("user_name"),
                        resultSet.getString("INET_NTOA(user_ip)"),
                        resultSet.getInt("port"),
                        resultSet.getInt("user_id")
                );
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * after user login, server update the user's IP and port with its Id.
     * @return true if update successful.
     */
    public static boolean updateIpAndPort(int userId,String IP,int port){
        try {
            Statement statement=SqlConnection.conn.createStatement();
            String stat="update user " +
                    "set user_ip=INET_ATON('"+IP+"')," +
                    "port="+port+
                    " where user_id="+userId;
//            System.out.println(stat); //TODO 调试用
            if(1==statement.executeUpdate(
                    stat
            )){
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * get all the user's friends when it login
     * @param userId
     * @return this user's friends
     */
    public static LinkedList<User> getFriends(int userId) {
        try {
            LinkedList<User> friends=new LinkedList<>();
            Statement statement = SqlConnection.conn.createStatement();
            String s1="select user1_id from friend " +
                    "where user2_id=" + userId;
//            System.out.println(s1);//TODO 调试用
            ResultSet resultSet1 = statement.executeQuery(s1);
            while (resultSet1!=null&&!resultSet1.isClosed()&&resultSet1.next()){
                int friendId=resultSet1.getInt(1);
                ResultSet friendInfo=statement.executeQuery(
                        "select " +
                                "user_name,INET_NTOA(user_ip),port from user " +
                                "where user_id=" + friendId);
                friendInfo.next();
                friends.add(new User(
                        friendInfo.getString("user_name"),
                        friendInfo.getString("INET_NTOA(user_ip)"),
                        friendInfo.getInt("port"),
                        friendId
                ));
            }
                ResultSet resultSet2 = statement.executeQuery(
                        "select user2_id from friend " +
                                "where user1_id=" + userId);
            while (resultSet2!=null&&!resultSet2.isClosed()&&resultSet2.next()){
                int friendId=resultSet2.getInt(1);
                String s2="select " +
                        "user_name,INET_NTOA(user_ip),port from user " +
                        "where user_id=" + friendId;
//                System.out.println(s2);//TODO 调试用
                ResultSet friendInfo=statement.executeQuery(s2);
                friendInfo.next();
                friends.add(new User(
                        friendInfo.getString("user_name"),
                        friendInfo.getString("INET_NTOA(user_ip)"),
                        friendInfo.getInt("port"),
                        friendId
                ));
                return friends;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

}
