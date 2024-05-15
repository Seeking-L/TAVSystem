package Threads;

import Entity.CommunicationRequest;
import Entity.TextMessageFromClient;
import Entity.User;
import Entity.VideoRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * 此线程用于：用户登录成功后，与朋友联系上之前。即用来不断扫描用户选择好友的输入
 */
public class TcpInputThread implements Runnable{
    private LinkedList<User> communicatingFriend;
    private Scanner scanner;
    private ObjectInputStream tcpIn;
    private ObjectOutputStream tcpOut;
    public LinkedList<User> friends;//friend list

    public TcpInputThread(LinkedList<User> communicatingFriend, Scanner scanner, ObjectInputStream tcpIn, ObjectOutputStream tcpOut, LinkedList<User> friends) {
        this.communicatingFriend=communicatingFriend;
        this.scanner = scanner;
        this.tcpIn = tcpIn;
        this.tcpOut = tcpOut;
        this.friends = friends;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()){
            OUT1:
            try {
                if(scanner.hasNext()){
                    if(communicatingFriend.isEmpty()) {//还未联系上好友
                        if (scanner.hasNextInt()) {
                            int friendId = scanner.nextInt();
                            for (User f : friends
                            ) {
                                if (f.getUserId() == friendId) {//尝试联络好友
                                    tcpOut.writeObject(new CommunicationRequest(friendId));
                                    tcpOut.flush();
                                    System.out.println("正在等待服务器回信，请稍候~");
                                    break OUT1;
                                }
                            }
                            System.out.println("无此ID的好友~");
                        } else {
                            scanner.nextLine();
                            System.out.println("若希望联系好友，请输入好友的ID~");
                        }
                    }else {//已联系上好友
//                        System.out.println(communicatingFriend.get(0));
                        String message=scanner.nextLine();

                        if(message.equals("/bye")){//结束程序
                            System.exit(0);
                        } else if (message.equals("/video")) {//请求开启video
                            VideoRequest videoRequest=new VideoRequest();
                            tcpOut.writeObject(videoRequest);
                            tcpOut.flush();
                            TextMessageFromClient textMessageFromClient = new TextMessageFromClient(
                                    communicatingFriend.get(0).getUserId(), "----对方要求开启video----");
                            tcpOut.writeObject(textMessageFromClient);
                            tcpOut.flush();
                        }else {//文字消息
                            TextMessageFromClient textMessageFromClient = new TextMessageFromClient(communicatingFriend.get(0).getUserId(), message);
                            try {
                                tcpOut.writeObject(textMessageFromClient);
                                tcpOut.flush();
                            } catch (IOException e) {
                                System.out.println("--------------------信息发送失败！--------------------");
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
