import Comman.ServerInfo;
import Threads.TcpInputScan;
import Threads.WaitSelectFriend;
import Utils.PortUtils;
import Entity.*;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class Client {
    private static boolean isCommunicating=false;
    private static ObjectInputStream tcpIn;
    private static ObjectOutputStream tcpOut;
    public static String userName;
    public static String password;
    public static int clientPort;
    public static Socket tcpSocket;
    public static int ID;//this user's ID
    private static int communicationDesId;//联络对象的ID
    private static String communicationDesName;//联络对象Name
    public static LinkedList<User> friends;//friend list
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        clientPort = PortUtils.selectServerPort();

        //可进行改进；https://blog.csdn.net/mg2flyingff/article/details/47810265
        //login
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
        
        //开启一个WaitSelectFriend线程，它在用户选择好友时，会不断扫描用户的输入
        Thread waitSelectFriend=new Thread(new WaitSelectFriend(scanner,tcpIn,tcpOut,friends));
        waitSelectFriend.start();
        
        Thread listenTcpMessage=new Thread(()->{
            while(true){
                try {
                    TcpMessage tcpMessage=null;
                    if (((tcpMessage = (TcpMessage) tcpIn.readObject()) != null)){
                        if(tcpMessage.getFlag()==4){//server回复client：该用户试图联系的好友是否available
                            CommunicationRequestReflect communicationRequestReflect=(CommunicationRequestReflect)tcpMessage;
                            if(!communicationRequestReflect.isFriendAvailable()){//好友 is not available，回到选择好友与等待被联络状态
                                System.out.println("这个好友不在线或正在与他人联络，请稍后再拨~");

                                //waitSelectFriend线程在选择一次好友之后会自动结束。故需要重新开启一个waitSelectFriend1
                                Thread waitSelectFriend1=new Thread(new WaitSelectFriend(scanner,tcpIn,tcpOut,friends));
                                waitSelectFriend1.start();
                            }else {//好友 is available,准备开启联络
                                if(isCommunicating) continue;//说明已经已经处在了通信状态

                                System.out.println("联络可用，正在准备开启联络~~");
                                isCommunicating=true;

                                //设置联系对象的ID和Name
                                communicationDesId=communicationRequestReflect.getFriendId();
                                for (User f:friends
                                     ) {
                                    if(f.getUserId()==communicationDesId) communicationDesName=f.getUserName();
                                }

                                scanner=null;
                                waitSelectFriend.interrupt();
                                Thread communicationInput=new Thread(new TcpInputScan(
                                        tcpOut,communicationDesId,communicationDesName));
                                communicationInput.start();
                            }
                        } else if (tcpMessage.getFlag()==5) {//有好友主动联系
                            if(isCommunicating) continue;//说明已经已经处于通信状态
                            isCommunicating=true;

                            scanner=null;
                            waitSelectFriend.interrupt();

                            //CommunicationStartNotify
                            CommunicationStartNotify communicationStartNotify=(CommunicationStartNotify)tcpMessage;

                            //设置联系对象的ID和Name
                            communicationDesId=communicationStartNotify.getFriendId();
                            for (User f:friends
                            ) {
                                if(f.getUserId()==communicationDesId) communicationDesName=f.getUserName();
                            }

                            System.out.println("您的好友 "+communicationDesName+" 发起了联络~~");
                            System.out.println("----------------------与 "+communicationDesName+" 的聊天室--------------------");

                            Thread communicationInput=new Thread(new TcpInputScan(
                                    tcpOut,communicationDesId,communicationDesName));
                            communicationInput.start();

                        } else if (tcpMessage.getFlag()==7) {
                            //TextMessage
                            TextMessageFromServer textMessageFromServer=(TextMessageFromServer) tcpMessage;
                            System.out.println("##### "+communicationDesName+":   "+textMessageFromServer.getMessage());
                            System.out.println();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
        listenTcpMessage.start();
    }
}
