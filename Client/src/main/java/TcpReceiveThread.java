import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class TcpReceiveThread implements Runnable {
    private Socket receiver;
    Datas IPandPortData;
    Thread udpReceiveThread;//UDP接收线程，负责接收video数据
    Thread udpSendThread;//UDP发送线程，负责发送video数据

    public TcpReceiveThread(Socket receiver, Datas IPandPortData) {
        this.receiver = receiver;
        this.IPandPortData = IPandPortData;
    }

    @Override
    public void run() {
        try {
            System.out.println("--------------------receiving----------------------------");
            BufferedReader in = null;
            OUT:
            while (!Thread.interrupted()) {
                in = new BufferedReader(new InputStreamReader(receiver.getInputStream()));
                String message;
                while ((message = in.readLine()) != null) {
                    try {
                        System.out.println("#####" + "对方:   " + message);
                        System.out.println();
                        if (message.equals("/bye")) {//对方终止了连接
                            System.out.println("-----对方终止了对话. connect lost");
                            Thread.sleep(2000);
                            System.exit(0);
                            break OUT;
                        } else if (message.equals("/video")) {
                            System.out.println("----对方开启了视频对话----");
                            System.out.println("----若您想关闭视频对话，请输入\"/video close\"");

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
                        } else if (message.equals("/video close")) {
                            if (udpSendThread != null) {
                                udpSendThread.interrupt();
                                System.out.println("----对方关闭了视频通话----");
                                System.out.println("----你关闭了视频通话sender----");
                            }
                            if (udpReceiveThread != null) {
                                udpReceiveThread.interrupt();
                                System.out.println("----你关闭了视频通话receiver----");
                            }
                        }
                    }catch (InterruptedException e){
                        break OUT;
                    }
                }
            }
            if (!receiver.isClosed()) receiver.close();
            in.close();
        } catch (SocketException e) {
            if (receiver.isClosed()) System.out.println("connect lost");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
