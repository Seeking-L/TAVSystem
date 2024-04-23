package Threads;

import Entity.CommunicationRequest;
import Entity.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * 此线程用于：用户登录成功后，与朋友联系上之前。即用来不断扫描用户选择好友的输入
 */
public class WaitSelectFriend implements Runnable{
    private Scanner scanner;
    private ObjectInputStream tcpIn;
    private ObjectOutputStream tcpOut;
    public LinkedList<User> friends;//friend list

    public WaitSelectFriend(Scanner scanner, ObjectInputStream tcpIn, ObjectOutputStream tcpOut, LinkedList<User> friends) {
        this.scanner = scanner;
        this.tcpIn = tcpIn;
        this.tcpOut = tcpOut;
        this.friends = friends;
    }

    @Override
    public void run() {
        OUT1:
        while (!Thread.interrupted()){
            try {
                if(scanner.hasNext()){
                    if(scanner.hasNextInt()){
                        int friendId=scanner.nextInt();
                        for (User f:friends
                        ) {
                            if(f.getUserId()==friendId){//尝试联络好友
                                tcpOut.writeObject(new CommunicationRequest(friendId));
                                tcpOut.flush();
                                System.out.println("正在等待服务器回信，请稍候~");
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
    }
}
