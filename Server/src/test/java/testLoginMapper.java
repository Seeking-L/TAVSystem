import Mapper.LoginMapper;
import org.junit.Test;

public class testLoginMapper {
    @Test
    public void testLogin(){
        LoginMapper loginMapper=new LoginMapper();
        System.out.println(loginMapper.login("ljq","123"));
    }
}
