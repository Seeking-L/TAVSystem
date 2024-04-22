import Comman.ServerInfo;
import Utils.PortUtils;
import Entity.*;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class Client {
    private static volatile boolean isCommunicating;
    private static ObjectInputStream tcpIn;
    private static ObjectOutputStream tcpOut;
    public static String userName;
    public static String password;
    public static int clientPort;
    public static Socket tcpSocket;
    public static int ID;//this user's ID
    public static LinkedList<User> friends;//friend list

    public static void main(String[] args) {
        isCommunicating=false;
        clientPort = PortUtils.selectServerPort();

        //可进行改进；https://blog.csdn.net/mg2flyingff/article/details/47810265
        //login
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入用户名：");
        userName = scanner.next();
        System.out.println("请输入密码：");
        password = scanner.next();

        try {
            tcpSocket = new Socket(ServerInfo.SERVERIP, ServerInfo.SERVERPORT);
        } catch (IOException e) {
            System.out.println("---------------连接服务器失败！--------------------");
            e.printStackTrace();
            System.exit(1);
        }
        try {
            tcpOut = new ObjectOutputStream(tcpSocket.getOutputStream());
            tcpIn = new ObjectInputStream(new BufferedInputStream(tcpSocket.getInputStream()));

            //TODO 离谱的bug！ https://blog.csdn.net/gary0917/article/details/83655937
//            tcpIn = new ObjectInputStream(new BufferedInputStream(tcpSocket.getInputStream()));
//            tcpOut = new ObjectOutputStream(tcpSocket.getOutputStream());


            tcpOut.writeObject(new TcpLogin(userName, password));//发送登录信息（用户名和密码）
            tcpOut.flush();
            System.out.println("已发送登录请求");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                TcpMessage tcpMessage = null;
                if (((tcpMessage = (TcpMessage) tcpIn.readObject()) != null)
                        && tcpMessage.getFlag() == 2) {
                    LoginResult loginResult = (LoginResult) tcpMessage;
                    if (loginResult.isSuccess()) {//login succeeded
                        friends = loginResult.getFriends();
                        System.out.println("您的好友：");
                        for (User f : friends) {
                            System.out.println(f);
                        }
                        System.out.println("请选择您要发起对话的好友，或等待好友联络~");
                        System.out.println("若要发起对话，请输入对方的ID~");
                        break;
                    }
                    else {
                        System.out.println("-----login failed-----");
                        System.out.println("请重新输入用户名：");
                        userName = scanner.next();
                        System.out.println("请重新输入密码：");
                        password = scanner.next();
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        Thread waitSelectFriend=new Thread(()->{
            OUT1:
            while (!Thread.interrupted()){
                try {
                    if(scanner.hasNext()){
                        if(scanner.hasNextInt()){
                            int friendId=scanner.nextInt();
                            for (User f:friends
                            ) {
                                if(f.getUserId()==friendId){//尝试联络好友
                                    tcpOut.writeObject(new ConnectionRequest(friendId));
                                    tcpOut.flush();
                                    break OUT1;//等待服务器回信的工作交给waitCommunication线程去做（该线程同时监控服务器回信以及朋友发起的联络）
                                }
                            }
                            System.out.println("无此ID的好友~");
                        }
                        else {
                            scanner.next();
                            System.out.println("若希望联系好友，请输入好友的ID~");
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        waitSelectFriend.start();
        Thread waitCommunication=new Thread(()->{
            while(true){
                try {
                    TcpMessage tcpMessage=null;
                    if (((tcpMessage = (TcpMessage) tcpIn.readObject()) != null)){

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        waitCommunication.start();
        waitSelectFriend.interrupt();


    }
}
