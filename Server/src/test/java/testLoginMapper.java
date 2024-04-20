import Mapper.InfoMapper;
import Mapper.LoginMapper;
import org.junit.Test;

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
}
