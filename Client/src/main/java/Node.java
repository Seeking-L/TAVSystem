import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Node {
    private int localReceiverPort;//this node's port while it work as a receiver.(for both TCP and UDP)
    private ServerSocket tempTcpServerSocket;//等待accept的TCP ServerSocket.等到accept后就没用了
    private int remoteReceiverPort;//对方的receiver socket的端口
    private String remoteIP;//对方的IP地址
    private Datas IPandPortData;
    private Socket tcpSendSocket;//local TCP sender socket
    private Socket tcpReceiveSocket;//local TCP receive socket

//    Thread udpReceiveThread;//UDP接收线程，负责接收video数据
//    Thread udpSendThread;//UDP发送线程，负责发送video数据

    Thread tcpSendThread;//TCP接收线程，负责接收text数据
    Thread tcpReceiveThread;//TCP发送线程，负责发送text数据

    public Node(int localServerPort) {
        this.IPandPortData=new Datas();
        this.localReceiverPort = localServerPort;//port for server socket
        this.IPandPortData.setLocalReceiverPort(this.localReceiverPort);
    }

    public void run() {
        Thread server = new Thread() {
            public void run() {
                try {
                    // 启动服务器
                    tempTcpServerSocket = new ServerSocket(localReceiverPort);
                    System.out.println("Server已启动，正在监听端口 " + localReceiverPort + "...");

                    // 监听并接受其他节点的连接
                    tcpReceiveSocket = tempTcpServerSocket.accept();
                    System.out.println("Server已连接到其他节点：" + tcpReceiveSocket.getRemoteSocketAddress());
                    remoteIP = tcpReceiveSocket.getInetAddress().getHostAddress();
                    IPandPortData.setRemoteIP(remoteIP);

                    if (tcpSendSocket == null) {//若此时自己的sendSocket仍为null(即本客户端是被连接者)

                        //接收对方的server端口号
                        BufferedReader bufferedReader = null;
                        while (true) {
                            bufferedReader = new BufferedReader(new InputStreamReader(tcpReceiveSocket.getInputStream()));
                            String message;
                            if ((message = bufferedReader.readLine()) != null) {
                                System.out.println("对方的Server Port：" + message);
                                remoteReceiverPort = Integer.parseInt(message);
                                IPandPortData.setRemoteReceiverPort(remoteReceiverPort);
                                break;
                            }
                        }

                        //连接对方的server
                        // 发送数据到其他节点
                        tcpSendSocket = new Socket(remoteIP,remoteReceiverPort);
                        tcpSendThread=new Thread(new TcpSendThread(tcpSendSocket,IPandPortData));
                        tcpSendThread.start();

                        System.out.println("TCP sender启动");
                    }

                    tcpReceiveThread=new Thread(new TcpReceiveThread(tcpReceiveSocket,IPandPortData));
                    tcpReceiveThread.start();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        server.start();

        System.out.println("若您想主动联系他人，请输入 \"search\" ");

        //当此Node还未作为服务端接收到连接，而用户输入了search时
        while (tcpSendSocket == null) {
            Scanner scan = new Scanner(System.in);
            if (scan.hasNext() && scan.next().equals("search")) {//TODO 这里应该让用户输入IP
                System.out.println("请输入对方IP地址: ");
                remoteIP = scan.next();
                IPandPortData.setRemoteIP(remoteIP);
                System.out.println("请输入对方端口号: ");
                remoteReceiverPort = scan.nextInt();
                IPandPortData.setRemoteReceiverPort(remoteReceiverPort);
                scan=null;

                try {
                    tcpSendSocket = new Socket(remoteIP,remoteReceiverPort);

                    //将自己的server port发送给对方
                    PrintWriter printWriter = new PrintWriter(tcpSendSocket.getOutputStream());
                    printWriter.println(localReceiverPort);
                    printWriter.flush();    //刷新使Server收到，也可以换成socketOut.println(readline, ture)
                    printWriter=null;//TODO-----------------

                    tcpSendThread=new Thread(new TcpSendThread(tcpSendSocket,IPandPortData));
                    tcpSendThread.start();


                    System.out.println("TCP sender启动");
//                scan.close();//这里不能关闭，为什么？//TODO-----------------
                    break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else{scan=null;}
        }
    }

    public static void main(String[] args) {
        try {
            InetAddress myAddr = null;
            try {
                myAddr = InetAddress.getLocalHost();
                System.out.println("您的IP地址为：" + myAddr.getHostAddress());
            } catch (UnknownHostException e) {
                System.out.println("UnknownHostException:您的IP地址无法获取！");
                System.exit(1);
            }
            System.out.println("请输入您想要使用的端口号 (在1024~49151之内)： ");
            Scanner scan = new Scanner(System.in);
            int serverPortNum = scan.nextInt();
            while (!isPortAvailable(serverPortNum)) {
                System.out.println("这个端口已被占用，请重新选择端口号进行输入 (在1024~49151之内)： ");
                serverPortNum = scan.nextInt();
            }
            System.out.println("端口可用，正在启动server......");

            Node node = new Node(serverPortNum);
            node.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     * LJQ copy from https://stackoverflow.com/questions/434718/sockets-discover-port-availability-using-java
     * */
    public static boolean isPortAvailable(int port) {//if a port is available.
        if (port < 1024 || port > 49151) {
            return false;
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }
}
