package Threads;

import Entity.*;
import Mapper.LoginMapper;
import Utils.SocketWorker;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * server与client联系时使用的线程。当两个用户开启联络，此线程被弃用
 */
public class SingelTcpThread implements Runnable{
    private User user;
    private Socket socket;
    private SocketWorker secondSocket;
    private HashMap<Integer, SocketWorker> sockets;
    private ObjectInputStream tcpIn;
    private ObjectOutputStream tcpOut;

    public SingelTcpThread(Socket socket, HashMap<Integer, SocketWorker> sockets) throws IOException {
        this.socket=socket;
        this.sockets=sockets;
        tcpIn=new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        tcpOut=new ObjectOutputStream(socket.getOutputStream());
    }


    @Override
    public void run() {
        while(true) {
            try {
                TcpMessage tcpMessage = null;
                if (((tcpMessage = (TcpMessage) tcpIn.readObject()) != null)) {
                    //login
                    if (tcpMessage.getFlag() == 1) {
                        TcpLogin loginMessage = (TcpLogin) tcpMessage;
                        System.out.println(loginMessage);
                        user = LoginMapper.login(loginMessage.getUserName(), loginMessage.getPassword());

                        if (user == null) {//login fail
                            tcpOut.writeObject(new LoginResult(false));
                            tcpOut.flush();
                        } else {//login succeeded
                            LinkedList<User> friends = LoginMapper.getFriends(user.getUserId());
                            tcpOut.writeObject(new LoginResult(true, user.getUserId(), friends));
                            tcpOut.flush();

                            //放入socket
                            synchronized (sockets) {
                                sockets.put(user.getUserId(),
                                        new SocketWorker(socket,tcpOut,tcpIn));
                            }
                            break;
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }
        while(sockets.containsKey(user.getUserId())) {
            try {
                TcpMessage tcpMessage = null;
                if (((tcpMessage = (TcpMessage) tcpIn.readObject()) != null)) {
                    if(!sockets.containsKey(user.getUserId())) {
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!结束");
                        break;
                    }
                    if (tcpMessage.getFlag() == 3) {
                        //一个用户发起了连接请求
                        //communication request
                        CommunicationRequest communicationRequest = (CommunicationRequest) tcpMessage;
                        System.out.println(communicationRequest);//TODO 调试用
                        synchronized (sockets) {
                            if (sockets.containsKey(communicationRequest.getFriendId())) {
                                //从hashmap中将即将要进行联络的两个用户的socket移出。这将会结束两个singelTcpThread
                                secondSocket = sockets.remove(communicationRequest.getFriendId());
                                sockets.remove(user.getUserId());

                                //回复发起者，告知好友在线
                                tcpOut.writeObject(new CommunicationRequestReflect(true, communicationRequest.getFriendId()));
                                tcpOut.flush();

                                //准备开始对话
                                Thread communicationThread = new Thread(//顺序不可以反，其中secondSocket是被联系者
                                        new CommunicationThread(sockets,
                                                socket, tcpIn, tcpOut,user.getUserId(),
                                                secondSocket.getSocket(),secondSocket.getTcpIn(),secondSocket.getTcpOut()
                                                ,communicationRequest.getFriendId())
                                );
                                communicationThread.start();

                            }else {
                                //回复发起者，好友 is not available
                                tcpOut.writeObject(new CommunicationRequestReflect(false,communicationRequest.getFriendId()));
                                tcpOut.flush();
                            }
                        }

                    }
                }
            }catch (SocketException e){
                e.printStackTrace();
                return;
            }catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
