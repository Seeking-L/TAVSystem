package Threads;

import Entity.LoginResult;
import Entity.TcpLogin;
import Entity.TcpMessage;
import Entity.User;
import Mapper.LoginMapper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

/**
 * server与client联系时使用的线程。当两个用户开启联络，此线程被弃用
 */
public class SingelTcpThread implements Runnable{
    private Socket socket;
    private ObjectInputStream tcpIn;
    private ObjectOutputStream tcpOut;

    public SingelTcpThread(Socket socket) throws IOException {
        this.socket=socket;
        tcpIn=new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        tcpOut=new ObjectOutputStream(socket.getOutputStream());
    }


    @Override
    public void run() {
        while(true){
            try {
                TcpMessage tcpMessage=null;
                if(((tcpMessage=(TcpMessage) tcpIn.readObject())!=null)
                        &&tcpMessage.getFlag()==1){
                    TcpLogin loginMessage=(TcpLogin)tcpMessage;
                    System.out.println(loginMessage);
                    int userId=LoginMapper.login(loginMessage.getUserName(),loginMessage.getPassword());

                    if(userId==0){//login fail
                        tcpOut.writeObject(new LoginResult(false));
                        tcpOut.flush();
                    }
                    else {//login succeeded
                        LinkedList<User> friends=LoginMapper.getFriends(userId);
                        tcpOut.writeObject(new LoginResult(true,userId,friends));
                        tcpOut.flush();
                    }

                }
            } catch (SocketException e){
                e.printStackTrace();
                break;
            }catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
