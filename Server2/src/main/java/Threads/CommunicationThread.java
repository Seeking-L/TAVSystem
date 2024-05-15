package Threads;

import Entity.*;
import Utils.PortUtils;
import Utils.SocketWorker;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class CommunicationThread implements Runnable {
    private HashMap<Integer, SocketWorker> sockets;

    //User1--------
    private Socket socket1;
    private ObjectInputStream tcpIn1;
    private ObjectOutputStream tcpOut1;
    private int Id1;

    //User2--------
    private Socket socket2;
    private ObjectInputStream tcpIn2;
    private ObjectOutputStream tcpOut2;
    private int Id2;

    //UDP
    private Thread udpThread;
    private int localUdpPort;
    private InetSocketAddress remoteReceiver1;//user1的接收端口
    private InetSocketAddress remoteReceiver2;//user2的接收端口


    public CommunicationThread(HashMap<Integer, SocketWorker> sockets,
                               Socket socket1, ObjectInputStream tcpIn1, ObjectOutputStream tcpOut1, int id1,
                               Socket socket2, ObjectInputStream tcpIn2, ObjectOutputStream tcpOut2, int id2) throws IOException {
        this.sockets = sockets;

        this.socket1 = socket1;
        this.tcpIn1 = tcpIn1;
        this.tcpOut1 = tcpOut1;
        Id1 = id1;

        this.socket2 = socket2;
        this.tcpIn2 = tcpIn2;
        this.tcpOut2 = tcpOut2;
        Id2 = id2;

        System.out.println("---聊天室  " + Id1 + "-" + Id2 + "  开启------");
        //向被发起者发送通知
        CommunicationStartNotify communicationStartNotify = new CommunicationStartNotify(id1);
        this.tcpOut2.writeObject(communicationStartNotify);
        this.tcpOut2.flush();

        udpThread=null;
        remoteReceiver1=(InetSocketAddress) socket1.getRemoteSocketAddress();
        remoteReceiver2=(InetSocketAddress) socket2.getRemoteSocketAddress();
    }

    @Override
    public void run() {
        Thread user1To2 = new Thread(() -> {
            try {
//                System.out.println(tcpIn1.available());
                while (true) {
                    TcpMessage tcpMessage = null;
                    if ((tcpMessage = (TcpMessage) tcpIn1.readObject()) != null) {//user1发来消息

                        if (tcpMessage.getFlag() == 6) {//文字消息
                            TextMessageFromClient textMessageFromClient1 = (TextMessageFromClient) tcpMessage;
                            TextMessageFromServer textMessageFromServer1 = new TextMessageFromServer(
                                    textMessageFromClient1.getDesId(),
                                    textMessageFromClient1.getMessage());
                            tcpOut2.writeObject(textMessageFromServer1);
                            tcpOut2.flush();
                            System.out.println("1to2:" + textMessageFromClient1);
                        }
                        else if (tcpMessage.getFlag()==8){//用户要求发起video
                            if(udpThread!=null) continue;
                            localUdpPort= PortUtils.selectServerPort();

                            //告知两个用户即将开启UDP
                            tcpOut1.writeObject(new VideoStartNotify(localUdpPort));
                            tcpOut2.writeObject(new VideoStartNotify(localUdpPort));
                            tcpOut1.flush();
                            tcpOut2.flush();

                            udpThread=new Thread(new UdpThread(
                                    Id1,
                                    Id2,
                                    localUdpPort,
                                    remoteReceiver1,
                                    remoteReceiver2
                                    ));
                            udpThread.start();
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        user1To2.start();
        Thread user2To1 = new Thread(() -> {
            try {
                Thread.sleep(500);//等待SingelTcpThread结束
//                System.out.println(tcpIn2.available());
                while (true) {
                    TcpMessage tcpMessage = null;
                    if ((tcpMessage = (TcpMessage) tcpIn2.readObject()) != null) {//user2发来消息
                        if (tcpMessage.getFlag() == 6) {
                            TextMessageFromClient textMessageFromClient2 = (TextMessageFromClient) tcpMessage;
                            TextMessageFromServer textMessageFromServer2 = new TextMessageFromServer(
                                    textMessageFromClient2.getDesId(),
                                    textMessageFromClient2.getMessage());
                            tcpOut1.writeObject(textMessageFromServer2);
                            tcpOut1.flush();
                            System.out.println("2to1:" + textMessageFromClient2);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        user2To1.start();
    }
}
