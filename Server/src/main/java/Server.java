import Threads.SingelTcpThread;
import Utils.PortUtils;
import Utils.SocketWorker;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
public class Server {
    public static int serverPort;
    public static volatile HashMap<Integer, SocketWorker> sockets;

    public static void main(String[] args) throws IOException {
        serverPort= PortUtils.selectServerPort();
        System.out.println("server port: "+serverPort);
        sockets=new HashMap<>();
        ServerSocket serverSocket=new ServerSocket(serverPort,50, InetAddress.getByName("0.0.0.0"));
        while (true){
            Socket socket=serverSocket.accept();
            System.out.println("tcp连接："+socket.getRemoteSocketAddress());
            Thread singelTcpThread=new Thread(new SingelTcpThread(socket,sockets));
            singelTcpThread.start();
        }
    }
}
