package Mapper;

import Common.SqlConnection;

import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class InfoMapper {
    public static InetSocketAddress getIpAndPort(int userId){
        try{
            Statement statement= SqlConnection.conn.createStatement();
            ResultSet resultSet=statement.executeQuery(
                    "select INET_NTOA(user_ip),port" +
                            " from user where user_id="+userId);
            if(resultSet.next()){
                String IP=resultSet.getString("INET_NTOA(user_ip)");
                int port=resultSet.getInt("port");
                return new InetSocketAddress(IP,port);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
