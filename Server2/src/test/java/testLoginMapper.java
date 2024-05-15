import Entity.User;
import Mapper.InfoMapper;
import Mapper.LoginMapper;
import org.junit.Test;

import java.util.LinkedList;
import java.util.UUID;

public class testLoginMapper {
    @Test
    public void testLogin(){
        System.out.println(LoginMapper.login("ljq","123"));
    }
    @Test
    public void testGetIpAndPort(){
        System.out.println(InfoMapper.getIpAndPort(1).getAddress()+
                ":"+
                InfoMapper.getIpAndPort(1).getPort());
    }
    @Test
    public void testUpdateIPandPort(){
        System.out.println(LoginMapper.updateIpAndPort(1,"122.234.123.222",1234));
        System.out.println(InfoMapper.getIpAndPort(1));
    }
    @Test
    public void testGetFriends(){
        LinkedList<User> friends=LoginMapper.getFriends(1);
        for (User f:friends
             ) {
            System.out.println(f);
        }
    }
}
