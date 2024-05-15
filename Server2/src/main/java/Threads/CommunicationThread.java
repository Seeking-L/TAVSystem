package Threads;

import Entity.CommunicationStartNotify;
import Entity.TcpMessage;
import Entity.TextMessageFromClient;
import Entity.TextMessageFromServer;
import Utils.SocketWorker;

import java.io.*;
import java.net.Socket;
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

    public CommunicationThread(HashMap<Integer, SocketWorker> sockets,
                               Socket socket1, ObjectInputStream tcpIn1, ObjectOutputStream tcpOut1, int id1,
                               SocketWorker socket2, int id2) throws IOException {
        this.sockets = sockets;

        this.socket1 = socket1;
        this.tcpIn1 = tcpIn1;
        this.tcpOut1 = tcpOut1;
        Id1 = id1;

        this.socket2 = socket2.getSocket();
        this.tcpIn2 = socket2.getTcpIn();
        this.tcpOut2 = socket2.getTcpOut();
        Id2 = id2;

        System.out.println("---聊天室  " + Id1 + "-" + Id2 + "  开启------");
        //向被发起者发送通知
        CommunicationStartNotify communicationStartNotify = new CommunicationStartNotify(id1);
        this.tcpOut2.writeObject(communicationStartNotify);
        this.tcpOut2.flush();
    }

    @Override
    public void run() {
        Thread user1To2 = new Thread(() -> {
            try {
                while (true) {
                    TcpMessage tcpMessage = null;
                    if ((tcpMessage = (TcpMessage) tcpIn1.readObject()) != null) {//user1发来消息
                        if (tcpMessage.getFlag() == 6) {
                            TextMessageFromClient textMessageFromClient1 = (TextMessageFromClient) tcpMessage;
                            TextMessageFromServer textMessageFromServer1 = new TextMessageFromServer(
                                    textMessageFromClient1.getDesId(),
                                    textMessageFromClient1.getMessage());
                            tcpOut2.writeObject(textMessageFromServer1);
                            tcpOut2.flush();
                            System.out.println("1to2:" + textMessageFromClient1);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        user1To2.start();
        Thread user2To1 = new Thread(() -> {
            while (true) {
                try {
                    TcpMessage tcpMessage = null;
                    Object object=tcpIn2.readObject();
                    if (object != null) {//user2发来消息
                        tcpMessage=(TcpMessage) object;
                        System.out.println(tcpMessage);
                        if (tcpMessage.getFlag() == 6) {
                            TextMessageFromClient textMessageFromClient2 = (TextMessageFromClient) tcpMessage;
                            System.out.println(textMessageFromClient2);
                            TextMessageFromServer textMessageFromServer2 = new TextMessageFromServer(
                                    textMessageFromClient2.getDesId(),
                                    textMessageFromClient2.getMessage());
                            tcpOut1.writeObject(textMessageFromServer2);
                            tcpOut1.flush();
                            System.out.println("2to1:" + textMessageFromClient2);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        });
        user2To1.start();
    }
}
