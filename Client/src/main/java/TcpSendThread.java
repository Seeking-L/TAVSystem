import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class TcpSendThread implements Runnable {
    private Socket sender;
    Datas IPandPortData;
    Thread udpReceiveThread;//UDP接收线程，负责接收video数据
    Thread udpSendThread;//UDP发送线程，负责发送video数据

    public TcpSendThread(Socket Sender, Datas IPandPortData) {
        this.sender = Sender;
        this.IPandPortData = IPandPortData;
    }

    @Override
    public void run() {
        try {
            System.out.println("-----若您想要开启视频对话，请输入\"/video\"-----");
            String readline = null;
            String inTemp = null;

            //创建两个流，系统输入流BufferedReader systemIn，socket输出流PrintWriter socketOut;
            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter socketOut = new PrintWriter(sender.getOutputStream());

            while (!Thread.interrupted() && (readline == null || !readline.equals("/bye"))) {
                if (readline != null && readline.equals("/video")) {
                    if (udpSendThread == null) {
                        InetSocketAddress remoteReceiver = new InetSocketAddress(IPandPortData.getRemoteIP(), IPandPortData.getRemoteReceiverPort());
                        udpSendThread = new Thread(new UdpSendThread(remoteReceiver));
                        udpSendThread.start();
                    }
                    if (udpReceiveThread == null) {
                        // 开启新线程处理数据传输
                        udpReceiveThread = new Thread(new UdpReceiveThread(IPandPortData.getLocalReceiverPort()));
                        udpReceiveThread.start();
                    }
                } else if (readline != null && readline.equals("/video close")) {
                    if (udpSendThread != null) {
                        udpSendThread.interrupt();
                        System.out.println("----你关闭了视频通话sender----");
                    }
                    if (udpReceiveThread != null) {
                        udpReceiveThread.interrupt();
                        System.out.println("----你关闭了视频通话receiver----");
                    }
                }
                System.out.println();
                readline = systemIn.readLine();
                socketOut.println(readline);
                socketOut.flush();    //刷新使Server收到，也可以换成socketOut.println(readline, ture)

            }

            if (!sender.isClosed()) sender.close();
            System.out.println("-----对方终止了对话. connect lost");
            Thread.sleep(2000);
            System.exit(0);

            systemIn.close();
            socketOut.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
