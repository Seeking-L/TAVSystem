import Reference.AudioSendThread;
import Reference.TcpSendThread;
import Entity.*;
import org.junit.Test;

public class test1 {
    @Test
    public void test1(){
        TcpLogin tcpLogin=new TcpLogin("1","2");
        TcpMessage tcpMessage=tcpLogin;
//        System.out.println(tcpMessage.getName());
    }
}
